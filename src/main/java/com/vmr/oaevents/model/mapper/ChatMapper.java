package com.vmr.oaevents.model.mapper;

import com.vmr.oaevents.model.Chat;
import com.vmr.oaevents.model.dto.chat.ChatInputDto;
import com.vmr.oaevents.model.dto.chat.ChatOutputDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(source = "receptor_id", target = "receptor.id")
    @Mapping(target = "emisor", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    Chat toEntity(ChatInputDto chatInputDto);

    @Mapping(source = "emisor.id", target = "emisor_id")
    @Mapping(source = "receptor.id", target = "receptor_id")
    ChatOutputDto toDto(Chat chat);

}
