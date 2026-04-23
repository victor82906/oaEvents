package com.vmr.oaevents.controller;

import com.vmr.oaevents.model.Empresa;
import com.vmr.oaevents.model.dto.empresa.EmpresaInputDto;
import com.vmr.oaevents.model.dto.empresa.EmpresaOutputDto;
import com.vmr.oaevents.model.mapper.EmpresaMapper;
import com.vmr.oaevents.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/empresa")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService service;
    private final EmpresaMapper mapper;

    @GetMapping
    public ResponseEntity<List<EmpresaOutputDto>> findAll() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaOutputDto> findById(@PathVariable Long id) {
        Empresa entity = service.findById(id);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @PostMapping
    public ResponseEntity<EmpresaOutputDto> create(@Valid @RequestBody EmpresaInputDto inputDto) {
        if (Objects.isNull(inputDto.getContrasena())){
            throw new IllegalArgumentException("Debe indicar su contraseña");
        }
        Empresa entity = mapper.toEntity(inputDto);
        entity = service.save(entity);
        return new ResponseEntity<>(mapper.toDto(entity), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaOutputDto> update(@PathVariable Long id, @Valid @RequestBody EmpresaInputDto inputDto) {
        Empresa entity = mapper.toEntity(inputDto);
        entity = service.update(id, entity);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
