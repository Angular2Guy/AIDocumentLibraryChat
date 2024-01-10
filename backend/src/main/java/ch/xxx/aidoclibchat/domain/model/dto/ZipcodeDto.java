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
package ch.xxx.aidoclibchat.domain.model.dto;

public class ZipcodeDto {
	private String zipcode;
	private String zipcodetype;
	private String city;
	private String state;
	private double lat;
	private double lgt;
	
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getZipcodetype() {
		return zipcodetype;
	}
	public void setZipcodetype(String zipcodetype) {
		this.zipcodetype = zipcodetype;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLgt() {
		return lgt;
	}
	public void setLgt(double lgt) {
		this.lgt = lgt;
	}

	@Override
	public String toString() {
		return "ZipcodeDto [zipcode=" + zipcode + ", zipcodetype=" + zipcodetype + ", city=" + city + ", state=" + state
				+ ", lat=" + lat + ", lgt=" + lgt + "]";
	}
}
