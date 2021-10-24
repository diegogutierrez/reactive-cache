package com.test.reactive.cache;

import reactor.test.StepVerifier;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReactiveCacheTest {

  @Test
  void test() {
    ReactiveCache cache = new ReactiveCache();
    System.out.println("request1");
    StepVerifier.create(cache.get("1")).assertNext(s -> {
      assertEquals("1-value", s);
    }).verifyComplete();

    System.out.println("request2");
    StepVerifier.create(cache.get("1")).assertNext(s -> {
      assertEquals("1-value", s);
    }).verifyComplete();
  }

  @Test
  void testMulitpleThreads() throws InterruptedException {
    ReactiveCache cache = new ReactiveCache();

    ExecutorService executorService = Executors.newFixedThreadPool(50);
    IntStream.range(1, 51).forEach(i -> executorService.submit(() -> {
      cache.get("1").block();
      System.out.println("request-" + i + " done");
    }));

    executorService.shutdown();
    executorService.awaitTermination(30, TimeUnit.SECONDS);
  }
}