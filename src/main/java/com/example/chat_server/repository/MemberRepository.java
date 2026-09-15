package com.example.chat_server.repository;

import com.example.chat_server.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByUserName(String userName);
    Member findByUserName(String userName);













}