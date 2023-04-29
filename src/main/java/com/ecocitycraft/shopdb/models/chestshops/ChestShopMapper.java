package com.ecocitycraft.shopdb.models.chestshops;

public final class ChestShopMapper {
    public static ChestShopRegionDto toChestShopRegionDto(String regionName) {
        if (regionName == null) {
            return null;
        }

        ChestShopRegionDto result = new ChestShopRegionDto();
        result.setName(regionName);
        return result;
    }

    public static ChestShopPlayerDto toChestShopPlayerDto(String playerName) {
        if (playerName == null) {
            return null;
        }

        ChestShopPlayerDto result = new ChestShopPlayerDto();
        result.setName(playerName);
        return result;
    }

    public static ChestShopDto toChestShopDto(ChestShopsQueryView chestShop) {
        if (chestShop == null) {
            return null;
        }

        ChestShopDto result = new ChestShopDto();
        result.setServer(chestShop.getServer());
        result.setLocation(chestShop.getLocation());
        result.setMaterial(chestShop.getMaterial());
        result.setOwner(toChestShopPlayerDto(chestShop.getOwnerName()));
        result.setTown(toChestShopRegionDto(chestShop.getTownName()));
        result.setQuantity(chestShop.getQuantity());
        result.setQuantityAvailable(chestShop.getQuantityAvailable());
        result.setBuyPrice(chestShop.getBuyPrice());
        result.setSellPrice(chestShop.getSellPrice());
        result.setBuyPriceEach(chestShop.getBuyPriceEach());
        result.setSellPriceEach(chestShop.getSellPriceEach());
        result.setFull(chestShop.getFull());
        result.setBuySign(chestShop.getBuySign());
        result.setSellSign(chestShop.getSellSign());

        return result;
    }
}
