package com.example.chat_server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

     @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/topic");

        /* 사용자가 서버로 보내는 주소 설정
         WebSocketConfig는 웹소켓 시작할 때 미리 설정값으로 정해놈 이 함수는/app으로 시작하면 @MessageMapping 쪽으로 보내라
        */
        registry.setApplicationDestinationPrefixes("/app");
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 접속 주소 "ws://localhost:8080/ws-chat" 아니면  new SockJS("/ws-chat")
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }
}
/* 메세지 전송 시 경로
 웹소켓 최초 실행시 config에서는 /app /topic" 만나면 각각 어디로 보낼지 정함
 클라이언트
       │
publish("/app/chat/send")
      │
      ▼
@MessageMapping("/chat/send")
      │
      │ DB 저장 등 작업하고
      ▼
convertAndSend("/topic/chat/1") 을 만나면
      │
      ▼
브로커
      │
      ▼
subscribe("/topic/chat/1")한 모든 브라우저에 전송*/



