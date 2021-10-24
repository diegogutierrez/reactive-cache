package com.test.reactive.cache;

import reactor.test.StepVerifier;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReactiveGuavaCacheTest {

  @Test
  void test() {
    ReactiveGuavaCache cache = new ReactiveGuavaCache();
    System.out.println("request1");
    StepVerifier.create(cache.get("1")).assertNext(s -> assertEquals("1-value", s)).verifyComplete();

    System.out.println("request2");
    StepVerifier.create(cache.get("1")).assertNext(s -> assertEquals("1-value", s)).verifyComplete();
  }

  @Test
  void testMulitpleThreads() throws InterruptedException {
    ReactiveGuavaCache cache = new ReactiveGuavaCache();

    ExecutorService executorService = Executors.newFixedThreadPool(50);
    IntStream.range(1, 51).forEach(i -> executorService.submit(() -> {
      cache.get("1").block();
      System.out.println("request-" + i + " done");
    }));

    executorService.shutdown();
    executorService.awaitTermination(30, TimeUnit.SECONDS);
  }
}