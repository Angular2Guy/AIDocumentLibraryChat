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
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import {
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { Subscription, catchError, interval, map, of, tap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TableSearch } from '../model/table-search';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
    selector: 'app-table-search',
    imports: [
        CommonModule,
        MatToolbarModule,
        MatButtonModule,
        MatTableModule,
        MatInputModule,
        MatTooltipModule,
        MatFormFieldModule,
        FormsModule,
        ReactiveFormsModule,
        MatProgressSpinnerModule,
    ],
    templateUrl: './table-search.component.html',
    styleUrl: './table-search.component.scss'
})
export class TableSearchComponent {
  protected searchValueControl = new FormControl('', [
    Validators.required,
    Validators.minLength(3),
  ]);
  protected importing = false;
  protected searching = false;
  protected requestFailed = false;
  protected msWorking = 0;
  protected searchResult: TableSearch | null = null;
  protected columnData: Map<string, string>[] = [];
  protected columnNames = new Set<string>();
  private repeatSub: Subscription | null = null;
  //private myJson = '{"question":"show the artworks name and the name of the museum that have the style Realism and the subject of portraits","resultList":[{"1_name":"Portrait of Margaret in Skating Costume","2_name":"Philadelphia Museum of Art"},{"1_name":"Portrait of Mary Adeline Williams","2_name":"Philadelphia Museum of Art"},{"1_name":"Portrait of a Little Girl","2_name":"Philadelphia Museum of Art"},{"1_name":"Portrait of Mrs. Frank Hamilton Cushing","2_name":"Philadelphia Museum of Art"},{"1_name":"Portrait of Walt Whitman","2_name":"Philadelphia Museum of Art"},{"1_name":"The Portrait of Miss Helen Parker","2_name":"Philadelphia Museum of Art"},{"1_name":"The Thinker, Portrait of Louis N. Kenton","2_name":"The Metropolitan Museum of Art"},{"1_name":"Portrait of a Man","2_name":"The Metropolitan Museum of Art"},{"1_name":"Portrait of a Woman (Emily Bertie Pott)","2_name":"The Metropolitan Museum of Art"},{"1_name":"Portrait of Lady Grantham","2_name":"Philadelphia Museum of Art"},{"1_name":"Portrait of Marianne Holbech","2_name":"Philadelphia Museum of Art"},{"1_name":"Portrait of Master Ward","2_name":"The Prado Museum"},{"1_name":"Madame Proudhon","2_name":"Musée d Orsay"},{"1_name":"Portrait of Mlle C. D","2_name":"Musée d Orsay"},{"1_name":"Portrait of a Man","2_name":"National Gallery of Art"},{"1_name":"Portrait of Louise-Antoinette Feuardent","2_name":"The J. Paul Getty Museum"},{"1_name":"Portrait of Frieda Schiff","2_name":"The Metropolitan Museum of Art"},{"1_name":"Portrait of Richard Palmer","2_name":"Los Angeles County Museum of Art"},{"1_name":"Marie-Yolande de Fitz-James","2_name":"Cleveland Museum Of Art"},{"1_name":"The Oboe Player, Portrait of Benjamin Sharp","2_name":"Philadelphia Museum of Art"}],"resultAmount":100}';

  constructor(
    private destroyRef: DestroyRef,
    private router: Router,
    private tableService: TableService
  ) {}

  protected search(): void {
    this.searchResult = null;
    const startDate = new Date();
    this.msWorking = 0;
    this.searching = true;
    this.requestFailed = false;
    this.repeatSub?.unsubscribe();
    this.repeatSub = interval(100)
      .pipe(
        map(() => new Date()),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(
        (newDate) => (this.msWorking = newDate.getTime() - startDate.getTime())
      );
    this.searchResult = {
      question: this.searchValueControl.value,
      resultList: [],
      resultAmount: 100,
    } as TableSearch;
    this.tableService
      .postTableSearch(this.searchResult)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        tap(() => (this.searching = false)),
        tap(() => this.repeatSub?.unsubscribe()),
        catchError((error) => {
          console.log(error);
          this.requestFailed = true;
          this.searching = false;
          return of({
            question: this.searchValueControl.value,
            resultAmount: 0,
            resultList: [],
          } as TableSearch);
        })
      )
      .subscribe((result) => {
        this.searchResult = result;
        this.columnData = !result?.resultList
          ? this.columnData
          : result.resultList;
        this.columnNames = this.getColumnNames(result);
        //console.log(this.searchResult);
      });
  }

  protected importData(): void {
    //this.importing = !this.importing;
    this.tableService
      .getDataImport()
      .pipe(tap(() => (this.importing = true)))
      .subscribe({
        next: (result) => (this.importing = !result),
        error: (ex) => (this.importing = false),
      });
    //console.log('importData');
  }

  protected showList(): void {
    this.router.navigate(['/doclist']);
  }

  protected logout(): void {
    //this.searchResult = JSON.parse(this.myJson) as TableSearch;
    //this.columnData = !this.searchResult?.resultList ? this.columnData : this.searchResult.resultList;
    //this.columnNames = this.getColumnNames(this.searchResult);
    //this.columnData.forEach(myMap => this.columnNames.forEach(myName => console.log(myMap.get(myName))));
    //console.log(this.columnData);
    console.log('logout');
  }

  private getColumnNames(tableSearch: TableSearch): Set<string> {
    const result = new Set<string>();
    this.columnData = [];
    const myList = !tableSearch?.resultList ? [] : tableSearch.resultList;
    myList.forEach((value) => {
      //console.log(value);
      const myMap = new Map<string, string>();
      Object.entries(value).forEach((entry) => {
        //console.log(`${entry}`);
        result.add(entry[0]);
        myMap.set(entry[0], entry[1]);
      });
      this.columnData.push(myMap);
    });
    return result;
  }
}
