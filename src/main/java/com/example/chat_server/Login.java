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

        Member member = memberRepository.findByUserName(id); // findByUsername= sql문 select랑 같음 id값이 있는 행을 다 가져옴

        if (member == null) { //id없음
            return "redirect:/?loginIdError";
        }

        if (!member.getPassword().equals(password)) { //비밀번호다름
            return "redirect:/?loginPasswordError";
        }
        session.setAttribute("loginSession", member);

        return "redirect:/ChatHome";
    }






}