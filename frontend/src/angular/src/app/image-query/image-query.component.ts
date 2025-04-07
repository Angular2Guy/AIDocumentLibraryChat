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
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Subscription, interval, map, tap } from 'rxjs';
import { ImageService } from '../service/image.service';
import { ImageFile } from '../model/image-file';
import {
  FormControl,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';

@Component({
    selector: 'app-image-query',
    imports: [
        CommonModule,
        MatProgressSpinnerModule,
        MatInputModule,
        MatButtonModule,
		MatIconModule,
        ReactiveFormsModule,
        MatToolbarModule,
    ],
    templateUrl: './image-query.component.html',
    styleUrl: './image-query.component.scss'
})
export class ImageQueryComponent {
  //'What do you see in the image? Describe the background. Describe the colors.'
  protected imageForm = new FormGroup({
    file: new FormControl<File | null>(null, Validators.required),
    prompt: new FormControl<string>(
      'What do you see in the image? Describe the background. Describe the colors.',
      Validators.compose([Validators.required, Validators.minLength(3)])
    ),
  });
  protected queryControl = new FormControl<string>(
    '',
    Validators.compose([Validators.required, Validators.minLength(3)])
  );
  protected uploading = false;
  protected result: ImageFile | null = null;
  protected results: ImageFile[] = [];
  protected msWorking = 0;
  protected uiMode: 'upload' | 'query' = 'upload';
  private repeatSub: Subscription | null = null;

  constructor(
    private imageService: ImageService,
    private destroyRef: DestroyRef,
    private router: Router
  ) {}

  protected onFileInputChange($event: Event): void {    
	this.result = null;
    const files = !$event.target
      ? null
      : ($event.target as HTMLInputElement).files;
    this.imageForm.controls.file.setValue(
      !!files && files.length > 0 ? files[0] : null
    );
  }

  protected createImageUrl(myResult: ImageFile): ImageFile {
    if (!!myResult) {
      myResult.b64Image =
        'data:image/' + myResult?.imageType + ';base64,' + myResult?.b64Image;
    }
    return myResult;
  }

  protected switchToUpload(): void {
    this.uiMode = 'upload';
  }

  protected switchToQuery(): void {
    this.uiMode = 'query';
  }

  protected query(): void {
    //console.log(this.queryControl.invalid);
    //console.log(this.queryControl.untouched);
    const formData = new FormData();
    formData.append('query', this.queryControl.value as unknown as string);
    formData.append('type', '');
    this.imageService
      .postQueryForm(formData)
      .subscribe(
        (myResults) =>
          (this.results = myResults.map(
            (myResult) => (myResult = this.createImageUrl(myResult))
          ))
      );
  }

  protected reset(): void {
	this.result = null;
	this.imageForm.controls['file'].reset();
  }
  
  protected upload(): void {
    //console.log(this.file);
    if (!!this.imageForm.controls.file.value) {
      const startDate = new Date();
      this.msWorking = 0;
      this.result = null;
      this.uploading = true;
      this.repeatSub?.unsubscribe();
      this.repeatSub = interval(100)
        .pipe(
          map(() => new Date()),
          takeUntilDestroyed(this.destroyRef)
        )
        .subscribe(
          (newDate) =>
            (this.msWorking = newDate.getTime() - startDate.getTime())
        );
      const formData = new FormData();
      const myFile = this.imageForm.controls.file.value;
      formData.append('file', myFile as Blob, myFile?.name as string);
      formData.append(
        'query',
        this.imageForm.controls.prompt.value as unknown as string
      );
      formData.append(
        'type',
        (this.imageForm.controls.file.value as unknown as File)?.type
      );
      //console.log(formData);
      //console.log(this.imageForm.controls.file.value);
      //console.log(this.imageForm.controls.query.value);
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
          this.result = this.createImageUrl(result);
          this.imageForm.controls.file.setValue(null);
          //console.log(result);
        });
    } else if (!this.uploading && !!this.result) {
      this.result = null;
    }
  }

  protected showList(): void {
    this.router.navigate(['/doclist']);
  }

  protected logout(): void {
    console.log('logout');
  }
}
