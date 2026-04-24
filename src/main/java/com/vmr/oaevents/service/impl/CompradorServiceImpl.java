package com.vmr.oaevents.service.impl;

import com.vmr.oaevents.model.Comprador;
import com.vmr.oaevents.repository.CompradorRepository;
import com.vmr.oaevents.service.CompradorService;
import com.vmr.oaevents.service.RolService;
import com.vmr.oaevents.service.UsuarioService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Comprador> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Comprador> buscar(String termino, Pageable pageable) {
        return repository.searchByNombreOrEmailOrDni(termino, pageable);
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
        if (usuarioService.existByEmail(entity.getEmail(), id)){
            throw new EntityExistsException("Email: " + entity.getEmail() + ", ya existente en la base de datos");
        } else if (this.existByDni(entity.getDni(), id)){
            throw new EntityExistsException("Dni: " + entity.getDni() + ", ya existente en la base de datos");
        }
        entity.setRol(rolService.findByNombre("COMPRADOR"));
        entity.setContrasena(comprador.getContrasena());
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        Comprador comprador = this.findById(id);
        // comprobar con y sin esto
        comprador.getEntradas()
                .forEach(entrada -> entrada.setComprador(null));
        repository.delete(this.findById(id));
    }

    @Override
    public boolean existByDni(String dni){
        return repository.existByDni(dni);
    }

    @Override
    public boolean existByDni(String dni, Long id) {
        return repository.existByDniAndIdNot(dni, id);
    }

}
