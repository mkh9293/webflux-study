package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoviesInfoService {

    private MovieInfoRepository movieInfoRepository;

    public MoviesInfoService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return this.movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {
        return this.movieInfoRepository.findById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(String id, MovieInfo movieInfo) {
        return this.movieInfoRepository.findById(id)
                .flatMap(movie -> {
                    movie.setCast(movieInfo.getCast());
                    movie.setName(movieInfo.getName());
                    movie.setRelease_date(movieInfo.getRelease_date());
                    movie.setYear(movieInfo.getYear());
                    return this.movieInfoRepository.save(movie);
                });
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return this.movieInfoRepository.deleteById(id);
    }
}
