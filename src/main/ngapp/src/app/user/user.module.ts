import { CRUDResourceService } from '../resource/crud-resource-service';
import { UserDataRoutingModule } from './user-data-routing.module';
import { UserDataComponent } from './user-data.component';
import { UserListComponent } from './user-list.component';
import { NgModule }       from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

@NgModule({
  imports: [
    UserDataRoutingModule,
    BrowserModule,
    FormsModule
  ],
  declarations: [
    UserListComponent,
    UserDataComponent
  ],
  providers : [CRUDResourceService]
})
export class UserModule {}