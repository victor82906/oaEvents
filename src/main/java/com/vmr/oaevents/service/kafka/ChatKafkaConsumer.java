package com.vmr.oaevents.service.kafka;

import com.vmr.oaevents.model.Chat;
import com.vmr.oaevents.model.dto.chat.ChatInputDto;
import com.vmr.oaevents.model.dto.chat.ChatOutputDto;
import com.vmr.oaevents.model.mapper.ChatMapper;
import com.vmr.oaevents.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatKafkaConsumer {

    private final ChatService chatService;
    private final ChatMapper mapper;
    private final SimpMessagingTemplate messagingTemplate; // Herramienta de WebSockets

    @KafkaListener(topics = "chat-topic", groupId = "chat-group")
    public void procesarMensaje(ChatInputDto inputDto) {
        Chat mensaje = mapper.toEntity(inputDto);
        mensaje = chatService.save(mensaje);

        ChatOutputDto outputDto = mapper.toDto(mensaje);

        // 3. Enviamos el mensaje EN TIEMPO REAL al receptor mediante WebSocket
        // El receptor debe estar suscrito en el frontend a: /user/{su_id}/queue/mensajes
        messagingTemplate.convertAndSendToUser(
                String.valueOf(outputDto.getReceptor_id()),
                "/queue/mensajes",
                outputDto
        );
    }
}