package com.wrpinheiro.deadcodedetection.controller;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

/**
 * Jersey configuration.
 *
 * @author wrpinheiro
 */
@Component
@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {

    /**
     * Registers the controller with Jerset and configure Swagger.
     */
    public JerseyConfig() {
        register(RepositoryController.class);

        this.configureSwagger();
    }

    /**
     * Configure swagger.
     */
    private void configureSwagger() {
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig config = new BeanConfig();
        config.setConfigId("dead-code-detection-service");
        config.setTitle("Dead Code Detection Service");
        config.setVersion("v1");
        config.setContact("Wellington Pinheiro");
        config.setSchemes(new String[] { "http", "https" });
        config.setBasePath("/api");
        config.setResourcePackage("com.wrpinheiro.deadcodedetection.controller");
        config.setPrettyPrint(true);
        config.setScan(true);
    }
}
