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
import { Component, DestroyRef, OnInit, inject } from '@angular/core';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { DocumentFile } from '../model/document-file';
import {
  MatDialog,
  MatDialogModule,
} from '@angular/material/dialog';
import { DocImportComponent } from '../doc-import/doc-import.component';
import { DocImportData } from '../doc-import/doc-import.component';
import { DocumentService } from '../service/document.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';

@Component({
    selector: 'app-doclist',
    imports: [
    MatToolbarModule,
    MatButtonModule,
    MatTableModule,
    MatDialogModule
],
    templateUrl: './doc-list.component.html',
    styleUrls: ['./doc-list.component.scss']
})
export class DocListComponent implements OnInit {
  protected displayedColumns: string[] = [
    'documentId',
    'documentName',
    'documentType',
  ];
  protected documents: DocumentFile[] = [];
  private destroyRef = inject(DestroyRef);
  private tabRef: Window | null = null;

  constructor(
    private dialog: MatDialog,
    private documentService: DocumentService,
    private router: Router
  ) {}

  public ngOnInit(): void {
    this.updateDocuments();
  }

  private updateDocuments(): void {
    this.documentService
      .getDocumentList()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((result) => (this.documents = result));
  }

  protected bookSummary(): void {
	this.router.navigate(['/booklist']);
  }
  
  protected search(): void {
    this.router.navigate(['/docsearch']);
  }

  protected mcpClient(): void {
    this.router.navigate(['/mcpclient']);
  }

  protected imageQuery(): void {
    this.router.navigate(['/imagequery']);
  }

  protected tableSearch(): void {
    this.router.navigate(['/tablesearch']);
  }

  protected functionSearch(): void {
    this.router.navigate(['/functionsearch']);
  }

  protected showDocument(documentId: number): void {
    if (!!this.tabRef) {
      this.tabRef.close();
    }
    this.tabRef = window.open(`/rest/document/content/${documentId}`, '_blank');
  }

  protected import(): void {
    const dialogRef = this.dialog.open(DocImportComponent, {
      data: {} as DocImportData,
    });
    dialogRef.afterClosed().subscribe((result) => this.updateDocuments());
  }

  protected logout(): void {
    console.log('logout');
  }
}
