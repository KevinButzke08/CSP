package csp.service;

import csp.controller.ItemDTO;
import csp.inventory.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item itemDTOtoItem(ItemDTO itemDTO);
}
