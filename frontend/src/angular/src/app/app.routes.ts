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
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'doclist',
    loadChildren: () => import('./doc-list').then((mod) => mod.DOCLIST),
  },
  {
    path: 'docsearch',
    loadChildren: () => import('./doc-search').then((mod) => mod.DOCSEARCH),
  },
  {
    path: 'tablesearch',
    loadChildren: () => import('./table-search').then((mod) => mod.TABLESEARCH),
  },
  {
    path: 'functionsearch',
    loadChildren: () =>
      import('./function-search').then((mod) => mod.FUNCTIONSEARCH),
  },
  {
    path: 'imagequery',
    loadChildren: () => import('./image-query').then((mod) => mod.IMAGEQUERY),
  },
  {
    path: 'bookimport',
    loadChildren: () => import('./book-import').then((mod) => mod.BOOKIMPORT),
  },
  {
    path: 'booklist',
    loadChildren: () => import('./book-list').then((mod) => mod.BOOKLIST),
  },
  {
    path: 'mcpclient',
    loadChildren: () => import('./mcp-client').then((mod) => mod.MCPCLIENTSEARCH),
  },
  { path: '**', redirectTo: 'doclist' },
];
