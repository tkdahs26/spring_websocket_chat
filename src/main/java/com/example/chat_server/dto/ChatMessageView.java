package com.example.chat_server.dto;

import com.example.chat_server.entity.ChatMessage;

public class ChatMessageView {


    private ChatMessage message;
    private boolean showDate;
    private String dateText;
    private String timeText;

    public ChatMessageView(ChatMessage message, boolean showDate, String dateText,String timeText) {
        this.message = message;
        this.showDate = showDate;
        this.dateText = dateText;
        this.timeText = timeText;
    }
    /*
    1.List<>쓰려고할때 한 번에  인덱스1개당 원소1개 하나만 add()할 수 있기 때문에
     message, isShowDate, dateText 3개를 하나의 인덱스에 한 번에 저장하려고

   List<Object> list = new ArrayList<>();
    list.add(message);list.add(isShowDate);list.add(dateText);

   [0] ──message
   [1] ──true
   [2] ──"2026년 7월 26일"
   [3] ──message
    처럼 인덱스1개당 원소1개



message_view_list 클래스만들고 3개를 필드로 만들었을때
Chat_message_view view =new Chat_message_view(message, isShowDate, dateText);
message_view_list.add(view);
└── [0]
      ├── message
      ├── true
      └── "2026년 7월 26일"
├── [1]
      ├── message
      ├── true
      └── "2026년 7월 26일"
    처럼 인덱스1개당 원소여러개개



    */


    public ChatMessage getMessage() {
        return message;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public String getDateText() {
        return dateText;
    }

    public String getTimeText() {
        return timeText;
    }

}
