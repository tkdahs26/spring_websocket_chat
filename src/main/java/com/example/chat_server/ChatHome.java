package com.example.chat_server;


import com.example.chat_server.entity.ChatMessage;
import com.example.chat_server.dto.ChatMessageView;
import com.example.chat_server.entity.ChatroomList;
import com.example.chat_server.entity.Member;
import com.example.chat_server.repository.ChatMessageRepository;
import com.example.chat_server.repository.ChatroomRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.chat_server.ChatPresenceManager;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Controller
public class ChatHome {
    private final ChatroomRepository chatRoomRepository;
    private final ChatPresenceManager chatPresenceManager;
    private final SimpMessagingTemplate messagingTemplate;
    public ChatHome(ChatroomRepository chatRoomRepository,ChatPresenceManager chatPresenceManager
    ,SimpMessagingTemplate messagingTemplate) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatPresenceManager = chatPresenceManager;
        this.messagingTemplate = messagingTemplate;
    }
     @Autowired
    private ChatMessageRepository chatMessageRepository;


    @GetMapping("/ChatHome")
    public String enterRoom(Long id, Model model,HttpSession session) {


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
        //현재 roomVar안에는 입장한 방정보  id room_name owner 가 들어잇음
        //${roomVar.room_name}하면 방제목  ${roomVar.owner.nickname} 방만든닉네임





        //메세지 나만 위치 오른쪽
        Member loginUser = (Member) session.getAttribute("loginSession");
        model.addAttribute("loginUser", loginUser);


        //현재인원표시
        chatPresenceManager.enterRoom(id, loginUser.getId());
        Long userCount =chatPresenceManager.getUserCount(id);
        model.addAttribute("userCount", userCount);

        Long count =chatPresenceManager.getUserCount(id);

        messagingTemplate.convertAndSend(
                "/topic/room/" + id + "/count",
                count
        );



        //  채팅내역 조회
        List<ChatMessage> messageList =chatMessageRepository.findByRoomOrderBySendTimeAsc(roomVar);
        /*model.addAttribute("messageList", messageList);
        날짜구분선 jsp이동하는 윗줄 삭제
        */
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

/*  웹소켓으로 바꾸면서 postmapping안씀 @MessageMapping으로 옮김
         roomVar_id을 저장할수도있지만 Chatroom_list객체로 저장하는이유: manytoone 으로 long이아닌 사용자정의자료형으로
        저장하면 나중에  chatRoomRepository.findById(message.getRoomId()) 로 select함수안하고 바로 message.get_room().getId();
        쓸수있음

    return "chatHome";="JSP(chatHome.jsp)를 읽어라"
return "redirect:/chatHome?roomId=" + roomVar_id;
이면 스프링은
"JSP를 보여주지 말고, 브라우저에게 /chatHome?roomId=3으로 다시 가라.
 public String enterRoom(Long id, Model model) {}입장 함수를 자동으로 다시호출해서 채팅 보낸 내역을 바로 받아 볼수있음
"
*/


    @GetMapping("/chat/room/users")
    @ResponseBody
    public Set<String> getRoomUsers(Long roomId) {

        return chatPresenceManager.getUsers(roomId);
    }


}





