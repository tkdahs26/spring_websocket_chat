package com.example.chat_server;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ChatPresenceManager {
    private final StringRedisTemplate redisTemplate;

    public ChatPresenceManager(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void enterRoom(Long roomId, Long memberId) {
        String key = "chat:room:" + roomId + ":users";
        redisTemplate.opsForSet().add(key, memberId.toString());
    }



    public void leaveRoom(Long roomId, Long memberId) {
        String key = "chat:room:" + roomId + ":users";
        redisTemplate.opsForSet().remove(key, memberId.toString());
    }



    public Long getUserCount(Long roomId) {
        String key = "chat:room:" + roomId + ":users";
        return redisTemplate.opsForSet().size(key);
        //key사이즈가 입장사람수라 불러온것
    }

    public Set<String> getUsers(Long roomId) {
        String key = "chat:room:" + roomId + ":users";

        return redisTemplate.opsForSet().members(key);
    }

}



/* "chat:room:" + roomId + ":users";이건 그냥 map자료구조의 key값이라고 생각하면됨

* opsForValue() -밸류불러오기
* opsForSet()-  List<>같이 Set<>자료형쓰기
* List = 여러 값이 중복이여도 순서대로 저장 1,2,3,1,2,3 하면 123123
Set = 여러 값을 중복 없이 저장 1,2,3,1,2,3 하면 123만저장

* 레디스는 모든 데이터가 (key,value) map안써도 map구조임
*
  같이 opsFor은 기능을 정하는함수


     public Long enterRoom(Long roomId) {
        String key = "chat:room:" + roomId + ":users";
        return redisTemplate.opsForValue().increment(key);
    }

    public Long leaveRoom(Long roomId) {
        String key = "chat:room:" + roomId + ":users";
        return redisTemplate.opsForValue().decrement(key);
    }

    public int getUserCount(Long roomId) {
        String key = "chat:room:" + roomId + ":users";

        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return 0;
        }

        return Integer.parseInt(value);
    }
* */