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
package ch.xxx.aidoclibchat.domain.common;

public class MetaData {
	public enum DataType {
		DOCUMENT, TABLE, COLUMN, ROW
	};

	public enum DocumentType {
		PDF, HTML, TEXT, XML, UNKNOWN
	};

	public enum ImageType {
		JPEG, PNG, SVG, UNKNOWN
	}
	
	public static final String ID = "id";
	public static final String DATATYPE = "datatype";
	public static final String DATANAME = "dataname";
	public static final String TABLE_NAME = "tablename";
	public static final String DISTANCE = "distance";
	public static final String REFERENCE_COLUMN = "referenceColumn";
	public static final String REFERENCE_TABLE = "referenceTable";
	public static final String PRIMARY_KEY = "primaryKey";
}
