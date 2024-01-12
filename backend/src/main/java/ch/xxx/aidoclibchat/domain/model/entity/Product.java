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
package ch.xxx.aidoclibchat.domain.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Product {
	@Id
	private Long id;
	private String country;
	private Long retailer;
	@Column(name="retailer_s")
	private String retailerS;
	private LocalDate date;
	private int day;
	private int month;
	private int year;
	private double price;
	private double priceOnline;
	private boolean imputed;
	private String deviceid;
	private String time;
	private String zipcode;
	private String photo;
	private String otherskuitem;
	private String comments;
	private String pricetype;
	private String code;
	private boolean saleOnline;
	@Column(name="country_s")
	private String countryS;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Long getRetailer() {
		return retailer;
	}
	public void setRetailer(Long retailer) {
		this.retailer = retailer;
	}
	public String getRetailerS() {
		return retailerS;
	}
	public void setRetailerS(String retailerS) {
		this.retailerS = retailerS;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getPriceOnline() {
		return priceOnline;
	}
	public void setPriceOnline(double priceOnline) {
		this.priceOnline = priceOnline;
	}
	public boolean isImputed() {
		return imputed;
	}
	public void setImputed(boolean imputed) {
		this.imputed = imputed;
	}
	public String getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getOtherskuitem() {
		return otherskuitem;
	}
	public void setOtherskuitem(String otherskuitem) {
		this.otherskuitem = otherskuitem;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getPricetype() {
		return pricetype;
	}
	public void setPricetype(String pricetype) {
		this.pricetype = pricetype;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public boolean getSaleOnline() {
		return saleOnline;
	}
	public void setSaleOnline(boolean saleOnline) {
		this.saleOnline = saleOnline;
	}
	public String getCountryS() {
		return countryS;
	}
	public void setCountryS(String countryS) {
		this.countryS = countryS;
	}
	@Override
	public String toString() {
		return "Product [id=" + id + ", country=" + country + ", retailer=" + retailer + ", retailerS=" + retailerS
				+ ", date=" + date + ", day=" + day + ", month=" + month + ", year=" + year + ", price=" + price
				+ ", priceOnline=" + priceOnline + ", imputed=" + imputed + ", deviceid=" + deviceid + ", time=" + time
				+ ", zipcode=" + zipcode + ", photo=" + photo + ", otherskuitem=" + otherskuitem + ", comments="
				+ comments + ", pricetype=" + pricetype + ", code=" + code + ", saleOnline=" + saleOnline
				+ ", countryS=" + countryS + "]";
	}
}
