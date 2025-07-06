/**
 *    Copyright 2023 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.mcpserver.client.external;

import java.util.Map;

import org.springframework.ai.tool.annotation.ToolParam;

/**
 *
 * @author sven
 */
public interface TmdbClient {
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

    Response loadMovies(Request request);

    record Request(@ToolParam(description = "The movie title") String query) {
    }

    record Response(Integer page, Integer total_pages, Integer total_results, Movie[] results) { }

    record Movie(boolean adult, String backdrop_path, Integer[] genere_ids, Long id, String original_language, 
      String original_title, String overview, Double popularity, String poster_path, String release_date, String title, 
      boolean video, Double vote_average, Integer vote_count) { }    
}
