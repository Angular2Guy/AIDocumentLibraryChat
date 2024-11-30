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
import { Component, OnInit } from '@angular/core';
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatInputModule} from '@angular/material/input';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { Book } from '../model/book';
import { Router } from '@angular/router';
import { DocumentService } from '../service/document.service';
import { Observable, catchError, debounceTime, distinct, of, switchMap, tap } from 'rxjs';

@Component({
    selector: 'app-book-list',
    imports: [FormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatAutocompleteModule,
        ReactiveFormsModule,
        MatToolbarModule, MatButtonModule],
    templateUrl: './book-list.component.html',
    styleUrl: './book-list.component.scss'
})
export class BookListComponent implements OnInit {
	protected myControl = new FormControl<string | Book>('');
	protected filteredOptions: Book[] = [];
	protected selBook: Book | null = null;
	
	constructor(private router: Router, private documentService: DocumentService) { }
	
    ngOnInit(): void {
      this.myControl.valueChanges.pipe(		 
		debounceTime(300), 
		tap(myValue => { if(typeof myValue === 'object') this.selBook = myValue;}),
	  switchMap(myValue => typeof myValue === 'string' ? this.documentService.getBooksByTitleAuthor(myValue).pipe(catchError(err => [])) : of([])))
	  .subscribe({next: myArr => this.filteredOptions = myArr, error: myErr => this.filteredOptions = [] as Book[]});
    }
	
	protected displayFn(book: Book): string {
	  return book && book?.title ? book.title : '';
	}
	
	protected logout(): void {
		console.log('logout');
	}
	
	protected back(): void {
		this.router.navigate(['doclist']);
	}
	
	protected addBook(): void {
		this.router.navigate(['bookimport']);
	}
}
