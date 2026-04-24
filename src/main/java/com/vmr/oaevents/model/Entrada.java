package com.vmr.oaevents.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime fechaCompra;
    private LocalDateTime fechaEvento;
    private String nombreComprador;
    private String dniComprador;
    private double precio;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "qr_id")
    private Qr qr;

    @ManyToOne
    @JoinColumn(name = "localidad_id")
    private Localidad localidad;

    @ManyToOne
    @JoinColumn(name = "zona_evento_id")
    private ZonaEvento zonaEvento;

    @ManyToOne
    @JoinColumn(name = "comprador_id")
    private Comprador comprador;

}
