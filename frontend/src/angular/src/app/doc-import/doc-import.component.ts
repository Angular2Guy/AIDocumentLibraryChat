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
import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog, MatDialogModule } from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';

export interface DocImportData {
	
}

@Component({
  selector: 'app-docimport',
  standalone: true,
  imports: [CommonModule,MatFormFieldModule, MatDialogModule,MatButtonModule, MatInputModule, FormsModule],
  templateUrl: './doc-import.component.html',
  styleUrls: ['./doc-import.component.scss']
})
export class DocImportComponent {
	constructor(public dialogRef: MatDialogRef<DocImportComponent>, @Inject(MAT_DIALOG_DATA) public data: DocImportComponent) { }
	
	protected cancel(): void {
		this.dialogRef.close();
	}
}
