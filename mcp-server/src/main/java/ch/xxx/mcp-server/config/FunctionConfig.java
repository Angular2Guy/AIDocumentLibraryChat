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
package ch.xxx.aidoclibchat.adapter.config;

import java.util.function.Function;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.xxx.aidoclibchat.domain.client.OpenLibraryClient;
import ch.xxx.aidoclibchat.domain.client.TmdbClient;

@Configuration
public class FunctionConfig {
	private final OpenLibraryClient openLibraryClient;
	private final TmdbClient tmdbClient;
	public static final String OPEN_LIBRARY_CLIENT = "openLibraryClient";
	public static final String THE_MOVIE_DATABASE_CLIENT = "theMovieDatabaseClient";
	
	public FunctionConfig(OpenLibraryClient openLibraryClient, TmdbClient tmdbClient) {
		this.openLibraryClient = openLibraryClient;
		this.tmdbClient = tmdbClient;
	}
	
	@Bean(OPEN_LIBRARY_CLIENT)
	@Tool(description = "Search for books by author, title or subject.")
	public Function<OpenLibraryClient.Request, OpenLibraryClient.Response> openLibraryClient() {		
		return this.openLibraryClient::apply;
	}

	@Bean(THE_MOVIE_DATABASE_CLIENT)
	@Tool(description = "Search for movies by title.")
	public Function<TmdbClient.Request, TmdbClient.Response> theMovieDatabaseClient() {
		return this.tmdbClient::apply;
	}
}
