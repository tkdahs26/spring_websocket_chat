# spring_websocket_chat
## 프로젝트 소개

Spring WebSocket과 STOMP를 기반으로 구현한 실시간 단체 채팅 프로젝트입니다.  
 Caffeine을 이용해 채팅방 참여자 목록 조회를 관리했습니다.
 <br><br>
 <br><br>

## 회원 가입 및 로그인
### 회원 가입

- 사용자가 아이디, 비밀번호, 닉네임을 입력하여 회원 가입할 수 있습니다.
- `MemberRepository`를 통해 아이디 중복 여부를 확인한 후, `Member` 엔티티로 사용자 정보를 생성하여 MySQL에 저장합니다.
- 비밀번호 확인 값이 일치하지 않거나 아이디가 이미 존재하는 경우 회원 가입을 중단하고 오류 메시지를 표시합니다.

### 로그인 및 세션 관리
- `MemberRepository`를 통해 아이디에 해당하는 사용자를 검증합니다.
- 로그인 성공 시 사용자의 ID를 지속적으로 조회하기 위해 로그인 정보를 `HttpSession`에 저장하여 이후 요청에서도 로그인 사용자를 조회할 수 있도록 구현했습니다.
- 로그아웃 시 세션을 무효화하여 저장된 로그인 정보를 제거합니다.


### 채팅 방 생성

- 로그인 사용자가 채팅 방 이름을 입력하여 새로운 채팅 방을 생성할 수 있습니다.
- `HttpSession`에서 로그인 사용자 정보를 조회하여 채팅 방 생성자를 `owner` 으로 지정하고, 생성할 때 입력한 방 이름,방 생성자`owner`를 `ChatroomRepository`를 통해 MySQL에 저장합니다.
 

### 카프카를 한 이유 
- k6와 별도의 db 저장 시간 측정 클래스를 만들고 카프카 도입 전 과부하 테스트 실행.<br>
- 테스트 후  테스트당 편차가 64.9%를 기록. 원인은  WebSocket이 동시 요청 환경에서 메시지 수신부터 DB 저장까지 모두 처리하는 구조인 것으로 판단.
- Kafka를 도입하면 웹소켓이 메세지 수신, 카프카는 DB 저장 및 메시지 전송 처리를 담당하기 때문에 처리 지연 편차를 줄이고 DB 저장 작업 속도가 개선되어서 메세지 처리 지연를 빠르게 할 수 있다고 판단함.<br>


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
- WebSocket/STOMP를 통한 실시간 메시지 처리 , 메시지 DB 저장
- Caffeine를 이용한 현재 참여 인원 및 버튼 누를 시 참여자 닉네임 표시
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
Cache / State  :　Caffeine
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













