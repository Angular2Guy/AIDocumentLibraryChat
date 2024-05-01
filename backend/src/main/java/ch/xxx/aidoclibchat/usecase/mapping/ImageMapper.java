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
package ch.xxx.aidoclibchat.usecase.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ch.xxx.aidoclibchat.domain.common.MetaData.ImageType;
import ch.xxx.aidoclibchat.domain.model.dto.ImageDto;

@Component
public class ImageMapper {
	private static final Logger LOG = LoggerFactory.getLogger(ImageMapper.class);

	public ImageDto map(MultipartFile multipartFile, String query) {
		var imageDto = new ImageDto();
		try {
			imageDto.setImageContent(multipartFile.getBytes());
			imageDto.setQuery(query);
			imageDto.setImageType(this.toImageType(multipartFile.getContentType()));
			imageDto.setContentSize(multipartFile.getSize());
		} catch (Exception e) {
			LOG.info("Mapping failed.", e);
		}
		return null;
	}

	private ImageType toImageType(String contentType) {
		var result = switch (contentType) {
		case MediaType.IMAGE_JPEG_VALUE -> ImageType.JPEG;
		case MediaType.IMAGE_PNG_VALUE -> ImageType.PNG;
		case "image/svg+xml" -> ImageType.SVG;
		default -> ImageType.UNKNOWN;
		};
		return result;
	}
}
