package com.vmr.oaevents.controller;

import com.vmr.oaevents.model.Validador;
import com.vmr.oaevents.model.dto.validador.ValidadorInputDto;
import com.vmr.oaevents.model.dto.validador.ValidadorOutputDto;
import com.vmr.oaevents.model.mapper.ValidadorMapper;
import com.vmr.oaevents.service.ValidadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/validador")
@RequiredArgsConstructor
public class ValidadorController {

    private final ValidadorService service;
    private final ValidadorMapper mapper;

    @GetMapping
    public ResponseEntity<List<ValidadorOutputDto>> findAll() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ValidadorOutputDto> findById(@PathVariable Long id) {
        Validador entity = service.findById(id);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @PostMapping
    public ResponseEntity<ValidadorOutputDto> create(@Valid @RequestBody ValidadorInputDto inputDto) {
        if (Objects.isNull(inputDto.getContrasena())){
            throw new IllegalArgumentException("Debe indicar su contraseña");
        }
        Validador entity = mapper.toEntity(inputDto);
        entity = service.save(entity);
        return new ResponseEntity<>(mapper.toDto(entity), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ValidadorOutputDto> update(@PathVariable Long id, @Valid @RequestBody ValidadorInputDto inputDto) {
        Validador entity = mapper.toEntity(inputDto);
        entity = service.update(id, entity);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
