import { CRUDResourceService } from '../resource/crud-resource-service';
import { JobDataRoutingModule } from './job-data-routing.module';
import { JobDataComponent } from './job-data.component';
import { JobListComponent } from './job-list.component';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { NgSelectModule } from '@ng-select/ng-select';

@NgModule( {
    imports: [
        JobDataRoutingModule,
        BrowserModule,
        FormsModule,
        NgSelectModule
    ],
    declarations: [
        JobListComponent,
        JobDataComponent
    ],
    providers: [CRUDResourceService],
    exports: [JobDataComponent]
} )
export class JobModule {}