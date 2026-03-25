package csp.service;

import csp.inventory.Item;
import csp.inventory.SoldItem;
import csp.repository.SoldItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Service
public class SellService {
    private final SoldItemRepository soldItemRepository;

    @Autowired
    public SellService(SoldItemRepository soldItemRepository) {
        this.soldItemRepository = soldItemRepository;
    }

    public void sellItem(Item item, int soldQuantity, BigDecimal sellPrice) {
        SoldItem soldItem = new SoldItem();

        soldItem.setName(item.getName());
        soldItem.setQuantity(soldQuantity);
        soldItem.setSellPrice(sellPrice);
        soldItem.setChangePercentage(sellPrice.subtract(item.getPurchasePrice()).divide(item.getPurchasePrice(), RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
        soldItem.setPurchasePrice(item.getPurchasePrice());
        soldItem.setTimestamp(LocalDateTime.now());

        soldItemRepository.save(soldItem);
    }
}
