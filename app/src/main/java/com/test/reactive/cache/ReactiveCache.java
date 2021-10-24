package com.test.reactive.cache;

import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReactiveCache {

  private final Map<String, String> cache;

  public ReactiveCache() {
    this.cache = new HashMap<>();
  }

  private Mono<String> handleCacheMiss(String key) {
    return Mono.just(key)
        .map(s -> {
          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            throw new RuntimeException("error", e);
          }
          return s + "-value";
        })
        .doOnNext(s -> System.out.println("Not found value for key:" + s));
  }

  public Mono<String> get(String key) {
    return CacheMono
        .lookup(
            k -> Mono.justOrEmpty(cache.get(key)).map(Signal::next),
            key
        )
        .onCacheMissResume(this.handleCacheMiss(key))
        .andWriteWith((k, sig) -> Mono.fromRunnable(() -> {
              System.out.println("adding new value");
              cache.put(k, Objects.requireNonNull(sig.get()));
            }
        ));
  }
}
