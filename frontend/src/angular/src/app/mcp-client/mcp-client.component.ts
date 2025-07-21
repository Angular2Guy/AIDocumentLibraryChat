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
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import { Router } from '@angular/router';

@Component({
  selector: 'app-mcp-client',
  imports: [
    CommonModule,
		MatToolbarModule,
		MatButtonModule,
		MatInputModule,
		MatFormFieldModule
	],
  templateUrl: './mcp-client.component.html',
  styleUrl: './mcp-client.component.scss'
})
export class McpClientComponent {
  constructor(private readonly router: Router) {}
  
  protected showList(): void {
    this.router.navigate(['/doclist']);
  }

  protected logout(): void {
  }
}
