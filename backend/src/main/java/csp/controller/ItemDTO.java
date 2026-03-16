package csp.controller;

import java.math.BigDecimal;

public record ItemDTO(String name, int quantity, BigDecimal purchasePrice) {
}
