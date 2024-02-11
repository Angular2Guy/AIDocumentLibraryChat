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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import ch.xxx.aidoclibchat.domain.model.dto.ArtistDto;
import ch.xxx.aidoclibchat.domain.model.dto.MuseumDto;
import ch.xxx.aidoclibchat.domain.model.dto.MuseumHoursDto;
import ch.xxx.aidoclibchat.domain.model.dto.SubjectDto;
import ch.xxx.aidoclibchat.domain.model.dto.TableSearchDto;
import ch.xxx.aidoclibchat.domain.model.dto.WorkDto;
import ch.xxx.aidoclibchat.domain.model.dto.WorkLinkDto;
import ch.xxx.aidoclibchat.domain.model.entity.Artist;
import ch.xxx.aidoclibchat.domain.model.entity.Museum;
import ch.xxx.aidoclibchat.domain.model.entity.MuseumHours;
import ch.xxx.aidoclibchat.domain.model.entity.MuseumHoursId;
import ch.xxx.aidoclibchat.domain.model.entity.Subject;
import ch.xxx.aidoclibchat.domain.model.entity.Work;
import ch.xxx.aidoclibchat.domain.model.entity.WorkLink;

@Component
public class TableMapper {
	public Work map(WorkDto dto) {
		var entity = new Work();
		entity.setArtistId(dto.getArtistId());
		entity.setHeight(dto.getHeight());
		entity.setId(dto.getId());
		entity.setMuseumId(dto.getMuseumId());
		entity.setName(dto.getName());
		entity.setStyle(dto.getStyle());
		entity.setWidth(dto.getWidth());
		return entity;
	}

	public MuseumHours map(MuseumHoursDto dto) {
		var entity = new MuseumHours();
		entity.setClose(dto.getClose());
		entity.setOpen(dto.getOpen());
		entity.setMuseumHoursId(new MuseumHoursId(dto.getMuseumId(), dto.getDay()));
		return entity;
	}

	public Museum map(MuseumDto dto) {
		var entity = new Museum();
		entity.setAddress(dto.getAddress());
		entity.setCity(dto.getCity());
		entity.setCountry(dto.getCountry());
		entity.setId(dto.getId());
		entity.setName(dto.getName());
		entity.setPhone(dto.getPhone());
		entity.setPostal(dto.getPostal());
		entity.setState(dto.getState());
		entity.setUrl(dto.getUrl());
		return entity;
	}

	public Artist map(ArtistDto dto) {
		var entity = new Artist();
		entity.setBirth(dto.getBirth());
		entity.setDeath(dto.getDeath());
		entity.setFirstName(dto.getFirstName());
		entity.setFullName(dto.getFullName());
		entity.setId(dto.getId());
		entity.setLastName(dto.getLastName());
		entity.setMiddleName(dto.getMiddleName());
		entity.setNationality(dto.getNationality());
		entity.setStyle(dto.getStyle());
		return entity;
	}

	public Subject map(SubjectDto dto) {
		var entity = new Subject();
		entity.setSubject(dto.getSubject());
		entity.setWorkId(dto.getWorkId());
		return entity;
	}

	public WorkLink map(WorkLinkDto dto) {
		var entity = new WorkLink();
		entity.setArtistId(dto.getArtistId());
		entity.setImageLink(dto.getImageLink());
		entity.setMuseumId(dto.getMuseumId());
		entity.setName(dto.getName());
		entity.setStyle(dto.getStyle());
		entity.setWorkId(dto.getWorkId());
		return entity;
	}

	public TableSearchDto map(SqlRowSet rowSet, String question) {
		List<Map<String, String>> result = new ArrayList<>();
		while (rowSet.next()) {
			final AtomicInteger atomicIndex = new AtomicInteger(1);
			Map<String, String> myRow = List.of(rowSet.getMetaData().getColumnNames()).stream()
					.map(myCol -> Map.entry(this.createPropertyName(myCol, rowSet, atomicIndex),
							Optional.ofNullable(rowSet.getObject(atomicIndex.get())).map(myOb -> myOb.toString()).orElse("")))
					.peek(x -> atomicIndex.set(atomicIndex.get() + 1))
					.collect(Collectors.toMap(myEntry -> myEntry.getKey(), myEntry -> myEntry.getValue()));
			result.add(myRow);
		}		
		return new TableSearchDto(question, result, 100);
	}

	private String createPropertyName(String columnName, SqlRowSet rowSet, AtomicInteger atomicIndex) {
		return columnName.contains("_") ? columnName : "" + atomicIndex.get() + "_" + columnName;
	}
}
