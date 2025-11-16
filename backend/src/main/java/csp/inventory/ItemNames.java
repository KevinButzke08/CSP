package csp.inventory;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ItemNames {
    CS_GO_WEAPON_CASE("CS:GO Weapon Case"),
    ESPORTS_2013_CASE("eSports 2013 Case"),
    OPERATION_BRAVO_CASE("Operation Bravo Case"),
    CS_GO_WEAPON_CASE_2("CS:GO Weapon Case 2"),
    ESPORTS_2013_WINTER_CASE("eSports 2013 Winter Case"),
    WINTER_OFFENSIVE_WEAPON_CASE("Winter Offensive Weapon Case"),
    CS_GO_WEAPON_CASE_3("CS:GO Weapon Case 3"),
    OPERATION_PHOENIX_WEAPON_CASE("Operation Phoenix Weapon Case"),
    HUNTSMAN_WEAPON_CASE("Huntsman Weapon Case"),
    OPERATION_BREAKOUT_WEAPON_CASE("Operation Breakout Weapon Case"),
    ESPORTS_2014_SUMMER_CASE("eSports 2014 Summer Case"),
    OPERATION_VANGUARD_WEAPON_CASE("Operation Vanguard Weapon Case"),
    CHROMA_CASE("Chroma Case"),
    CHROMA_2_CASE("Chroma 2 Case"),
    FALCHION_CASE("Falchion Case"),
    SHADOW_CASE("Shadow Case"),
    REVOLVER_CASE("Revolver Case"),
    OPERATION_WILDFIRE_CASE("Operation Wildfire Case"),
    CHROMA_3_CASE("Chroma 3 Case"),
    GAMMA_CASE("Gamma Case"),
    GAMMA_2_CASE("Gamma 2 Case"),
    GLOVE_CASE("Glove Case"),
    SPECTRUM_CASE("Spectrum Case"),
    CLUTCH_CASE("Clutch Case"),
    HORIZON_CASE("Horizon Case"),
    DANGER_ZONE_CASE("Danger Zone Case"),
    PRISMA_CASE("Prisma Case"),
    SHATTERED_WEB_CASE("Shattered Web Case"),
    CS20_CASE("CS20 Case"),
    PRISMA_2_CASE("Prisma 2 Case"),
    FRACTURE_CASE("Fracture Case"),
    OPERATION_BROKEN_FANG_CASE("Operation Broken Fang Case"),
    SNAKEBITE_CASE("Snakebite Case"),
    OPERATION_RIPTIDE_CASE("Operation Riptide Case"),
    DREAMS_AND_NIGHTMARES_CASE("Dreams & Nightmares Case"),
    RECOIL_CASE("Recoil Case"),
    REVOLUTION_CASE("Revolution Case"),
    KILOWATT_CASE("Kilowatt Case"),
    GALLERY_CASE("Gallery Case"),
    FEVER_CASE("Fever Case");


    private final String marketName;

    ItemNames(String marketName) {
        this.marketName = marketName;
    }

    public String getMarketName() {
        return marketName;
    }

    // lookup by market name O(1)
    private static final Map<String, ItemNames> BY_MARKET_NAME =
            Arrays.stream(values())
                    .collect(Collectors.toMap(ItemNames::getMarketName, e -> e));

    public static boolean isValidMarketName(String marketName) {
        return BY_MARKET_NAME.get(marketName) != null;
    }


}
