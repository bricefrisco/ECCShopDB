package com.ecocitycraft.shopdb.models.chestshops;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ChestShopsQueryView {
    private String id;
    private Server server;
    private Location location;
    private String material;
    private String ownerName;
    private String townName;
    private Integer quantity;
    private Integer quantityAvailable;
    private Double buyPrice;
    private Double sellPrice;
    private Double buyPriceEach;
    private Double sellPriceEach;
    private Boolean isFull;
    private Boolean isHidden;
    private Boolean isBuySign;
    private Boolean isSellSign;

    public String getId() {
        return id;
    }

    public Server getServer() {
        return server;
    }

    public Location getLocation() {
        return location;
    }

    public String getMaterial() {
        return material;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getTownName() {
        return townName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    public Double getBuyPrice() {
        return buyPrice;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public Double getBuyPriceEach() {
        return buyPriceEach;
    }

    public Double getSellPriceEach() {
        return sellPriceEach;
    }

    public Boolean getFull() {
        return isFull;
    }

    public Boolean getHidden() {
        return isHidden;
    }

    public Boolean getBuySign() {
        return isBuySign;
    }

    public Boolean getSellSign() {
        return isSellSign;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public void setBuyPrice(Double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public void setBuyPriceEach(Double buyPriceEach) {
        this.buyPriceEach = buyPriceEach;
    }

    public void setSellPriceEach(Double sellPriceEach) {
        this.sellPriceEach = sellPriceEach;
    }

    public void setFull(Boolean full) {
        isFull = full;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }

    public void setBuySign(Boolean buySign) {
        isBuySign = buySign;
    }

    public void setSellSign(Boolean sellSign) {
        isSellSign = sellSign;
    }
}
