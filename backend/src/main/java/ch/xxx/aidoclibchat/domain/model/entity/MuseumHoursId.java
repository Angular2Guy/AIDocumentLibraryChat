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

import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class MuseumHoursId {
	private Long museumId;
	private String day;
	
	public MuseumHoursId() { }
	
	public MuseumHoursId(Long museumId, String day) {
		super();
		this.museumId = museumId;
		this.day = day;
	}

	public Long getMuseumId() {
		return museumId;
	}
	public void setMuseumId(Long museumId) {
		this.museumId = museumId;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}

	@Override
	public int hashCode() {
		return Objects.hash(day, museumId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MuseumHoursId other = (MuseumHoursId) obj;
		return Objects.equals(day, other.day) && Objects.equals(museumId, other.museumId);
	}
}
