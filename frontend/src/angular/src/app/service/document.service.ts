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
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DocumentFile } from '../model/document-file';
import { DocumentSearch, DocumentSearchResult } from '../model/documents';

@Injectable({
  providedIn: 'root',
})
export class DocumentService {
  constructor(private httpClient: HttpClient) {}

  public getDocumentList(): Observable<DocumentFile[]> {
    return this.httpClient.get<DocumentFile[]>('/rest/document/list');
  }

  public getDocumentById(id: number): Observable<DocumentFile> {
    return this.httpClient.get<DocumentFile>(`/rest/document/id/${id}`);
  }

  public postDocumentForm(formData: FormData): Observable<string> {
    return this.httpClient.post<string>('/rest/document/upload', formData);
  }

  public postDocumentSearch(
    documentSearch: DocumentSearch
  ): Observable<DocumentSearchResult> {
    return this.httpClient.post<DocumentSearchResult>(
      '/rest/document/search',
      documentSearch
    );
  }
}
