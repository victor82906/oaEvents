package com.vmr.oaevents.model.dto.recinto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecintoInputDto {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email no valido")
    private String email;

    //@NotBlank(message = "La contraseña es obligatoria")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "La contraseña debe tener al menos 8 caracteres, una letra y un numero")
    private String contrasena;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "^[679]\\d{8}$", message = "El formato de telefono es incorrecto")
    private String telefono;

    @NotBlank(message = "La ubicacion es obligatoria")
    private String ubicacion;

    @NotBlank(message = "El mapa es obligatorio")
    private String mapa;

}
