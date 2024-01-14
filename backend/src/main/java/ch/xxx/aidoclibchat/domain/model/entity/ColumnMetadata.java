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

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ColumnMetadata {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;
    private String columnName;
    private String columnDescription;
    private boolean columnPrimaryKey;
    private String referenceTableName;
    private String referenceTableColumn;
    @ManyToOne
    @JoinColumn(name="table_metadata_id", nullable=false)
    private TableMetadata tableMetadata;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getColumnDescription() {
		return columnDescription;
	}
	public void setColumnDescription(String columnDescription) {
		this.columnDescription = columnDescription;
	}
	public String getReferenceTableName() {
		return referenceTableName;
	}
	public void setReferenceTableName(String referenceTableName) {
		this.referenceTableName = referenceTableName;
	}
	public String getReferenceTableColumn() {
		return referenceTableColumn;
	}
	public void setReferenceTableColumn(String referenceTableColumn) {
		this.referenceTableColumn = referenceTableColumn;
	}
	public TableMetadata getTableMetadata() {
		return tableMetadata;
	}
	public void setTableMetadata(TableMetadata tableMetadata) {
		this.tableMetadata = tableMetadata;
	}
	public boolean isColumnPrimaryKey() {
		return columnPrimaryKey;
	}
	public void setColumnPrimaryKey(boolean columnPrimaryKey) {
		this.columnPrimaryKey = columnPrimaryKey;
	}
}
