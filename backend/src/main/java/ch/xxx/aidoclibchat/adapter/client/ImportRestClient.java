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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ch.xxx.aidoclibchat.domain.client.ImportClient;
import ch.xxx.aidoclibchat.domain.model.dto.AmazonProductDto;
import ch.xxx.aidoclibchat.domain.model.dto.ProductDto;
import ch.xxx.aidoclibchat.domain.model.dto.SupermarketDto;
import ch.xxx.aidoclibchat.domain.model.dto.ZipcodeDto;
import ch.xxx.aidoclibchat.domain.model.entity.AmazonProduct;
import ch.xxx.aidoclibchat.domain.model.entity.Product;
import ch.xxx.aidoclibchat.domain.model.entity.Supermarket;
import ch.xxx.aidoclibchat.domain.model.entity.Zipcode;
import ch.xxx.aidoclibchat.usecase.mapping.TableMapper;

@Component
public class ImportRestClient implements ImportClient {
	private final CsvMapper csvMapper;
	private final TableMapper tableMapper;

	public ImportRestClient(TableMapper tableMapper) {
		this.tableMapper = tableMapper;
		this.csvMapper = new CsvMapper();
		this.csvMapper.registerModule(new JavaTimeModule());
	}

	public List<Zipcode> importZipcodes() {
		RestClient restClient = RestClient.create();
		String result = restClient.get().uri(
				"https://raw.githubusercontent.com/Angular2Guy/AIDocumentLibraryChat/master/retailData/zipcodes.csv")
				.retrieve().body(String.class);
		return this.mapString(result, ZipcodeDto.class).stream().map(myDto -> this.tableMapper.map(myDto)).toList();
	}

	private <T> List<T> mapString(String result, Class<T> myClass) {
		List<T> zipcodes = List.of();
		try {
			zipcodes = this.csvMapper.readerFor(myClass).with(CsvSchema.builder().setUseHeader(true).build())
					.<T>readValues(result).readAll();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return zipcodes;
	}

	public List<Supermarket> importSupermarkets() {
		RestClient restClient = RestClient.create();
		String result = restClient.get().uri(
				"https://raw.githubusercontent.com/Angular2Guy/AIDocumentLibraryChat/master/retailData/supermarket-1day-45zips.csv")
				.retrieve().body(String.class);
		return this.mapString(result, SupermarketDto.class).stream().map(myDto -> this.tableMapper.map(myDto)).toList();
	}

	public List<AmazonProduct> importAmazonProducts() {
		RestClient restClient = RestClient.create();
		String result = restClient.get().uri(
				"https://raw.githubusercontent.com/Angular2Guy/AIDocumentLibraryChat/master/retailData/amazon_compare.csv")
				.retrieve().body(String.class);
		return this.mapString(result, AmazonProductDto.class).stream().map(myDto -> this.tableMapper.map(myDto))
				.filter(Optional::isPresent).map(Optional::get).toList();
	}

	public List<Product> importProducts() {
		RestClient restClient = RestClient.create();
		String result = restClient.get().uri(
				"https://raw.githubusercontent.com/Angular2Guy/AIDocumentLibraryChat/master/retailData/online_offline_ALL_clean.csv")
				.retrieve().body(String.class);
		return this.mapString(result, ProductDto.class).stream().map(myDto -> this.tableMapper.map(myDto))
				.filter(Optional::isPresent).map(Optional::get).toList();
	}
}
