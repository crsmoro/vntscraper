import { CRUDResourceService } from '../resource/crud-resource-service';
import { SettingsEmailComponent } from './settings-email.component';
import { SettingsGeneralComponent } from './settings-general.component';
import { SettingsComponent } from './settings.component';
import { NgModule }       from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

@NgModule({
  imports : [
    FormsModule,
    BrowserModule
  ],
  declarations: [
    SettingsComponent,
    SettingsGeneralComponent,
    SettingsEmailComponent
  ],
  providers : [CRUDResourceService]
})
export class SettingsModule {}