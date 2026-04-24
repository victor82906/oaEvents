package com.vmr.oaevents.service;

import com.vmr.oaevents.model.Qr;
import com.vmr.oaevents.model.dto.qr.CheckQrDto;

import java.util.List;

public interface QrService {
    List<Qr> findAll();
    Qr findById(Long id);
    Qr findByCodigo(String codigo);
    Qr save(Qr entity);
    Qr update(Long id, Qr entity);
    void deleteById(Long id);
    boolean checkQr(CheckQrDto checkQrDto);
}
