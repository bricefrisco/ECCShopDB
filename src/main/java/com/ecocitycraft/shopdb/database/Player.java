package com.ecocitycraft.shopdb.database;

import com.ecocitycraft.shopdb.models.chestshops.SortBy;
import com.ecocitycraft.shopdb.models.players.PlayersQueryView;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "player")
public class Player extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(unique = true, nullable = false)
    @Size(min = 3, max = 16)
    public String name;

    @OneToMany(mappedBy = "owner")
    public List<ChestShop> chestShops;

    @ManyToMany
    @JoinTable(name = "region_mayors", joinColumns = @JoinColumn(name = "mayors_id"), inverseJoinColumns = @JoinColumn(name = "towns_id"))
    public List<Region> towns;

    public static Long id(String name) {
        name = name.toLowerCase(Locale.ROOT);

        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT p.id FROM player p " +
                        "WHERE p.name = ?1"
        );

        q.setParameter(1, name);

        return ((BigInteger) q.getSingleResult()).longValue();
    }

    public static Long count(String name) {
        name = name.toLowerCase(Locale.ROOT);

        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT COUNT(*) FROM player p " +
                        "WHERE (?1 = '' OR p.name = ?1)"
        );

        q.setParameter(1, name);

        return ((BigInteger) q.getSingleResult()).longValue();
    }

    public static List<PlayersQueryView> find(String name, SortBy sortBy, Integer page, Integer pageSize) {
        name = name.toLowerCase(Locale.ROOT);

        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT p.id, p.name, " +
                        "(SELECT COUNT(*) FROM chest_shop_sign WHERE owner_id = p.id) AS num_chest_shops, " +
                        "(SELECT COUNT(*) FROM region_mayors WHERE mayors_id = p.id) AS num_regions " +
                        "FROM player p " +
                        "WHERE (?1 = '' OR p.name = ?1) " +
                        mapSortBy(sortBy) + " " +
                        "LIMIT ?2 OFFSET ?3"
        );

        q.setParameter(1, name);
        q.setParameter(2, pageSize);
        q.setParameter(3, page * pageSize);

        List<Object[]> results = q.getResultList();
        return results.stream().map(record -> {
            PlayersQueryView view = new PlayersQueryView();
            view.setName((String) record[1]);
            view.setNumChestShops(((BigInteger) record[2]).intValue());
            view.setNumRegions(((BigInteger) record[3]).intValue());
            return view;
        }).collect(Collectors.toList());
    }

    public static Long countByRegionId(Long regionId) {
        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT COUNT(*) FROM player p " +
                        "WHERE p.id IN (SELECT mayors_id FROM region_mayors WHERE towns_id = ?1)"
        );

        q.setParameter(1, regionId);

        return ((BigInteger) q.getSingleResult()).longValue();
    }

    public static List<PlayersQueryView> findByRegionId(Long regionId, Integer page, Integer pageSize) {
        Query q = Panache.getEntityManager().createNativeQuery(
                "SELECT p.id, p.name, " +
                        "(SELECT COUNT(*) FROM chest_shop_sign WHERE owner_id = p.id) AS num_chest_shops, " +
                        "(SELECT COUNT(*) FROM region_mayors WHERE mayors_id = p.id) AS num_regions " +
                        "FROM player p " +
                        "WHERE p.id IN (SELECT mayors_id FROM region_mayors WHERE towns_id = ?1) " +
                        "ORDER BY p.name ASC " +
                        "LIMIT ?2 OFFSET ?3"
        );

        q.setParameter(1, regionId);
        q.setParameter(2, pageSize);
        q.setParameter(3, page * pageSize);

        List<Object[]> results = q.getResultList();
        return results.stream().map(record -> {
            PlayersQueryView view = new PlayersQueryView();
            view.setName((String) record[1]);
            view.setNumChestShops(((BigInteger) record[2]).intValue());
            view.setNumRegions(((BigInteger) record[3]).intValue());
            return view;
        }).collect(Collectors.toList());
    }

    public static Player findByName(String name) {
        if (name == null) return null;
        name = name.toLowerCase(Locale.ROOT);

        Optional<Player> maybePlayer = Player.find("name = ?1", name).firstResultOptional();
        return maybePlayer.orElse(null);
    }

    public static HashMap<String, Player> getOrAddPlayers(Set<String> playerNames) {
        HashMap<String, Player> players = new HashMap<>();

        for (String name : playerNames) {
            Player player = findByName(name);
            if (player == null) {
                player = new Player();
                player.name = name.toLowerCase(Locale.ROOT);
                Player.persist(player);
            }
            players.put(name, player);
        }

        return players;
    }

    public static List<PanacheEntityBase> findPlayerNames() {
        return Player.find("SELECT name FROM Player WHERE (?1 = true) ORDER BY name", true).list();
    }

    private static String mapSortBy(SortBy sortBy) {
        switch (sortBy) {
            case NUM_CHEST_SHOPS:
                return "ORDER BY num_chest_shops DESC";
            case NUM_REGIONS:
                return "ORDER BY num_regions DESC";
            default:
                return "ORDER BY p.name ASC";
        }
    }

    public String getName() {
        return name.toLowerCase(Locale.ROOT);
    }

    public void setName(String name) {
        this.name = name.toLowerCase(Locale.ROOT);
    }
}
