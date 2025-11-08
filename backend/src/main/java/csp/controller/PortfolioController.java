package csp.controller;

import csp.inventory.Item;
import csp.inventory.Portfolio;
import csp.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Autowired
    PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<Portfolio> getPortfolio() {
        Portfolio responsePortfolio = portfolioService.getPortfolio();
        return ResponseEntity.ok(responsePortfolio);
    }

    @PostMapping("/items")
    public ResponseEntity<Portfolio> addItem(@RequestBody Item item) {
        portfolioService.addItemToPortfolio(item);
        return ResponseEntity.ok(portfolioService.getPortfolio());
    }

    @DeleteMapping("/items/{itemName}")
    public ResponseEntity<Portfolio> deleteItem(@PathVariable String itemName) {
        portfolioService.deleteItemFromPortfolio(itemName);
        return ResponseEntity.ok(portfolioService.getPortfolio());
    }

}
