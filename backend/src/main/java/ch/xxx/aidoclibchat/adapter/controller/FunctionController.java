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

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.aidoclibchat.domain.model.dto.FunctionResult;
import ch.xxx.aidoclibchat.domain.model.dto.FunctionSearch;
import ch.xxx.aidoclibchat.usecase.service.FunctionService;

@RestController
@RequestMapping("rest/function")
public class FunctionController {
	private final FunctionService functionService;
	
	public FunctionController(FunctionService functionService) {
		this.functionService = functionService;
	}
	
	//example: http://localhost:8080/rest/function/books?question=show+books+of+author+kevin+rudd+with+title+avoidable+war
//	@GetMapping(path =  "/books", produces = "application/json")
//	public OpenLibraryClient.Response postQuestion(@RequestParam(name="question", defaultValue = "") String question) {
//		return this.functionService.functionCall(question);
//	}
	
	@PostMapping(path="/books", produces = MediaType.APPLICATION_JSON_VALUE)
	public FunctionResult postQuestion(@RequestBody FunctionSearch functionSearch) {
		return this.functionService.functionCall(functionSearch.question(), functionSearch.resultFormat());
	}
	
}
