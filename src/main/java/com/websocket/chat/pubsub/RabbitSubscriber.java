package com.websocket.chat.pubsub;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.websocket.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    public void receiveMessage(ChatMessage message) {
        try {
            log.info("@@@@@ 수신 양호 @@@@ {}", message.toString());
            // 채팅방을 구독한 클라이언트에게 메시지 발송
            messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }

}