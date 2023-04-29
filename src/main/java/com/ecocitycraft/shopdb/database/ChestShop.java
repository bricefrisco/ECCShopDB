package com.ecocitycraft.shopdb.database;

import com.ecocitycraft.shopdb.models.chestshops.*;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "chest_shop_sign")
public class ChestShop extends PanacheEntityBase {
    @Id
    public String id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public Server server;
    @Embedded
    public Location location;
    @Column(nullable = false)
    public String material;
    @ManyToOne(fetch = FetchType.LAZY)
    public Player owner;
    @ManyToOne(fetch = FetchType.LAZY)
    public Region town;
    public Integer quantity;
    @Column(name = "quantity_available")
    public Integer quantityAvailable;
    @Column(name = "buy_price")
    public Double buyPrice;
    @Column(name = "sell_price")
    public Double sellPrice;
    @Column(name = "buy_price_each")
    public Double buyPriceEach;
    @Column(name = "sell_price_each")
    public Double sellPriceEach;
    @Column(name = "is_full")
    public Boolean isFull;
    @Column(name = "is_hidden")
    public Boolean isHidden;
    @Column(name = "is_buy_sign")
    public Boolean isBuySign;
    @Column(name = "is_sell_sign")
    public Boolean isSellSign;

    public static Long count(String material, TradeType tradeType, Server server, Boolean hideUnavailable, SortBy sortBy, Boolean distinct) {
        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT COUNT(*) FROM (" +
                        "SELECT " + getDistinctValue(distinct, sortBy, tradeType) + " * " +
                        "FROM chest_shop_sign shop " +
                        "WHERE (?1 = '' OR shop.material = ?1) AND " +
                        "(?2 IS FALSE OR shop.is_buy_sign = true) AND " +
                        "(?2 IS TRUE OR shop.is_sell_sign = true) AND " +
                        "(?3 = '' OR shop.server = ?3) AND " +
                        "(?4 IS FALSE OR shop.is_full = false) AND " +
                        "(?5 IS FALSE OR shop.quantity_available > 0) AND " +
                        "shop.is_hidden = false " +
                        ") as dt"
        );

        q.setParameter(1, material);
        q.setParameter(2, tradeType == TradeType.BUY);
        q.setParameter(3, Server.toString(server));
        q.setParameter(4, hideUnavailable && tradeType == TradeType.SELL);
        q.setParameter(5, hideUnavailable && tradeType == TradeType.BUY);

        return ((BigInteger) q.getSingleResult()).longValue();
    }

    public static List<ChestShopsQueryView> find(String material, TradeType tradeType, Server server, Boolean hideUnavailable, SortBy sortBy, Boolean distinct, Integer page, Integer pageSize) {
        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT " + getDistinctValue(distinct, sortBy, tradeType) + " " +
                        "shop.id, shop.server, shop.x, shop.y, shop.z, shop.material, owner.name AS owner_name, " +
                        "town.name AS town_name, shop.quantity, shop.quantity_available, shop.buy_price, " +
                        "shop.sell_price, shop.buy_price_each, shop.sell_price_each, shop.is_full, shop.is_hidden, " +
                        "shop.is_buy_sign, shop.is_sell_sign " +
                        "FROM chest_shop_sign shop " +
                        "INNER JOIN player owner ON shop.owner_id = owner.id " +
                        "INNER JOIN region town ON shop.town_id = town.id " +
                        "WHERE (?1 = '' OR shop.material = ?1) AND " +
                        "(?2 IS FALSE OR shop.is_buy_sign = true) AND " +
                        "(?2 IS TRUE OR shop.is_sell_sign = true) AND " +
                        "(?3 = '' OR shop.server = ?3) AND " +
                        "(?4 IS FALSE OR shop.is_full = false) AND " +
                        "(?5 IS FALSE OR shop.quantity_available > 0) AND " +
                        "shop.is_hidden = false " +
                        getSortValue(sortBy, tradeType) + " " +
                        "LIMIT ?6 OFFSET ?7"
        );

        q.setParameter(1, material);
        q.setParameter(2, tradeType == TradeType.BUY);
        q.setParameter(3, Server.toString(server));
        q.setParameter(4, hideUnavailable && tradeType == TradeType.SELL);
        q.setParameter(5, hideUnavailable && tradeType == TradeType.BUY);
        q.setParameter(6, pageSize);
        q.setParameter(7, page * pageSize);

        List<Object[]> results = q.getResultList();

