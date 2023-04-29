package com.ecocitycraft.shopdb.models.players;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class PlayersQueryView {
    private String name;
    private int numChestShops;
    private int numRegions;

    public String getName() {
        return name;
    }

    public int getNumChestShops() {
        return numChestShops;
    }

    public int getNumRegions() {
        return numRegions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumChestShops(int numChestShops) {
        this.numChestShops = numChestShops;
    }

    public void setNumRegions(int numRegions) {
        this.numRegions = numRegions;
    }
}
