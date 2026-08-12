package com.example.chat_server;
import com.example.chat_server.entity.Member;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.chat_server.repository.MemberRepository;


@Controller
public class SignUp {
private final MemberRepository memberRepository;



    public SignUp(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @GetMapping("/Signup")
    public String signup() {

        return "Signup";
    }




    @PostMapping("/signupPost")
    public String signUpMethod(String userName, String password, String passwordConfirm, String nickName, Model model) {
        // 비밀번호 확인
        if (!password.equals(passwordConfirm)) {
            model.addAttribute("errorMessage", "비밀번호가 다릅니다.");
            model.addAttribute("userName", userName);
            model.addAttribute("nickName", nickName);
            return "Signup";
        }


        if (memberRepository.existsByUserName(userName)) {
            model.addAttribute("errorMessage", "이미 사용 중인 아이디입니다.");
            model.addAttribute("userName", userName);
            model.addAttribute("nickName", nickName);
            return "Signup";
        }


        Member member = new Member(); //엔티티 생성자 만들어서 set함수에 넣으면 엔티티가 자동으로 db테이블에 넣음
        member.setUserName(userName);
        member.setPassword(password);
        member.setNickName(nickName);

        memberRepository.save(member);

        return "redirect:/?joinSuccess";

    }

}