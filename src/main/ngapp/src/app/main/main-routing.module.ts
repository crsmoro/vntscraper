import { AuthAdminGuard } from '../auth/auth-admin-guard';
import { AuthGuard } from '../auth/auth-guard';
import { AuthService } from '../auth/auth.service';
import { LoginComponent } from '../login/login.component';
import { SearchComponent } from '../search/search.component';
import { SettingsComponent } from '../settings/settings.component';
import { SeedboxListComponent } from '../seedbox/seedbox-list.component';
import { TrackerUserListComponent } from '../trackeruser/trackeruser-list.component';
import { JobListComponent } from '../job/job-list.component';
import { UserListComponent } from '../user/user-list.component';
import { MainComponent } from './main.component';
import { NgModule } from '@angular/core';

import { RouterModule, Routes } from '@angular/router';

const mainRoutes: Routes = [
    {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
    },
    {
        path: '',
        component: MainComponent,
        canActivate: [AuthGuard],
        children: [
            {
                path: 'home',
                component: SearchComponent
            },
            {
                path: 'jobs',
                component: JobListComponent
            },
            {
                path: 'trackerusers',
                component: TrackerUserListComponent
            },
            {
                path: 'seedboxes',
                component: SeedboxListComponent
            },
            {
                path: 'users',
                component: UserListComponent,
                canActivate: [AuthAdminGuard]
            },
            {
                path: 'settings',
                component: SettingsComponent,
                canActivate: [AuthAdminGuard]
            }
        ]
    }
];

@NgModule( {
    imports: [
        RouterModule.forChild( mainRoutes )
    ],
    exports: [
        RouterModule
    ],
    providers: [
        AuthAdminGuard,
        AuthGuard,
        AuthService
    ]
} )
export class MainRoutingModule { }