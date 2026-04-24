package com.vmr.oaevents.model.dto.qr;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QrOutputDto {

    private Long id;
    private String codigo;
    private String foto;
    private Boolean usado;
    private Long entrada_id;

}
