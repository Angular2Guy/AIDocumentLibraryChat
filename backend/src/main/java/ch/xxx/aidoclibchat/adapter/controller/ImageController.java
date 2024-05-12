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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.xxx.aidoclibchat.domain.model.dto.ImageDto;
import ch.xxx.aidoclibchat.usecase.mapping.ImageMapper;
import ch.xxx.aidoclibchat.usecase.service.ImageService;

@RestController
@RequestMapping("rest/image")
public class ImageController {
	private static final Logger LOG = LoggerFactory.getLogger(ImageController.class);
	private final ImageMapper imageMapper;
	private final ImageService imageService;		
	
	public ImageController(ImageMapper imageMapper, ImageService imageService) {
		this.imageMapper = imageMapper;
		this.imageService = imageService;
	}
	
	@PostMapping("/query")
	public List<ImageDto> postImageQuery(@RequestParam("query") String query,@RequestParam("type") String type, @RequestParam("file") MultipartFile imageQuery) {		
		var result = this.imageService.queryImage(this.imageMapper.map(imageQuery, query));		
		return result;
	}
	
	@PostMapping("/import")
	public ImageDto postImportImage(@RequestParam("query") String query,@RequestParam("type") String type, @RequestParam("file") MultipartFile imageQuery) {		
		var result = this.imageService.importImage(this.imageMapper.map(imageQuery, query), this.imageMapper.map(imageQuery));		
		return result;
	}
		
}
