<%--
  Created by IntelliJ IDEA.
  User: bsm
  Date: 26. 7. 25.
  Time: 오전 5:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>채팅만들기</title>
</head>
<body>

<form action="/chatRoom/makeRoom"method="post">
    <input type="text" name="roomName"placeholder="채팅방 이름"required>
    <button type="submit">방 만들기</button>
</form>

</body>
</html>
