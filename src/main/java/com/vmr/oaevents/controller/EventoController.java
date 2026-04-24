package com.vmr.oaevents.controller;

import com.vmr.oaevents.model.Evento;
import com.vmr.oaevents.model.dto.evento.EventoInputDto;
import com.vmr.oaevents.model.dto.evento.EventoOutputDto;
import com.vmr.oaevents.model.mapper.EventoMapper;
import com.vmr.oaevents.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/evento")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService service;
    private final EventoMapper mapper;

    @GetMapping
    public ResponseEntity<List<EventoOutputDto>> findAll() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/page")
    public ResponseEntity<Page<EventoOutputDto>> findAllPaged(Pageable pageable) {
        Page<Evento> page = service.findAll(pageable);
        return ResponseEntity.ok(page.map(mapper::toDto));
    }

    @GetMapping("/aceptados/page")
    public ResponseEntity<Page<EventoOutputDto>> findAllAceptados(Pageable pageable) {
        Page<Evento> page = service.findAllByAceptado(true, pageable);
        return ResponseEntity.ok(page.map(mapper::toDto));
    }

    @GetMapping("/pendientes/page")
    public ResponseEntity<Page<EventoOutputDto>> findAllPendientes(Pageable pageable) {
        Page<Evento> page = service.findAllByAceptado(false, pageable);
        return ResponseEntity.ok(page.map(mapper::toDto));
    }

    @GetMapping("/empresa/{empresaId}/page")
    public ResponseEntity<Page<EventoOutputDto>> findByEmpresaId(@PathVariable Long empresaId, Pageable pageable) {
        Page<Evento> page = service.findByEmpresaId(empresaId, pageable);
        return ResponseEntity.ok(page.map(mapper::toDto));
    }

    @GetMapping("/empresa/{empresaId}/aceptados/page")
    public ResponseEntity<Page<EventoOutputDto>> findAceptadosByEmpresaId(@PathVariable Long empresaId, Pageable pageable) {
        Page<Evento> page = service.findByEmpresaIdAndAceptado(empresaId, true, pageable);
        return ResponseEntity.ok(page.map(mapper::toDto));
    }

    @GetMapping("/empresa/{empresaId}/pendientes/page")
    public ResponseEntity<Page<EventoOutputDto>> findPendientesByEmpresaId(@PathVariable Long empresaId, Pageable pageable) {
        Page<Evento> page = service.findByEmpresaIdAndAceptado(empresaId, false, pageable);
        return ResponseEntity.ok(page.map(mapper::toDto));
    }

    @GetMapping("/buscar/titulo/page")
    public ResponseEntity<Page<EventoOutputDto>> findByTitulo(
            @RequestParam String titulo, 
            Pageable pageable) {
        Page<Evento> page = service.findByTitulo(titulo, pageable);
        return ResponseEntity.ok(page.map(mapper::toDto));
    }

    @GetMapping("/buscar/fechas/page")
    public ResponseEntity<Page<EventoOutputDto>> findByFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Pageable pageable) {
        Page<Evento> page = service.findByRangoFechas(fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(page.map(mapper::toDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoOutputDto> findById(@PathVariable Long id) {
        Evento entity = service.findById(id);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @PostMapping
    public ResponseEntity<EventoOutputDto> create(@Valid @RequestBody EventoInputDto inputDto) {
        Evento entity = mapper.toEntity(inputDto);
        entity = service.save(entity);
        return new ResponseEntity<>(mapper.toDto(entity), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoOutputDto> update(@PathVariable Long id, @Valid @RequestBody EventoInputDto inputDto) {
        Evento entity = mapper.toEntity(inputDto);
        entity = service.update(id, entity);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
