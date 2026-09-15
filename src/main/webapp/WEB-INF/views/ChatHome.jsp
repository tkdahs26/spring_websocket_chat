<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  Created by IntelliJ IDEA.
  User: bsm
  Date: 26. 7. 24.
  Time: 오전 6:20
  To change this template use File | Settings | File Templates.
--%>

<!-- 이 부분은 HTML 주석입니다. -->
<%--        --%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>채팅방</title>

    <link rel="stylesheet"href="/css/chatHome.css">
</head>
<body>

<div class="chatUi">

    <aside class="leftBorder">
        <h2>채팅방 목록</h2>
        <div class="roomListParent">
            <c:forEach var="roomForEach" items="${chatroomListVar}"> <!--chatroomListVar를 하나씩 roomForEach에 넣는다-->

                <div class="roomList">
                    <a href="/ChatHome?id=${roomForEach.id}">
                        ${roomForEach.roomName}
                    </a>
                </div>

            </c:forEach>


        </div>
        <div class="roomButtonArea">
        <button  class="makeRoomButton" onclick="location.href='/makeChatroom'"> 방 만들기 </button>
        <button class="logOutButton" onclick="logout()">로그아웃</button>
        </div>

        <div class="myProfile">
            <div class="profileImageBox">
                <img class="profileImage" src="/image/profile.png"  alt="프로필사진">
            </div>

            <div class="profileId">아이디: ${loginUser.userName}</div>
            <div class="profileNickName">닉네임: ${loginUser.nickName} </div>
        </div>
    </aside>
    <%-- ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ오른쪽 시작 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ           --%>
    <main class="rightBorder">
        <div class="chatInfomation">
            <span>선택한 채팅방이름 : ${roomVar.roomName} </span>
            <span>방 만든 사람 : ${roomVar.owner.nickName}</span>

                <span id="userCount">현재 참여 인원 : ${userCount}</span>
                <span class="userListArea">
                    <button type="button" id="userListButton">
                        현재 참여 인원
                    </button>
                <div id="userListPopUp" style="display:none;">
                    <div id="userList"></div>
                </div>
                 </span>


            </h5>
        </div>






        <div class="messageStack">

 <!-- ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ날짜구분선ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ -->
    <c:forEach var="view" items="${messageViewList}"><!-- for문  -->

        <c:if test="${view.showDate}"><!-- 만약view.isShowDate값이 참이면 날짜 구분선을 씀-->
            <div class="dateDivider">
                <span>${view.dateText}</span>
            </div>
        </c:if>
                 <c:set var="messageVar" value="${view.message}" />
                 <%-- messageVar은 message엔티티 필드를 가져온것 view는 ChatMessageView dto필드를 가져온것
                 dto 필드.message로 엔티티가져올수있는이유 dto message의 자료형이  private ChatMessage message;라서
                 마음대로 message.sender.id할수있음 sender.id할수있는이유가 sender는 Member자료형이라 Member의 필드인 id를 쓸수있음
                참고로 JSP에서 messageVar.sender.id는 무조건 getter함수 호출임
                이걸 풀면 messageVar.getSender().getId() 즉 getter()함수가 정의되있어야 사용가능

                 c:set 은 value값을 var로 바꿔서 처리한다                 --%>
            <!--ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 날짜구분선 -ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ-->


                <c:choose>
                <%--<c:when test="${messageVar.sender.id == loginUser.id}">
                    sessionScope쓰면 로그인세션얻을수있음 이거하면 새로고침해도 로그인세션안날아가서 내가보낸메세지오른쪽유지
                  --%>
                    <c:when test="${messageVar.sender.id == sessionScope.loginSession.id}">
                    <script>  console.log("내가보낸메세지");</script>

                    <div class="myMessage">
   --%>

                        <div class="messageContent">
                                ${messageVar.messageContent}
                        </div>
                        <div class="messageTime">
                                ${view.timeText}
                        </div>
                    </div>
                    </c:when>

                <c:otherwise>

                    <script>  console.log("남이보낸메세지");</script>

                    <div class="otherMessage">
                        <div class="senderName">
                                ${messageVar.sender.nickName}
                        </div>
                        <div class="otherMessageRow">
                             <div class="messageContent">
                                ${messageVar.messageContent}
                             </div>

                             <div class="messageTime">
                                     ${view.timeText}
                             </div>
                        </div>
                    </div>

                </c:otherwise>

                </c:choose>
            </c:forEach>
        </div>




        <form id="messageForm">
            <input type="text"id="messageInput"autocomplete="off">
            <button type="submit">전송</button>
        </form>


    </main>
</div>

</body>
</html>

<script>
    const params = new URLSearchParams(window.location.search);

    if (params.has("makeSuccess")) {
        alert("채팅방을 정상적으로 만들었습니다..");
        history.replaceState({}, "", "/chatHome");
    }

