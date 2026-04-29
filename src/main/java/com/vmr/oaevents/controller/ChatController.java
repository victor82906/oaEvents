package com.vmr.oaevents.controller;

import com.vmr.oaevents.model.Chat;
import com.vmr.oaevents.model.dto.chat.ChatInputDto;
import com.vmr.oaevents.model.dto.chat.ChatOutputDto;
import com.vmr.oaevents.model.mapper.ChatMapper;
import com.vmr.oaevents.service.ChatService;
import com.vmr.oaevents.service.kafka.ChatKafkaProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService service;
    private final ChatMapper mapper;
    private final ChatKafkaProducer kafkaProducer;

    @GetMapping
    @PreAuthorize("hasRole('RECINTO')")
    public ResponseEntity<List<ChatOutputDto>> findAll() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/conversacion/{emisorId}/{receptorId}")
    @PreAuthorize("principal.id == #emisorId or principal.id == #receptorId")
    public ResponseEntity<Page<ChatOutputDto>> findConversation(
            @PathVariable Long emisorId,
            @PathVariable Long receptorId,
            Pageable pageable) {
        Page<Chat> page = service.findConversation(emisorId, receptorId, pageable);
        return ResponseEntity.ok(page.map(mapper::toDto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@chatService.isEmisor(#id, principal.id)")
    public ResponseEntity<ChatOutputDto> findById(@PathVariable Long id) {
        Chat entity = service.findById(id);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> create(@Valid @RequestBody ChatInputDto inputDto) {
        Chat entity = mapper.toEntity(inputDto);
        kafkaProducer.enviarMensajeAKafka(entity);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("@chatService.isEmisor(#id, principal.id)")
    public ResponseEntity<ChatOutputDto> update(@PathVariable Long id, @Valid @RequestBody ChatInputDto inputDto) {
        Chat entity = mapper.toEntity(inputDto);
        entity = service.update(id, entity);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@chatService.isEmisor(#id, principal.id)")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
