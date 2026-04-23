package com.vmr.oaevents.controller;

import com.vmr.oaevents.model.Recinto;
import com.vmr.oaevents.model.dto.recinto.RecintoInputDto;
import com.vmr.oaevents.model.dto.recinto.RecintoOutputDto;
import com.vmr.oaevents.model.mapper.RecintoMapper;
import com.vmr.oaevents.service.RecintoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recinto")
@RequiredArgsConstructor
public class RecintoController {

    private final RecintoService service;
    private final RecintoMapper mapper;

    @GetMapping
    public ResponseEntity<List<RecintoOutputDto>> findAll() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecintoOutputDto> findById(@PathVariable Long id) {
        Recinto entity = service.findById(id);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @PostMapping
    public ResponseEntity<RecintoOutputDto> create(@Valid @RequestBody RecintoInputDto inputDto) {
        if (Objects.isNull(inputDto.getContrasena())){
            throw new IllegalArgumentException("Debe indicar su contraseña");
        }
        Recinto entity = mapper.toEntity(inputDto);
        entity = service.save(entity);
        return new ResponseEntity<>(mapper.toDto(entity), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecintoOutputDto> update(@PathVariable Long id, @Valid @RequestBody RecintoInputDto inputDto) {
        Recinto entity = mapper.toEntity(inputDto);
        entity = service.update(id, entity);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
