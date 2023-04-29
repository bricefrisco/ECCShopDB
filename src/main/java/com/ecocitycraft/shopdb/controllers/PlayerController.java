package com.ecocitycraft.shopdb.controllers;

import com.ecocitycraft.shopdb.database.ChestShop;
import com.ecocitycraft.shopdb.database.Player;
import com.ecocitycraft.shopdb.database.Region;
import com.ecocitycraft.shopdb.models.PaginatedResponse;
import com.ecocitycraft.shopdb.models.chestshops.*;
import com.ecocitycraft.shopdb.models.exceptions.ExceptionMessage;
import com.ecocitycraft.shopdb.models.exceptions.SDBIllegalArgumentException;
import com.ecocitycraft.shopdb.models.exceptions.SDBNotFoundException;
import com.ecocitycraft.shopdb.models.players.PlayerDto;
import com.ecocitycraft.shopdb.models.players.PlayerMapper;
import com.ecocitycraft.shopdb.models.players.PlayersQueryView;
import com.ecocitycraft.shopdb.models.regions.RegionDto;
import com.ecocitycraft.shopdb.models.regions.RegionMapper;
import com.ecocitycraft.shopdb.models.regions.RegionsQueryView;
import com.ecocitycraft.shopdb.services.Pagination;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("/players")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlayerController {
    Logger LOGGER = LoggerFactory.getLogger(PlayerController.class);

    @GET
    @Transactional
    public PaginatedResponse<PlayerDto> getPlayers(
            @DefaultValue("1") @QueryParam("page") Integer page,
            @DefaultValue("6") @QueryParam("pageSize") Integer pageSize,
            @DefaultValue("") @QueryParam("name") String name,
            @DefaultValue("name") @QueryParam("sortBy") SortBy sortBy
    ) {
        LOGGER.info("GET /players");

        if (page < 1) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE);
        if (pageSize < 1 || pageSize > 100) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE_SIZE);

        long totalResults = Player.count(name);
        List<PlayersQueryView> players = Player.find(name, sortBy, page - 1, pageSize);

        List<PlayerDto> results = players.stream().map(PlayerMapper::toPlayerDto).collect(Collectors.toList());
        return new PaginatedResponse<>(page, Pagination.getNumPages(pageSize, totalResults), totalResults, results);
    }

    @GET
    @Path("player-names")
    public List<PanacheEntityBase> getPlayerNames() {
        return Player.findPlayerNames();
    }

    @GET
    @Path("{name}")
    public PlayerDto getPlayer(@PathParam("name") String name) {
        LOGGER.info("GET /players/" + name);

        List<PlayersQueryView> players = Player.find(name, SortBy.NAME, 0, 10);
        if (players.size() == 0 || !players.get(0).getName().equalsIgnoreCase(name)) {
            throw new SDBNotFoundException(String.format(ExceptionMessage.PLAYER_NOT_FOUND, name));
        }

        return PlayerMapper.toPlayerDto(players.get(0));
    }

    @GET
    @Path("{name}/regions")
    public PaginatedResponse<RegionDto> getPlayerRegions(
            @DefaultValue("1") @QueryParam("page") Integer page,
            @DefaultValue("6") @QueryParam("pageSize") Integer pageSize,
            @PathParam("name") String name
    ) {
        LOGGER.info("GET /players/" + name + "/regions");

        if (page < 1) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE);
        if (pageSize < 1 || pageSize > 100) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE_SIZE);
        if (name == null || name.isEmpty()) throw new SDBIllegalArgumentException(ExceptionMessage.EMPTY_PLAYER_NAME);

        Long id;
        try {
            id = Player.id(name);
        } catch (NoResultException e) {
            throw new SDBNotFoundException(String.format(ExceptionMessage.PLAYER_NOT_FOUND, name));
        }

        long totalResults = Region.countByPlayerId(id);
        List<RegionsQueryView> regions = Region.findByPlayerId(id, page - 1, pageSize);
        List<RegionDto> results = regions.stream().map(RegionMapper::toRegionDto).collect(Collectors.toList());

        return new PaginatedResponse<>(page, Pagination.getNumPages(pageSize, totalResults), totalResults, results);
    }

    @GET
    @Path("{name}/chest-shops")
    public PaginatedResponse<ChestShopDto> getPlayerChestShops(
            @DefaultValue("1") @QueryParam("page") Integer page,
            @DefaultValue("6") @QueryParam("pageSize") Integer pageSize,
            @DefaultValue("buy") @QueryParam("tradeType") TradeType tradeType,
            @PathParam("name") String name) {
        LOGGER.info("GET /players/" + name + "/chest-shops");

        if (page < 1) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE);
        if (pageSize < 1 || pageSize > 100) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE_SIZE);
        if (name == null || name.isEmpty()) throw new SDBIllegalArgumentException(ExceptionMessage.EMPTY_PLAYER_NAME);

        Long id;
        try {
            id = Player.id(name);
        } catch (NoResultException e) {
            throw new SDBNotFoundException(String.format(ExceptionMessage.PLAYER_NOT_FOUND, name));
        }

        long totalResults = ChestShop.countByPlayerId(id, tradeType);
        List<ChestShopsQueryView> chestShops = ChestShop.findByPlayerId(id, tradeType, page - 1, pageSize);
        List<ChestShopDto> results = chestShops.stream().map(ChestShopMapper::toChestShopDto).collect(Collectors.toList());

        return new PaginatedResponse<>(page, Pagination.getNumPages(pageSize, totalResults), totalResults, results);
    }
}
