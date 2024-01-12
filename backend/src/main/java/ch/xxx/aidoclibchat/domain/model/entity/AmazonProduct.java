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
public class AmazonProduct {
	@Id
	private Long id;
	private LocalDate dateAmazon;
	private LocalDate date;
	private double price;
	private double priceOnline;
	private double priceAmazon;
	private boolean saleOnline;
	private String productOnline;
	private String productAmazon;
	private String merchant;
	private String url;
	private boolean imputed;
	private String comments;
	private String pricetype;
	private int datediff;
	private int catId;
	private String category;
	private Long retailerId;
	@Column(name="retailer_s")
	private String retailerS;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDate getDateAmazon() {
		return dateAmazon;
	}
	public void setDateAmazon(LocalDate dateAmazon) {
		this.dateAmazon = dateAmazon;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
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
	public double getPriceAmazon() {
		return priceAmazon;
	}
	public void setPriceAmazon(double priceAmazon) {
		this.priceAmazon = priceAmazon;
	}
	public boolean isSaleOnline() {
		return saleOnline;
	}
	public void setSaleOnline(boolean saleOnline) {
		this.saleOnline = saleOnline;
	}
	public String getProductOnline() {
		return productOnline;
	}
	public void setProductOnline(String productOnline) {
		this.productOnline = productOnline;
	}
	public String getProductAmazon() {
		return productAmazon;
	}
	public void setProductAmazon(String productAmazon) {
		this.productAmazon = productAmazon;
	}
	public String getMerchant() {
		return merchant;
	}
	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isImputed() {
		return imputed;
	}
	public void setImputed(boolean imputed) {
		this.imputed = imputed;
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
	public int getDatediff() {
		return datediff;
	}
	public void setDatediff(int datediff) {
		this.datediff = datediff;
	}
	public int getCatId() {
		return catId;
	}
	public void setCatId(int catId) {
		this.catId = catId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Long getRetailerId() {
		return retailerId;
	}
	public void setRetailerId(Long retailerId) {
		this.retailerId = retailerId;
	}
	public String getRetailerS() {
		return retailerS;
	}
	public void setRetailerS(String retailerS) {
		this.retailerS = retailerS;
	}
	@Override
	public String toString() {
		return "AmazonProduct [id=" + id + ", dateAmazon=" + dateAmazon + ", date=" + date + ", price=" + price
				+ ", priceOnline=" + priceOnline + ", priceAmazon=" + priceAmazon + ", saleOnline=" + saleOnline
				+ ", productOnline=" + productOnline + ", productAmazon=" + productAmazon + ", merchant=" + merchant
				+ ", url=" + url + ", imputed=" + imputed + ", comments=" + comments + ", pricetype=" + pricetype
				+ ", datediff=" + datediff + ", catId=" + catId + ", category=" + category + ", retailerId="
				+ retailerId + ", retailerS=" + retailerS + "]";
	}	
}
