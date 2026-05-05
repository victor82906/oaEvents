package com.vmr.oaevents.service.impl;

import com.vmr.oaevents.model.Empresa;
import com.vmr.oaevents.model.Evento;
import com.vmr.oaevents.model.TipoEvento;
import com.vmr.oaevents.model.ZonaEvento;
import com.vmr.oaevents.repository.EventoRepository;
import com.vmr.oaevents.repository.ZonaEventoRepository;
import com.vmr.oaevents.service.EmpresaService;
import com.vmr.oaevents.service.EventoService;
import com.vmr.oaevents.service.TipoEventoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements EventoService {

    private final EventoRepository repository;
    private final TipoEventoService tipoEventoService;
    private final EmpresaService empresaService;
        private final ZonaEventoRepository zonaEventoRepository;
    private final ImagenService imagenService;

    @Override
    public List<Evento> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<Evento> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Evento> findAllByAceptado(boolean aceptado, Pageable pageable) {
        return repository.findByAceptado(aceptado, pageable);
    }

    @Override
    public Page<Evento> findByEmpresaId(Long empresaId, Pageable pageable) {
        empresaService.findById(empresaId);
        return repository.findByEmpresaId(empresaId, pageable);
    }

    @Override
    public Page<Evento> findByEmpresaIdAndAceptado(Long empresaId, boolean aceptado, Pageable pageable) {
        empresaService.findById(empresaId);
        return repository.findByEmpresaIdAndAceptado(empresaId, aceptado, pageable);
    }

    @Override
    public Page<Evento> findByTitulo(String titulo, Pageable pageable) {
        return repository.findByTituloContainingIgnoreCase(titulo, pageable);
    }

    @Override
    public Page<Evento> findByEmpresaIdAndTitulo(Long empresaId, String titulo, Pageable pageable) {
        empresaService.findById(empresaId);
        return repository.findByEmpresaIdAndTituloContainingIgnoreCase(empresaId, titulo, pageable);
    }

    @Override
    public Page<Evento> findByCompradorIdAndTitulo(Long compradorId, String titulo, Pageable pageable) {
        return repository.findEventosByCompradorIdAndTitulo(compradorId, titulo, pageable);
    }

    @Override
    public Page<Evento> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        LocalDateTime start = fechaInicio.atStartOfDay();
        LocalDateTime end = fechaFin.atTime(LocalTime.MAX);
        
        return repository.findByFechaBetween(start, end, pageable);
    }

    @Override
    public Page<Evento> findAceptadosByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        LocalDateTime start = fechaInicio.atStartOfDay();
        LocalDateTime end = fechaFin.atTime(LocalTime.MAX);
        
        return repository.findByAceptadoAndFechaBetween(true, start, end, pageable);
    }

    @Override
    public Evento findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado con id: " + id));
    }

    @Override
    public Evento save(Evento entity) {
        TipoEvento tipoEvento = tipoEventoService.findById(entity.getTipoEvento().getId());
        Empresa empresa = empresaService.findById(entity.getEmpresa().getId());
        entity.setTipoEvento(tipoEvento);
        entity.setEmpresa(empresa);
        entity.setAceptado(false);
        return repository.save(entity);
    }

    @Override
    public Evento update(Long id, Evento entity) {
        Evento evento = this.findById(id);
        entity.setId(id);
        entity.setEmpresa(evento.getEmpresa());
        entity.setAceptado(evento.isAceptado());
        entity.setFoto(evento.getFoto());
        TipoEvento tipoEvento = tipoEventoService.findById(entity.getTipoEvento().getId());
        entity.setTipoEvento(tipoEvento);
        return repository.save(entity);
    }

    @Override
    public Evento accept(Long id) {
        Evento evento = this.findById(id);
        evento.setAceptado(true);
        return repository.save(evento);
    }

    @Override
    public Evento cancel(Long id) {
        Evento evento = this.findById(id);
        evento.setAceptado(false);
        return repository.save(evento);
    }

    @Override
    public Evento addFoto(Long id, MultipartFile archivo) {
        Evento evento = this.findById(id);
        
        if (evento.getFoto() != null && !evento.getFoto().isEmpty()) {
            imagenService.eliminarImagen(evento.getFoto());
        }

        String ruta = imagenService.guardarImagen(archivo);
        evento.setFoto(ruta);

        return repository.save(evento);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Evento evento = this.findById(id);
        
        if (!Objects.isNull(evento.getFoto()) && !evento.getFoto().isEmpty()) {
            imagenService.eliminarImagen(evento.getFoto());
        }
        
        evento.getZonasEvento().stream()
                .map(ZonaEvento::getId)
                .forEach(this.zonaEventoRepository::deleteById);
        repository.delete(evento);
    }

    @Override
    public boolean isPropietario(Long eventoId, Long empresaId) {
        Evento evento = this.findById(eventoId);
        return evento.getEmpresa() != null && evento.getEmpresa().getId().equals(empresaId);
    }
}
