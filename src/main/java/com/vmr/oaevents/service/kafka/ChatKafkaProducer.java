package com.vmr.oaevents.service.kafka;

import com.vmr.oaevents.model.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatKafkaProducer {

    private final KafkaTemplate<String, Chat> kafkaTemplate;

    public void enviarMensajeAKafka(Chat mensaje) {
        // Enviamos el DTO al tópico "chat-topic"
        kafkaTemplate.send("chat-topic", mensaje);
    }
}
