import { CRUDResourceService } from '../resource/crud-resource-service';
import { SettingsComponent } from './settings.component';
import { Component, Input, Host } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Observable } from 'rxjs/Observable';


@Component({
  selector: 'settings-email-component',
  templateUrl: './settings-email.html'
})
export class SettingsEmailComponent {

  private parent: SettingsComponent;

  @Input() general: any;

  constructor(@Host() parent: SettingsComponent, private resource : CRUDResourceService) {
    this.parent = parent;
    resource.setUrl('Preferences.vnt');
  }

  cancel() {
    this.parent.settingsEmail = null;
  }

  onSubmit(preferencesForm: NgForm) {
    console.log(this.general);
    if (preferencesForm.valid) {
      this.resource.save(this.general).subscribe(res => { this.cancel() }, err => alert('Erro ao salvar os dados'));
    }
    return false;
  }
}