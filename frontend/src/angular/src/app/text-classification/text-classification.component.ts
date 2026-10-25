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
import {Component, DestroyRef, inject, signal} from '@angular/core';
import {Router} from "@angular/router";
import {MatIconModule} from "@angular/material/icon";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from "@angular/material/button";
import {ReactiveFormsModule} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {ClassificationService} from "../service/classification.service";
import {tap} from "rxjs";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {JsonPipe} from "@angular/common";
import {ClassificationSummary} from "../model/classification-summary";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";

@Component({
  selector: 'app-text-classification',
  imports: [
    JsonPipe,
    MatIconModule,
    MatToolbarModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatInputModule],
  templateUrl: './text-classification.component.html',
  styleUrl: './text-classification.component.scss',
})
export class TextClassificationComponent {
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly classificationService = inject(ClassificationService);
  private readonly noFileName = "No file uploaded";
  protected filename = signal(this.noFileName);
  protected importFile = signal<Blob | null>(null);
  protected working = signal<boolean>(false);
  protected result = signal<ClassificationSummary | null>(null);

  protected docList(): void {
    this.router.navigate(['doclist']);
  }

  protected logout(): void {
    console.log('logout');
  }

  protected onFileSelected($event: Event): void {
  const files = !$event.target
    ? null
    : ($event.target as HTMLInputElement).files;
  this.filename.set(!!files && files.length > 0 ? files[0].name : this.noFileName)
    this.importFile.set(!!files && files.length > 0 ? files[0] : null);

  if(!!this.importFile()) {
    const formData = new FormData();
    formData.append('file', (this.importFile() as Blob));
    this.classificationService.postFile(formData)
      .pipe(
        tap(() => {
          this.working.set(true);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((result) => {
        this.working.set(false);
        this.result.set(result);
      });
  }
}
}
