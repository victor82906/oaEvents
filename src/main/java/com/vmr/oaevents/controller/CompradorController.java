package com.vmr.oaevents.controller;

import com.vmr.oaevents.model.Comprador;
import com.vmr.oaevents.model.dto.comprador.CompradorInputDto;
import com.vmr.oaevents.model.dto.comprador.CompradorOutputDto;
import com.vmr.oaevents.model.mapper.CompradorMapper;
import com.vmr.oaevents.service.CompradorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comprador")
@RequiredArgsConstructor
public class CompradorController {

    private final CompradorService service;
    private final CompradorMapper mapper;

    @GetMapping
    public ResponseEntity<List<CompradorOutputDto>> findAll() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompradorOutputDto> findById(@PathVariable Long id) {
        Comprador entity = service.findById(id);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @PostMapping
    public ResponseEntity<CompradorOutputDto> create(@Valid @RequestBody CompradorInputDto inputDto) {
        if (Objects.isNull(inputDto.getContrasena())){
            throw new IllegalArgumentException("Debe indicar su contraseña");
        }
        Comprador entity = mapper.toEntity(inputDto);
        entity = service.save(entity);
        return new ResponseEntity<>(mapper.toDto(entity), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompradorOutputDto> update(@PathVariable Long id, @Valid @RequestBody CompradorInputDto inputDto) {
        Comprador entity = mapper.toEntity(inputDto);
        entity = service.update(id, entity);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
