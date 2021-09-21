package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

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

    @Test
    public void fluxTestElementes_WithoutError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring").log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .verifyComplete();
    }

    @Test
    public void fluxTestElementes_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
//                .expectError(RuntimeException.class)
                .expectErrorMessage("Exception occured")
                .verify();
    }

    @Test
    public void fluxTestElementesCount_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception occured")
                .verify();
    }

    @Test
    public void fluxTestElementes_WithError1() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectErrorMessage("Exception occured")
                .verify();
    }
}
