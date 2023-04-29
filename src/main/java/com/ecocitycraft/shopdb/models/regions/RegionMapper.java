package com.ecocitycraft.shopdb.models.regions;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public final class RegionMapper {
    public static RegionDto toRegionDto(RegionsQueryView regionsQueryView) {
        if (regionsQueryView == null) {
            return null;
        }

        RegionDto result = new RegionDto();
        result.setName(regionsQueryView.getName());
        result.setServer(regionsQueryView.getServer());
        result.setiBounds(regionsQueryView.getiBounds());
        result.setoBounds(regionsQueryView.getoBounds());
        result.setNumChestShops(regionsQueryView.getNumChestShops());
        result.setNumMayors(regionsQueryView.getNumMayors());
        result.setActive(regionsQueryView.getActive());
        result.setLastUpdated(regionsQueryView.getLastUpdated());

        return result;
    }
}
