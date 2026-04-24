package com.vmr.oaevents.service;

import com.vmr.oaevents.model.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface EventoService {
    List<Evento> findAll();
    Page<Evento> findAll(Pageable pageable);
    Page<Evento> findAllByAceptado(boolean aceptado, Pageable pageable);
    Page<Evento> findByEmpresaId(Long empresaId, Pageable pageable);
    Page<Evento> findByEmpresaIdAndAceptado(Long empresaId, boolean aceptado, Pageable pageable);
    Page<Evento> findByTitulo(String titulo, Pageable pageable);
    Page<Evento> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);
    Evento findById(Long id);
    Evento save(Evento entity);
    Evento update(Long id, Evento entity);
    void deleteById(Long id);
}
