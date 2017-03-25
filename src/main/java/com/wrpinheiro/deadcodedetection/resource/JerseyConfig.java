package com.wrpinheiro.deadcodedetection.resource;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

/**
 * Created by wrpinheiro on 3/22/17.
 */
@Component
@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(RepositoryResource.class);

        this.configureSwagger();
    }

    private void configureSwagger() {
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig config = new BeanConfig();
        config.setConfigId("dead-code-detection-resource");
        config.setTitle("Dead Code Detection Service");
        config.setVersion("v1");
        config.setContact("Wellington Pinheiro");
        config.setSchemes(new String[] { "http", "https" });
        config.setBasePath("/api");
        config.setResourcePackage("com.wrpinheiro.deadcodedetection.resource");
        config.setPrettyPrint(true);
        config.setScan(true);
    }
}