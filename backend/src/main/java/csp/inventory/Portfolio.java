package csp.inventory;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
@Entity
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Item> itemList = new ArrayList<>();
    private float currentValue;
    private float totalPurchasePrice;
    private float changePercentage;
}
