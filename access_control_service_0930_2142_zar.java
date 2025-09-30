// 代码生成时间: 2025-09-30 21:42:56
package com.example.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.handler.UserPasswordAuthenticationHandler;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;

public class AccessControlService extends AbstractVerticle {

    private JWTAuth jwtAuthProvider;
    private LocalSessionStore sessionStore;

    @Override
    public void start(Future<Void> startFuture) {
        // Initialize JWT Auth Provider
        jwtAuthProvider = JWTAuth.create(vertx, new JWTAuthOptions()
            .addPubSecKeyOptions(new JWTAuthPubSecKeyOptions()
                .setPubKey("your-public-key")
                .setSecKey("your-private-key")));

        // Initialize Local Session Store
        sessionStore = LocalSessionStore.create(vertx);

        // Create a router object
        Router router = Router.router(vertx);

        // Handle Body
        router.route().handler(BodyHandler.create());

        // Session and user session handler
        router.route().handler(SessionHandler.create(sessionStore));
        router.route().handler(UserSessionHandler.create(jwtAuthProvider));

        // Protected route example
        router.route("/protected").handler(this::handleProtectedRoute);

        // Authentication route
        router.post("/login").handler(UserPasswordAuthenticationHandler.create(jwtAuthProvider));

        // Start the HTTP server and assign the router
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router);
        server.listen(config().getInteger("http.port", 8080), result -> {
            if (result.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(result.cause());
            }
        });
    }

    // Handler for protected route
    private void handleProtectedRoute(RoutingContext context) {
        // Check if the user is authorized
        User user = context.user();
        if (user == null) {
            // If not authorized, send a 401 status code
            context.response().setStatusCode(401).end("Not authorized");
        } else {
            // If authorized, send a success message
            context.response().setStatusCode(200).end("Welcome to the protected area");
        }
    }

    // Main method to deploy the verticle
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new AccessControlService());
    }
}