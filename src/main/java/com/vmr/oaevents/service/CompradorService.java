package com.vmr.oaevents.service;

import com.vmr.oaevents.model.Comprador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompradorService {
    List<Comprador> findAll();
    Page<Comprador> findAll(Pageable pageable);
    Page<Comprador> buscar(String termino, Pageable pageable);
    Comprador findById(Long id);
    Comprador save(Comprador entity);
    Comprador update(Long id, Comprador entity);
    void deleteById(Long id);
    boolean existByDni(String dni);
    boolean existByDni(String dni, Long id);
}
