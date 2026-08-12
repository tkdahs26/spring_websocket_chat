package com.example.chat_server.dto;

public class ChatSocketResponse {

    private Long messageId;
    private Long roomId;
    private Long senderId;
    private String senderNickname;
    private String messageContent;
    private String sendTime;

    public ChatSocketResponse(Long messageId,Long roomId,Long senderId,
                              String senderNickname,String messageContent,String sendTime) {
        this.messageId = messageId;
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.messageContent = messageContent;
        this.sendTime = sendTime;
    }






    public Long getMessageId() {
        return messageId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getSendTime() {
        return sendTime;
    }
}