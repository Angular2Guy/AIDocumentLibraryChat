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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.client.ImportClient;

@Service
public class TableService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TableService.class);
	private ImportClient importClient;
	
	public TableService(ImportClient importClient) {
		this.importClient = importClient;
	}
	
	@Async
	public void importData() {
		this.importClient.importZipcodes().forEach(myZipcode -> LOGGER.info(myZipcode.toString()));
		this.importClient.importSupermarkets().forEach(mySupermarket -> LOGGER.info(mySupermarket.toString()));
		this.importClient.importProducts().forEach(myProduct -> LOGGER.info(myProduct.toString()));
		this.importClient.importAmazonProducts().forEach(myAmazonProduct -> LOGGER.info(myAmazonProduct.toString()));
	}
}
