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
import { Book, ChapterPages } from '../model/book';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

enum FormGroupKey{
	file='file',
	chapters='chapters',
	chapterStart='chapterStart',
	chapterEnd='chapterEnd'
}

@Component({
  selector: 'app-book-import',
  standalone: true,
  imports: [MatIconModule,MatToolbarModule,MatButtonModule,ReactiveFormsModule, CommonModule],
  templateUrl: './book-import.component.html',
  styleUrl: './book-import.component.scss'
})
export class BookImportComponent {
	protected bookForm = new FormGroup({
		[FormGroupKey.file]: new FormControl<File | null>(null, Validators.required),
		[FormGroupKey.chapters]: new FormArray([this.createChapterGroupForm()])
	});
	private destroyRef = inject(DestroyRef);
	protected FormGroupKey = FormGroupKey;
	protected uploading = false;
	protected book: Book | null = null;

	constructor(private documentService: DocumentService) {}
	
	get chapters() {
		return this.bookForm.controls[FormGroupKey.chapters] as FormArray<FormGroup>;
	}
	
	protected createChapterGroupForm(): FormGroup {
		return new FormGroup({
			[FormGroupKey.chapterStart]: new FormControl(0, Validators.required),
			[FormGroupKey.chapterEnd]: new FormControl(0, Validators.required)
		});
	} 
	
	protected logout(): void {
		console.log('logout');
	}
	
	protected onFileSelected($event: Event): void {
		const files = !$event.target
		      ? null
		      : ($event.target as HTMLInputElement).files;
		    this.bookForm.controls[FormGroupKey.file].setValue(!!files && files.length > 0 ? files[0] : null);

	    if (!!this.bookForm.controls[FormGroupKey.file].value) {	        
	        const formData = new FormData();
			const chapters = [{startPage: 1, endPage: 2} as ChapterPages];
	        formData.append('book', this.bookForm.controls[FormGroupKey.file].value)
			formData.append('chapters', JSON.stringify(chapters));

	        this.documentService.postBookForm(formData).pipe(tap(() => {
			            this.uploading = true;
			          }),takeUntilDestroyed(this.destroyRef)).subscribe(result => {
						this.uploading = false;
						this.book = result; 
					});
	    }
	}
}
