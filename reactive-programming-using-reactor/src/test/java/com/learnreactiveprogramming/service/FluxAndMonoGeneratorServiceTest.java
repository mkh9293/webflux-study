package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {

        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(namesFlux)
//                .expectNext("alex", "ben", "chloe")
//                .expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void namesFlux_map() {
        int stringLenth = 3;

        var nameFlux = fluxAndMonoGeneratorService.namesFlux_map(stringLenth);

        StepVerifier.create(nameFlux)
//                .expectNext("ALEX", "BEN", "CHLOE")
//                .expectNext("ALEX", "CHLOE")
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();

    }

    @Test
    void namesFlux_immutablility() {

        var nameFlux = fluxAndMonoGeneratorService.namesFlux_immutablility();

        StepVerifier.create(nameFlux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMap() {
        int stringLenth = 3;

        var nameFlux = fluxAndMonoGeneratorService.namesFlux_flatMap(stringLenth);

        StepVerifier.create(nameFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMap_async() {
        int stringLenth = 3;

        var nameFlux = fluxAndMonoGeneratorService.namesFlux_flatMap_async(stringLenth);

        StepVerifier.create(nameFlux)
//                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatmap() {
        int stringLenth = 3;

        var nameFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(stringLenth);

        StepVerifier.create(nameFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesMono_flatmap() {
        int stringLenth = 3;

        var nameFlux = fluxAndMonoGeneratorService.namesMono_flatmap(stringLenth);

        StepVerifier.create(nameFlux)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesMono_flatmapMany() {
        int stringLenth = 3;

        var nameFlux = fluxAndMonoGeneratorService.namesMono_flatmapMany(stringLenth);

        StepVerifier.create(nameFlux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {
        int stringLenth = 3;

        var nameFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLenth);

        StepVerifier.create(nameFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_1() {
        int stringLenth = 6;

        var nameFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLenth);

        StepVerifier.create(nameFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchifEmpty() {
        int stringLenth = 6;

        var nameFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchifEmpty(stringLenth);

        StepVerifier.create(nameFlux)
                .expectNext("D","E","F","A","U","L","T")
                .verifyComplete();
    }

    @Test
    void explore_concat() {
        var concatFlux = fluxAndMonoGeneratorService.explore_concat();

        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }
}
