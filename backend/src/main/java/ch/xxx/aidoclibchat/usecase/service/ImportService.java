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

import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.model.entity.Artist;
import ch.xxx.aidoclibchat.domain.model.entity.ArtistRepository;
import ch.xxx.aidoclibchat.domain.model.entity.Museum;
import ch.xxx.aidoclibchat.domain.model.entity.MuseumRepository;
import ch.xxx.aidoclibchat.domain.model.entity.MuseumHours;
import ch.xxx.aidoclibchat.domain.model.entity.MuseumHoursRepository;
import ch.xxx.aidoclibchat.domain.model.entity.Work;
import ch.xxx.aidoclibchat.domain.model.entity.WorkRepository;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@Service
@Transactional(value = TxType.REQUIRES_NEW)
public class ImportService {
	private final WorkRepository zipcodeRepository;
	private final MuseumHoursRepository supermarketRepository;
	private final MuseumRepository productRepository;
	private final ArtistRepository amazonProductRepository;

	public ImportService(WorkRepository zipcodeRepository, MuseumHoursRepository supermarketRepository,
			MuseumRepository productRepository, ArtistRepository amazonProductRepository) {
		this.zipcodeRepository = zipcodeRepository;
		this.supermarketRepository = supermarketRepository;
		this.productRepository = productRepository;
		this.amazonProductRepository = amazonProductRepository;
	}

	public void deleteData() {
		this.amazonProductRepository.deleteAll();
		this.supermarketRepository.deleteAll();
		this.productRepository.deleteAll();
		this.zipcodeRepository.deleteAll();
	}

	public void saveAllData(List<Work> zipcodes, List<MuseumHours> supermarkets, List<Museum> products,
			List<Artist> amazonProducts) {
		this.zipcodeRepository.saveAll(zipcodes);
		this.supermarketRepository.saveAll(supermarkets);
		this.productRepository.saveAll(products);
		this.amazonProductRepository.saveAll(amazonProducts);
	}
}
