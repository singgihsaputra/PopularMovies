package net.crevion.singgih.popularmoviesapp.network;

import net.crevion.singgih.popularmoviesapp.model.Movies;
import net.crevion.singgih.popularmoviesapp.model.MoviesReview;
import net.crevion.singgih.popularmoviesapp.model.MoviesVideo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by singgih on 16/04/2016.
 */
public interface MovieApi {
    @GET("3/movie/popular")
    Call<Movies.MovieResult> popularMovies(@Query("api_key") String apiKey, @Query("page") String page);
    @GET("3/movie/top_rated")
    Call<Movies.MovieResult> ratedMovies(@Query("api_key") String apiKey, @Query("page") String page);
    @GET
    Call<MoviesVideo.VideosResult> videoMovies(@Url String url, @Query("api_key") String apiKey);
    @GET
    Call<MoviesReview.ReviewsResult> reviewMovies(@Url String url, @Query("api_key") String apiKey);

}
