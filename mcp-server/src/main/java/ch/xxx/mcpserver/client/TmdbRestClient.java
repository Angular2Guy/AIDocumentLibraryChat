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
package ch.xxx.mcpserver.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.xxx.mcpserver.client.external.TmdbClient;

@Component
public class TmdbRestClient implements TmdbClient {
    private static final Logger LOG = LoggerFactory.getLogger(TmdbRestClient.class);
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private final RestClient restClient;
    @Value("${tmdb.api.key:}")
    private String apiKey;

    public TmdbRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    //@EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        var request = new Request("Alien");
        var response = this.loadMovies(request);
        LOG.info("TMDB Response: {}", toJson(response));
    }

    @Override
    public Response loadMovies(Request request) {
        var url = BASE_URL + "search/movie?query=" + request.query();
        var response = restClient.get()
                .uri(url)
                .header("accept", "application/json")
                .header("Authorization", "Bearer "+this.apiKey)
                .retrieve()
                .body(Response.class);        
        //LOG.info("TMDB Response: {}", toJson(response));
        return response;
    }

    private static String toJson(Object obj) {
        var result = "";
        try {
            result = new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOG.error("Error converting object to JSON", e);            
        }
        return result;
    }

}