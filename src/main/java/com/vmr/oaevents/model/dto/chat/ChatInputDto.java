package com.vmr.oaevents.model.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatInputDto {

    @NotBlank(message = "El campo mensaje no puede estar vacio")
    private String mensaje;

    private LocalDateTime fecha;

    private Long emisor_id;

    @NotNull(message = "Id del receptor obligatorio")
    private Long receptor_id;

}
