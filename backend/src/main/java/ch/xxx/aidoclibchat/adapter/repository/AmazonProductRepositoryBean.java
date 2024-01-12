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
package ch.xxx.aidoclibchat.adapter.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import ch.xxx.aidoclibchat.domain.model.entity.AmazonProduct;
import ch.xxx.aidoclibchat.domain.model.entity.AmazonProductRepository;

@Repository
public class AmazonProductRepositoryBean implements AmazonProductRepository {
	private final JpaAmazonProductRepository jpaAmazonProductRepository;
	
	public AmazonProductRepositoryBean(JpaAmazonProductRepository jpaAmazonProductRepository) {
		this.jpaAmazonProductRepository = jpaAmazonProductRepository;
	}
	
	@Override
	public List<AmazonProduct> saveAll(Iterable<AmazonProduct> entites) {
		return this.jpaAmazonProductRepository.saveAll(entites);
	}
	
	@Override
	public void deleteAll() {
		this.jpaAmazonProductRepository.deleteAll();
	}
}
