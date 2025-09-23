// 代码生成时间: 2025-09-24 00:03:32
package com.example.vertx.sort;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SortingService extends AbstractVerticle {

    /*
     * Deploys the verticle and initializes the sorting service.
     */
    @Override
    public void start(Future<Void> startFuture) {
        super.start(startFuture);

        // Register a HTTP endpoint to sort numbers.
        vertx.createHttpServer()
            .requestHandler(req -> {
                if (!req.uri().equals("/sort")) {
                    req.response().setStatusCode(404).end("Not Found");
                } else {
                    try {
                        List<Integer> numbers = Arrays.asList(req.params().get("numbers").split(",")
                            .stream().map(Integer::parseInt).toList());
                        List<Integer> sortedNumbers = sortNumbers(numbers);
                        req.response().setStatusCode(200).end(Arrays.toString(sortedNumbers.toArray()));
                    } catch (Exception e) {
                        // Handle any exceptions that may occur during sorting.
                        req.response().setStatusCode(400).end("Invalid input: " + e.getMessage());
                    }
                }
            })
            .listen(8080, result -> {
                if (result.succeeded()) {
                    startFuture.complete();
                } else {
                    startFuture.fail(result.cause());
                }
            });
    }

    /*
     * Sorts a list of numbers using Collections.sort method.
     *
     * @param numbers List of integers to sort.
     * @return Sorted list of integers.
     */
    private List<Integer> sortNumbers(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("Numbers list cannot be null or empty");
        }
        Collections.sort(numbers);
        return numbers;
    }
}
