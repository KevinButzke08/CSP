package csp.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Item {
    @Id
    private Long id;
    private String name;
    private int quantity;
    private float currentPrice;
    private float purchasePrice;
}
