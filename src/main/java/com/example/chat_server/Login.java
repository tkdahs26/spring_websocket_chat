package com.example.chat_server;


import com.example.chat_server.entity.Member;
import com.example.chat_server.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Login {
    private final MemberRepository memberRepository;



    public Login(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @GetMapping("/")
    public String home() {
        return "LoginFirst";
    }
    @GetMapping("/SignUp")
    public String signUp() {
        return "SignUp";
    }

    @PostMapping("/loginFirst")
    public String login(String id, String password, HttpSession session) {

        Member member = memberRepository.findByUserName(id);

        if (member == null) { //id없음
            return "redirect:/?loginIdError";
        }

        if (!member.getPassword().equals(password)) { //비밀번호다름
            return "redirect:/?loginPasswordError";
        }
        session.setAttribute("loginSession", member); //세션에 setattribute로 첫번째를 저장함 나중에 getattribute"첫번째"하면 두번째값을 반환함

        return "redirect:/ChatHome";
    }


    @GetMapping("/logOut")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }





}