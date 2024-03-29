package com.ecocitycraft.shopdb.models.players;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
@RegisterForReflection
public class PlayerDto {
    private String name;
    private Timestamp lastSeen;
    private Timestamp lastUpdated;
    private int numChestShops;
    private int numRegions;

    public String getName() {
        return name;
    }

    public Timestamp getLastSeen() {
        return lastSeen;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumChestShops() {
        return numChestShops;
    }

    public int getNumRegions() {
        return numRegions;
    }

    public void setLastSeen(Timestamp lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setNumChestShops(int numChestShops) {
        this.numChestShops = numChestShops;
    }

    public void setNumRegions(int numRegions) {
        this.numRegions = numRegions;
    }
}
