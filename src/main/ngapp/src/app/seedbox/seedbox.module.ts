import { CRUDResourceService } from '../resource/crud-resource-service';
import { SeedboxDataRoutingModule } from './seedbox-data-routing.module';
import { SeedboxDataComponent } from './seedbox-data.component';
import { SeedboxListComponent } from './seedbox-list.component';
import { NgModule }       from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

@NgModule({
  imports: [
    SeedboxDataRoutingModule,
    BrowserModule,
    FormsModule
  ],
  declarations: [
    SeedboxListComponent,
    SeedboxDataComponent
  ],
  providers : [CRUDResourceService]
})
export class SeedboxModule {}