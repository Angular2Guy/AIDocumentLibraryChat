import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-function-search',
  standalone: true,
  imports: [CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatInputModule,
    MatTooltipModule,
    MatFormFieldModule,
    FormsModule,
    ReactiveFormsModule,
    MatProgressSpinnerModule],
  templateUrl: './function-search.component.html',
  styleUrl: './function-search.component.scss'
})
export class FunctionSearchComponent {
	  protected searchValueControl = new FormControl('', [
    Validators.required,
    Validators.minLength(3),
  ]);
	constructor(private router: Router) { }
	
	protected showList(): void {
		this.router.navigate(['/doclist']);
	}
	
	protected search(): void {
		console.log('search');
	}
	
	protected logout(): void {
		console.log('logout');
	}
}
