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
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-book-import',
  standalone: true,
  imports: [MatIconModule],
  templateUrl: './book-import.component.html',
  styleUrl: './book-import.component.scss'
})
export class BookImportComponent {
	protected file: File | null = null;

	constructor(private http: HttpClient) {}
	
	onFileSelected($event: Event) {
		const files = !$event.target
		      ? null
		      : ($event.target as HTMLInputElement).files;
		    this.file = !!files && files.length > 0 ? files[0] : null;

		this.file?.name
	    if (!!this.file) {	        
	        const formData = new FormData();

	        formData.append("thumbnail", this.file);

	        const upload$ = this.http.post("/api/thumbnail-upload", formData);

	        upload$.subscribe();
	    }
	}
}
