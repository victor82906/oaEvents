package com.vmr.oaevents.service.impl;

import com.vmr.oaevents.model.Validador;
import com.vmr.oaevents.repository.ValidadorRepository;
import com.vmr.oaevents.service.RolService;
import com.vmr.oaevents.service.UsuarioService;
import com.vmr.oaevents.service.ValidadorService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidadorServiceImpl implements ValidadorService {

    private final ValidadorRepository repository;
    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Validador> findAll() {
        return repository.findAll();
    }

    @Override
    public Validador findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Validador no encontrado con id: " + id));
    }

    @Override
    public Validador save(Validador entity) {
        if (usuarioService.existByEmail(entity.getEmail())){
            throw new EntityExistsException("Email: " + entity.getEmail() + ", ya existente en la base de datos");
        } else if (this.existByDni(entity.getDni())){
            throw new EntityExistsException("Dni: " + entity.getDni() + ", ya existente en la base de datos");
        }
        entity.setRol(rolService.findByNombre("VALIDADOR"));
        entity.setContrasena(passwordEncoder.encode(entity.getContrasena()));
        return repository.save(entity);
    }

    @Override
    public Validador update(Long id, Validador entity) {
        Validador validador = this.findById(id);
        entity.setId(id);
        if (usuarioService.existByEmail(entity.getEmail())){
            throw new EntityNotFoundException("Email: " + entity.getEmail() + ", ya existente en la base de datos");
        } else if (this.existByDni(entity.getDni())){
            throw new EntityNotFoundException("Dni: " + entity.getDni() + ", ya existente en la base de datos");
        }
        entity.setRol(rolService.findByNombre("VALIDADOR"));
        entity.setContrasena(validador.getContrasena());
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.delete(this.findById(id));
    }

    @Override
    public boolean existByDni(String dni){
        return repository.existByDni(dni);
    }

}