        return results.stream().map(record -> {
            ChestShopsQueryView view = new ChestShopsQueryView();
            view.setId((String) record[0]);
            view.setServer(Server.valueOf((String) record[1]));

            Location location = new Location();
            location.setX(((BigInteger) record[2]).intValue());
            location.setY(((BigInteger) record[3]).intValue());
            location.setZ(((BigInteger) record[4]).intValue());

            view.setLocation(location);
            view.setMaterial((String) record[5]);
            view.setOwnerName((String) record[6]);
            view.setTownName((String) record[7]);
            view.setQuantity(((BigInteger) record[8]).intValue());
            view.setQuantityAvailable(((Double) record[9]).intValue());
            view.setBuyPrice((Double) record[10]);
            view.setSellPrice((Double) record[11]);
            view.setBuyPriceEach((Double) record[12]);
            view.setSellPriceEach((Double) record[13]);
            view.setFull((Boolean) record[14]);
            view.setHidden((Boolean) record[15]);
            view.setBuySign((Boolean) record[16]);
            view.setSellSign((Boolean) record[17]);

            return view;
        }).collect(Collectors.toList());
    }

    public static Long countByRegionId(Long regionId, TradeType tradeType) {
        Query q = Panache.getEntityManager().createNativeQuery(
                        "SELECT COUNT(shop.id) " +
                        "FROM chest_shop_sign shop " +
                        "WHERE shop.town_id = ?1 AND " +
                        "(?2 IS FALSE OR shop.is_buy_sign = true) AND " +
                        "(?2 IS TRUE OR shop.is_sell_sign = true) AND " +
                        "shop.is_hidden = false"
        );

        q.setParameter(1, regionId);
        q.setParameter(2, tradeType == TradeType.BUY);

        return ((BigInteger) q.getSingleResult()).longValue();
    }

    public static Long countByPlayerId(Long playerId, TradeType tradeType) {
        Query q = Panache.getEntityManager().createNativeQuery(
                        "SELECT COUNT(shop.id) " +
                        "FROM chest_shop_sign shop " +
                        "WHERE shop.owner_id = ?1 AND " +
                        "(?2 IS FALSE OR shop.is_buy_sign = true) AND " +
                        "(?2 IS TRUE OR shop.is_sell_sign = true) AND " +
                        "shop.is_hidden = false"
        );

        q.setParameter(1, playerId);
        q.setParameter(2, tradeType == TradeType.BUY);

        return ((BigInteger) q.getSingleResult()).longValue();
    }

    public static List<ChestShopsQueryView> findByRegionId(Long regionId, TradeType tradeType, Integer page, Integer pageSize) {
        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT shop.material, shop.quantity, shop.buy_price, shop.sell_price, shop.is_full," +
                        "shop.quantity_available, owner.name AS owner_name, town.name AS town_name, shop.x, shop.y, shop.z " +
                        "FROM chest_shop_sign shop " +
                        "INNER JOIN player owner ON shop.owner_id = owner.id " +
                        "INNER JOIN region town ON shop.town_id = town.id " +
                        "WHERE shop.town_id = ?1 AND " +
                        "(?2 IS FALSE OR shop.is_buy_sign = true) AND " +
                        "(?2 IS TRUE OR shop.is_sell_sign = true) AND " +
                        "shop.is_hidden = false " +
                        "ORDER BY shop.material " +
                        "LIMIT ?3 OFFSET ?4"
        );

        q.setParameter(1, regionId);
        q.setParameter(2, tradeType == TradeType.BUY);
        q.setParameter(3, pageSize);
        q.setParameter(4, page * pageSize);

        List<Object[]> results = q.getResultList();
        return results.stream().map(result -> {
            ChestShopsQueryView view = new ChestShopsQueryView();
            view.setMaterial((String) result[0]);
            view.setQuantity(((BigInteger) result[1]).intValue());
            view.setBuyPrice((Double) result[2]);
            view.setSellPrice((Double) result[3]);
            view.setFull((Boolean) result[4]);
            view.setQuantityAvailable(((Double) result[5]).intValue());
            view.setOwnerName((String) result[6]);
            view.setTownName((String) result[7]);

            Location location = new Location();
            location.setX(((BigInteger) result[8]).intValue());
            location.setY(((BigInteger) result[9]).intValue());
            location.setZ(((BigInteger) result[10]).intValue());

            view.setLocation(location);

            return view;
        }).collect(Collectors.toList());
    }

    public static List<ChestShopsQueryView> findByPlayerId(Long playerId, TradeType tradeType, Integer page, Integer pageSize) {
        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT shop.material, shop.quantity, shop.buy_price, shop.sell_price, shop.is_full," +
                        "shop.quantity_available, owner.name AS owner_name, town.name AS town_name, shop.x, shop.y, shop.z " +
                        "FROM chest_shop_sign shop " +
                        "INNER JOIN player owner ON shop.owner_id = owner.id " +
                        "INNER JOIN region town ON shop.town_id = town.id " +
                        "WHERE shop.owner_id = ?1 AND " +
                        "(?2 IS FALSE OR shop.is_buy_sign = true) AND " +
                        "(?2 IS TRUE OR shop.is_sell_sign = true) AND " +
                        "shop.is_hidden = false " +
                        "LIMIT ?3 OFFSET ?4"
        );

        q.setParameter(1, playerId);
        q.setParameter(2, tradeType == TradeType.BUY);
        q.setParameter(3, pageSize);
        q.setParameter(4, page * pageSize);

        List<Object[]> results = q.getResultList();
        return results.stream().map(result -> {
            ChestShopsQueryView view = new ChestShopsQueryView();
            view.setMaterial((String) result[0]);
            view.setQuantity(((BigInteger) result[1]).intValue());
            view.setBuyPrice((Double) result[2]);
            view.setSellPrice((Double) result[3]);
            view.setFull((Boolean) result[4]);
            view.setQuantityAvailable(((Double) result[5]).intValue());
            view.setOwnerName((String) result[6]);
            view.setTownName((String) result[7]);

            Location location = new Location();
            location.setX(((BigInteger) result[8]).intValue());
            location.setY(((BigInteger) result[9]).intValue());
            location.setZ(((BigInteger) result[10]).intValue());

            view.setLocation(location);

            return view;
        }).collect(Collectors.toList());
    }

    public static List<PanacheEntityBase> findDistinctMaterialNames(TradeType tradeType, Server server) {
        return ChestShop.find("SELECT DISTINCT material FROM ChestShop " +
                        "WHERE isHidden = false AND " +
                        "(?1 = '' OR server = ?1) AND " +
                        "(?2 IS FALSE OR is_buy_sign = true) AND " +
                        "(?2 IS TRUE OR is_sell_sign = true) " +
                        "ORDER BY material",
                Server.toString(server),
                tradeType == TradeType.BUY).list();
    }

    public static List<ChestShop> findInRegion(Region region) {
        return findInLocation(region.server, region.iBounds, region.oBounds);
    }

    public static List<ChestShop> findInLocation(Server server, Location iBounds, Location oBounds) {
        int lx = iBounds.getX();
        int ly = iBounds.getY();
        int lz = iBounds.getZ();
        int ux = oBounds.getX();
        int uy = oBounds.getY();
        int uz = oBounds.getZ();
        return findInLocation(server, lx, ux, ly, uy, lz, uz);
    }

    public static List<ChestShop> findInLocation(Server server, int lx, int ux, int ly, int uy, int lz, int uz) {
        return ChestShop.find(
                "server = ?1 AND " +
                        "?2 <= x AND ?3 >= x AND " +
                        "?4 <= y AND ?5 >= y AND " +
                        "?6 <= z AND ?7 >= z",
                server, lx, ux, ly, uy, lz, uz
        ).list();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChestShop chestShop = (ChestShop) o;
        return Objects.equals(material, chestShop.material) && Objects.equals(owner, chestShop.owner) && Objects.equals(town, chestShop.town) && Objects.equals(quantity, chestShop.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, owner, town, quantity);
    }

    public Location getLocation() {
        if (location == null) location = new Location();
        return location;
    }

    private static String getDistinctValue(Boolean distinct, SortBy sortBy, TradeType tradeType) {
        if (!distinct) return "";
        return "DISTINCT ON (" + mapDistinctOn(sortBy, tradeType) + ", shop.material, shop.owner_id, shop.town_id, shop.quantity) ";
    }

    private static String getSortValue(SortBy sortBy, TradeType tradeType) {
        return "ORDER BY " + mapSortBy(sortBy, tradeType) + ", shop.material, shop.owner_id, shop.town_id, shop.quantity";
    }

    private static String mapDistinctOn(SortBy sortBy, TradeType tradeType) {
        if (sortBy == SortBy.BEST_PRICE && tradeType == TradeType.BUY) return "shop.buy_price_each";
        if (sortBy == SortBy.BEST_PRICE && tradeType == TradeType.SELL) return "shop.sell_price_each";
        if (sortBy == SortBy.QUANTITY_AVAILABLE && tradeType == TradeType.BUY) return "shop.quantity_available";
        if (sortBy == SortBy.QUANTITY_AVAILABLE && tradeType == TradeType.SELL) return "shop.quantity_available";
        if (sortBy == SortBy.QUANTITY) return "shop.quantity";
        return "shop.material";
    }

    private static String mapSortBy(SortBy sortBy, TradeType tradeType) {
        if (sortBy == SortBy.BEST_PRICE && tradeType == TradeType.BUY) return "shop.buy_price_each ASC";
        if (sortBy == SortBy.BEST_PRICE && tradeType == TradeType.SELL) return "shop.sell_price_each DESC";
        if (sortBy == SortBy.QUANTITY_AVAILABLE && tradeType == TradeType.BUY) return "shop.quantity_available DESC";
        if (sortBy == SortBy.QUANTITY_AVAILABLE && tradeType == TradeType.SELL) return "shop.quantity_available ASC";
        if (sortBy == SortBy.QUANTITY) return "shop.quantity DESC";
        return "shop.material ASC";
    }
}

