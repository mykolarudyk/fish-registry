import { Routes } from '@angular/router';
import { FishListComponent } from './components/fish/fish-list/fish-list.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'fish' },
  { path: 'fish', component: FishListComponent },
  { path: '**', redirectTo: 'fish' }
];
