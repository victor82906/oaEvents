package com.vmr.oaevents.model.dto.qr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CheckQrDto {

    @NotNull(message = "El id de evento es obligatorio")
    private Long evento_id;

    @NotEmpty(message = "Debe enviar al menos el id de una zona valida para esta puerta")
    private List<Long> zonas_ids;

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

}
