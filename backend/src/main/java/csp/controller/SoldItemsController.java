package csp.controller;

import csp.service.SoldItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping("/api/soldItems")
public class SoldItemsController {
    private final SoldItemsService soldItemsService;

    @Autowired
    public SoldItemsController(SoldItemsService soldItemsService) {
        this.soldItemsService = soldItemsService;
    }

    @GetMapping("/total-sell-price")
    public ResponseEntity<BigDecimal> getTotalSellPrice() {
        return ResponseEntity.ok(soldItemsService.getTotalSellPrice());
    }

    @GetMapping("/total-realized-value")
    public ResponseEntity<BigDecimal> getTotalRealizedValue() {
        return ResponseEntity.ok(soldItemsService.getTotalRealizedValue());
    }
}
