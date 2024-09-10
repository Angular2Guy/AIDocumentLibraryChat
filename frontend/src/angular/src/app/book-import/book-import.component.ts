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
import { Component, DestroyRef, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { DocumentService } from '../service/document.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { tap } from 'rxjs';
import { ChapterPages } from '../model/book';

@Component({
  selector: 'app-book-import',
  standalone: true,
  imports: [MatIconModule],
  templateUrl: './book-import.component.html',
  styleUrl: './book-import.component.scss'
})
export class BookImportComponent {
	protected file: File | null = null;
	private destroyRef = inject(DestroyRef);
	protected uploading = false;

	constructor(private documentService: DocumentService) {}
	
	protected onFileSelected($event: Event): void {
		const files = !$event.target
		      ? null
		      : ($event.target as HTMLInputElement).files;
		    this.file = !!files && files.length > 0 ? files[0] : null;

	    if (!!this.file) {	        
	        const formData = new FormData();
			const chapters = [{startPage: 1, endPage: 2} as ChapterPages];
	        formData.append('book', this.file)
			formData.append('chapters', JSON.stringify(chapters));

	        this.documentService.postBookForm(formData).pipe(tap(() => {
			            this.uploading = true;
			          }),takeUntilDestroyed(this.destroyRef)).subscribe(result => console.log(result));
	    }
	}
}
