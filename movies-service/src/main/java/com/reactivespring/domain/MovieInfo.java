package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieInfo {

    private String movieInfoId;
    @NotBlank(message = "MovieInfo.name must be present")
    private String name;
    @NotNull
    @Positive(message = "MovieInfo.year must be positive value")
    private Integer year;
    @NotEmpty(message = "MovieInfo.cast must be not null")
    private List<@NotBlank(message = "MovieInfo.cast must be present") String> cast;
    private LocalDate release_date;

}
