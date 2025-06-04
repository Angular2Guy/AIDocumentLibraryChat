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
import { Component, DestroyRef, Inject, inject } from '@angular/core';

import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogModule,
} from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { DocumentService } from '../service/document.service';
import { tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

export interface DocImportData {}

@Component({
    selector: 'app-docimport',
    imports: [
    MatFormFieldModule,
    MatDialogModule,
    MatButtonModule,
    MatInputModule,
    MatIconModule,
    FormsModule,
    MatProgressSpinnerModule
],
    templateUrl: './doc-import.component.html',
    styleUrls: ['./doc-import.component.scss']
})
export class DocImportComponent {
  protected file: File | null = null;
  protected uploading = false;
  private destroyRef = inject(DestroyRef);

  constructor(
    private dialogRef: MatDialogRef<DocImportComponent>,
    @Inject(MAT_DIALOG_DATA) private data: DocImportComponent,
    private documentService: DocumentService
  ) {}

  protected onFileInputChange($event: Event): void {
    const files = !$event.target
      ? null
      : ($event.target as HTMLInputElement).files;
    this.file = !!files && files.length > 0 ? files[0] : null;
  }

  protected upload(): void {
    //console.log(this.file);
    if (!!this.file) {
      const formData = new FormData();
      formData.append('file', this.file as Blob, this.file.name as string);
      this.documentService
        .postDocumentForm(formData)
        .pipe(
          tap(() => {
            this.uploading = true;
          }),
          takeUntilDestroyed(this.destroyRef)
        )
        .subscribe((result) => {
          this.uploading = false;
          //console.log(result);
          this.dialogRef.close();
        });
    }
  }

  protected cancel(): void {
    this.dialogRef.close();
  }
}
