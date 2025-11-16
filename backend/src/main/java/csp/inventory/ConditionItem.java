package csp.inventory;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ConditionItem extends Item {
    private Condition condition;
}
