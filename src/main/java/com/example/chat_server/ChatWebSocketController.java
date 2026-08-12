package com.example.chat_server;
import com.example.chat_server.dto.ChatSocketRequest;
import com.example.chat_server.dto.ChatSocketResponse;
import com.example.chat_server.entity.ChatMessage;
import com.example.chat_server.entity.ChatroomList;
import com.example.chat_server.entity.Member;
import com.example.chat_server.repository.ChatMessageRepository;
import com.example.chat_server.repository.ChatroomRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;




@Controller
public class ChatWebSocketController {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatroomRepository chatroomRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatPresenceManager chatPresenceManager;
    /*Chat_message_repository repo = new Chat_message_repository();안해도 윗줄에 객체 저장되는이유
    * @Controller가 자동으로 new로 객체생성해서 필드에 저장해줌
    * 이유: 스프링 창고에 저장해서 다른 스프링 클래스에서 객체 생성 안할수있게 만드려고
    *
    * */
    public ChatWebSocketController(ChatMessageRepository chatMessageRepository, ChatroomRepository chatroomRepository,
                                   SimpMessagingTemplate messagingTemplate,ChatPresenceManager chatPresenceManager) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatroomRepository = chatroomRepository;
        this.messagingTemplate = messagingTemplate;
        this.chatPresenceManager = chatPresenceManager;
    }














/*
* ChatSocketRequest 는  서버가 사용자한테 받아옴
* ChatSocketResponse 는  서버가 사용자한테 뿌려줌
* */


    @MessageMapping("/chat/send") // app/chat/send Websocketconfig에서 app은 사용자가 서버로 보낸다는것 이게 이거였음
    public void sendMessage(ChatSocketRequest request, SimpMessageHeaderAccessor accessor) {

        System.out.println("서버 sendMessage 실행됨");
        System.out.println("roomId = " + request.getRoomId());
        System.out.println("content = " + request.getMessageContent());
/*
request는 Json으로됨  자바스크립트가 입력할때  roomId: 3, messageContent: "" 이렇게입력함
  자바스크립트가 입력하면 json형태가됨 @MessageMapping이 json을 함수 매개변수의 자료형을 보고 거기에서 setter메소드를 스프링이 찾아서 대입함
 그래서 아랫줄처럼  getter메소드 getRoomId()를 사용가능

*/
        ChatroomList room = chatroomRepository.findById(request.getRoomId()).orElseThrow();

        Member sender =(Member) accessor.getSessionAttributes().get("loginSession");


        //아래함수에서 레디스 사용하기위해
        accessor.getSessionAttributes().put("roomId", request.getRoomId());
        accessor.getSessionAttributes().put("memberId", sender.getId());
        //현재참여인원




        ChatMessage message = new ChatMessage(); //메세지 db 저장
        message.setRoom(room);
        message.setSender(sender);
        message.setContent(request.getMessageContent());
        message.setSendTime(LocalDateTime.now());

        ChatMessage saved =chatMessageRepository.save(message);
       /*
        레포지토리 명령어지만 자료형이 Chat_message인 이유
        save가 반환을 DB엔티티로함 레포지토리는 대부분 Member나 DB엔티티클래스로 반환
        */


        /*
        saved.getId() = Chat_message saved =chatMessageRepository.save(message);로 DB저장할때 테이블의 ID
*/
        ChatSocketResponse response =new ChatSocketResponse(saved.getId(),room.getId(),sender.getId(),
                                                            sender.getNickName(),saved.getMessageContent(),
                                    saved.getSendTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        );




        //  /topic/room/ Websocketconfig에서 topic
        messagingTemplate.convertAndSend("/topic/room/" + room.getId(),response);
       /*웹소켓이 그냥 특정 url에 json으로된 객체를 stompClient.subscribe("/topic/room/" + roomId, function (message))
       여기 첫번째매개변수는 url 두번째매개변수는 message 여기로 json 이동하는거였음+ 실시간통신은 웹소켓내부에서자동실행

       * */
    }
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {


        SimpMessageHeaderAccessor accessor =SimpMessageHeaderAccessor.wrap(event.getMessage());
        /*disconnect할때 event.getMessage에서 스프링이 json객체주는것처럼 Message<?> message = event.getMessage(); 메세지자료형으로 된 객체 준다
        * */
        Long roomId = Long.parseLong(accessor.getSessionAttributes().get("roomId").toString());
        Long userId = Long.parseLong(accessor.getSessionAttributes().get("memberId").toString());
        chatPresenceManager.leaveRoom(roomId,userId);

        Long count = chatPresenceManager.getUserCount(roomId);

        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/count",
                count
        );
    }
}


