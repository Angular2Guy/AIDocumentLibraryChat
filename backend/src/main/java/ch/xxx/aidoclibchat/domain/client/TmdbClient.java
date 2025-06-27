/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package ch.xxx.aidoclibchat.domain.client;

import java.util.Map;
import java.util.function.Function;

import org.springframework.ai.tool.annotation.ToolParam;

/**
 *
 * @author sven
 */
public interface TmdbClient extends Function<TmdbClient.Request, TmdbClient.Response> {
    public static final Map<Integer, String> GENRE_MAP = Map.ofEntries(
        Map.entry(28, "Action"),
        Map.entry(12, "Adventure"),
        Map.entry(16, "Animation"),
        Map.entry(35, "Comedy"),
        Map.entry(80, "Crime"),
        Map.entry(99, "Documentary"),
        Map.entry(18, "Drama"),
        Map.entry(10751, "Family"),
        Map.entry(14, "Fantasy"),
        Map.entry(36, "History"),
        Map.entry(27, "Horror"),
        Map.entry(10402, "Music"),
        Map.entry(9648, "Mystery"),
        Map.entry(10749, "Romance"),
        Map.entry(878, "Science Fiction"),
        Map.entry(10770, "TV Movie"),
        Map.entry(53, "Thriller"),
        Map.entry(10752, "War"),
        Map.entry(37, "Western")
    );

    record Request(@ToolParam(description = "The movie title") String query) {
    }

    record Response(Integer page, Integer total_pages, Integer total_results, Movie[] results) { }

    record Movie(boolean adult, String backdrop_path, Integer[] genere_ids, Long id, String original_language, 
      String original_title, String overview, Double popularity, String poster_path, String release_date, String title, 
      boolean video, Double vote_average, Integer vote_count) { }    
}
