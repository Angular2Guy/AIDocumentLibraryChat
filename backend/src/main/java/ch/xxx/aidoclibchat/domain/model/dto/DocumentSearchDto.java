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

import java.util.ArrayList;
import java.util.List;

public class DocumentSearchDto {
	private String searchString;
	private List<String> resultStrings = new ArrayList<>();
	private List<DocumentDto> documents = new ArrayList<>();
	
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	public List<DocumentDto> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentDto> documents) {
		this.documents = documents;
	}
	public List<String> getResultStrings() {
		return resultStrings;
	}
	public void setResultStrings(List<String> resultStrings) {
		this.resultStrings = resultStrings;
	}	
}
