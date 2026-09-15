package com.example.chat_server.redis;



import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatPresenceManager {

    /*
     * Key   = roomId
     * Value = 해당 방에 들어가 있는 memberId Set
     *
     * 예)
     * 1 -> {"10", "20", "30"}
     * 2 -> {"15", "25"}
     */
    private final Cache<Long, Set<String>> cache;

    public ChatPresenceManager() {
        this.cache = Caffeine.newBuilder()
                .build();
    }


    // 방 입장
    public void enterRoom(Long roomId, Long memberId) {

        Set<String> users = cache.get(
                roomId,key -> ConcurrentHashMap.newKeySet()
        );

        users.add(memberId.toString());
    }


    // 방 퇴장
    public void leaveRoom(Long roomId, Long memberId) {

        Set<String> users = cache.getIfPresent(roomId);

        if (users == null) {
            return;
        }

        users.remove(memberId.toString());

        // 방에 아무도 없으면 roomId 자체도 Cache에서 제거
        if (users.isEmpty()) {
            cache.invalidate(roomId);
        }
    }


    // 현재 참여 인원 수
    public Long getUserCount(Long roomId) {

        Set<String> users = cache.getIfPresent(roomId);

        if (users == null) {
            return 0L;
        }

        return (long) users.size();
    }


    // 현재 참여자 PK 목록
    public Set<String> getUsers(Long roomId) {

        Set<String> users = cache.getIfPresent(roomId);

        if (users == null) {
            return Set.of();
        }

        return Set.copyOf(users);
    }
    public void printCache() {
        System.out.println("Caffeine 전체 데이터 = " + cache.asMap());
    }


}