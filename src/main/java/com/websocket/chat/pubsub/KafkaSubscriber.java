package com.websocket.chat.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.websocket.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaSubscriber {

    @Autowired
    private SimpMessagingTemplate template;

    @KafkaListener(id = "main-listener", topics = "CHAT_ROOM")
    public void receive(ChatMessage message) throws Exception {
        log.info("message='{}'", message.toString());
        this.template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
}
