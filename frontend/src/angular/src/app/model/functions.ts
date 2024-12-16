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
export interface FunctionSearch {
  question: string;
  resultFormat: string;
}

export interface Book {
  title: string;
  type: string;
  author_name: string[];
  language: string[];
  publish_date: string[];
  publisher: string[];
  subject: string[];
  place: string[];
  time: string[];
  person: string[];
  ratings_average: number;
}

export interface FunctionResponse {
  numFound: number;
  start: number;
  numFoundExact: boolean;
  docs: Book[];
}

export interface FunctionResult {
	result?: string;
	jsonResult?: JsonResult[];
}

export interface JsonResult {
	author: string;
	books: JsonBook[];
}

export interface JsonBook {
	title: string;
	summary: string;
}