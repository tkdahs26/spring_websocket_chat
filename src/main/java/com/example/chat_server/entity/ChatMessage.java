package com.example.chat_server.entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ChatMessage {
    //전송버튼누를때 메세시 저장 db
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private ChatroomList room;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Column(nullable = false)
    private String messageContent;

    private LocalDateTime sendTime;


    public Long getId() {
        return id;
    }

    public void setRoom(ChatroomList room) {
        this.room = room;
    }
    public void setSender(Member sender) {
        this.sender = sender;
    }

    public void setContent(String messageContent) {
        this.messageContent = messageContent;
    }
    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public ChatroomList getRoom() {
        return room;
    }
    public Member getSender() {  //getter함수하면 지금 DB데이터를 꺼내옴
        return sender;
    }

    public String getMessageContent() {
        return messageContent;
    }
    public LocalDateTime getSendTime() {
        return sendTime;
    }
}
