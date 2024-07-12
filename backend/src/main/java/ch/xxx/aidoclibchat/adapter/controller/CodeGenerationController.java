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
package ch.xxx.aidoclibchat.adapter.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.aidoclibchat.domain.model.dto.GithubSource;
import ch.xxx.aidoclibchat.domain.model.dto.GithubSources;
import ch.xxx.aidoclibchat.usecase.service.CodeGenerationService;

@RestController
@RequestMapping("rest/code-generation")
public class CodeGenerationController {
	private final CodeGenerationService codeGenerationService;

	public CodeGenerationController(CodeGenerationService codeGenerationService) {
		this.codeGenerationService = codeGenerationService;
	}

	// http://localhost:8080/rest/code-generation/test?url=https://github.com/Angular2Guy/MovieManager/blob/master/backend/src/main/java/ch/xxx/moviemanager/adapter/controller/ActorController.java&testUrl=https://github.com/Angular2Guy/MovieManager/blob/master/backend/src/test/java/ch/xxx/moviemanager/adapter/controller/MovieControllerTest.java
	// http://localhost:8080/rest/code-generation/test?url=https://github.com/Angular2Guy/MovieManager/blob/master/backend/src/main/java/ch/xxx/moviemanager/usecase/service/ActorService.java&testUrl=https://github.com/Angular2Guy/MovieManager/blob/master/backend/src/test/java/ch/xxx/moviemanager/usecase/service/MovieServiceTest.java
	@GetMapping("/test")
	public String getGenerateTests(@RequestParam("url") String url,
			@RequestParam(name = "testUrl", required = false) String testUrl) {
		return this.codeGenerationService.generateTest(URLDecoder.decode(url, StandardCharsets.UTF_8),
				Optional.ofNullable(testUrl).map(myValue -> URLDecoder.decode(myValue, StandardCharsets.UTF_8)));
	}

	@GetMapping("/sources")
	public GithubSources getSources(@RequestParam("url") String url, @RequestParam(name="testUrl", required = false) String testUrl) {
		var sources = this.codeGenerationService.createTestSources(URLDecoder.decode(url, StandardCharsets.UTF_8),
				true);
		var test = Optional.ofNullable(testUrl)
				.map(myTestUrl -> this.codeGenerationService
						.createTestSources(URLDecoder.decode(myTestUrl, StandardCharsets.UTF_8), false))
				.orElse(new GithubSource("none", "none", List.of(), List.of()));
		return new GithubSources(sources, test);
	}
}
