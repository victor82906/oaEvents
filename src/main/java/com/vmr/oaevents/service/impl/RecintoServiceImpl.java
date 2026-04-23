package com.vmr.oaevents.service.impl;

import com.vmr.oaevents.model.Recinto;
import com.vmr.oaevents.repository.RecintoRepository;
import com.vmr.oaevents.service.RecintoService;
import com.vmr.oaevents.service.RolService;
import com.vmr.oaevents.service.UsuarioService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecintoServiceImpl implements RecintoService {

    private final RecintoRepository repository;
    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Recinto> findAll() {
        return repository.findAll();
    }

    @Override
    public Recinto findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recinto no encontrado con id: " + id));
    }

    @Override
    public Recinto save(Recinto entity) {
        if (usuarioService.existByEmail(entity.getEmail())){
            throw new EntityExistsException("Email: " + entity.getEmail() + ", ya existente en la base de datos");
        }
        entity.setRol(rolService.findByNombre("RECINTO"));
        entity.setContrasena(passwordEncoder.encode(entity.getContrasena()));
        return repository.save(entity);
    }

    @Override
    public Recinto update(Long id, Recinto entity) {
        Recinto recinto = this.findById(id);
        entity.setId(id);
        if (usuarioService.existByEmail(entity.getEmail())){
            throw new EntityNotFoundException("Email: " + entity.getEmail() + ", ya existente en la base de datos");
        }
        entity.setRol(rolService.findByNombre("RECINTO"));
        entity.setContrasena(recinto.getContrasena());
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.delete(this.findById(id));
    }
}
