// 代码生成时间: 2025-09-16 18:28:01
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class TestDataGenerator extends AbstractVerticle {

    // Configuration keys
    private static final String CONFIG_KEY_PORT = "port";
    private static final String CONFIG_KEY_DATA_SIZE = "dataSize";

    private int port;
    private int dataSize;
    private Vertx vertx;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // Read configuration from the deployment options
        JsonObject config = config();
        port = config.getInteger(CONFIG_KEY_PORT, 8080);
        dataSize = config.getInteger(CONFIG_KEY_DATA_SIZE, 1000);

        // Start the test data generation service
        vertx.createHttpServer()
            .requestHandler(req -> {
                req.response()
                    .putHeader("content-type", "application/json")
                    .end(JsonArray.generateTestArray(dataSize).encode());
            })
            .listen(port, result -> {
                if (result.succeeded()) {
                    // Start promise is completed when the server is listening
                    startPromise.complete();
                } else {
                    // Fail the start promise in case of error
                    startPromise.fail(result.cause());
                }
            });
    }

    // Method to generate test data array of a given size
    private static JsonArray generateTestArray(int size) {
        JsonArray array = new JsonArray();
        for (int i = 0; i < size; i++) {
            // Generate a simple JSON object with a unique ID and random data
            JsonObject obj = new JsonObject()
                .put("id", i)
                .put("data", "sampleData" + i);
            array.add(obj);
        }
        return array;
    }

    // Main method to deploy the verticle
    public static void main(String[] args) {
        // Create a Vertx instance
        Vertx vertx = Vertx.vertx();

        // Deploy the TestDataGenerator verticle
        vertx.deployVerticle(new TestDataGenerator(), res -> {
            if (res.succeeded()) {
                // Deployment was successful
                System.out.println("TestDataGenerator deployed successfully");
            } else {
                // Deployment failed
                System.err.println("Failed to deploy TestDataGenerator: " + res.cause());
            }
        });
    }
}
