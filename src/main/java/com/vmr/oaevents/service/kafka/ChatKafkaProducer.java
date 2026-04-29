package com.vmr.oaevents.service.kafka;

import com.vmr.oaevents.model.dto.chat.ChatInputDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatKafkaProducer {

    private final KafkaTemplate<String, ChatInputDto> kafkaTemplate;

    public void enviarMensajeAKafka(ChatInputDto inputDto) {
        // Enviamos el DTO al tópico "chat-topic"
        kafkaTemplate.send("chat-topic", inputDto);
    }
}
