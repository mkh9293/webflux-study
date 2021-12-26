package com.reactivespring.router;

import com.reactivespring.domain.Review;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class})
@AutoConfigureWebTestClient
public class ReviewUnitTest {

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    String REVIEW_URL = "/v1/reviews";

    @Test
    void addReview() {
        when(reviewReactiveRepository.save(isA(Review.class))).thenReturn(Mono.just(
                new Review("abc", 1L, "Awesome Movie", 9.0)
        ));

        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        webTestClient
                .post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedReview = reviewEntityExchangeResult.getResponseBody();

                    assert savedReview != null;
                    assert savedReview.getReviewId() != null;
                });
    }

    @Test
    void addReview_validation() {
        when(reviewReactiveRepository.save(isA(Review.class))).thenReturn(Mono.just(
                new Review("abc", 1L, "Awesome Movie", 9.0)
        ));

        var review = new Review(null, null, "Awesome Movie", -9.0);

        webTestClient
                .post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest();
//                .expectBody(Review.class)
//                .consumeWith(reviewEntityExchangeResult -> {
//                    var savedReview = reviewEntityExchangeResult.getResponseBody();
//
//                    assert savedReview != null;
//                    assert savedReview.getReviewId() != null;
//                });
    }

    @Test
    void getReviews() {
        when(reviewReactiveRepository.findAll()).thenReturn(Flux.fromIterable(
                List.of(new Review("abc", 1L, "Awesome Movie", 9.0))
        ));

        webTestClient
                .get()
                .uri(REVIEW_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(1);
    }

    @Test
    void updateReviews() {
        when(reviewReactiveRepository.findById(isA(String.class))).thenReturn(Mono.just(
                new Review("abc", 1L, "Awesome Movie update", 7.0)
        ));
        when(reviewReactiveRepository.save(isA(Review.class))).thenReturn(Mono.just(
                new Review("abc", 1L, "Awesome Movie update", 7.0)
        ));

        var review = new Review("abc", 1L, "Awesome Movie update", 7.0);

        webTestClient
                .put()
                .uri(REVIEW_URL + "/{id}", "abc")
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.comment")
                .isEqualTo("Awesome Movie update");
    }

    @Test
    void deleteReviews() {
        when(reviewReactiveRepository.deleteById(isA(String.class))).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(REVIEW_URL + "/{id}", "abc")
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void findByMovieInfoId() {
        when(reviewReactiveRepository.findReviewByMovieInfoId(isA(Long.class))).thenReturn(Flux.fromIterable(
           List.of(new Review("abc", 1L, "Awesome Movie update", 7.0))
        ));

        var uri = UriComponentsBuilder.fromUriString(REVIEW_URL)
                .queryParam("movieInfoId", 1L)
                .build().toUri();

        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(1);
    }

}
