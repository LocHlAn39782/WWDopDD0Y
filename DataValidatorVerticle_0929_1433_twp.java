// 代码生成时间: 2025-09-29 14:33:54
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

import java.util.regex.Pattern;

// DataValidatorVerticle 是一个 Vert.x 组件，用于验证 JSON 数据格式
public class DataValidatorVerticle extends AbstractVerticle {

    // 启动方法
    @Override
    public void start(Future<Void> startFuture) {
        try {
            // 绑定服务代理到一个事件总线上的地址
            new ServiceBinder(vertx)
                    .setAddress("dataValidatorService")
                    .register(DataValidator.class, new DataValidatorImpl());

            startFuture.complete();
        } catch (Exception e) {
            // 错误处理
            startFuture.fail(e);
        }
    }
}

// DataValidator 是一个服务接口，定义了数据验证器的契约
interface DataValidator {
    // 验证 JSON 数据的方法
    void validate(JsonObject data, Handler<AsyncResult<Boolean>> resultHandler);
}

// DataValidatorImpl 是 DataValidator 接口的实现类
class DataValidatorImpl implements DataValidator {

    // 正则表达式模式，用于验证邮箱格式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$");

    // 实现接口中的方法，对传入的 JSON 数据进行验证
    @Override
    public void validate(JsonObject data, Handler<AsyncResult<Boolean>> resultHandler) {
        // 检查 JSON 对象是否包含必要的字段
        if (data == null || data.isEmpty()) {
            resultHandler.handle(Future.failedFuture("Invalid data provided"));
            return;
        }

        // 验证邮箱字段是否存在且格式正确
        String email = data.getString("email");
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            resultHandler.handle(Future.failedFuture("Invalid email format"));
            return;
        }

        // 验证其他必要的字段，例如年龄
        int age = data.getInteger("age", -1);
        if (age <= 0) {
            resultHandler.handle(Future.failedFuture("Invalid age"));
            return;
        }

        // 如果所有验证通过，则返回 true
        resultHandler.handle(Future.succeededFuture(true));
    }
}
