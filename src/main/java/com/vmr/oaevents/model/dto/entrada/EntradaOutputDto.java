package com.vmr.oaevents.model.dto.entrada;

import com.vmr.oaevents.model.dto.qr.QrOutputDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EntradaOutputDto {

    private Long id;
    private LocalDateTime fechaCompra;
    private LocalDateTime fechaEvento;
    private String nombreComprador;
    private String dniComprador;
    private double precio;
    private Long localidad_id;
    private Long zonaEvento_id;
    private Long comprador_id;
    private QrOutputDto qr;

}
