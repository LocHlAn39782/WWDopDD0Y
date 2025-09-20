// 代码生成时间: 2025-09-20 23:19:02
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

// 哈希值计算工具服务接口
public interface HashValueCalculatorService {
    String address = "hashValueCalculatorService";
    void calculateHash(String data, Handler<AsyncResult<String>> resultHandler);
}

// 哈希值计算工具服务实现
public class HashValueCalculatorServiceImpl implements HashValueCalculatorService {

    @Override
    public void calculateHash(String data, Handler<AsyncResult<String>> resultHandler) {
        try {
            // 使用SHA-256算法计算哈希值
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            // 将哈希值转换为Base64编码字符串
            String encodedHash = Base64.getEncoder().encodeToString(hash);
            resultHandler.handle(Future.succeededFuture(encodedHash));
        } catch (NoSuchAlgorithmException e) {
            resultHandler.handle(Future.failedFuture("Error calculating hash: " + e.getMessage()));
        }
    }
}

// Verticle 类，用于启动和部署服务
public class HashValueCalculatorVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        // 创建服务代理构建器
        ServiceProxyBuilder builder = new ServiceProxyBuilder(vertx);

        // 部署哈希值计算工具服务
        builder.setAddress(HashValueCalculatorService.address).build(HashValueCalculatorService.class, res -> {
            if (res.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(res.cause());
            }
        });
    }
}
