package com.example.chat_server;
import com.example.chat_server.Async.AsyncChatMessageService;
import com.example.chat_server.dto.ChatSocketRequest;
import com.example.chat_server.dto.ChatSocketResponse;
import com.example.chat_server.entity.ChatroomList;
import com.example.chat_server.entity.Member;
import com.example.chat_server.redis.ChatPresenceManager;
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
import java.util.Set;
import java.util.UUID;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;




@Controller
public class ChatWebSocketController {
    private final ChatroomRepository chatroomRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatPresenceManager chatPresenceManager;
    private final AsyncChatMessageService asyncChatMessageService;


    public ChatWebSocketController( ChatroomRepository chatroomRepository,
                                    SimpMessagingTemplate messagingTemplate,ChatPresenceManager chatPresenceManager,
                                   AsyncChatMessageService asyncChatMessageService) {
        this.chatroomRepository = chatroomRepository;
        this.messagingTemplate = messagingTemplate;
        this.chatPresenceManager = chatPresenceManager;
        this.asyncChatMessageService = asyncChatMessageService;
    }



    @MessageMapping("/chat/send")
    public void sendMessage(ChatSocketRequest request, SimpMessageHeaderAccessor accessor) {
        DrainTimeMetrics.recordMessageReceived();
        long start2 = System.nanoTime();
        System.out.println("서버 sendMessage 실행됨");
        System.out.println("roomId = " + request.getRoomId());
        System.out.println("content = " + request.getMessageContent());
        ChatroomList room = chatroomRepository.findById(request.getRoomId()).orElseThrow();
        long webSocketStart = System.nanoTime();
        Member sender =(Member) accessor.getSessionAttributes().get("loginSession");



        System.out.println("Service thread = " + Thread.currentThread().getName()        );

        //현재참여인원


        String messageUuid = UUID.randomUUID().toString();
        ChatSocketResponse response =new ChatSocketResponse(null,messageUuid,request.getRoomId(),
                        sender.getId(),sender.getNickName(),request.getMessageContent(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                );

        // 사용자에게 먼저 전송
        messagingTemplate.convertAndSend("/topic/room/" + request.getRoomId(),                response
        );




        long enqueueTime = System.nanoTime();
        //  DB 저장은 뒤에서 실행
        asyncChatMessageService.save(request.getRoomId(),sender.getId(),request.getMessageContent() ,enqueueTime       );

        long end2 = System.nanoTime();
        double asyncExecutionMs =(end2 - start2) / 1_000_000.0;
        System.out.println("Queue 대기 제외 실행시간 = "+ asyncExecutionMs + " ms");

    }



    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        SimpMessageHeaderAccessor accessor =SimpMessageHeaderAccessor.wrap(event.getMessage());
        /*disconnect할때 event.getMessage에서 스프링이 json객체주는것처럼 Message<?> message = event.getMessage(); 메세지자료형으로 된 객체 준다
        * */
        System.out.println("===== disconnect 발생 ====="+ LocalDateTime.now());

        System.out.println("disconnect 세션 = " + accessor.getSessionAttributes());


        Object roomIdObj =accessor.getSessionAttributes().get("roomId");
        Member member =(Member) accessor.getSessionAttributes().get("loginSession");


        // 안전장치
        if (roomIdObj == null || member == null) {
            System.out.println("roomId 또는 loginSession 없음");
            return;
        }


        Long roomId = Long.valueOf(roomIdObj.toString());
        Long userId = member.getId();


        chatPresenceManager.leaveRoom(roomId, userId);

        Long count =chatPresenceManager.getUserCount(roomId);

        System.out.println("퇴장 완료 roomId=" + roomId+ ", memberId=" + userId
        );




        System.out.println(
                "퇴장 후 roomId=" + roomId + ", count=" + count
        );

        chatPresenceManager.printCache();
        System.out.println("5");
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/count",count        );





    }
}


