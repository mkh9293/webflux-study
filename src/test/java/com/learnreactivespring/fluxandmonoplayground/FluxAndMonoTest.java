package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxAndMonoTest {

    @Test
    public void fluxText() {
         Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                 .concatWith(Flux.error(new RuntimeException("Exception occured")))
                 .concatWith(Flux.just("After Error"))
                 .log();

         stringFlux.subscribe(
                 System.out::println,
                 (e) -> System.out.println("Exception is " + e),
                 () -> System.out.println("Completed")
         );
    }
}
