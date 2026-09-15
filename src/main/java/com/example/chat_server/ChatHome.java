package com.example.chat_server;


import com.example.chat_server.entity.ChatMessage;
import com.example.chat_server.dto.ChatMessageView;
import com.example.chat_server.entity.ChatroomList;
import com.example.chat_server.entity.Member;
import com.example.chat_server.repository.ChatMessageRepository;
import com.example.chat_server.repository.ChatroomRepository;
import com.example.chat_server.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.chat_server.redis.ChatPresenceManager;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


@Controller
public class ChatHome {
    private final ChatroomRepository chatRoomRepository;
    private final ChatPresenceManager chatPresenceManager;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;


    public ChatHome(ChatroomRepository chatRoomRepository,ChatPresenceManager chatPresenceManager
    ,SimpMessagingTemplate messagingTemplate,MemberRepository memberRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatPresenceManager = chatPresenceManager;
        this.messagingTemplate = messagingTemplate;
        this.memberRepository = memberRepository;
    }
     @Autowired
    private ChatMessageRepository chatMessageRepository;

/*
    @GetMapping("/chatHome")
    public String chatroom_list(Model model, HttpSession session) {

        List<Chatroom_list> chatroomListVar = chatRoomRepository.findAll();
        model.addAttribute("chatroomListVar", chatroomListVar);
        return "chatHome";
    }*/

/*
    @GetMapping("/makeChatroom")
    public String MakeChatroom() {
        return "MakeChatroom";
    }
*/
    @GetMapping("/ChatHome")
    public String enterRoom(Long id, Model model,HttpSession session) {
        chatPresenceManager.printCache();
        System.out.println("1");


        List<ChatroomList> chatroomListVar = chatRoomRepository.findAll();
        model.addAttribute("chatroomListVar", chatroomListVar);
        if (id == null) {
            return "ChatHome";
        }

        ChatroomList roomVar = chatRoomRepository.findById(id).get();

        if (roomVar == null) {
            return "redirect:/ChatHome?roomNotFound";
        }
        model.addAttribute("roomVar", roomVar);




        //메세지 나만 위치 오른쪽
        Member loginUser = (Member) session.getAttribute("loginSession");
        model.addAttribute("loginUser", loginUser);
//ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
 //전에있던 룸id와 현재입장한 룸id를 비교하고 아니면 캐시에서제거후 데이터이동

        Long previousRoomId = (Long) session.getAttribute("roomId");

        chatPresenceManager.printCache();
        System.out.println("2");

        if (previousRoomId != null && !previousRoomId.equals(id)) {

            chatPresenceManager.leaveRoom(previousRoomId,loginUser.getId());

            Long previousCount =chatPresenceManager.getUserCount(previousRoomId);

            //  인원수 변경 알려줌
            messagingTemplate.convertAndSend("/topic/room/" + previousRoomId + "/count",previousCount
            );
            System.out.println("방 이동 인원 변경 실행");
            chatPresenceManager.printCache();
            System.out.println("3");
        }


// 새로운 방 저장
        session.setAttribute("roomId", id);


 //새로운 방 입장
        chatPresenceManager.enterRoom(id,loginUser.getId());
        chatPresenceManager.printCache();
        System.out.println("4");
        Long count = chatPresenceManager.getUserCount(id);

        messagingTemplate.convertAndSend(
                "/topic/room/" + id + "/count",
                count
        );

        //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
        session.setAttribute("roomId", id);



        //레디스 보낼 값 ,  현재인원표시
        Long userCount =chatPresenceManager.getUserCount(id);
        model.addAttribute("userCount", userCount);



        messagingTemplate.convertAndSend("/topic/room/" + id + "/count",count);



        //  채팅내역 조회
        List<ChatMessage> messageList =chatMessageRepository.findByRoomOrderBySendTimeAsc(roomVar);

        List<ChatMessageView> messageViewList =new ArrayList<>(); //List로해야 메세지를 for문으로 무한대를 jsp로보낼수있음
        LocalDate previousDate = null;
        DateTimeFormatter dateFormatter =DateTimeFormatter.ofPattern("yyyy년 M월 d일");


        for (ChatMessage message : messageList) {
            LocalDate currentDate =message.getSendTime().toLocalDate();
            boolean showDate =previousDate == null|| !previousDate.equals(currentDate);//이전메세지없음or이전날짜 현재날짜 비교 equals때문에 false지만 !만나서 true
            String dateText =currentDate.format(dateFormatter);
            String timeText =message.getSendTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            ChatMessageView view =new ChatMessageView(message,showDate,dateText,timeText);
            System.out.println("showDate="+showDate);
            messageViewList.add(view);//윗줄 messageList 하나만 보내기 에서 3개  보내기로 바꿈
            previousDate = currentDate; //현재날짜를 이전날짜로 대입해서 계속 메세지보낼때마다 showDate에서 비교함
        }
        model.addAttribute("messageViewList", messageViewList);


        return "ChatHome";
    }

    @GetMapping("/chat/room/users")
    @ResponseBody
    public List<String> getRoomUsers(Long roomId) {
        Set<String> redisUserIds = chatPresenceManager.getUsers(roomId);

        List<Long> membersId = new ArrayList<>();

        // String PK 를 Long PK로
        Iterator<String> iterator = redisUserIds.iterator();
        while (iterator.hasNext()) {
            String redisUserId = iterator.next();
            Long memberId = Long.valueOf(redisUserId);
            membersId.add(memberId);
        }

        List<Member> members = memberRepository.findAllById(membersId);



        return members.stream().map(Member::getNickName).toList();
    }


}





