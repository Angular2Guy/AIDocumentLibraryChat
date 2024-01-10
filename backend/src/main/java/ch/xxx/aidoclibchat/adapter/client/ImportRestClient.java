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

import java.lang.reflect.Array;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import ch.xxx.aidoclibchat.domain.client.ImportClient;
import ch.xxx.aidoclibchat.domain.model.dto.AmazonProductDto;
import ch.xxx.aidoclibchat.domain.model.dto.ProductDto;
import ch.xxx.aidoclibchat.domain.model.dto.SupermarketDto;
import ch.xxx.aidoclibchat.domain.model.dto.ZipcodeDto;

@Component
public class ImportRestClient implements ImportClient {
	private CsvMapper csvMapper;
	
	public ImportRestClient(CsvMapper csvMapper) {
		this.csvMapper = csvMapper;
	}
	
	public List<ZipcodeDto> importZipcodes() {
		RestClient restClient = RestClient.create();
		String result = restClient.get().uri("https://github.com/Angular2Guy/AIDocumentLibraryChat/blob/master/retailData/zipcodes.csv")
		.retrieve().body(String.class);
		/*
		var zipcodes = new ZipcodeDto[0];
		try {
			zipcodes = this.csvMapper.readValue(result, ZipcodeDto[].class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return List.of(zipcodes);
		*/
		return this.mapString(result, ZipcodeDto[].class);
	}
	
	private <T> List<T> mapString(String result, Class<T[]> myClass) {
		@SuppressWarnings("unchecked")
		var zipcodes = (T[]) Array.newInstance(myClass, 0);
		try {
			zipcodes = this.csvMapper.readValue(result, myClass);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return List.of(zipcodes);
	}
	
	public List<SupermarketDto> importSupermarkets() {
		RestClient restClient = RestClient.create();
		String result = restClient.get().uri("https://github.com/Angular2Guy/AIDocumentLibraryChat/blob/master/retailData/supermarket-1day-45zips.csv")
		.retrieve().body(String.class);
		return this.mapString(result,SupermarketDto[].class);
	}
	
	public List<AmazonProductDto> importAmazonProducts() {
		RestClient restClient = RestClient.create();
		String result = restClient.get().uri("https://github.com/Angular2Guy/AIDocumentLibraryChat/blob/master/retailData/amazon_compare.csv")
		.retrieve().body(String.class);
		return this.mapString(result,AmazonProductDto[].class);
	}
	
	public List<ProductDto> importProducts() {
		RestClient restClient = RestClient.create();
		String result = restClient.get().uri("https://github.com/Angular2Guy/AIDocumentLibraryChat/blob/master/retailData/online_offline_ALL_clean.csv")
		.retrieve().body(String.class);
		return this.mapString(result,ProductDto[].class);
	}
}
