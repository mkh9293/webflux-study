package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.Valid;

@RestController
@RequestMapping("v1")
@Slf4j
public class MovieInfosController {

    private final MoviesInfoService moviesInfoService;

//    private Sinks.Many<MovieInfo> moviesInfoSink = Sinks.many().replay().all();
    private final Sinks.Many<MovieInfo> moviesInfoSink = Sinks.many().replay().latest();

    public MovieInfosController(MoviesInfoService moviesInfoService) {
        this.moviesInfoService = moviesInfoService;
    }

    @GetMapping("movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String id) {
        return moviesInfoService.getMovieInfoById(id)
                .map(response -> ResponseEntity.ok().body(response))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @GetMapping("movieinfos")
    public Flux<MovieInfo> getAllMovieInfos(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "name", required = false) String name
    ) {
        log.info("Year is : {}, Name is : {} ", year, name);

        if(year != null) {
            return moviesInfoService.getMovieInfoByYear(year);
        } else if (name != null) {
            return moviesInfoService.getMovieInfoByName(name);
        }

        return moviesInfoService.getAllMovieInfos().log();
    }

    @GetMapping(value = "movieinfos/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> getMovies() {
        return moviesInfoSink.asFlux().log();
    }

    @PostMapping("movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return moviesInfoService.addMovieInfo(movieInfo)
                .doOnNext(savedMovie -> moviesInfoSink.tryEmitNext(savedMovie))
                .log();
    }

    @PutMapping("movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable String id, @RequestBody MovieInfo movieInfo) {
        return moviesInfoService.updateMovieInfo(id, movieInfo)
                .map(response -> ResponseEntity.ok().body(response))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @DeleteMapping("movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id) {
        return moviesInfoService.deleteMovieInfo(id).log();
    }
}