</script>
<script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/bundles/stomp.umd.min.js"></script>

<!-- 웹소켓 시작 -->

<c:if test="${roomVar != null}"> <!--방입장시 roomVar반환한거 가져옴 이게 null이 아니라면 -->

    <script>
        const roomId = "${roomVar.id}";
        const loginUserId = "${loginUser.id}";

        const wsProtocol =location.protocol === "https:" ? "wss:" : "ws:";
        const stompClient = new StompJs.Client({brokerURL: wsProtocol + "//" + location.host + "/websocket"});


        stompClient.onConnect = function () {

            console.log("웹소켓 연결 성공");
            console.log("구독 주소:", "/topic/room/" + roomId);



            stompClient.subscribe("/topic/room/" + roomId, function (message) {
                    const received = JSON.parse(message.body);
                    console.log("받은 메시지:", received);
                    addMessageToScreen(received);
                }
            );

            stompClient.subscribe(
                "/topic/room/" + roomId + "/count",
                function(message) {
                    document.getElementById("userCount").textContent =message.body;
                }
            );


        };

        stompClient.onStompError = function (frame) {
            console.error("STOMP 오류:", frame);
        };

        stompClient.activate();



        <!--                                메세지 서버로 전송(사용자역할)                                    -->
        const messageForm =document.getElementById("messageForm");

        const messageInput =document.getElementById("messageInput");

        messageForm.addEventListener("submit", function (event) {
            event.preventDefault();

            const content = messageInput.value.trim();


            console.log("전송 직전 roomId:", roomId);
            console.log("전송 직전 content:", content);



            if (content === "") {
                alert("빈 문장입니다");
                return;
            }
                        //메세지 입력값 서버로 전송함수
            stompClient.publish({destination: "/app/chat/send",
                body: JSON.stringify({
                    roomId: Number(roomId),
                    messageContent: content
                })
            });
            console.log("입력값 서버 전송 완료");

            messageInput.value = "";
        });


        <!--                                서버에서 DB저장 후 메세지 출력(서버역할)                                    -->
        function addMessageToScreen(message) {  //message는 ChatWebSocketController의 response객체
            const messageStack =document.querySelector(".messageStack");

            const wrapper = document.createElement("div");

            const isMine =String(message.senderId) === String(loginUserId);

            if (isMine) {
                wrapper.className = "myMessage"; //위 jsp랑 클래스이름은 같지만 중복안되고 append개념이라 쌓임


                /*wrapper.innerHTML 에서 wrapper가 <div></div>니까
                 <div>
                <span class="messageTime">
                  14:35
                 </span>
                </div>  */
                wrapper.innerHTML =`<span class="messageTime">
                \${message.sendTime}
            </span>

            <div class="messageContent">
                \${escapeHtml(message.messageContent)}
            </div> `;



            } else {
                wrapper.className = "otherMessage";

                wrapper.innerHTML = `<div class="senderName">
                \${escapeHtml(message.senderNickname)}
            </div>

            <div class="otherMessageRow">
                <div class="messageContent">
                    \${escapeHtml(message.messageContent)}
                </div>

                <span class="messageTime">
                    \${message.sendTime}
                </span>
            </div>
        `;
            }

            messageStack.appendChild(wrapper);
            /*
            wrapper를 messageStack의 마지막 자식으로 추가하라는 뜻

            */

            messageStack.scrollTop =messageStack.scrollHeight;
        }



        function escapeHtml(text) {
            const div = document.createElement("div");
            div.textContent = text;  //윗줄까지 읽으면 <div>text</div>랑같음
            return div.innerHTML; //<div>태그를 뺸 text만 반환
        }

        //현재인원버튼클릭시 인원목록
        document.getElementById("userListButton").addEventListener("click", function () {

            const popUp = document.getElementById("userListPopUp");

            // 보이게되어있으면 닫기
            if (popUp.style.display === "block") {
                popUp.style.display = "none";
                return;
            }

            fetch("/chat/room/users?roomId=" + roomId)
                .then(response => response.json())
                .then(users => {

                    const userList = document.getElementById("userList");

                    userList.innerHTML = ""; //버튼을 두 번 눌렀을 때 기존 목록에 또 추가되는 걸 방지


                    //users 안에 있는 값을 하나씩 꺼내서 반복
                    users.forEach(function(user) {

                        const div = document.createElement("div");

                        div.textContent = user;

                        userList.appendChild(div);
                    });

                    //팝업 none이였던걸 보이게표시
                    popUp.style.display = "block";

                });
        });

        async function logout() {

            if (stompClient && stompClient.active) {
                await stompClient.deactivate();
            }

            location.href = "/logOut";
        }


    </script>
</c:if>




