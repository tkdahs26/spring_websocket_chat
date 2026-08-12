package com.example.chat_server.repository;

import com.example.chat_server.entity.ChatMessage;
import com.example.chat_server.entity.ChatroomList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomOrderBySendTimeAsc(ChatroomList room);
    /*findByRoomOrderBySendTimeAsc 에서 SendTime은 실제 내가만든 필드명




    room_var가 Chatroom_list자료형이라 Chatroom_list 테이블에서
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Chatroom_list room; 했기 때문에 room_id를 반환

    */

}