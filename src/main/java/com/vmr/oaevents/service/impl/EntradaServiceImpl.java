package com.vmr.oaevents.service.impl;

import com.vmr.oaevents.model.*;
import com.vmr.oaevents.model.dto.entrada.EntradaCompraInputDto;
import com.vmr.oaevents.model.dto.entrada.EntradaCompraLogueadoInputDto;
import com.vmr.oaevents.repository.EntradaRepository;
import com.vmr.oaevents.security.AuthenticationFacade;
import com.vmr.oaevents.service.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    private final EventoService eventoService;
    private final QrGeneratorService qrGeneratorService;
    private final PdfService pdfService;
    private final AuthenticationFacade authenticationFacade;
    private int NUM_ENTRADAS_VALIDAS = 8;

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
        compradorService.findById(compradorId);
        return repository.findByCompradorId(compradorId, pageable);
    }

    @Override
    public List<Entrada> findByCompradorIdAndEventoId(Long compradorId, Long eventoId) {
        compradorService.findById(compradorId);
        eventoService.findById(eventoId);
        return repository.findByCompradorIdAndZonaEvento_Evento_Id(compradorId, eventoId);
    }

    @Override
    public Entrada findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrada no encontrada con id: " + id));
    }

    @Override
    @Transactional
    public Entrada save(Entrada entity, boolean logueado) {
        Localidad localidad = localidadService.findById(entity.getLocalidad().getId());
        ZonaEvento zonaEvento = zonaEventoService.findById(entity.getZonaEvento().getId());
        Comprador comprador;
        if(logueado) {
            comprador = authenticationFacade.getAuthenticatedComprador();
            entity.setComprador(comprador);
        } else {
            comprador = entity.getComprador();
            entity.setComprador(null);
        }

        String codigo = UUID.randomUUID().toString();
        String rutaQr = qrGeneratorService.generateQr(codigo);

        Qr qr = new Qr();
        qr.setCodigo(codigo);
        qr.setFoto(rutaQr);
        qr.setUsado(false);

        entity.setQr(qr);
        entity.setLocalidad(localidad);
        entity.setZonaEvento(zonaEvento);
        entity.setFechaCompra(LocalDateTime.now());
        entity.setFechaEvento(zonaEvento.getEvento().getFecha());
        entity.setNombreComprador(comprador.getNombre());
        entity.setEmailComprador(comprador.getEmail());
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
        datos.put("emailComprador", entrada.getEmailComprador());
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
            datos.put("numeroZona", entrada.getZonaEvento().getZona().getNumero());
            pista = entrada.getZonaEvento().getZona().getPista();
        } else {
            datos.put("puerta", "N/A");
        }

        datos.put("pista", pista);

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

    @Override
    @Transactional
    public List<Long> procesarPagoLogueado(EntradaCompraLogueadoInputDto entradaCompraLogueadoInputDto){
        // validar tarjeta credito

        List<Long> entrada_ids = new ArrayList<>();
        eventoService.findById(entradaCompraLogueadoInputDto.getEvento_id());
        ZonaEvento zonaEvento = zonaEventoService.findById(entradaCompraLogueadoInputDto.getZonaEvento_id());

        List<Long> localidad_ids = entradaCompraLogueadoInputDto.getLocalidad_ids();
        if(localidad_ids.size() > NUM_ENTRADAS_VALIDAS){
            throw new IllegalArgumentException("No se pueden comprar mas de " + NUM_ENTRADAS_VALIDAS + " entradas seguidas");
        }

        List<Localidad> localidadesLibres = localidadService.findLocalidadesLibresByZonaEventoId(zonaEvento.getId());
        List<Long> idsLibres = localidadesLibres.stream().map(Localidad::getId).toList();

        localidad_ids.forEach(id -> {
            if (!idsLibres.contains(id)) {
                throw new IllegalArgumentException("Alguna de las localidades seleccionada no esta libre");
            }
        });

        localidad_ids.forEach(id -> {
            Entrada entrada = new Entrada();
            Localidad localidad = new Localidad();
            localidad.setId(id);

            entrada.setLocalidad(localidad);
            entrada.setZonaEvento(zonaEvento);

            entrada = this.save(entrada, true);
            entrada_ids.add(entrada.getId());
        });

        return entrada_ids;
    }

    @Override
    @Transactional
    public List<Long> procesarPago(EntradaCompraInputDto entradaCompraInputDto) {
        // validar tarjeta credito

        List<Long> entrada_ids = new ArrayList<>();
        eventoService.findById(entradaCompraInputDto.getEvento_id());
        ZonaEvento zonaEvento = zonaEventoService.findById(entradaCompraInputDto.getZonaEvento_id());

        List<Long> localidad_ids = entradaCompraInputDto.getLocalidad_ids();
        if(localidad_ids.size() > NUM_ENTRADAS_VALIDAS){
            throw new IllegalArgumentException("No se pueden comprar mas de " + NUM_ENTRADAS_VALIDAS + " entradas seguidas");
        }

        List<Localidad> localidadesLibres = localidadService.findLocalidadesLibresByZonaEventoId(zonaEvento.getId());
        List<Long> idsLibres = localidadesLibres.stream().map(Localidad::getId).toList();

        localidad_ids.forEach(id -> {
            if (!idsLibres.contains(id)) {
                throw new IllegalArgumentException("Alguna de las localidades seleccionada no esta libre");
            }
        });

        Comprador comprador = new Comprador();
        comprador.setNombre(entradaCompraInputDto.getNombreComprador());
        comprador.setEmail(entradaCompraInputDto.getEmailComprador());
        comprador.setDni(entradaCompraInputDto.getDniComprador());

        localidad_ids.forEach(id -> {
            Entrada entrada = new Entrada();
            Localidad localidad = new Localidad();
            localidad.setId(id);

            entrada.setLocalidad(localidad);
            entrada.setZonaEvento(zonaEvento);
            entrada.setComprador(comprador);

            entrada = this.save(entrada, false);
            entrada_ids.add(entrada.getId());
        });

        return entrada_ids;
    }

    @Override
    public boolean isComprador(Long entradaId, Long usuarioId) {
        Entrada entrada = this.findById(entradaId);
        return entrada.getComprador() != null && entrada.getComprador().getId().equals(usuarioId);
    }

}
