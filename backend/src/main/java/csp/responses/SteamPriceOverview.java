package csp.responses;

public record SteamPriceOverview(boolean success, String lowest_price, String volume, String median_price) {
}
