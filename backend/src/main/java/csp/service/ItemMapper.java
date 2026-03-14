package csp.service;

import csp.controller.ItemDTO;
import csp.inventory.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "purchasePrice", source = "purchasePrice")
    Item itemDTOtoItem(ItemDTO itemDTO);
}
