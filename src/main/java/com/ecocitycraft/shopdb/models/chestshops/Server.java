package com.ecocitycraft.shopdb.models.chestshops;

import com.ecocitycraft.shopdb.models.exceptions.ExceptionMessage;
import com.ecocitycraft.shopdb.models.exceptions.SDBIllegalArgumentException;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Server {
    @JsonProperty("main")
    MAIN,
    @JsonProperty("main-north")
    MAIN_NORTH,
    @JsonProperty("main-east")
    MAIN_EAST,
    @JsonProperty("main-south")
    MAIN_SOUTH;

    public static Server fromString(String s) {
        switch (s) {
            case "main":
                return MAIN;
            case "main-north":
                return MAIN_NORTH;
            case "main-east":
                return MAIN_EAST;
            case "main-south":
                return MAIN_SOUTH;
            default:
                throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_SERVER + ": " + s);
        }
    }

    public static String toString(Server server) {
        if (server == null) return "";
        switch (server) {
            case MAIN:
                return "MAIN";
            case MAIN_NORTH:
                return "MAIN_NORTH";
            case MAIN_EAST:
                return "MAIN_EAST";
            case MAIN_SOUTH:
                return "MAIN_SOUTH";
            default:
                throw new SDBIllegalArgumentException(ExceptionMessage.INVALID_SERVER + ": " + server);
        }
    }
}
