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
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatToolbarModule} from '@angular/material/toolbar'; 
import {MatButtonModule} from '@angular/material/button'; 
import {MatTableModule} from '@angular/material/table'; 
import { HttpClientModule } from '@angular/common/http';
import { DocumentFile } from '../model/DocumentFile';
import {MatDialog, MatDialogRef, MatDialogModule} from '@angular/material/dialog';
import { DocImportComponent } from '../doc-import/doc-import.component';
import { DocImportData } from '../doc-import/doc-import.component';

@Component({
  selector: 'app-doclist',
  standalone: true,
  imports: [CommonModule,MatToolbarModule,MatButtonModule,MatTableModule,HttpClientModule,MatDialogModule],
  templateUrl: './doclist.component.html',
  styleUrls: ['./doclist.component.scss']
})
export class DoclistComponent {
	protected displayedColumns: string[] = ['documentId', 'documentName', 'documentType'];
	protected documents: DocumentFile[] = [];
	
	constructor(public dialog: MatDialog) { }
	
	protected import(): void {
		const dialogRef = this.dialog.open(DocImportComponent, {data: {} as DocImportData});
		dialogRef.afterClosed().subscribe(result => console.log(result));
		console.log('import');
	}

	protected logout(): void {
		console.log('logout');
	}
}
