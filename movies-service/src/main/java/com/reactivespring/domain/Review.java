package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private String reviewId;

    @NotNull(message = "Review.movieInfoId must be not null")
    private Long movieInfoId;
    private String comment;

    @Min(value = 0L, message = "Review.negative : pleast pass a non-negative value")
    private Double rating;
}
