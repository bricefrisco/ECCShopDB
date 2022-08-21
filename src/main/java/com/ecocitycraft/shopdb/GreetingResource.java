package com.ecocitycraft.shopdb;

import com.ecocitycraft.shopdb.models.exceptions.SDBUnauthorizedException;
import com.ecocitycraft.shopdb.services.APIKeyValidator;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {
    @Inject
    APIKeyValidator apiKeyValidator;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@HeaderParam("Authorization") String authHeader) {
        try {
            apiKeyValidator.validateAPIKey(authHeader);
        } catch (SDBUnauthorizedException e) {
            return "Unauthorized.";
        }

        return "Authorized.";
    }
}
