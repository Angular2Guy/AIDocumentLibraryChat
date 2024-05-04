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
import { Component, DestroyRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import {MatInputModule} from '@angular/material/input'; 
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { tap } from 'rxjs';
import { ImageService } from '../service/image.service';

@Component({
  selector: 'app-image-query',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule, MatInputModule],
  templateUrl: './image-query.component.html',
  styleUrl: './image-query.component.scss'
})
export class ImageQueryComponent {
  protected file: File | null = null;
  protected query = '';
  protected uploading = false;
  //private destroyRef = inject(DestroyRef);
  
  constructor(private imageService: ImageService, private destroyRef: DestroyRef) { }
	
  protected onFileInputChange($event: Event): void {
    const files = !$event.target
      ? null
      : ($event.target as HTMLInputElement).files;
    this.file = !!files && files.length > 0 ? files[0] : null;
  }
  
  protected upload(): void {
    //console.log(this.file);
    if (!!this.file) {
      const formData = new FormData();
      formData.append('file', this.file as Blob, this.file.name as string);
      formData.append('query', this.query);
      formData.append('type', this.file.type)
      this.imageService
        .postImageForm(formData)
        .pipe(
          tap(() => {
            this.uploading = true;
          }),
          takeUntilDestroyed(this.destroyRef)
        )
        .subscribe((result) => {
          this.uploading = false;
          console.log(result);
        });
    }
  }
  
  protected cancel(): void {
	console.log('cancel');
  }
}
