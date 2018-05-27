import { CRUDResourceService } from '../resource/crud-resource-service';
import { TrackerUserDataRoutingModule } from './trackeruser-data-routing.module';
import { TrackerUserDataComponent } from './trackeruser-data.component';
import { TrackerUserListComponent } from './trackeruser-list.component';
import { NgModule }       from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

@NgModule({
  imports: [
    TrackerUserDataRoutingModule,
    BrowserModule,
    FormsModule
  ],
  declarations: [
    TrackerUserListComponent,
    TrackerUserDataComponent
  ],
  providers : [CRUDResourceService]
})
export class TrackerUserModule {}