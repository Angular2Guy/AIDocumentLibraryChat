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
import { DocumentFile } from './document-file';

export enum SearchType {
  PARAGRAPH = 'PARAGRAPH',
  DOCUMENT = 'DOCUMENT',
}

export interface DocumentSearch {
  searchString: string;
  searchType: SearchType;
  resultAmount: number;
}

export interface DocumentSearchResult {
  searchString: string;
  resultStrings: string[];
  documents: DocumentFile[];
}
