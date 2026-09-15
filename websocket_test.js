import ws from 'k6/ws';
import http from 'k6/http';
import { check } from 'k6';
import { Trend, Counter, Rate } from 'k6/metrics';

// ===========================
// k6 결과 지표
// ===========================

// 메시지를 보낸 뒤
// "내가 보낸 바로 그 메시지"를 다시 받을 때까지 시간
const messageLatency = new Trend('message_latency');

// 실제 채팅 SEND 개수
const messagesSent = new Counter('messages_sent');

// 내가 보낸 메시지를 정상적으로 다시 받은 개수
const messagesReceived = new Counter('messages_received');

// 메시지 처리 실패율
const messageFailures = new Rate('message_failures');

export const options = {
    vus: 200,
    iterations: 200,
};

export default function () {
    // ===========================
    // 1. 로그인
    // ===========================
    const loginRes = http.post(
        'http://localhost:8080/loginFirst',
        {
            id: 'test_id1',
            password: 'test_password1',
        }
    );

    /*
     * 로그인 결과에서 세션 쿠키를 꺼냄.
     *
     * 기존 코드에서는 WebSocket 연결할 때
     * 로그인 세션 쿠키를 명시적으로 전달하지 않았음.
     *
     * k6의 cookie jar를 사용해서
     * 현재 로그인한 HTTP 세션 쿠키를 가져옴.
     */
    const jar = http.cookieJar();
    const cookies = jar.cookiesForURL(
        'http://localhost:8080'
    );

    let cookieHeader = '';
    for (const cookieName in cookies) {
        const cookieValues = cookies[cookieName];
        if (cookieValues.length > 0) {
            if (cookieHeader.length > 0) {
                cookieHeader += '; ';
            }
            cookieHeader +=
                `${cookieName}=${cookieValues[0]}`;
        }
    }

    /*
     * ★ 오류 수정
     *
     * 로그인이 실패했거나 세션 쿠키가 없으면
     * WebSocket에 접속하지 않고 종료.
     *
     * 인증 없이 접속하면 서버에서
     * sender가 null이 되어
     * NullPointerException이 발생함.
     */
    check(loginRes, {
        '로그인 성공': (r) => r.status === 200,
    });

    if (cookieHeader.length === 0) {
        messageFailures.add(true);
        return;
    }

    // ===========================
    // 2. WebSocket 연결
    // ===========================
    const url =
        'ws://localhost:8080/websocket';

    const res = ws.connect(url,
        /*
         * 로그인했을 때 받은 JSESSIONID를
         * WebSocket handshake에도 전달.
         */
        {
            headers: {
                Cookie: cookieHeader,
            },
        },
        function (socket) {
            /*
             * 이번 iteration에서
             * 정상적으로 다시 받은
             * "내 메시지" 개수
             */
            let receivedCount = 0;

            /*
             * iteration 하나당
             * 보낼 메시지 개수
             */
            const messageCount = 10;

            /*
             * 이번 iteration만의 고유 ID 생성.
             */
            const testId =
                `${__VU}-${__ITER}-${Date.now()}`;

            /*
             * 이미 받은 메시지 번호를 기록.
             */
            const receivedIndexes = new Set();

            // ===========================
            // 3. 서버 메시지 수신
            // ===========================
            socket.on('message', function (message) {
                // ===========================
                // STOMP CONNECT 성공
                // ===========================
                if (message.startsWith('CONNECTED')) {
                    // ---------------------------
                    // 채팅방 구독
                    // ---------------------------
                    socket.send(
                        "SUBSCRIBE\n" +
                        "id:sub-0\n" +
                        "destination:/topic/room/1\n" +
                        "\n\u0000"
                    );

                    // ===========================
                    // 메시지 10개 전송
                    // ===========================
                    for (
                        let i = 0;
                        i < messageCount;
                        i++
                    ) {
                        const sentAt = Date.now();

                        const messageContent =
                            `[k6_websocket]|${testId}|${i}|${sentAt}`;

                        const body = JSON.stringify({
                            roomId: 1,
                            messageContent:
                            messageContent
                        });

                        /*
                         * ★ 오류 수정
                         *
                         * STOMP 프레임은
                         * 헤더마다 개행(\n)이 필요하고
                         * 헤더와 body 사이에 빈 줄이 필요함.
                         */
                        socket.send(
                            "SEND\n" +
                            "destination:/app/chat/send\n" +
                            "content-type:application/json;charset=UTF-8\n" +
                            "\n" +
                            body +
                            "\u0000"
                        );

                        // 전송 횟수 기록
                        messagesSent.add(1);
                    }

                    return;
                }

                // ===========================
                // STOMP MESSAGE 수신
                // ===========================
                if (message.startsWith('MESSAGE')) {
                    try {
                        // ---------------------------
                        // STOMP body 위치 찾기
                        // ---------------------------
                        const bodyStart =
                            message.indexOf('\n\n');

                        if (bodyStart === -1) {
                            messageFailures.add(true);
                            return;
                        }

                        // ---------------------------
                        // STOMP body 추출
                        // ---------------------------
                        const bodyText =
                            message
                                .substring(
                                    bodyStart + 2
                                )
                                .replace(
                                    /\u0000/g,
                                    ''
                                );

                        const data =
                            JSON.parse(bodyText);

                        /*
                         * 서버 ChatSocketResponse의
                         * messageContent
                         */
                        const content =
                            data.messageContent;

                        // ===========================
                        // k6 테스트 메시지가 아니면 무시
                        // ===========================
                        if (
                            !content ||
                            !content.startsWith(
                                '[k6_websocket]|'
                            )
                        ) {
                            return;
                        }

                        const parts =
                            content.split('|');

                        if (parts.length !== 4) {
                            return;
                        }

                        const receivedTestId =
                            parts[1];
                        const messageIndex =
                            Number(parts[2]);
                        const sentAt =
                            Number(parts[3]);

                        // ===========================
                        // 다른 VU / 다른 iteration
                        // 메시지는 절대 측정하지 않음
                        // ===========================
                        if (
                            receivedTestId !== testId
                        ) {
                            return;
                        }

                        // ===========================
                        // 값 검증
                        // ===========================
                        if (
                            Number.isNaN(messageIndex) ||
                            Number.isNaN(sentAt)
                        ) {
                            messageFailures.add(true);
                            return;
                        }

                        if (
                            messageIndex < 0 ||
                            messageIndex >= messageCount
                        ) {
                            messageFailures.add(true);
                            return;
                        }

                        // ===========================
                        // 중복 메시지 방지
                        // ===========================
                        if (
                            receivedIndexes.has(
                                messageIndex
                            )
                        ) {
                            return;
                        }

                        receivedIndexes.add(
                            messageIndex
                        );

                        // ===========================
                        // 정확한 end-to-end latency
                        // ===========================
                        const latency =
                            Date.now() - sentAt;

                        messageLatency.add(
                            latency
                        );
                        messagesReceived.add(1);
                        messageFailures.add(
                            false
                        );

                        receivedCount++;

                        // ===========================
                        // 내가 보낸 10개를
                        // 전부 정상적으로 받았으면 종료
                        // ===========================
                        if (
                            receivedCount >=
                            messageCount
                        ) {
                            socket.close();
                        }
                    } catch (e) {
                        messageFailures.add(
                            true
                        );
                    }
                }
            });

            // ===========================
            // 4. STOMP CONNECT
            // ===========================
            socket.send(
                "CONNECT\n" +
                "accept-version:1.2\n" +
                "heart-beat:0,0\n" +
                "\n\u0000"
            );

            // ===========================
            // 5. 15초 timeout
            // ===========================
            socket.setTimeout(
                function () {
                    if (
                        receivedCount <
                        messageCount
                    ) {
                        const missing =
                            messageCount -
                            receivedCount;

                        for (
                            let i = 0;
                            i < missing;
                            i++
                        ) {
                            messageFailures.add(
                                true
                            );
                        }

                        socket.close();
                    }
                },
                15000
            );
        }
    );

    // ===========================
    // 6. WebSocket handshake 검사
    // ===========================
    check(res, {
        'WebSocket 연결 성공':
            (r) =>
                r &&
                r.status === 101,
    });
}
