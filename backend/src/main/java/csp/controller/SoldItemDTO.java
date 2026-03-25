package csp.controller;

import java.math.BigDecimal;

public record SoldItemDTO(String name, int quantity, BigDecimal sellPrice) {
}
