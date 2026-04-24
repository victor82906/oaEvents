package com.vmr.oaevents.service.impl;

import com.vmr.oaevents.model.Empresa;
import com.vmr.oaevents.repository.EmpresaRepository;
import com.vmr.oaevents.service.EmpresaService;
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
    public Page<Empresa> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Empresa> findAllByActiva(boolean activa, Pageable pageable) {
        return repository.findByActiva(activa, pageable);
    }

    @Override
    public Page<Empresa> buscar(String termino, Pageable pageable) {
        return repository.searchByNombreOrEmailOrCif(termino, pageable);
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
        entity.setActiva(false);
        return repository.save(entity);
    }

    @Override
    public Empresa update(Long id, Empresa entity) {
        Empresa empresa = this.findById(id);
        entity.setId(id);
        if (usuarioService.existByEmail(entity.getEmail(), id)){
            throw new EntityExistsException("Email: " + entity.getEmail() + ", ya existente en la base de datos");
        } else if(this.existByCif(entity.getCif(), id)){
            throw new EntityExistsException("Cif: " + entity.getCif() + ", ya existente en la base de datos");
        }
        entity.setRol(rolService.findByNombre("EMPRESA"));
        entity.setContrasena(empresa.getContrasena());
        entity.setActiva(empresa.isActiva());
        return repository.save(entity);
    }

    @Override
    public Empresa activate(Long id) {
        Empresa empresa = this.findById(id);
        empresa.setActiva(true);
        return repository.save(empresa);
    }

    @Override
    public Empresa deactivate(Long id) {
        Empresa empresa = this.findById(id);
        empresa.setActiva(false);
        return repository.save(empresa);
    }

    @Override
    public void deleteById(Long id) {
        Empresa empresa = this.findById(id);
        // comprobar con y sin esto
        empresa.getEventos()
                .forEach(evento -> evento.setEmpresa(null));
        repository.delete(this.findById(id));
    }

    @Override
    public boolean existByCif(String cif){
        return repository.existByCif(cif);
    }

    @Override
    public boolean existByCif(String cif, Long id){
        return repository.existByCifAndIdNot(cif, id);
    }

}
