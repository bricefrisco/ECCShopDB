package com.ecocitycraft.shopdb.models.regions;

import com.ecocitycraft.shopdb.models.chestshops.Location;
import com.ecocitycraft.shopdb.models.chestshops.Server;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.sql.Timestamp;

@RegisterForReflection
public class RegionsQueryView {
    private Long id;
    private String name;
    private Server server;
    private Location iBounds;
    private Location oBounds;
    private Integer numChestShops;
    private Integer numMayors;

    private Boolean active;
    private Timestamp lastUpdated;

    public Long getId() {
        return id;
    }

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

    public Integer getNumChestShops() {
        return numChestShops;
    }

    public Integer getNumMayors() {
        return numMayors;
    }

    public Boolean getActive() {
        return active;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setNumChestShops(Integer numChestShops) {
        this.numChestShops = numChestShops;
    }

    public void setNumMayors(Integer numMayors) {
        this.numMayors = numMayors;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
