package com.example.chat_server;


import com.example.chat_server.entity.ChatroomList;
import com.example.chat_server.entity.Member;
import com.example.chat_server.repository.ChatroomRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class MakeChatroom {
    private final ChatroomRepository chatroomRepository;

    public MakeChatroom(ChatroomRepository chatroomRepository) {
        this.chatroomRepository = chatroomRepository;
    }
    @GetMapping("/makeChatroom")
    public String makeChatroom() {
        return "MakeChatroom";
    }

    /*form action으로하는건 postMapping
    location.href= 같이 url로하는건 getMapping
    *
    * */
    @PostMapping("/chatRoom/makeRoom")
    public String createRoom(String roomName,HttpSession session){
        Member loginSession =(Member) session.getAttribute("loginSession");// 로그인 했을때 저장한 멤버 세션 꺼내기

        if (loginSession == null) {
            return "redirect:/?loginSessionError";
        }



        ChatroomList chatroom = new ChatroomList();
        chatroom.setRoomName(roomName);
        chatroom.setOwner(loginSession);

        chatroomRepository.save(chatroom);

        return "redirect:/ChatHome?makeSuccess";



    }









}
