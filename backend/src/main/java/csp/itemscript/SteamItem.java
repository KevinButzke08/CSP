package csp.itemscript;

import lombok.Getter;

@Getter
public class SteamItem {
    private String name;
    private String hash_name;
    private int sell_listings;
    private int sell_price;
    private String sell_price_text;
}
