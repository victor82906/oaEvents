package com.vmr.oaevents.repository;

import com.vmr.oaevents.model.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    
    Page<Evento> findByEmpresaId(Long empresaId, Pageable pageable);
    
    Page<Evento> findByEmpresaIdAndAceptado(Long empresaId, boolean aceptado, Pageable pageable);
    
    Page<Evento> findByAceptado(boolean aceptado, Pageable pageable);

    Page<Evento> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    Page<Evento> findByFechaBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}
