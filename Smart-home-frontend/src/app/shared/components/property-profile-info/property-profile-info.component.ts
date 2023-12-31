import { Component, Input } from '@angular/core';
import { Property } from '../../model/property';

@Component({
  selector: 'app-property-profile-info',
  templateUrl: './property-profile-info.component.html',
  styleUrls: ['./property-profile-info.component.scss'],
})
export class PropertyProfileInfoComponent {
  @Input() property!: Property;
  @Input() isAdminLogged!: boolean;
  @Input() viewReport = false;
}
