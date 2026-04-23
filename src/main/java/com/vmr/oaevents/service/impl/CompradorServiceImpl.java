package com.vmr.oaevents.service.impl;

import com.vmr.oaevents.model.Comprador;
import com.vmr.oaevents.repository.CompradorRepository;
import com.vmr.oaevents.service.CompradorService;
import com.vmr.oaevents.service.RolService;
import com.vmr.oaevents.service.UsuarioService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompradorServiceImpl implements CompradorService {

    private final CompradorRepository repository;
    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Comprador> findAll() {
        return repository.findAll();
    }

    @Override
    public Comprador findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comprador no encontrado con id: " + id));
    }

    @Override
    public Comprador save(Comprador entity) {
        if (usuarioService.existByEmail(entity.getEmail())){
            throw new EntityExistsException("Email: " + entity.getEmail() + ", ya existente en la base de datos");
        } else if (this.existByDni(entity.getDni())){
            throw new EntityExistsException("Dni: " + entity.getDni() + ", ya existente en la base de datos");
        }
        entity.setRol(rolService.findByNombre("COMPRADOR"));
        entity.setContrasena(passwordEncoder.encode(entity.getContrasena()));
        return repository.save(entity);
    }

    @Override
    public Comprador update(Long id, Comprador entity) {
        Comprador comprador = this.findById(id);
        entity.setId(id);
        if (usuarioService.existByEmail(entity.getEmail())){
            throw new EntityNotFoundException("Email: " + entity.getEmail() + ", ya existente en la base de datos");
        } else if (this.existByDni(entity.getDni())){
            throw new EntityNotFoundException("Dni: " + entity.getDni() + ", ya existente en la base de datos");
        }
        entity.setRol(rolService.findByNombre("COMPRADOR"));
        entity.setContrasena(comprador.getContrasena());
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
