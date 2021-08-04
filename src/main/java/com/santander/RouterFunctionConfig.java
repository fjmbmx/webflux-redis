package com.santander;

import com.santander.handler.UserHandler;
import com.santander.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

    @Autowired
    private  UserService userService;

    @Bean
    public RouterFunction<ServerResponse> routes(UserHandler userHandler) {
        return route(GET("/api/v2/users").or(GET("/api/v3/users")), userHandler::listar)
                .andRoute(GET("/api/v2/users/{id}"),userHandler::ver)
                .andRoute(POST("/api/v2/users"),userHandler::crear)
                .andRoute(PUT("/api/v2/users/{id}"),userHandler::actualizar)
                .andRoute(DELETE("/api/v2/users/{id}"),userHandler::eliminar)
                .andRoute(POST("/api/v2/users/upload/{id}"),userHandler::upload)
                .andRoute(POST("/api/v2/users/upload"),userHandler::crearConFoto);

    }
}
