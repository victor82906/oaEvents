package com.vmr.oaevents.service.impl;

import com.vmr.oaevents.model.Empresa;
import com.vmr.oaevents.repository.EmpresaRepository;
import com.vmr.oaevents.service.EmpresaService;
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
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository repository;
    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Empresa> findAll() {
        return repository.findAll();
    }

    @Override
    public Empresa findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con id: " + id));
    }

    @Override
    public Empresa save(Empresa entity) {
        if (usuarioService.existByEmail(entity.getEmail())){
            throw new EntityExistsException("Email: " + entity.getEmail() + ", ya existente en la base de datos");
        } else if(this.existByCif(entity.getCif())){
            throw new EntityExistsException("Cif: " + entity.getCif() + ", ya existente en la base de datos");
        }
        entity.setRol(rolService.findByNombre("EMPRESA"));
        entity.setContrasena(passwordEncoder.encode(entity.getContrasena()));
        return repository.save(entity);
    }

    @Override
    public Empresa update(Long id, Empresa entity) {
        Empresa empresa = this.findById(id);
        entity.setId(id);
        if (usuarioService.existByEmail(entity.getEmail())){
            throw new EntityNotFoundException("Email: " + entity.getEmail() + ", ya existente en la base de datos");
        } else if(this.existByCif(entity.getCif())){
            throw new EntityNotFoundException("Cif: " + entity.getCif() + ", ya existente en la base de datos");
        }
        entity.setRol(rolService.findByNombre("EMPRESA"));
        entity.setContrasena(empresa.getContrasena());
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.delete(this.findById(id));
    }

    @Override
    public boolean existByCif(String cif){
        return repository.existByCif(cif);
    }

}
