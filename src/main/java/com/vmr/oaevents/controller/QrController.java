package com.vmr.oaevents.controller;

import com.vmr.oaevents.model.Qr;
import com.vmr.oaevents.model.dto.qr.CheckQrDto;
import com.vmr.oaevents.model.dto.qr.QrInputDto;
import com.vmr.oaevents.model.dto.qr.QrOutputDto;
import com.vmr.oaevents.model.mapper.QrMapper;
import com.vmr.oaevents.service.QrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/qr")
@RequiredArgsConstructor
public class QrController {

    private final QrService service;
    private final QrMapper mapper;

    @GetMapping
    public ResponseEntity<List<QrOutputDto>> findAll() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<QrOutputDto> findById(@PathVariable Long id) {
        Qr entity = service.findById(id);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @PostMapping
    public ResponseEntity<QrOutputDto> create(@Valid @RequestBody QrInputDto inputDto) {
        Qr entity = mapper.toEntity(inputDto);
        entity = service.save(entity);
        return new ResponseEntity<>(mapper.toDto(entity), HttpStatus.CREATED);
    }

    @PostMapping("/check")
    public ResponseEntity<Map<String, String>> checkQr(@Valid @RequestBody CheckQrDto checkQrDto) {
        service.checkQr(checkQrDto);
        return ResponseEntity.ok(Map.of("mensaje", "El QR es válido y ha sido registrado con éxito."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QrOutputDto> update(@PathVariable Long id, @Valid @RequestBody QrInputDto inputDto) {
        Qr entity = mapper.toEntity(inputDto);
        entity = service.update(id, entity);
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
