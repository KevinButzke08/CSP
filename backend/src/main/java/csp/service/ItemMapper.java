package csp.service;

import csp.controller.ItemDTO;
import csp.inventory.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "id", ignore = true) // frontend should not send it
    @Mapping(target = "currentPrice", expression = "java(java.math.BigDecimal.ZERO)") // set BigDecimal.ZERO
    Item itemDTOtoItem(ItemDTO itemDTO);
}
