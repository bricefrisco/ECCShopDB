package com.ecocitycraft.shopdb.database;

import com.ecocitycraft.shopdb.models.chestshops.Location;
import com.ecocitycraft.shopdb.models.regions.RegionsQueryView;
import com.ecocitycraft.shopdb.models.chestshops.Server;
import com.ecocitycraft.shopdb.models.chestshops.SortBy;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Entity
@Table(name = "region")
public class Region extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public Server server;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "i_x")),
            @AttributeOverride(name = "y", column = @Column(name = "i_y")),
            @AttributeOverride(name = "z", column = @Column(name = "i_z"))
    })
    public Location iBounds;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "o_x")),
            @AttributeOverride(name = "y", column = @Column(name = "o_y")),
            @AttributeOverride(name = "z", column = @Column(name = "o_z"))
    })
    public Location oBounds;

    @OneToMany(mappedBy = "town", fetch = FetchType.LAZY)
    public List<ChestShop> chestShops;

    @ManyToMany
    @JoinTable(name = "region_mayors", joinColumns = @JoinColumn(name = "towns_id"), inverseJoinColumns = @JoinColumn(name = "mayors_id"))
    public List<Player> mayors;

    public Boolean active;

    @Column(name = "last_updated")
    public Timestamp lastUpdated;

    public static Long id(Server server, String name) {
        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT id FROM region WHERE server = ?1 AND name = ?2"
        );

        q.setParameter(1, Server.toString(server));
        q.setParameter(2, name.toLowerCase(Locale.ROOT));

        return ((BigInteger) q.getSingleResult()).longValue();
    }

    public static Long count(Server server, Boolean active, String name) {
        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT COUNT(r.id) " +
                        "FROM region r " +
                        "WHERE (?1 = '' OR r.server = ?1) AND " +
                        "(?2 = false OR r.active = true) AND " +
                        "(?3 = '' OR r.name = ?3)"
        );

        q.setParameter(1, Server.toString(server));
        q.setParameter(2, active);
        q.setParameter(3, name);

        return ((Number) q.getSingleResult()).longValue();
    }

    public static List<RegionsQueryView> find(Server server, Boolean active, String name, SortBy sortBy, Integer page, Integer pageSize) {
        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT r.id, r.active, r.i_x, r.i_y, r.i_z, r.last_updated, r.name, r.o_x, r.o_y, r.o_z, r.server, " +
                        "(SELECT COUNT(*) FROM chest_shop_sign WHERE town_id = r.id) AS num_chest_shops, " +
                        "(SELECT COUNT(*) FROM region_mayors WHERE towns_id = r.id) AS num_mayors " +
                        "FROM region r " +
                        "WHERE (?1 = '' OR r.server = ?1) AND " +
                        "(?2 = false OR r.active = true) AND " +
                        "(?3 = '' OR r.name = ?3) " +
                        mapSortBy(sortBy) + " " +
                        "LIMIT ?4 OFFSET ?5"
        );

        q.setParameter(1, Server.toString(server));
        q.setParameter(2, active);
        q.setParameter(3, name.toLowerCase());
        q.setParameter(4, pageSize);
        q.setParameter(5, page * pageSize);

        List<Object[]> results = q.getResultList();
        return results.stream().map(record -> {
            RegionsQueryView view = new RegionsQueryView();
            view.setId(((BigInteger) record[0]).longValue());
            view.setActive((Boolean) record[1]);
            Location location = new Location();
            location.setX(((BigInteger) record[2]).intValue());
            location.setY(((BigInteger) record[3]).intValue());
            location.setZ(((BigInteger) record[4]).intValue());
            view.setiBounds(location);
            view.setLastUpdated((Timestamp) record[5]);
            view.setName((String) record[6]);
            location = new Location();
            location.setX(((BigInteger) record[7]).intValue());
            location.setY(((BigInteger) record[8]).intValue());
            location.setZ(((BigInteger) record[9]).intValue());
            view.setoBounds(location);
            view.setServer(Server.valueOf(((String) record[10]).toUpperCase()));
            view.setNumChestShops(((BigInteger) record[11]).intValue());
            view.setNumMayors(((BigInteger) record[12]).intValue());
            return view;
        }).collect(Collectors.toList());
    }

    public static Long countByPlayerId(Long playerId) {
        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT COUNT(r.id) " +
                        "FROM region r " +
                        "WHERE r.id IN (SELECT towns_id FROM region_mayors WHERE mayors_id = ?1)"
        );

        q.setParameter(1, playerId);

        return ((Number) q.getSingleResult()).longValue();
    }

    public static List<RegionsQueryView> findByPlayerId(Long playerId, Integer page, Integer pageSize) {
        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT r.name, r.active, r.server, " +
                        "(SELECT COUNT(*) FROM chest_shop_sign WHERE town_id = r.id) AS num_chest_shops, " +
                        "(SELECT COUNT(*) FROM region_mayors WHERE towns_id = r.id) AS num_mayors " +
                        "FROM region r " +
                        "WHERE r.id IN (SELECT towns_id FROM region_mayors WHERE mayors_id = ?1) " +
                        "ORDER BY r.name ASC " +
                        "LIMIT ?2 OFFSET ?3"
        );

        q.setParameter(1, playerId);
        q.setParameter(2, pageSize);
        q.setParameter(3, page * pageSize);

        List<Object[]> results = q.getResultList();
        return results.stream().map(record -> {
            RegionsQueryView view = new RegionsQueryView();
            view.setName((String) record[0]);
            view.setActive((Boolean) record[1]);
            view.setServer(Server.valueOf(((String) record[2]).toUpperCase()));
            view.setNumChestShops(((BigInteger) record[3]).intValue());
            view.setNumMayors(((BigInteger) record[4]).intValue());
            return view;
        }).collect(Collectors.toList());
    }

    public static Region findByServerAndName(Server server, String name) {
        if (server == null || name == null) return null;
        return Region.find("server = ?1 AND name = ?2", server, name.toLowerCase(Locale.ROOT)).firstResult();
    }

    public static List<PanacheEntityBase> findRegionNames(Server server, Boolean active) {
        return Region.find("SELECT DISTINCT name FROM Region WHERE " +
                "(?1 = '' OR server = ?1) AND " +
                "(?2 = false OR active = true) " +
                "ORDER BY name", Server.toString(server), active).list();
    }

    private static String mapSortBy(SortBy sortBy) {
        switch (sortBy) {
            case NUM_CHEST_SHOPS:
                return "ORDER BY num_chest_shops DESC";
            case NUM_PLAYERS:
                return "ORDER BY num_mayors DESC";
            default:
                return "ORDER BY r.name ASC";
        }
    }

    public String getName() {
        return name.toLowerCase(Locale.ROOT);
    }

    public void setName(String name) {
        this.name = name.toLowerCase(Locale.ROOT);
    }

    public Location getiBounds() {
        if (iBounds == null) iBounds = new Location();
        return iBounds;
    }

    public Location getoBounds() {
        if (oBounds == null) oBounds = new Location();
        return oBounds;
    }
}
