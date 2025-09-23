// 代码生成时间: 2025-09-23 12:08:14
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.auth.authorization.PermissionBasedAuthorization;
import io.vertx.ext.auth.authorization.RoleBasedAuthorization;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserPermissionService is a verticle that provides a user permission management system.
 */
public class UserPermissionService extends AbstractVerticle implements UserPermissionServiceAPI {

  private final AuthorizationProvider authProvider;

  public UserPermissionService(AuthorizationProvider authProvider) {
    this.authProvider = authProvider;
  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    super.start();

    // Bind the service to the event bus
    ServiceBinder binder = new ServiceBinder(vertx);
    binder.setAddress(UserPermissionServiceAPI.SERVICE_ADDRESS)
      .register(UserPermissionServiceAPI.class, this);

    startFuture.complete();
  }

  @Override
  public void addUserWithPermissions(String userId, JsonArray permissions, Handler<AsyncResult<Void>> resultHandler) {
    try {
      // Create a new user with the specified permissions
      JsonObject newUser = new JsonObject().put("userId", userId);
      PermissionBasedAuthorization pba = PermissionBasedAuthorization.create(permissions.getList());
      authProvider.addAuthorization(newUser, pba);

      // Save the user to the auth provider
      authProvider.authenticate(newUser, res -> {
        if (res.succeeded()) {
          User user = res.result();
          resultHandler.handle(Future.succeededFuture());
        } else {
          resultHandler.handle(Future.failedFuture(res.cause()));
        }
      });
    } catch (Exception e) {
      // Handle any exceptions that occur during the addition of a user
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  @Override
  public void checkUserPermissions(String userId, String requiredPermission, Handler<AsyncResult<Boolean>> resultHandler) {
    authProvider.getUser(userId, res -> {
      if (res.succeeded()) {
        User user = res.result();
        boolean hasPermission = user.authorizations().stream()
          .filter(auth -> auth instanceof PermissionBasedAuthorization)
          .map(auth -> ((PermissionBasedAuthorization) auth).check(requiredPermission))
          .anyMatch(permission -> permission);
        resultHandler.handle(Future.succeededFuture(hasPermission));
      } else {
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
  }
}

interface UserPermissionServiceAPI {
  String SERVICE_ADDRESS = "user.permissions.service";

  void addUserWithPermissions(String userId, JsonArray permissions, Handler<AsyncResult<Void>> resultHandler);

  void checkUserPermissions(String userId, String requiredPermission, Handler<AsyncResult<Boolean>> resultHandler);
}
