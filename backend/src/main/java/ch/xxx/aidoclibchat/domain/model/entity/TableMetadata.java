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

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class TableMetadata {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;
	private String tableName;
	private String tableDescription;
	private String tableDdl;
    @OneToMany(mappedBy="tableMetadata")
    private Set<ColumnMetadata> columnMetadata;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableDescription() {
		return tableDescription;
	}
	public void setTableDescription(String tableDescription) {
		this.tableDescription = tableDescription;
	}
	public Set<ColumnMetadata> getColumnMetadata() {
		return columnMetadata;
	}
	public void setColumnMetadata(Set<ColumnMetadata> columnMetadata) {
		this.columnMetadata = columnMetadata;
	}
	public String getTableDdl() {
		return tableDdl;
	}
	public void setTableDdl(String tableDdl) {
		this.tableDdl = tableDdl;
	}
}
