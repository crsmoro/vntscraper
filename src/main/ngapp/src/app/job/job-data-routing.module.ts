import { AuthAdminGuard } from '../auth/auth-admin-guard';
import { AuthGuard } from '../auth/auth-guard';
import { MainComponent } from '../main/main.component';
import { JobDataComponent } from './job-data.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const mainRoutes: Routes = [
  {
    path: '',
    component: MainComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'jobs/new',
        component: JobDataComponent
      },
      {
        path: 'jobs/:id',
        component: JobDataComponent
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
export class JobDataRoutingModule { }