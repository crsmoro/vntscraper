import { CRUDResourceService } from '../resource/crud-resource-service';
import { SettingsComponent } from './settings.component';
import { Component, Input, Host } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Http, Headers } from '@angular/http';
import { Observable } from 'rxjs/Observable';


@Component({
  selector: 'settings-general-component',
  templateUrl: './settings-general.html'
})
export class SettingsGeneralComponent {

  private languages = [{ code: 'pt-BR', label: 'Português (Brasil)' }, { code: 'pt-PT', label: 'Português (Portugal)' }, { code: 'es-ES', label: 'Español' }, { code: 'en-US', label: 'English (United States)' }];

  private parent: SettingsComponent;

  private tmdbLanguages: any = [];

  @Input() general: any;

  constructor(@Host() parent: SettingsComponent, private resource : CRUDResourceService) {
    this.parent = parent;
    this.resource.setUrl('Preferences.vnt');
  }

  addLanguage(code: string) {
    if (!this.general.tmdbLanguages) {
      this.general.tmdbLanguages = [];
    }
    if (!this.languageAlreayExists(code)) {
      this.general.tmdbLanguages.push({ order: this.general.tmdbLanguages.length + 1, language: code });
    }
  }

  removeLanguage(idx: number) {
    this.general.tmdbLanguages.splice(idx, 1);
    this.updatePositions();
  }

  upLanguage(idx: number) {
    if (idx > 0) {
      let tmp = this.general.tmdbLanguages[idx - 1];
      this.general.tmdbLanguages[idx - 1] = this.general.tmdbLanguages[idx];
      this.general.tmdbLanguages[idx] = tmp;
    }
    this.updatePositions();
  }

  downLanguage(idx: number) {
    if (idx < this.general.tmdbLanguages.length) {
      let tmp = this.general.tmdbLanguages[idx + 1];
      this.general.tmdbLanguages[idx + 1] = this.general.tmdbLanguages[idx];
      this.general.tmdbLanguages[idx] = tmp;
    }
    this.updatePositions();
  }

  updatePositions() {
    for (let idx in this.general.tmdbLanguages) {
      this.general.tmdbLanguages[idx].order = Number(idx) + 1;
    }
  }

  findLanguage(code: string) {
    for (let language of this.languages) {
      if (language.code == code) {
        return language;
      }
    }
    return null;
  }

  languageAlreayExists(code: string): boolean {
    for (let tmdbLanguages of this.general.tmdbLanguages) {
      if (tmdbLanguages.language == code) {
        return true;
      }
    }
    return false;
  }

  cancel() {
    this.parent.settingsGeneral = null;
  }

  onSubmit(preferencesForm: NgForm) {
    console.log(this.general);
    if (preferencesForm.valid) {
      this.resource.save(this.general).subscribe(res => { this.cancel() }, err => alert('Erro ao salvar os dados'));;
    }
    return false;
  }
}