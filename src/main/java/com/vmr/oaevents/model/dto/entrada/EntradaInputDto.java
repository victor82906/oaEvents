package com.vmr.oaevents.model.dto.entrada;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EntradaInputDto {

    private LocalDateTime fechaCompra;

    private LocalDateTime fechaEvento;

    private String nombreComprador;

    private String dniComprador;

    private Double precio;

    @NotNull(message = "El id de localidad es obligatorio")
    private Long localidad_id;

    @NotNull(message = "El id de zonaEvento es obligatorio")
    private Long zonaEvento_id;

    @NotNull(message = "El id de comprador es obligatorio")
    private Long comprador_id;

}
