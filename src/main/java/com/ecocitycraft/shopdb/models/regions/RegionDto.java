package com.ecocitycraft.shopdb.models.regions;

import com.ecocitycraft.shopdb.models.chestshops.Location;
import com.ecocitycraft.shopdb.models.chestshops.Server;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
@RegisterForReflection
public class RegionDto {
    private String name;
    private Server server;
    private Location iBounds;
    private Location oBounds;
    private int numChestShops;
    private int numMayors;
    private Boolean active;
    private Timestamp lastUpdated;

    public String getName() {
        return name;
    }

    public Server getServer() {
        return server;
    }

    public Location getiBounds() {
        return iBounds;
    }

    public Location getoBounds() {
        return oBounds;
    }

    public int getNumChestShops() {
        return numChestShops;
    }

    public int getNumMayors() {
        return numMayors;
    }

    public Boolean getActive() {
        return active;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setiBounds(Location iBounds) {
        this.iBounds = iBounds;
    }

    public void setoBounds(Location oBounds) {
        this.oBounds = oBounds;
    }

    public void setNumChestShops(int numChestShops) {
        this.numChestShops = numChestShops;
    }

    public void setNumMayors(int numMayors) {
        this.numMayors = numMayors;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
