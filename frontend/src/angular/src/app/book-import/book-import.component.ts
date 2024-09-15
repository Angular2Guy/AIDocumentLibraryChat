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
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

enum FormGroupKey{
	file='file',
	chapters='chapters',
	chapterStart='chapterStart',
	chapterEnd='chapterEnd'
}

@Component({
  selector: 'app-book-import',
  standalone: true,
  imports: [MatIconModule,MatToolbarModule,MatButtonModule,ReactiveFormsModule, CommonModule,MatFormFieldModule,MatInputModule],
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
	
	protected removeChapter(chapterFg: FormGroup) {
		const chapterFormArray = this.bookForm.controls[FormGroupKey.chapters] as FormArray<FormGroup>;				
		for(let i = 0;i < chapterFormArray.length;i++) {
			if(chapterFormArray.at(i) === chapterFg) {
				chapterFormArray.removeAt(i);
			}
		}
	}
	
	protected addChapter(): void {
		const chapterFormArray = this.bookForm.controls[FormGroupKey.chapters] as FormArray<FormGroup>;
		chapterFormArray.push(this.createChapterGroupForm());
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
			const chapterFormArray = (this.bookForm.controls[FormGroupKey.chapters] as FormArray<FormGroup>);
			let chapters = new Array(chapterFormArray.length)
					    .map((v, index) => chapterFormArray.at(index) as FormGroup)
						.map(chapterFg => ({startPage: chapterFg.controls[FormGroupKey.chapterStart].value, 
							endPage: chapterFg.controls[FormGroupKey.chapterEnd].value } as ChapterPages));
			// for testing only
			//chapters = this.frankensteinChapters();
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
	
	private frankensteinChapters(): ChapterPages[] {
		let chapters = [{startPage: 17, endPage: 19} as ChapterPages];
		chapters.push({startPage: 20, endPage: 23} as ChapterPages);
		chapters.push({startPage: 24, endPage: 28} as ChapterPages);
		chapters.push({startPage: 34, endPage: 37} as ChapterPages);
		chapters.push({startPage: 38, endPage: 42} as ChapterPages);
		chapters.push({startPage: 43, endPage: 48} as ChapterPages);
		chapters.push({startPage: 49, endPage: 54} as ChapterPages);
		chapters.push({startPage: 55, endPage: 58} as ChapterPages);
		chapters.push({startPage: 59, endPage: 62} as ChapterPages);
		chapters.push({startPage: 63, endPage: 67} as ChapterPages);
		chapters.push({startPage: 68, endPage: 71} as ChapterPages);
		chapters.push({startPage: 72, endPage: 75} as ChapterPages);
		chapters.push({startPage: 76, endPage: 79} as ChapterPages);
		chapters.push({startPage: 80, endPage: 85} as ChapterPages);
		chapters.push({startPage: 86, endPage: 91} as ChapterPages);
		chapters.push({startPage: 92, endPage: 94} as ChapterPages);
		chapters.push({startPage: 95, endPage: 99} as ChapterPages);
		chapters.push({startPage: 100, endPage: 104} as ChapterPages);
		chapters.push({startPage: 105, endPage: 110} as ChapterPages);
		chapters.push({startPage: 111, endPage: 117} as ChapterPages);
		chapters.push({startPage: 118, endPage: 123} as ChapterPages);
		chapters.push({startPage: 124, endPage: 128} as ChapterPages);
		chapters.push({startPage: 129, endPage: 142} as ChapterPages);
		return chapters;
	}
}
