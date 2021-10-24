package com.test.reactive.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ReactiveGuavaCache {

  private final Cache<String, String> cache;

  public ReactiveGuavaCache() {
    this.cache = CacheBuilder.newBuilder().maximumSize(10000)
        .expireAfterWrite(1, TimeUnit.DAYS)
        .build();
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
            k -> Mono.justOrEmpty(cache.getIfPresent(key)).map(Signal::next),
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
