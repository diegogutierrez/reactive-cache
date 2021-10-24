package com.test.reactive.cache;

import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.util.HashMap;
import java.util.Map;

public class ReactiveMapCache {

  final Map<String, Signal<? extends String>> mapStringSignalCache = new HashMap<>();

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
        .lookup(mapStringSignalCache, key)
        .onCacheMissResume(this.handleCacheMiss(key));
  }
}
