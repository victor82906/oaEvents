package com.vmr.oaevents.model.mapper;

import com.vmr.oaevents.model.Zona;
import com.vmr.oaevents.model.dto.zona.ZonaInputDto;
import com.vmr.oaevents.model.dto.zona.ZonaOutputDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ZonaMapper {

    @Mapping(source = "recinto_id", target = "recinto.id")
    Zona toEntity(ZonaInputDto zonaInputDto);

    @Mapping(source = "recinto.id", target = "recinto_id")
    ZonaOutputDto toDto(Zona zona);

}
