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
import {inject, Service} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {ClassificationSummary} from "../model/classification-summary";

@Service()
export class ClassificationService {
  private readonly httpClient = inject(HttpClient);

  public postFile(formData: FormData): Observable<ClassificationSummary> {
    //return of(JSON.parse('{ "summaries": [ { "label": "Politics", "provided": 5, "found": 5, "score": 0.5893555194659168, "text": "" }, { "label": "Sport", "provided": 5, "found": 5, "score": 0.6039054928923634, "text": "" }, { "label": "Technology", "provided": 5, "found": 5, "score": 0.6384364719040033, "text": "" }, { "label": "Entertainment", "provided": 5, "found": 5, "score": 0.5967397754808416, "text": "" }, { "label": "Business", "provided": 5, "found": 5, "score": 0.6346503735910027, "text": "" } ], "results": [{ "label": "Business", "provided": 5, "found": 5, "score": 0.6346503735910027, "text": "hallo" }] } '));
    return this.httpClient.post<ClassificationSummary>('/rest/text-classification/classify', formData);
  }
}
