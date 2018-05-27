import { AuthAdminGuard } from '../auth/auth-admin-guard';
import { AuthGuard } from '../auth/auth-guard';
import { MainComponent } from '../main/main.component';
import { TrackerUserDataComponent } from './trackeruser-data.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const mainRoutes: Routes = [
  {
    path: '',
    component: MainComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'trackerusers/new',
        component: TrackerUserDataComponent
      },
      {
        path: 'trackerusers/:id',
        component: TrackerUserDataComponent
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
export class TrackerUserDataRoutingModule { }