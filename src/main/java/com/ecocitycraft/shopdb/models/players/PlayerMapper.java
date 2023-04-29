package com.ecocitycraft.shopdb.models.players;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public final class PlayerMapper {
    public static PlayerDto toPlayerDto(PlayersQueryView player) {
        if (player == null) {
            return null;
        }

        PlayerDto result = new PlayerDto();
        result.setName(player.getName());
        result.setNumChestShops(player.getNumChestShops());
        result.setNumRegions(player.getNumRegions());

        return result;
    }
}
