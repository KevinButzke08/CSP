package csp.inventory;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
@Entity
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Item> itemList;
    private float currentValue;
    private float totalPurchasePrice;
    private float changePercentage;
}
