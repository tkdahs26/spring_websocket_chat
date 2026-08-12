package com.example.chat_server.dto;

public class ChatSocketRequest {


    private Long roomId;
    private String messageContent;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
        /*
stompClient.send("/app/chat/send", ...) 사용자(JavaScript)가 이렇게 서버(Java)로 보내면
서버는 @MessageMapping("/chat/send")인 메서드를 찾음(/app은 웹소켓시작할때 설정값이라안읽음)
     */
}
