package com.vmr.oaevents.service.impl;

import com.vmr.oaevents.model.Chat;
import com.vmr.oaevents.model.Usuario;
import com.vmr.oaevents.repository.ChatRepository;
import com.vmr.oaevents.security.AuthenticationFacade;
import com.vmr.oaevents.service.ChatService;
import com.vmr.oaevents.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository repository;
    private final UsuarioService usuarioService;
    private final AuthenticationFacade authenticationFacade;

    @Override
    public List<Chat> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<Chat> findConversation(Long emisorId, Long receptorId, Pageable pageable) {
        usuarioService.findById(emisorId);
        usuarioService.findById(receptorId);
        return repository.findConversation(emisorId, receptorId, pageable);
    }

    @Override
    public Chat findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chat no encontrado con id: " + id));
    }

    @Override
    public Chat save(Chat entity) {
        Usuario receptor = usuarioService.findById(entity.getReceptor().getId());
        entity.setReceptor(receptor);
        entity.setEmisor(authenticationFacade.getAuthenticatedUsuario());
        entity.setFecha(LocalDateTime.now());
        return repository.save(entity);
    }

    @Override
    public Chat update(Long id, Chat entity) {
        Chat chat = this.findById(id);
        entity.setId(id);
        entity.setEmisor(chat.getEmisor());
        entity.setReceptor(chat.getReceptor());
        entity.setFecha(chat.getFecha());
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        this.findById(id);
        repository.delete(this.findById(id));
    }

    @Override
    public boolean isEmisor(Long chatId, Long usuarioId) {
        Chat chat = this.findById(chatId);
        return chat.getEmisor().getId().equals(usuarioId);
    }
}
