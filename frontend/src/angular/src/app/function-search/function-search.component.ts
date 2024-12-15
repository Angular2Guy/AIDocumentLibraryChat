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
import { MatTreeNestedDataSource, MatTreeModule } from '@angular/material/tree';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import {
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Book, FunctionResponse, FunctionSearch } from '../model/functions';
import { FunctionSearchService } from '../service/function-search.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Subscription, interval, map, tap } from 'rxjs';
import { NestedTreeControl } from '@angular/cdk/tree';
import { MatIconModule } from '@angular/material/icon';
import {MatRadioModule} from '@angular/material/radio';

interface TreeNode {
  name: string;
  children?: TreeNode[];
}

@Component({
    selector: 'app-function-search',
    imports: [
        CommonModule,
        MatToolbarModule,
        MatButtonModule,
        MatInputModule,
        MatTooltipModule,
        MatTreeModule,
        MatIconModule,
		MatRadioModule,
        MatFormFieldModule,
        FormsModule,
        ReactiveFormsModule,
        MatProgressSpinnerModule,
    ],
    templateUrl: './function-search.component.html',
    styleUrl: './function-search.component.scss'
})
export class FunctionSearchComponent {
  private repeatSub: Subscription | null = null;
  protected searchValueControl = new FormControl('', [
    Validators.required,
    Validators.minLength(3),
  ]);
  protected searching = false;
  protected msWorking = 0;
  protected treeControl = new NestedTreeControl<TreeNode>(
    (node) => node.children
  );
  protected dataSource = new MatTreeNestedDataSource<TreeNode>();
  protected response = '';
  protected resultFormats = ['text','json'];
  protected resultFormatControl = new FormControl(this.resultFormats[0]);

  constructor(
    private router: Router,
    private destroyRef: DestroyRef,
    private functionSearchService: FunctionSearchService
  ) {}

  protected hasChild = (_: number, node: TreeNode) =>
    !!node.children && node.children.length > 0;

  protected showList(): void {
    this.router.navigate(['/doclist']);
  }

  protected search(): void {
    this.searching = true;
    this.dataSource.data = [];
    const startDate = new Date();
    this.repeatSub?.unsubscribe();
    this.repeatSub = interval(100)
      .pipe(
        map(() => new Date()),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(
        (newDate) => (this.msWorking = newDate.getTime() - startDate.getTime())
      );
    this.functionSearchService
      .postLibraryFunction({
        question: this.searchValueControl.value,
		resultFormat: this.resultFormatControl.value
      } as FunctionSearch)
      .pipe(
        tap(() => this.repeatSub?.unsubscribe()),
        takeUntilDestroyed(this.destroyRef),
        tap(() => (this.searching = false))
      )
	  .subscribe(value => this.response = value.result
	  );
      //.subscribe((value) => (this.dataSource.data = this.mapResult(value)));
  }

  private mapResult(functionResponse: FunctionResponse): TreeNode[] {
    return functionResponse.docs.map((myBook) => this.mapBook(myBook));
  }

  private mapBook(book: Book): TreeNode {
    const rootNode = { name: book.title, children: [] } as TreeNode;
    rootNode.children?.push({ name: 'Title: ' + book.title } as TreeNode);
    rootNode.children?.push({ name: 'Type: ' + book.type } as TreeNode);
    rootNode.children?.push({
      name: 'Average Ratings: ' + book.ratings_average,
    } as TreeNode);
    rootNode.children?.push({
      name: 'Authors',
      children: this.mapArray(book.author_name),
    } as TreeNode);
    rootNode.children?.push({
      name: 'Languages',
      children: this.mapArray(book.language),
    } as TreeNode);
    rootNode.children?.push({
      name: 'Persons',
      children: this.mapArray(book.person),
    } as TreeNode);
    rootNode.children?.push({
      name: 'Places',
      children: this.mapArray(book.place),
    } as TreeNode);
    rootNode.children?.push({
      name: 'Publishdates',
      children: this.mapArray(book.publish_date),
    } as TreeNode);
    rootNode.children?.push({
      name: 'Publishers',
      children: this.mapArray(book.publisher),
    } as TreeNode);
    rootNode.children?.push({
      name: 'Subjects',
      children: this.mapArray(book.subject),
    } as TreeNode);
    rootNode.children?.push({
      name: 'Times',
      children: this.mapArray(book.time),
    } as TreeNode);
    console.log(rootNode);
    return rootNode;
  }

  private mapArray(values: string[]): TreeNode[] {
    return !!values ? values.map((myStr) => ({ name: myStr } as TreeNode)) : [];
  }

  protected logout(): void {
    console.log('logout');
  }
}
