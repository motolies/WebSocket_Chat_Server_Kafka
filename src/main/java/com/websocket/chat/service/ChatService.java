package com.websocket.chat.service;

import com.websocket.chat.model.ChatMessage;
import com.websocket.chat.repo.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChannelTopic channelTopic;
    //    private final RedisTemplate redisTemplate;
    private final ChatRoomRepository chatRoomRepository;

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    /**
     * destination정보에서 roomId 추출
     */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessage chatMessage) {
        chatMessage.setUserCount(chatRoomRepository.getUserCount(chatMessage.getRoomId()));
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
            chatMessage.setSender("[알림]");
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
            chatMessage.setSender("[알림]");
        }
//        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);

        log.info("@@@@@@@@@@ 발행함 @@@@@@@@@ {}", chatMessage.toString());
        kafkaTemplate.send("CHAT_ROOM", chatMessage);
    }

}
