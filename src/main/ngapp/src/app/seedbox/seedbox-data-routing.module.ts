import { AuthAdminGuard } from '../auth/auth-admin-guard';
import { AuthGuard } from '../auth/auth-guard';
import { MainComponent } from '../main/main.component';
import { SeedboxDataComponent } from './seedbox-data.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const mainRoutes: Routes = [
  {
    path: '',
    component: MainComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'seedboxes/new',
        component: SeedboxDataComponent
      },
      {
        path: 'seedboxes/:id',
        component: SeedboxDataComponent
      }
    ]
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(mainRoutes)
  ],
  exports: [
    RouterModule
  ],
  providers: [
    AuthGuard
  ]
})
export class SeedboxDataRoutingModule { }