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
package ch.xxx.aidoclibchat.adapter.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import ch.xxx.aidoclibchat.domain.client.TmdbClient;

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

    @Override
    public Response apply(Request request) {
        var url = BASE_URL + "search/movie?query=" + request.query();
        var response = restClient.get()
                .uri(url)
                .header("accept", "application/json")
                .header("Authorization", "Bearer "+this.apiKey)
                .retrieve()
                .body(String.class);
        LOG.info("Response from TMDB: {}", response);
        return new Response(response);
    }

}