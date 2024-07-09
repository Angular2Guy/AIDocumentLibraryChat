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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.aidoclibchat.domain.model.dto.GithubSource;
import ch.xxx.aidoclibchat.usecase.service.CodeGenerationService;

@RestController
@RequestMapping("rest/code-generation")
public class CodeGenerationController {
	private final CodeGenerationService codeGenerationService;
	
	public CodeGenerationController(CodeGenerationService codeGenerationService) {
		this.codeGenerationService = codeGenerationService;
	}
	
	@GetMapping("/test")
	public GithubSource getGenerateTests(@RequestParam("url") String url) {		
		return this.codeGenerationService.generateTests(URLDecoder.decode(url, StandardCharsets.UTF_8), true);
	}
}
