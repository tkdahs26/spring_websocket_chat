# spring_websocket_chat
## 프로젝트 소개

Spring WebSocket과 STOMP를 기반으로 구현한 실시간 단체 채팅 프로젝트입니다.  
Kafka를 이용해 채팅 메시지를 처리하고, Redis를 이용해 채팅방 참여자 상태를 관리했습니다.
 <br><br>
 <br><br>

## 회원가입 및 로그인
### 회원가입

- 사용자가 아이디, 비밀번호, 닉네임을 입력하여 회원가입할 수 있습니다.
- `MemberRepository`를 통해 아이디 중복 여부를 확인한 후, `Member` 엔티티로 사용자 정보를 생성하여 MySQL에 저장합니다.
- 비밀번호 확인 값이 일치하지 않거나 아이디가 이미 존재하는 경우 회원가입을 중단하고 오류 메시지를 표시합니다.

### 로그인 및 세션 관리
- `MemberRepository`를 통해 아이디에 해당하는 사용자를 검증합니다.
- 로그인 성공 시 사용자의 ID를 지속적으로 조회하기 위해 로그인 정보를 `HttpSession`에 저장하여 이후 요청에서도 로그인 사용자를 조회할 수 있도록 구현했습니다.
- 로그아웃 시 세션을 무효화하여 저장된 로그인 정보를 제거합니다.


### 채팅방 생성

- 로그인 사용자가 채팅방 이름을 입력하여 새로운 채팅방을 생성할 수 있습니다.
- `HttpSession`에서 로그인 사용자 정보를 조회하여 채팅방 생성자를 `owner` 으로 지정하고, 생성할때 입력한 방 이름,생성자를 `ChatroomRepository`를 통해 MySQL에 저장합니다.
   <br><br>
 <br><br>
 <br><br>


## 실시간 채팅 메시지 처리 흐름

**사용자 메세지 입력**  
 ▼  
:arrow_down:STOMP SEND `/app/chat/send`  
    <br><br> <br>
**Spring WebSocket Controller**  
 ▼  
:arrow_down: 로그인 세션에서 `Member` 조회  
:arrow_down: `senderId` 추출  
:arrow_down: `ChatKafkaMessage` DTO 생성 ( `roomId`, `senderId`, `messageContent` )    
    
 <br>

**Kafka Producer**  
 ▼  
:arrow_down: `chat-message` Topic으로 메시지 전송      
 
 <br>

 
**Kafka Consumer**  
 ▼  
:arrow_down: `roomId`로 채팅방 조회 (JPA)  
:arrow_down: `senderId`로 사용자 조회 (JPA)  
:arrow_down: `ChatMessage` Entity 생성  
:arrow_down: 채팅 메시지 DB 저장 (JPA)  
:arrow_down: 브라우저 전송용 `ChatSocketResponse` DTO 생성      
     
<br><br>
**SimpMessagingTemplate**  
 ▼  
:arrow_down: `/topic/room/{roomId}`로 메시지 발행      
 <br>

**채팅방 구독 사용자**  
▼  
:computer:  STOMP Subscribe를 통해 채팅방에 실시간 메시지 수신 
      
 <br><br>
 <br><br>
 <br><br>


 🛠 Tech Stack  
 <br>
⚙️ Backend
<br><br>
Language  :　Java 
<br><br>
Framework  :　Spring Boot · Spring MVC · Spring Data JPA 
<br><br>
Real-Time  :　Spring WebSocket · STOMP 
<br><br>
<br>
🗄 Data 
<br> <br>
Database  :　MySQL
<br><br>
Cache / State  :　Redis
<br><br>
Message Broker  :　Apache Kafka
<br><br>
<br>
🐳 Infra
<br><br>
Docker 
<br><br>
Docker Compose
<br><br>
<br>
🎨 Frontend
<br><br>
Real-Time Client   : STOMP
<br><br>
View   :　JSP · JSTL
<br><br>
Basic   :　HTML5 · CSS3 · JavaScript















