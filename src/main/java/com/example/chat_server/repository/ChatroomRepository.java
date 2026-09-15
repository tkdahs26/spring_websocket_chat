package com.example.chat_server.repository;

import com.example.chat_server.entity.ChatroomList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomRepository extends JpaRepository<ChatroomList, Long> {
}