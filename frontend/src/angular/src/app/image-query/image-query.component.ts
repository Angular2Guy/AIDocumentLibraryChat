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
import { MatButtonModule } from '@angular/material/button';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { tap } from 'rxjs';
import { ImageService } from '../service/image.service';
import { ImageFile } from '../model/image-file';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-image-query',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule, MatInputModule,MatButtonModule,ReactiveFormsModule],
  templateUrl: './image-query.component.html',
  styleUrl: './image-query.component.scss'
})
export class ImageQueryComponent {
  protected imageForm = new FormGroup({
	file: new FormControl<File | null>(null, Validators.required),
	query: new FormControl<string>('', Validators.minLength(3))
  });
  protected uploading = false;
  protected result: ImageFile | null = null;
  
  constructor(private imageService: ImageService, private destroyRef: DestroyRef) { }
	
  protected onFileInputChange($event: Event): void {
	this.result = null;
    const files = !$event.target
      ? null
      : ($event.target as HTMLInputElement).files;
    this.imageForm.controls.file.setValue(!!files && files.length > 0 ? files[0] : null);
  }
  
  protected createImageUrl(): string {
	return !this.result ? '' : 'data:image/'+this?.result?.imageType+';base64,'+this?.result?.b64Image;
  }
  
  protected upload(): void {
    //console.log(this.file);
    if (!!this.imageForm.controls.file) {
	  this.result = null;
	  this.uploading = true;
      const formData = new FormData();
      const myFile = this.imageForm.controls.file.value;
      formData.append('file', myFile as Blob, myFile?.name as string);
      formData.append('query', this.imageForm.controls.query.value as unknown as string);
      formData.append('type', (this.imageForm.controls.file.value as unknown as File)?.type);
      console.log(formData);
      console.log(this.imageForm.controls.file.value);
      console.log(this.imageForm.controls.query.value);
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
          this.result = result;
          console.log(result);
        });
    }
  }
  
  protected cancel(): void {
	console.log('cancel');
  }
}
