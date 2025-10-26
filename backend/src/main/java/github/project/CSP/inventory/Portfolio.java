package github.project.CSP.inventory;

import lombok.Data;

import java.util.List;
@Data
public class Portfolio {
    private List<Item> itemList;
    private float currentValue;
    private float totalPurchasePrice;
    private float changePercentage;
}
