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
import { Component, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableService } from '../service/table.service';
import {MatToolbarModule} from '@angular/material/toolbar'; 
import {MatButtonModule} from '@angular/material/button';
import {MatTableModule} from '@angular/material/table';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FormControl, FormsModule,ReactiveFormsModule, Validators} from '@angular/forms';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner'; 
import { Router } from '@angular/router';
import { Subscription, catchError, interval, map, of, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {TableSearch} from '../model/table-search';

@Component({
  selector: 'app-table-search',
  standalone: true,
  imports: [CommonModule,MatToolbarModule,MatButtonModule,MatTableModule,MatInputModule,
  	MatFormFieldModule,FormsModule,ReactiveFormsModule,MatProgressSpinnerModule],
  templateUrl: './table-search.component.html',
  styleUrl: './table-search.component.scss'
})
export class TableSearchComponent {
	protected searchValueControl = new FormControl('', [Validators.required, Validators.minLength(3)]);
	protected searching = false;
	protected requestFailed = false;
	protected msWorking = 0;
	protected searchResult: TableSearch | null = null;
	private repeatSub: Subscription | null = null;
	
	constructor(private destroyRef: DestroyRef, private router: Router, private tableService: TableService) { }
	
	protected search(): void {
		this.searchResult = null;
		const startDate = new Date();
		this.msWorking = 0;
		this.searching = true;
		this.requestFailed = false;
		this.repeatSub?.unsubscribe();
		this.repeatSub = interval(100).pipe(map(() => new Date()), takeUntilDestroyed(this.destroyRef)).subscribe(newDate => this.msWorking = newDate.getTime() - startDate.getTime());
		this.searchResult = {question: this.searchValueControl.value, resultList: [], resultAmount: 100} as TableSearch;
		this.tableService.postTableSearch(this.searchResult)
				  .pipe(takeUntilDestroyed(this.destroyRef), tap(() => this.searching = false), tap(() => this.repeatSub?.unsubscribe()), catchError(error => {
			  console.log(error);
			  this.requestFailed = true;
			  this.searching = false;
			  return of({question: this.searchValueControl.value, resultAmount: 0, resultList: []} as TableSearch);
		  }))
		  .subscribe(result => {
			  this.searchResult = result;
			  console.log(this.searchResult);
			  });
	}
	
	protected showList(): void {
		this.router.navigate(['/doclist']);
	}
	
	protected logout(): void {
		console.log('logout');
	}
}
