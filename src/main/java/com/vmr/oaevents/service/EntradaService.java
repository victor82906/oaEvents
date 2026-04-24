package com.vmr.oaevents.service;

import com.vmr.oaevents.model.Entrada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EntradaService {
    List<Entrada> findAll();
    Page<Entrada> findAll(Pageable pageable);
    Page<Entrada> findByCompradorId(Long compradorId, Pageable pageable);
    Entrada findById(Long id);
    Entrada save(Entrada entity);
    Entrada update(Long id, Entrada entity);
    void deleteById(Long id);
    byte[] descargarEntradaPdf(Long id);
}
