package com.vmr.oaevents.service.impl;

import com.vmr.oaevents.model.Qr;
import com.vmr.oaevents.model.dto.qr.CheckQrDto;
import com.vmr.oaevents.repository.QrRepository;
import com.vmr.oaevents.service.QrService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QrServiceImpl implements QrService {

    private final QrRepository repository;

    @Override
    public List<Qr> findAll() {
        return repository.findAll();
    }

    @Override
    public Qr findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Qr no encontrado con id: " + id));
    }

    @Override
    public Qr findByCodigo(String codigo) {
        return repository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Código QR no encontrado"));
    }

    @Override
    public Qr save(Qr entity) {
        return repository.save(entity);
    }

    @Override
    public Qr update(Long id, Qr entity) {
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        Qr qr = this.findById(id);
        qr.getEntrada().setQr(null);
        repository.delete(this.findById(id));
    }

    @Override
    @Transactional
    public boolean checkQr(CheckQrDto checkQrDto) {
        Qr qr = this.findByCodigo(checkQrDto.getCodigo());

        if (qr.isUsado()) {
            throw new IllegalArgumentException("El QR ya ha sido usado");
        }

        Long eventoIdEntrada = qr.getEntrada().getZonaEvento().getEvento().getId();
        Long zonaIdEntrada = qr.getEntrada().getZonaEvento().getZona().getId();

        if (!eventoIdEntrada.equals(checkQrDto.getEvento_id())) {
            throw new IllegalArgumentException("El QR no pertenece a este evento");
        }

        if (!checkQrDto.getZonas_ids().contains(zonaIdEntrada)) {
            throw new IllegalArgumentException("El QR no tiene acceso por esta puerta");
        }

        qr.setUsado(true);
        repository.save(qr);
        return true;
    }
}
