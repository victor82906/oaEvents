package com.vmr.oaevents.service.impl;

import com.vmr.oaevents.model.*;
import com.vmr.oaevents.repository.EntradaRepository;
import com.vmr.oaevents.service.CompradorService;
import com.vmr.oaevents.service.EntradaService;
import com.vmr.oaevents.service.LocalidadService;
import com.vmr.oaevents.service.ZonaEventoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EntradaServiceImpl implements EntradaService {

    private final EntradaRepository repository;
    private final LocalidadService localidadService;
    private final ZonaEventoService zonaEventoService;
    private final CompradorService compradorService;
    private final QrGeneratorService qrGeneratorService;
    private final PdfService pdfService;

    @Override
    public List<Entrada> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<Entrada> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Entrada> findByCompradorId(Long compradorId, Pageable pageable) {
        // Valida que el comprador exista (opcional pero recomendable)
        compradorService.findById(compradorId);
        return repository.findByCompradorId(compradorId, pageable);
    }

    @Override
    public Entrada findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrada no encontrada con id: " + id));
    }

    @Override
    public Entrada save(Entrada entity) {
        Localidad localidad = localidadService.findById(entity.getLocalidad().getId());
        ZonaEvento zonaEvento = zonaEventoService.findById(entity.getZonaEvento().getId());
        Comprador comprador = compradorService.findById(entity.getComprador().getId());

        String codigo = UUID.randomUUID().toString();
        String rutaQr = qrGeneratorService.generateQr(codigo);

        Qr qr = new Qr();
        qr.setCodigo(codigo);
        qr.setFoto(rutaQr);
        qr.setUsado(false);

        entity.setQr(qr);
        entity.setLocalidad(localidad);
        entity.setZonaEvento(zonaEvento);
        entity.setComprador(comprador);
        entity.setFechaCompra(LocalDateTime.now());
        entity.setFechaEvento(entity.getZonaEvento().getEvento().getFecha());
        entity.setNombreComprador(comprador.getNombre());
        entity.setDniComprador(comprador.getDni());
        entity.setPrecio(zonaEvento.getPrecio());
        return repository.save(entity);
    }

    @Override
    public Entrada update(Long id, Entrada entity) {
        Entrada entrada = this.findById(id);
        entity.setId(id);
        entity.setLocalidad(entrada.getLocalidad());
        entity.setZonaEvento(entrada.getZonaEvento());
        entity.setFechaCompra(entrada.getFechaCompra());
        Comprador comprador = compradorService.findById(entity.getComprador().getId());
        entity.setComprador(comprador);
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        this.findById(id);
        repository.delete(this.findById(id));
    }

    @Override
    public byte[] descargarEntradaPdf(Long id) {
        Entrada entrada = this.findById(id);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Map<String, Object> datos = new HashMap<>();
        datos.put("fechaCompra", entrada.getFechaCompra() != null ? entrada.getFechaCompra().format(formatter) : "");
        datos.put("fechaEvento", entrada.getFechaEvento() != null ? entrada.getFechaEvento().format(formatter) : "");
        datos.put("nombreComprador", entrada.getNombreComprador());
        datos.put("dniComprador", entrada.getDniComprador());
        datos.put("precio", String.format("%.2f", entrada.getPrecio()));

        if (!Objects.isNull(entrada.getZonaEvento()) && !Objects.isNull(entrada.getZonaEvento().getEvento())){
            datos.put("nombreEvento", entrada.getZonaEvento().getEvento().getTitulo());
        } else {
            datos.put("nombreEvento", "N/A");
        }

        boolean pista = true;

        if (!Objects.isNull(entrada.getZonaEvento()) && !Objects.isNull(entrada.getZonaEvento().getZona())) {
            datos.put("puerta", entrada.getZonaEvento().getZona().getPuertaEntrada());
            pista = entrada.getZonaEvento().getZona().isPista();
        } else {
            datos.put("puerta", "N/A");
        }

        datos.put("pista", true);

        if (!pista && !Objects.isNull(entrada.getLocalidad())) {
            datos.put("fila", entrada.getLocalidad().getFila());
            datos.put("numero", entrada.getLocalidad().getNumero());
        }

        if (!Objects.isNull(entrada.getQr())) {
            datos.put("codigoQr", entrada.getQr().getCodigo());
            datos.put("fotoQr", Paths.get(entrada.getQr().getFoto()).toAbsolutePath().toUri().toString());
        } else {
            datos.put("codigoQr", "N/A");
            datos.put("fotoQr", "");
        }

        return pdfService.generarPdf("entrada", datos);
    }
}
