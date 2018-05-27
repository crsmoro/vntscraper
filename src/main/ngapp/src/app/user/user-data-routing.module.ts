import { AuthAdminGuard } from '../auth/auth-admin-guard';
import { AuthGuard } from '../auth/auth-guard';
import { MainComponent } from '../main/main.component';
import { UserDataComponent } from './user-data.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const mainRoutes: Routes = [
  {
    path: '',
    component: MainComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'users/new',
        component: UserDataComponent,
        canActivate : [AuthAdminGuard]
      },
      {
        path: 'users/:id',
        component: UserDataComponent,
        canActivate : [AuthAdminGuard]
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
    AuthAdminGuard,
    AuthGuard
  ]
})
export class UserDataRoutingModule { }