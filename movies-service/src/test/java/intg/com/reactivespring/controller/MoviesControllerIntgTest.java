package com.reactivespring.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(
        properties = {
                "restClient.moviesInfoUrl=http://localhost:8084/v1/movieinfos",
                "restClient.reviewsUrl=http://localhost:8084/v1/reviews",
        }
)
public class MoviesControllerIntgTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void retrieveMovieById() {
        var movieId = "abc";

        stubFor(
                get(urlEqualTo("/v1/movieinfos" + "/" + movieId))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\n" +
                                        "  \"movieInfoId\": \"1\",\n" +
                                        "  \"name\": \"Batman Begins\",\n" +
                                        "  \"year\": 2005,\n" +
                                        "  \"cast\": [\n" +
                                        "    \"Christian Bale\",\n" +
                                        "    \"Michael Cane\"\n" +
                                        "  ],\n" +
                                        "  \"release_date\": \"2005-06-15\"\n" +
                                        "}\n")
                        )
        );

        stubFor(
                get(urlPathEqualTo("/v1/reviews"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("[\n" +
                                        "  {\n" +
                                        "    \"reviewId\": \"1\",\n" +
                                        "    \"movieInfoId\": 1,\n" +
                                        "    \"comment\": \"Awesome Movie\",\n" +
                                        "    \"rating\": 9.0\n" +
                                        "  },\n" +
                                        "  {\n" +
                                        "    \"reviewId\": \"2\",\n" +
                                        "    \"movieInfoId\": 1,\n" +
                                        "    \"comment\": \"Excellent Movie\",\n" +
                                        "    \"rating\": 8.0\n" +
                                        "  }\n" +
                                        "]\n")
                        )
        );

        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var movie = movieEntityExchangeResult.getResponseBody();

                    assert Objects.requireNonNull(movie).getReviewList().size() == 2;
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                });


    }

    @Test
    void retrieveMovieById_404() {
        var movieId = "abc";

        stubFor(
                get(urlEqualTo("/v1/movieinfos" + "/" + movieId))
                        .willReturn(aResponse().withStatus(404))
        );

        stubFor(
                get(urlPathEqualTo("/v1/reviews"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("[\n" +
                                        "  {\n" +
                                        "    \"reviewId\": \"1\",\n" +
                                        "    \"movieInfoId\": 1,\n" +
                                        "    \"comment\": \"Awesome Movie\",\n" +
                                        "    \"rating\": 9.0\n" +
                                        "  },\n" +
                                        "  {\n" +
                                        "    \"reviewId\": \"2\",\n" +
                                        "    \"movieInfoId\": 1,\n" +
                                        "    \"comment\": \"Excellent Movie\",\n" +
                                        "    \"rating\": 8.0\n" +
                                        "  }\n" +
                                        "]\n")
                        )
        );

        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .isEqualTo("There is no Movie avaliable for the passed in Id : abc");

        WireMock.verify(1, getRequestedFor(urlEqualTo("/v1/movieinfos" + "/" + movieId)));
    }

    @Test
    void retrieveMovieById_review_404() {
        var movieId = "abc";

        stubFor(
                get(urlEqualTo("/v1/movieinfos" + "/" + movieId))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\n" +
                                        "  \"movieInfoId\": \"1\",\n" +
                                        "  \"name\": \"Batman Begins\",\n" +
                                        "  \"year\": 2005,\n" +
                                        "  \"cast\": [\n" +
                                        "    \"Christian Bale\",\n" +
                                        "    \"Michael Cane\"\n" +
                                        "  ],\n" +
                                        "  \"release_date\": \"2005-06-15\"\n" +
                                        "}\n")
                        )
        );

        stubFor(
                get(urlPathEqualTo("/v1/reviews"))
                        .willReturn(aResponse().withStatus(404))
        );

        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var movie = movieEntityExchangeResult.getResponseBody();

                    assert Objects.requireNonNull(movie).getReviewList().size() == 0;
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                });
    }

    @Test
    void retrieveMovieById_5xx() {
        var movieId = "abc";

        stubFor(
                get(urlEqualTo("/v1/movieinfos" + "/" + movieId))
                        .willReturn(
                                aResponse()
                                        .withStatus(500)
                                        .withBody("MovieInfo Service Unavaliable")
                        )
        );

        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server Exception in MoviesInfoService MovieInfo Service Unavaliable");

        WireMock.verify(4, getRequestedFor(urlEqualTo("/v1/movieinfos" + "/" + movieId)));
    }

    @Test
    void retrieveMovieById_reviews_5xx() {
        var movieId = "abc";

        stubFor(
                get(urlEqualTo("/v1/movieinfos" + "/" + movieId))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\n" +
                                        "  \"movieInfoId\": \"1\",\n" +
                                        "  \"name\": \"Batman Begins\",\n" +
                                        "  \"year\": 2005,\n" +
                                        "  \"cast\": [\n" +
                                        "    \"Christian Bale\",\n" +
                                        "    \"Michael Cane\"\n" +
                                        "  ],\n" +
                                        "  \"release_date\": \"2005-06-15\"\n" +
                                        "}\n")
                        )
        );

        stubFor(
                get(urlPathEqualTo("/v1/reviews"))
                        .willReturn(aResponse().withStatus(500).withBody("Review Service Not Avaliable"))
        );

        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server Exception in ReviewsService Review Service Not Avaliable");

        WireMock.verify(4, getRequestedFor(urlPathEqualTo("/v1/reviews")));
    }
}
