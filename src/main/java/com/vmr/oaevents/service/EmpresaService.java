package com.vmr.oaevents.service;

import com.vmr.oaevents.model.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmpresaService {
    List<Empresa> findAll();
    Page<Empresa> findAll(Pageable pageable);
    Page<Empresa> findAllByActiva(boolean activa, Pageable pageable);
    Page<Empresa> buscar(String termino, Pageable pageable);
    Empresa findById(Long id);
    Empresa save(Empresa entity);
    Empresa update(Long id, Empresa entity);
    Empresa activate(Long id);
    Empresa deactivate(Long id);
    void deleteById(Long id);
    boolean existByCif(String cif);
    boolean existByCif(String cif, Long id);
}
