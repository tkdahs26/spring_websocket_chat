# spring_websocket_chat
## 회원가입 및 로그인

### 회원가입


- 사용자가 아이디, 비밀번호, 이메일, 이름을 입력하여 회원가입할 수 있습니다.
- 가입한 사용자 정보는 SQLite 데이터베이스에 저장됩니다.

### 로그인 및 세션 관리
- 로그인 성공 시 서버 세션에 사용자 정보를 저장합니다.
- 페이지를 이동해도 로그인 상태가 유지됩니다.

### DB 저장 결과


회원가입 완료 후 사용자 정보가 SQLite DB에 정상적으로 저장




문제: 페이지 이동 및 요청 처리 과정에서 로그인한 사용자의 ID를 지속적으로 식별하기 어려웠습니다.
해결: 로그인 성공 시 사용자 정보를 HttpSession에 저장하고, 이후 요청에서 세션의 로그인 정보를 조회하여 사용자를 식별하도록 구현했습니다.



 🛠 Tech Stack  
 <br>
⚙️ Backend

Language　Java 
<br>
Framework　Spring Boot · Spring MVC · Spring Data JPA 
<br>
Real-Time　Spring WebSocket · STOMP 
<br>

🗄 Data 
<br>
 
Database　MySQL   
<br>
Cache / State　Redis
<br>
Message Broker　Apache Kafka
<br>
🐳 Infra
<br>
Docker · Docker Compose
<br>
🎨 Frontend
<br>
View　JSP · JSTL
<br>
Basic　HTML5 · CSS3 · JavaScript
<br>
Real-Time Client　STOMP.js
<br>















