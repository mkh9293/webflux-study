package com.learnreactivespring.controller;

import com.learnreactivespring.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemClientController {

    WebClient webClient = WebClient.create("http://localhost:8080");

    @GetMapping("/client/retrieve")
    public Flux<Item> getAllItemsUsingRetrieve() {
        return webClient.get()
                .uri("/v1/items")
                .retrieve()
                .bodyToFlux(Item.class)
                .log("Items in Client Project retrieve");
    }

    @GetMapping("/client/exchange")
    public Flux<Item> getAllItemsUsingExchange() {
        return webClient.get()
                .uri("/v1/items")
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log("Items in Client Project exchange : ");
    }

    @GetMapping("/client/retrieve/singleItem")
    public Mono<Item> getOneItemUsingRetrieve() {

        String id = "ABC";

        return webClient.get()
                .uri("/v1/items/{id}", id)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Items in Client Project retrieve single item : ");
    }

    @GetMapping("/client/exchange/singleItem")
    public Mono<Item> getOneItemUsingExchange() {

        String id = "ABC";

        return webClient.get()
                .uri("/v1/items/{id}", id)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("Items in Client Project exchange single item : ");
    }

    @PostMapping("/client/createItem")
    public Mono<Item> createItem(@RequestBody Item item) {

        return webClient.post()
                .uri("/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Created item is : ");
    }

    @PutMapping("/client/updateItem/{id}")
    public Mono<Item> createItem(@PathVariable String id, @RequestBody Item item) {

        return webClient.put()
                .uri("/v1/items/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Updated item is : ");
    }

    @DeleteMapping("/client/deleteItem/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        return webClient.delete()
                .uri("/v1/items/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .log("Delete item is : ");
    }

    @GetMapping("/client/retrieve/error")
    public Flux<Item> errorRetrieve() {
        return webClient.get().uri("/v1/items/runtimeException")
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    Mono<String> errorMono = clientResponse.bodyToMono(String.class);
                    return errorMono.flatMap(message -> {
                        log.error("The error message is : " + message);
                        throw new RuntimeException(message);
                    });
                }).bodyToFlux(Item.class);
    }

    @GetMapping("/client/exchange/error")
    public Flux<Item> errorExchange() {
        return webClient.get().uri("/v1/items/runtimeException")
                .exchangeToFlux(clientResponse -> {
                    if(clientResponse.statusCode().is5xxServerError()) {
                        return clientResponse.bodyToFlux(String.class)
                                .flatMap(errorMessage -> {
                                    log.error("Error Message in errorExchange : " + errorMessage);
                                    return Flux.error(new RuntimeException(errorMessage));
                                });
                    } else {
                        return clientResponse.bodyToFlux(Item.class);
                    }
                });
    }
}
