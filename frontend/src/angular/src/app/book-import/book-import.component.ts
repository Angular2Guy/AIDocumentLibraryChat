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
import { Book, ChapterHeading } from '../model/book';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';

enum FormGroupKey{
	file='file',
	chapters='chapters',
	chapterHeading='chapterHeading',
}

@Component({
    selector: 'app-book-import',
    imports: [MatIconModule, MatToolbarModule, MatButtonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule],
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

	constructor(private documentService: DocumentService, private router: Router) {}
	
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
			[FormGroupKey.chapterHeading]: new FormControl('', Validators.required),
		});
	} 
	
	protected logout(): void {
		console.log('logout');
	}
	
	protected bookList(): void {
		this.router.navigate(['booklist']);
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
						.map(chapterFg => ({title: chapterFg.controls[FormGroupKey.chapterHeading].value } as ChapterHeading));
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
	
	private frankensteinChapters(): ChapterHeading[] {
		let chapters: ChapterHeading[] = [];
		for(let i = 1;i<25;i++) {
			chapters.push({title: `Chapter ${i}`} as ChapterHeading);
		}
		chapters.push({title: '*** END OF THE PROJECT GUTENBERG EBOOK'} as ChapterHeading);
		return chapters;
	}
}
