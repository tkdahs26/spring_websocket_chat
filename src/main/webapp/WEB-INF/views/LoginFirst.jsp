<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>]












<form action="/loginFirst" method="post">
    <input type="text" name="id" placeholder="아이디">
    <input type="password" name="password" placeholder="비밀번호">
    <button type="submit">로그인</button>
</form>

<button onclick="location.href='/SignUp'">
    회원가입
</button>

<script>
    const params = new URLSearchParams(window.location.search);

    if (params.has("joinSuccess")) {
        alert("회원가입이 완료되었습니다.");
        history.replaceState({}, "", "/");
    }

    if (params.has("loginIdError")) {
        alert("ID오류 입니다.");
        history.replaceState({}, "", "/");
    }

    if (params.has("loginPasswordError")) {
        alert("비밀번호 오류입니다.");
        history.replaceState({}, "", "/");
    }

    if (params.has("loginSessionError")) {
        alert("로그인 세션 에러 다시 로그인 해주세요");
        history.replaceState({}, "", "/");
    }
</script>