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
### WebSocket 세션 연동 문제

**문제**
WebSocket 메시지 처리 과정에서 HTTP Session의 로그인 사용자 정보를
가져오지 못해 sender가 null이 되는 문제가 발생했습니다.

**원인**
WebSocket 연결 과정에서 HTTP Session 정보가 WebSocket Session으로
전달되지 않았습니다.

**해결**
HandshakeInterceptor를 적용하여 HTTP Session 정보를
WebSocket SessionAttributes로 전달하도록 수정했습니다.
