package com.example.chat_server.Async;

import com.example.chat_server.DbSaveMetrics;
import com.example.chat_server.DrainTimeMetrics;
import com.example.chat_server.dto.ChatSocketResponse;
import com.example.chat_server.entity.ChatMessage;
import com.example.chat_server.entity.ChatroomList;
import com.example.chat_server.entity.Member;
import com.example.chat_server.repository.ChatMessageRepository;
import com.example.chat_server.repository.ChatroomRepository;
import com.example.chat_server.repository.MemberRepository;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AsyncChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatroomRepository chatroomRepository;
    private final MemberRepository memberRepository;


    public AsyncChatMessageService(ChatMessageRepository chatMessageRepository,ChatroomRepository chatroomRepository,
            MemberRepository memberRepository
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatroomRepository = chatroomRepository;
        this.memberRepository = memberRepository;

    }

    /*
     * @Async 때문에 이 메소드는 sendMessage()를 실행한 WebSocket 처리 스레드가 아니라
     * 별도의 비동기 스레드에서 실행된다.
     *
     * controller에서 Member 엔티티 자체를 넘기지 않고 memberId만 넘긴 뒤
     * 여기에서 다시 조회하는 이유:
     * 비동기 스레드에서 HTTP/WebSocket 세션 객체에 의존하지 않도록 하기 위해서다.
     */

    @Async("chatTaskExecutor")
    public void save(Long roomId,Long memberId,String content,long enqueueTime) {


        long asyncStart = System.nanoTime();
        double queueWaitMs =(asyncStart - enqueueTime) / 1_000_000.0;
        System.out.println("Async Queue 대기시간 = " + queueWaitMs + " ms");



        System.out.println("Service thread = " + Thread.currentThread().getName()
        );

        long start2 = System.nanoTime();



        ChatroomList room =chatroomRepository.getReferenceById(roomId);

        Member sender =memberRepository.getReferenceById(memberId);

        ChatMessage message = new ChatMessage();
        message.setRoom(room);
        message.setSender(sender);
        message.setContent(content);
        message.setSendTime(LocalDateTime.now());

        chatMessageRepository.save(message);

        long end2 = System.nanoTime();
        double asyncExecutionMs =(end2 - start2) / 1_000_000.0;
        System.out.println("Queue 대기 제외 DB작업실행시간 = "+ asyncExecutionMs + " ms");
        DrainTimeMetrics.recordAsyncFinished();

    }
}
