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
package ch.xxx.aidoclibchat.usecase.service;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.model.entity.Artist;
import ch.xxx.aidoclibchat.domain.model.entity.ArtistRepository;
import ch.xxx.aidoclibchat.domain.model.entity.DocumentVsRepository;
import ch.xxx.aidoclibchat.domain.model.entity.Museum;
import ch.xxx.aidoclibchat.domain.model.entity.MuseumRepository;
import ch.xxx.aidoclibchat.domain.model.entity.Subject;
import ch.xxx.aidoclibchat.domain.model.entity.SubjectRepository;
import ch.xxx.aidoclibchat.domain.model.entity.MuseumHours;
import ch.xxx.aidoclibchat.domain.model.entity.MuseumHoursRepository;
import ch.xxx.aidoclibchat.domain.model.entity.Work;
import ch.xxx.aidoclibchat.domain.model.entity.WorkLink;
import ch.xxx.aidoclibchat.domain.model.entity.WorkLinkRepository;
import ch.xxx.aidoclibchat.domain.model.entity.WorkRepository;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@Service
@Transactional(value = TxType.REQUIRES_NEW)
public class ImportService {
	private final WorkRepository workRepository;
	private final MuseumHoursRepository museumHoursRepository;
	private final MuseumRepository museumRepository;
	private final ArtistRepository artistRepository;
	private final SubjectRepository subjectRepository;
	private final WorkLinkRepository workLinkRepository;
	private final DocumentVsRepository documentVsRepository;
	

	public ImportService(WorkRepository zipcodeRepository, MuseumHoursRepository supermarketRepository,WorkLinkRepository workLinkRepository,
			MuseumRepository productRepository, ArtistRepository amazonProductRepository, SubjectRepository subjectRepository,DocumentVsRepository documentVsRepository) {
		this.workRepository = zipcodeRepository;
		this.museumHoursRepository = supermarketRepository;
		this.museumRepository = productRepository;
		this.artistRepository = amazonProductRepository;
		this.subjectRepository = subjectRepository;
		this.workLinkRepository = workLinkRepository;
		this.documentVsRepository = documentVsRepository;
	}

	public void deleteData() {
		this.subjectRepository.deleteAll();
		this.workLinkRepository.deleteAll();
		this.museumHoursRepository.deleteAll();
		this.workRepository.deleteAll();
		this.artistRepository.deleteAll();
		this.museumRepository.deleteAll();
	}

	public void saveAllData(List<Work> works, List<MuseumHours> museumHours, List<Museum> museums,
			List<Artist> artists, List<Subject> subjects, List<WorkLink> workLinks) {
		this.museumRepository.saveAll(museums);
		this.artistRepository.saveAll(artists);
		this.workRepository.saveAll(works);
		this.museumHoursRepository.saveAll(museumHours);
		this.subjectRepository.saveAll(subjects);
		this.workLinkRepository.saveAll(workLinks);
	}
	
	public List<Document> findAllTableDocuments() {
		return this.documentVsRepository.findAllTableDocuments();
	}
	
	public void deleteByIds(List<String> ids) {
		this.documentVsRepository.deleteByIds(ids);
	}
}
