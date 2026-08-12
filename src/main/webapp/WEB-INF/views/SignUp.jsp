<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원가입</title>
</head>
<body>
<h2>회원가입</h2>
<form action="/signupPost" method="post">
    <label for="id">아이디</label><br>
    <input type="text" id="id" name="userName" value="${userName}" placeholder="아이디"><br><br>

    <label for="password">비밀번호</label><br>
    <input type="password" id="password" name="password" placeholder="비밀번호"><br><br>

    <label for="passwordConfirm">비밀번호 확인</label><br>
    <input type="password" id="passwordConfirm" name="passwordConfirm" placeholder="비밀번호 확인"><br><br>

    <label for="nickName">닉네임</label><br>
    <input type="text" id="nickName" name="nickName"  value="${nickName}" placeholder="닉네임"><br><br>

    <button type="submit">회원가입</button>


</form>

<script>
    const errorMessage = '${errorMessage}';

    if (errorMessage) {
        alert(errorMessage);
    }
</script>