package csp.itemscript;

import lombok.Getter;

import java.util.List;
@Getter
public class SteamMarketListings {
    private Boolean success;
    private int start;
    private int pagesize;
    private int total_count;
    private List<SteamItem> results;
}
