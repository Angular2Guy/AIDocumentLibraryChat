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

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import ch.xxx.aidoclibchat.domain.client.ImportClient;
import ch.xxx.aidoclibchat.domain.model.dto.ArtistDto;
import ch.xxx.aidoclibchat.domain.model.dto.MuseumDto;
import ch.xxx.aidoclibchat.domain.model.dto.MuseumHoursDto;
import ch.xxx.aidoclibchat.domain.model.dto.SubjectDto;
import ch.xxx.aidoclibchat.domain.model.dto.WorkDto;
import ch.xxx.aidoclibchat.domain.model.dto.WorkLinkDto;
import ch.xxx.aidoclibchat.domain.model.entity.Artist;
import ch.xxx.aidoclibchat.domain.model.entity.Museum;
import ch.xxx.aidoclibchat.domain.model.entity.MuseumHours;
import ch.xxx.aidoclibchat.domain.model.entity.Subject;
import ch.xxx.aidoclibchat.domain.model.entity.Work;
import ch.xxx.aidoclibchat.domain.model.entity.WorkLink;
import ch.xxx.aidoclibchat.usecase.mapping.TableMapper;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;

@Component
public class ImportRestClient implements ImportClient {
	private final CsvMapper csvMapper;
	private final TableMapper tableMapper;
	private final RestClient restClient;

	public ImportRestClient(TableMapper tableMapper, RestClient restClient, CsvMapper csvMapper) {
		this.tableMapper = tableMapper;
		this.restClient = restClient;
		this.csvMapper = csvMapper;		
	}

	@Override
	public List<Artist> importArtists() {
		String result = this.restClient.get().uri(
				"https://raw.githubusercontent.com/Angular2Guy/AIDocumentLibraryChat/master/museumDataset/artist.csv")
				.retrieve().body(String.class);
		return this.mapString(result, ArtistDto.class).stream().map(this.tableMapper::map).toList();
	}

	private <T> List<T> mapString(String result, Class<T> myClass) {
		List<T> zipcodes = List.of();
		zipcodes = this.csvMapper.readerFor(myClass).with(CsvSchema.builder().setUseHeader(true).build())
				.<T>readValues(result).readAll();
		return zipcodes;
	}

	@Override
	public List<Museum> importMuseums() {
		String result = this.restClient.get().uri(
				"https://raw.githubusercontent.com/Angular2Guy/AIDocumentLibraryChat/master/museumDataset/museum.csv")
				.retrieve().body(String.class);
		return this.mapString(result, MuseumDto.class).stream().map(myDto -> this.tableMapper.map(myDto)).toList();
	}

	@Override
	public List<MuseumHours> importMuseumHours() {
		String result = this.restClient.get().uri(
				"https://raw.githubusercontent.com/Angular2Guy/AIDocumentLibraryChat/master/museumDataset/museum_hours.csv")
				.retrieve().body(String.class);
		return this.mapString(result, MuseumHoursDto.class).stream().map(myDto -> this.tableMapper.map(myDto)).toList();
	}

	@Override
	public List<Work> importWorks() {
		String result = this.restClient.get().uri(
				"https://raw.githubusercontent.com/Angular2Guy/AIDocumentLibraryChat/master/museumDataset/work.csv")
				.retrieve().body(String.class);
		return this.mapString(result, WorkDto.class).stream().map(myDto -> this.tableMapper.map(myDto)).toList();
	}
	
	@Override
	public List<Subject> importSubjects() {
		String result = this.restClient.get().uri(
				"https://raw.githubusercontent.com/Angular2Guy/AIDocumentLibraryChat/master/museumDataset/subject.csv")
				.retrieve().body(String.class);
		return this.mapString(result, SubjectDto.class).stream().map(myDto -> this.tableMapper.map(myDto)).toList();
	}
	
	@Override
	public List<WorkLink> importWorkLinks() {
		String result = this.restClient.get().uri(
				"https://raw.githubusercontent.com/Angular2Guy/AIDocumentLibraryChat/master/museumDataset/work_link.csv")
				.retrieve().body(String.class);
		return this.mapString(result, WorkLinkDto.class).stream().map(myDto -> this.tableMapper.map(myDto)).toList();
	}
}
