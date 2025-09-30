import { Routes } from '@angular/router';
import { FishListComponent } from './features/fish/fish-list/fish-list.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'fish' },
   {
    path: 'fish',
    loadComponent: () =>
      import('./features/fish/fish-list/fish-list.component')
        .then(m => m.FishListComponent)
  },
  { path: '**', redirectTo: 'fish' }
];
