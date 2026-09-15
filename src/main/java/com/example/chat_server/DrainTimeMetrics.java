package com.example.chat_server;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DrainTimeMetrics {

    // 이번 테스트에서 처리할 전체 메시지 수
    private static final int TOTAL_TASKS = 2000;

    // 첫 메시지가 서버에 들어온 시각
    private static final AtomicLong firstMessageTime = new AtomicLong(0);

    // 이번 테스트에서 서버에 들어온 메시지 개수
    private static final AtomicInteger receivedCount = new AtomicInteger(0);

    // 완료된 Async DB 작업 개수
    private static final AtomicInteger completedCount = new AtomicInteger(0);

    // Drain time이 중복 출력되는 것을 막기 위한 값
    private static volatile boolean printed = false;


    /*
     * WebSocket Controller의 sendMessage() 시작할 때 호출
     */
    public static synchronized void recordMessageReceived() {

        /*
         * 이전 테스트 2000개가 전부 끝났다면
         * 다음 테스트의 첫 메시지가 들어올 때 자동 초기화
         */
        if (completedCount.get() >= TOTAL_TASKS) {

            firstMessageTime.set(0);
            receivedCount.set(0);
            completedCount.set(0);
            printed = false;

            System.out.println(
                    "========== DRAIN TIME 측정 초기화 =========="
            );
        }


        int received = receivedCount.incrementAndGet();


        // 첫 번째 메시지만 시작시간 기록
        if (received == 1) {

            long start = System.nanoTime();

            firstMessageTime.set(start);

            System.out.println(
                    "========== DRAIN TIME 측정 시작 =========="
            );
        }


        // 확인용
        if (received == TOTAL_TASKS) {

            System.out.println(
                    "총 2000개 메시지 서버 진입 완료"
            );
        }
    }


    /*
     * Async DB 저장 작업이 완전히 끝난 뒤 호출
     */
    public static void recordAsyncFinished() {

        int completed = completedCount.incrementAndGet();


        // 마지막 2000번째 Async 작업 완료
        if (completed == TOTAL_TASKS && !printed) {

            synchronized (DrainTimeMetrics.class) {

                if (!printed) {

                    printed = true;

                    long lastAsyncEnd = System.nanoTime();

                    long first = firstMessageTime.get();

                    double drainTimeMs =
                            (lastAsyncEnd - first)
                                    / 1_000_000.0;


                    System.out.println();
                    System.out.println(
                            "======================================"
                    );

                    System.out.println(
                            "Async 작업 완료 개수 = "
                                    + completed
                    );

                    System.out.println(
                            "DRAIN TIME = "
                                    + drainTimeMs
                                    + " ms"
                    );

                    System.out.println(
                            "======================================"
                    );
                    System.out.println();
                }
            }
        }
    }
}