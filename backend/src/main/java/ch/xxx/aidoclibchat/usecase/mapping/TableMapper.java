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

import java.util.Optional;

import org.springframework.stereotype.Component;

import ch.xxx.aidoclibchat.domain.model.dto.AmazonProductDto;
import ch.xxx.aidoclibchat.domain.model.dto.ProductDto;
import ch.xxx.aidoclibchat.domain.model.dto.SupermarketDto;
import ch.xxx.aidoclibchat.domain.model.dto.ZipcodeDto;
import ch.xxx.aidoclibchat.domain.model.entity.Artist;
import ch.xxx.aidoclibchat.domain.model.entity.Museum;
import ch.xxx.aidoclibchat.domain.model.entity.MuseumHours;
import ch.xxx.aidoclibchat.domain.model.entity.Work;

@Component
public class TableMapper {
	public Work map(ZipcodeDto dto) {
		var entity = new Work();
//		entity.setCity(dto.getCity());
//		entity.setLat(dto.getLat());
//		entity.setLgt(dto.getLgt());
//		entity.setState(dto.getState());
//		entity.setZipcode(dto.getZipcode());
//		entity.setZipcodetype(dto.getZipcodetype());
		return entity;
	}

	public MuseumHours map(SupermarketDto dto) {
		var entity = new MuseumHours();
//		entity.setId(dto.getId());
//		entity.setPrice(dto.getPrice());
//		entity.setPrices(dto.getPrices());
//		entity.setZip(dto.getZip());
//		entity.setZips(dto.getZips());
		return entity;
	}

	public Optional<Museum> map(ProductDto dto) {
		var myOpt = Optional.of(new Museum());
		var entity = myOpt.get();
		try {
//			entity.setCode(dto.getCode());
//			entity.setComments(dto.getComments());
//			entity.setCountry(dto.getCountry());
//			entity.setCountryS(dto.getCountryS());
//			entity.setDate(dto.getDate());
//			entity.setDay(dto.getDay());
//			entity.setDeviceid(dto.getDeviceid());
//			entity.setId(Long.parseLong(dto.getId()));
//			entity.setImputed(dto.getImputed() == 1);
//			entity.setMonth(dto.getMonth());
//			entity.setOtherskuitem(dto.getOtherskuitem());
//			entity.setPhoto(dto.getPhoto());
//			entity.setPrice(dto.getPrice());
//			entity.setPrice(dto.getPrice());
//			entity.setPriceOnline(dto.getPriceOnline());
//			entity.setPricetype(dto.getPricetype());
//			entity.setRetailer(dto.getRetailer());
//			entity.setRetailerS(dto.getRetailerS());
//			entity.setSaleOnline(Optional.ofNullable(dto.getSaleOnline()).stream().allMatch(myStr -> !myStr.isBlank()));
//			entity.setTime(dto.getTime());
//			entity.setYear(dto.getYear());
//			entity.setZipcode(dto.getZipcode());
		} catch (Exception e) {
			myOpt = Optional.empty();
		}
		return myOpt;
	}
	
	public Optional<Artist> map(AmazonProductDto dto) {
		var myOpt = Optional.of(new Artist());
		var entity = myOpt.get();
		try {
//			entity.setCategory(dto.getCategory());
//			entity.setCatId(dto.getCatId());
//			entity.setComments(dto.getComments());
//			entity.setDate(dto.getDate());
//			entity.setDateAmazon(dto.getDateAmazon());
//			entity.setDatediff(dto.getDatediff());
//			entity.setId(Long.parseLong(dto.getId()));
//			entity.setImputed(dto.getImputed() == 1);
//			entity.setMerchant(dto.getMerchant());
//			entity.setPrice(dto.getPrice());
//			entity.setPriceAmazon(dto.getPriceAmazon());
//			entity.setPriceOnline(dto.getPriceOnline());
//			entity.setPricetype(dto.getPricetype());
//			entity.setProductAmazon(dto.getProductAmazon());
//			entity.setPriceOnline(dto.getPriceOnline());
//			entity.setRetailerId(dto.getRetailerId());
//			entity.setRetailerS(dto.getRetailerS());
//			entity.setSaleOnline(dto.isSaleOnline() == 1);
//			entity.setUrl(dto.getUrl());
		}catch(Exception e) {
			myOpt = Optional.empty();
		}
		return myOpt;
	}
}
