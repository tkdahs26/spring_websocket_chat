# spring_websocket_chat
## 프로젝트 소개

Spring WebSocket과 STOMP를 기반으로 구현한 실시간 단체 채팅 프로젝트입니다.  
Docker를 활용해 Kafka와 Redis 실행 환경을 구성하고 Kafka를 이용해 채팅 메시지를 처리하며 Redis를 이용해 채팅방 참여자 목록 조회를 관리했습니다.
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
- `HttpSession`에서 로그인 사용자 정보를 조회하여 채팅방 생성자를 `owner` 으로 지정하고, 생성할때 입력한 방 이름,방 생성자`owner`를 `ChatroomRepository`를 통해 MySQL에 저장합니다.


### 카프카를 한 이유 
동시 메시지 입력 시 처리 지연으로 인한 메세지 처리 지연을 개선하기 위해 Kafka 사용.
특히 웹소켓에서 메세지 작업 처리 시 DB 저장 작업은 지연 될 것으로 예측해서 별도의 db 저장 지연 시간 측정 클래스를 만들어 실행.
결국 웹소켓에서 메세지 작업 처리하던 것을 Kafka를 도입해 웹소켓에서 메시지 수신, 카프카 후속 처리로 분리했습니다.

### k6테스트 200명의 동시 사용자 총 2,000건의 메시지 부하 테스트 5번 테스트중 최대 값

```text 5번 테스트중 최대 값
웹소켓
message_latency................: avg=272.4635 min=67       med=259      max=549      p(90)=444     p(95)=487.2
messages_received..............: 2000   1030.93182/s
messages_sent..................: 2000   1030.93182/s
-- DB SAVE RESULTS --
db_save_count................: 2000
db_save_duration.............: avg=15.16ms    min=1.74ms    med=12.22ms    max=93.45ms    p(90)=22.06ms    p(95)=29.81ms

카프카
message_latency................: avg=203.3855 min=57       med=220      max=298      p(90)=273      p(95)=280
messages_received..............: 2000   4706.505317/s
messages_sent..................: 2000   4706.505317/s
-- DB SAVE RESULTS --
db_save_count................: 2000
db_save_duration.............: avg=1.61ms    min=1.16ms    med=1.49ms    max=56.74ms    p(90)=1.89ms    p(95)=2.29ms

```
#### 평균 지연 시간      272.46 ms 에서 203.39 ms로 최대 25.4% 감소<br>
#### p95 지연 시간       487.2ms에서    280ms로 최대 42.5% 감소<br>
#### 초당 메시지 처리량  1,030건에서    4,706건으로 최대 4.6배 초당 처리량 향상<br>
#### DB 저장 평균시간    15.16 ms에서   1.61 ms로 최대 89.4% 감소<br>
#### DB 저장 p95         29.81 ms에서   2.29 ms로 최대 92.3% 감소

<br>
<br>
<br>
<br>

###  테스트 5번 테스트의 전체 평균 값 (웹소켓, 카프카)
```text 
웹소켓
message_latency................: avg=304.90       min=73.20      med=266.20      max=620.40      p(90)=516.60      p(95)=558.66
messages_received..............: 2000   1319.15/s
messages_sent..................: 2000   1319.15/s
-- DB SAVE RESULTS --
db_save_count..................: 2000
db_save_duration...............: avg=15.97ms    min=1.76ms    med=12.67ms    max=131.10ms    p(90)=22.56ms    p(95)=34.09ms

카프카
message_latency................: avg=440.72      min=376.60      med=445.10      max=496.40      p(90)=480.60     p(95)=485.01
messages_received..............: 2000   2079.57/s
messages_sent..................: 2000   2079.57/s

-- DB SAVE RESULTS --
db_save_count..................: 2000
db_save_duration...............: avg=1.71ms    min=1.18ms    med=1.55ms    max=53.53ms    p(90)=2.03ms    p(95)=2.55ms

```
####     웹소켓   카프카 5회 평균값
#### 평균 지연 시간      304.90ms에서    440.72ms로   44.5% 증가<br>
#### p95 지연 시간       558.66ms에서    485.01ms로   13.2%감소<br>
#### 초당 메시지 처리량  1,319건에서     2,079건으로  57.6% 초당 처리량 향상<br>
#### DB 저장 평균시간    15.97ms에서     1.71ms로     89.3%감소<br>
#### DB 저장 p95         34.09ms에서     2.55ms로     92.5% 감소


#### 결론 : 5회 부하 테스트의 평균값을 비교한 결과 Kafka 적용 후 메세지 평균 지연 시간은 44.5% 느려졌지만 p95 메시지 지연시간은  13.2% 조금 더 빨라졌습니다.
#### 또한 초당 메시지 처리량이 약 57.6% 증가하고 DB 저장 평균 시간이 약 89.3% 감소한 것이 큰 격차를 줬습니다.
#### 즉 웹소켓만 쓰면 메세지 한 건씩은 빠르게 처리 하겠지만 많은 건을 동시에 처리하는 할 땐  메시지 수신과 후속 처리를 분리한 카프카를 썼을 시
#### 작업을 분리할수록 유리하므로 카프카가 DB저장시간,초당 메시지 처리량 작업은 높은 격차로 우위에 있다는 것을 알 수 있습니다.



 <br><br>
 <br><br>
 <br><br>


## 실시간 채팅 메시지 처리 흐름

**사용자 메세지 입력**  
 ▼  
:arrow_down:STOMP SEND `/app/chat/send`  
    <br><br> <br>
**Spring WebSocket**  
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

##   주요 기능 및 구동 화면

사용자 `민수` 에서 본 화면  
<img width="1919" height="821" alt="minsoo" src="https://github.com/user-attachments/assets/888cfd6f-bf40-43e6-a41b-1dc9c9b46117" />

 <br><br>
 
사용자 `지훈` 에서 본 화면  
<img width="1919" height="817" alt="jihoon" src="https://github.com/user-attachments/assets/84d587f6-336f-46a2-9cba-96e25d1e9f3f" />
 <br><br> <br><br> <br><br>


 <br>
 
`test_chatroom1`채팅방 화면(테스트용)
 <img width="1917" height="820" alt="test_id2" src="https://github.com/user-attachments/assets/b52a3216-dcbb-4156-9701-6ee4d08ffc61" />
 
전반적인 주요 기능 및 k6 과부하 테스트할때 사용한 채팅방입니다.
- WebSocket/STOMP 및 Kafka를 통한 실시간 메시지 처리 , 메시지 DB 저장
- Redis를 이용한 현재 참여 인원 및 버튼 누를 시 참여자 닉네임 표시
- 로그인 사용자와 상대방 메시지의 좌·우 구분 표시
- 상대방 메세지에 닉네임,보낸 시간표시,   날짜표시줄 표시

 
 <br><br>
 <br><br> 
 🛠 Tech Stack  
 <br>
⚙️ Backend
<br><br>
Language  :　Java 
<br>
Framework  :　Spring Boot · Spring MVC · Spring Data JPA 
<br>
Real-Time  :　Spring WebSocket · STOMP 
<br><br>
<br>
🗄 Data 
<br> <br>
Database  :　MySQL
<br>
Cache / State  :　Redis
<br>
Message Broker  :　Apache Kafka
<br><br>
<br>
🐳 Infra
<br><br>
Docker 
<br>
Docker Compose
<br><br>
<br>
🎨 Frontend
<br><br>
Real-Time Client   : STOMP
<br>
View   :　JSP · JSTL
<br>
Basic   :　HTML5 · CSS3 · JavaScript
<br><br><br>
📊 Performance Test
<br><br>
Load Testing : k6













