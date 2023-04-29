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
import com.ecocitycraft.shopdb.models.regions.RegionRequest;
import com.ecocitycraft.shopdb.models.regions.RegionsQueryView;
import com.ecocitycraft.shopdb.services.APIKeyValidator;
import com.ecocitycraft.shopdb.services.ChestShopService;
import com.ecocitycraft.shopdb.services.Pagination;
import com.ecocitycraft.shopdb.services.RegionService;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("/regions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegionController {
    Logger LOGGER = LoggerFactory.getLogger(RegionController.class);

    @Inject
    RegionService regionService;

    @Inject
    APIKeyValidator apiKeyValidator;

    @Inject
    ChestShopService chestShopService;

    @GET
    @Transactional
    public PaginatedResponse<RegionDto> getRegions(
            @DefaultValue("1") @QueryParam("page") Integer page,
            @DefaultValue("6") @QueryParam("pageSize") Integer pageSize,
            @QueryParam("server") Server server,
            @DefaultValue("false") @QueryParam("active") Boolean active,
            @DefaultValue("") @QueryParam("name") String name,
            @DefaultValue("name") @QueryParam("sortBy") SortBy sortBy
    ) {
        LOGGER.info("GET /regions");
        if (page < 1) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE);
        if (pageSize < 1 || pageSize > 100) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE_SIZE);

        long totalResults = Region.count(server, active, name);
        List<RegionsQueryView> regions = Region.find(server, active, name, sortBy, page - 1, pageSize);

        List<RegionDto> results = regions.stream().map(RegionMapper::toRegionDto).collect(Collectors.toList());
        return new PaginatedResponse<>(page, Pagination.getNumPages(pageSize, totalResults), totalResults, results);
    }

    @GET
    @Path("region-names")
    public List<PanacheEntityBase> getRegionNames(
            @QueryParam("server") Server server,
            @DefaultValue("false") @QueryParam("active") Boolean active) {
        LOGGER.info("GET /region-names");
        return Region.findRegionNames(server, active);
    }

    @GET
    @Path("{server}/{name}")
    public RegionDto getRegion(
            @PathParam("server") Server server,
            @PathParam("name") String name
    ) {
        LOGGER.info("GET /regions/" + server + "/" + name);

        if (name == null) throw new SDBIllegalArgumentException(ExceptionMessage.EMPTY_REGION_NAME);
        if (server == null) throw new SDBIllegalArgumentException(ExceptionMessage.EMPTY_SERVER_NAME);

        List<RegionsQueryView> regions = Region.find(server, false, name, SortBy.NAME, 0, 10);
        if (regions.size() != 1) {
            throw new SDBNotFoundException(String.format(ExceptionMessage.REGION_NOT_FOUND, name, server));
        }

        RegionsQueryView region = regions.get(0);
        if (!region.getName().equalsIgnoreCase(name)) {
            throw new SDBNotFoundException(String.format(ExceptionMessage.REGION_NOT_FOUND, name, server));
        }

        return RegionMapper.toRegionDto(region);
    }

    @GET
    @Path("{server}/{name}/players")
    public PaginatedResponse<PlayerDto> getRegionOwners(
            @PathParam("server") Server server,
            @PathParam("name") String name,
            @DefaultValue("1") @QueryParam("page") Integer page,
            @DefaultValue("6") @QueryParam("pageSize") Integer pageSize) {
        LOGGER.info("GET /regions/" + server + "/" + name + "/players");

        if (page < 1) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE);
        if (pageSize < 1 || pageSize > 100) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE_SIZE);
        if (name == null) throw new SDBIllegalArgumentException(ExceptionMessage.EMPTY_REGION_NAME);
        if (server == null) throw new SDBIllegalArgumentException(ExceptionMessage.EMPTY_SERVER_NAME);

        Long id;
        try {
            id = Region.id(server, name);
        } catch (NoResultException e) {
            throw new SDBNotFoundException(String.format(ExceptionMessage.REGION_NOT_FOUND, name, server));
        }

        long totalResults = Player.countByRegionId(id);
        List<PlayersQueryView> players = Player.findByRegionId(id, page - 1, pageSize);
        List<PlayerDto> results = players.stream().map(PlayerMapper::toPlayerDto).collect(Collectors.toList());

        return new PaginatedResponse<>(page, Pagination.getNumPages(pageSize, totalResults), totalResults, results);
    }

    @GET
    @Path("{server}/{name}/chest-shops")
    public PaginatedResponse<ChestShopDto> getRegionChestShops(
            @PathParam("server") Server server,
            @PathParam("name") String name,
            @DefaultValue("1") @QueryParam("page") Integer page,
            @DefaultValue("6") @QueryParam("pageSize") Integer pageSize,
            @DefaultValue("buy") @QueryParam("tradeType") TradeType tradeType) {
        LOGGER.info("GET /regions/" + server + "/" + name + "/chest-shops");

        if (page < 1) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE);
        if (pageSize < 1 || pageSize > 100) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE_SIZE);

        Long id;
        try {
            id = Region.id(server, name);
        } catch (NoResultException e) {
            throw new SDBNotFoundException(String.format(ExceptionMessage.REGION_NOT_FOUND, name, server));
        }

        long totalResults = ChestShop.countByRegionId(id, tradeType);
        List<ChestShopsQueryView> chestShops = ChestShop.findByRegionId(id, tradeType, page - 1, pageSize);
        List<ChestShopDto> results = chestShops.stream().map(ChestShopMapper::toChestShopDto).collect(Collectors.toList());

        return new PaginatedResponse<>(page, Pagination.getNumPages(pageSize, totalResults), totalResults, results);
    }

    @PUT
    @Transactional
    @Consumes("application/json")
    public String addRegion(RegionRequest request, @HeaderParam("Authorization") String authHeader) throws Exception {
        apiKeyValidator.validateAPIKey(authHeader);
        Region r = regionService.listRegion(request);
        chestShopService.linkAndShowChestShops(r);
        return "Successfully listed region " + request.getName();
    }

    @DELETE
    @Transactional
    @Consumes("application/json")
    public String removeRegion(RegionRequest request, @HeaderParam("Authorization") String authHeader) throws Exception {
        apiKeyValidator.validateAPIKey(authHeader);
        Region r = regionService.unlistRegion(request);
        chestShopService.linkAndHideChestShops(r);
        return "Successfully unlisted region " + request.getName();
    }

}
