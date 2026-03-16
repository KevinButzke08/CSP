package csp.controller;

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

    @PostMapping("/refresh")
    public ResponseEntity<Portfolio> refreshPortfolio() {
        Portfolio responsePortfolio = portfolioService.refreshPortfolio();
        return ResponseEntity.ok(responsePortfolio);
    }

    @PostMapping("/items")
    public ResponseEntity<Portfolio> addItem(@RequestBody ItemDTO itemDTO) {
        portfolioService.addItemToPortfolio(itemDTO);
        return ResponseEntity.ok(portfolioService.getPortfolio());
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Portfolio> deleteItem(@PathVariable Long itemId) {
        portfolioService.deleteItemFromPortfolio(itemId);
        return ResponseEntity.ok(portfolioService.getPortfolio());
    }
}
