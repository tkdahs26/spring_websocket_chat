package com.example.chat_server.repository;

import com.example.chat_server.entity.ChatMessage;
import com.example.chat_server.entity.ChatroomList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomOrderBySendTimeAsc(ChatroomList room);



}