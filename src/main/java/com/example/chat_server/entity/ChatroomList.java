package com.example.chat_server.entity;

import jakarta.persistence.*;

@Entity
public class ChatroomList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomName;

    @ManyToOne //자동으로 DB 숫자 id만 저장
    @JoinColumn(name = "owner_id")
    private Member owner;

    public Long getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public Member getOwner() {
        return owner;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setOwner(Member owner) {
        this.owner = owner;
    }
}