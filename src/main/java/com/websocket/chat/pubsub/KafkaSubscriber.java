package com.websocket.chat.pubsub;

import com.websocket.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaSubscriber {

    @Autowired
    private SimpMessagingTemplate template;

    @KafkaListener(topics = "chat-topic")
    public void receive(ChatMessage message) throws Exception {
        log.info("@@@@@@@@@@ 구독함 @@@@@@@@@ {}", message.toString());
        this.template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
}