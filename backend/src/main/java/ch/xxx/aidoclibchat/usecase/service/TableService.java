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

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.client.ImportClient;
import ch.xxx.aidoclibchat.domain.model.entity.AmazonProduct;
import ch.xxx.aidoclibchat.domain.model.entity.Product;
import ch.xxx.aidoclibchat.domain.model.entity.Supermarket;
import ch.xxx.aidoclibchat.domain.model.entity.Zipcode;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TableService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TableService.class);
	private final ImportClient importClient;
	private final ImportService importService;
	
	public TableService(ImportClient importClient, ImportService importService) {
		this.importClient = importClient;				
		this.importService = importService;
	}
	
	@Async
	public void importData() {
		var start = new Date();
		//this.importClient.importZipcodes().forEach(myZipcode -> LOGGER.info(myZipcode.toString()));
		//this.importClient.importSupermarkets().forEach(mySupermarket -> LOGGER.info(mySupermarket.toString()));
		//this.importClient.importProducts().forEach(myProduct -> LOGGER.info(myProduct.toString()));
		//this.importClient.importAmazonProducts().forEach(myAmazonProduct -> LOGGER.info(myAmazonProduct.toString()));
		List<Zipcode> zipcodes = this.importClient.importZipcodes();
		List<Supermarket> supermarkets = this.importClient.importSupermarkets();
		List<Product> products = this.importClient.importProducts();
		List<AmazonProduct> amazonProducts = this.importClient.importAmazonProducts();
		this.importService.deleteData();
		this.importService.saveAllData(zipcodes, supermarkets, products, amazonProducts);		
		LOGGER.info("Import done in {}ms.", new Date().getTime() - start.getTime());
	}
}
