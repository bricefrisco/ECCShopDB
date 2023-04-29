package com.ecocitycraft.shopdb.controllers;

import com.ecocitycraft.shopdb.database.ChestShop;
import com.ecocitycraft.shopdb.models.PaginatedResponse;
import com.ecocitycraft.shopdb.models.chestshops.*;
import com.ecocitycraft.shopdb.models.exceptions.ExceptionMessage;
import com.ecocitycraft.shopdb.models.exceptions.SDBIllegalArgumentException;
import com.ecocitycraft.shopdb.services.APIKeyValidator;
import com.ecocitycraft.shopdb.services.ChestShopService;
import com.ecocitycraft.shopdb.services.Pagination;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.stream.Collectors;

@Path("/chest-shops")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChestShopController {
    Logger LOGGER = LoggerFactory.getLogger(ChestShopController.class);

    @Inject
    ChestShopService chestShopService;

    @Inject
    APIKeyValidator apiKeyValidator;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PaginatedResponse<ChestShopDto> getChestShopSigns(
            @DefaultValue("1") @QueryParam("page") Integer page,
            @DefaultValue("6") @QueryParam("pageSize") Integer pageSize,
            @DefaultValue("") @QueryParam("material") String material,
            @QueryParam("server") Server server,
            @DefaultValue("buy") @QueryParam("tradeType") TradeType tradeType,
            @DefaultValue("false") @QueryParam("hideUnavailable") Boolean hideUnavailable,
            @DefaultValue("best-price") @QueryParam("sortBy") SortBy sortBy,
            @DefaultValue("false") @QueryParam("distinct") Boolean distinct) {
        LOGGER.info("GET /chest-shops");

        if (page < 1) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE);
        if (pageSize > 100 || pageSize < 1) throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_PAGE_SIZE);

        long totalResults = ChestShop.count(material, tradeType, server, hideUnavailable, sortBy, distinct);
        List<ChestShopDto> results = ChestShop.find(material, tradeType, server, hideUnavailable, sortBy, distinct, page - 1, pageSize)
                .stream().map(ChestShopMapper::toChestShopDto).collect(Collectors.toList());

        return new PaginatedResponse<>(page, Pagination.getNumPages(pageSize, totalResults), totalResults, shuffle(results, tradeType, sortBy));
    }

    @GET
    @Path("material-names")
    public List<PanacheEntityBase> getChestShopSignMaterialNames(
            @QueryParam("server") Server server,
            @DefaultValue("buy") @QueryParam("tradeType") TradeType tradeType) {
        LOGGER.info("GET /chest-shops/material-names");

        return ChestShop.findDistinctMaterialNames(tradeType, server);
    }

    @POST
    @Transactional
    public String createChestShopSigns(List<ShopEvent> shopEvents, @HeaderParam("Authorization") String authHeader) {
        apiKeyValidator.validateAPIKey(authHeader);
        return chestShopService.createChestShopSigns(shopEvents);
    }

    private List<ChestShopDto> shuffle(List<ChestShopDto> dtos, TradeType tradeType, SortBy sortBy) {
        if (sortBy != SortBy.BEST_PRICE) return dtos;
        List<ChestShopDto> results = new ArrayList<>();

        HashMap<Double, List<ChestShopDto>> priceMap = new HashMap<>();

        for (ChestShopDto dto : dtos) {
            Double price = tradeType == TradeType.BUY ? dto.getBuyPriceEach() : dto.getSellPriceEach();

            List<ChestShopDto> samePrices = priceMap.get(price);
            if (samePrices == null) {
                samePrices = new ArrayList<>();
                samePrices.add(dto);
                priceMap.put(price, samePrices);
            } else {
                samePrices.add(dto);
            }
        }

        for (List<ChestShopDto> samePrices : priceMap.values()) {
            Collections.shuffle(samePrices);
            results.addAll(samePrices);
        }

        return results.stream().sorted((a, b) -> {
            if (tradeType == TradeType.BUY) {
                return Double.compare(a.getBuyPriceEach(), b.getBuyPriceEach());
            } else {
                return Double.compare(b.getSellPriceEach(), a.getSellPriceEach());
            }
        }).collect(Collectors.toList());
    }
}
