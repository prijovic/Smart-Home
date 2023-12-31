import { Component, Input, OnInit } from '@angular/core';
import { User } from '../../../../../shared/model/user.model';
import {
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  Observable,
} from 'rxjs';
import { FormControl, FormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { selectUserEmailsSearchResults } from '../../../../../users/store/users.selectors';
import { searchUserEmails } from '../../../../../users/store/users.actions';
import { addPropertyMember } from '../../../../store/properties.actions';
import { ConfirmationDialogComponent } from '../../../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-property-profile-members',
  templateUrl: './property-profile-members.component.html',
  styleUrls: ['./property-profile-members.component.scss'],
})
export class PropertyProfileMembersComponent implements OnInit {
  @Input() members!: User[];
  @Input() owner!: User;
  @Input() propertyId!: string;
  users$!: Observable<Array<User>>;
  userIdGroup = new FormGroup({
    email: new FormControl(''),
    id: new FormControl(''),
  });

  constructor(private store: Store, private dialog: MatDialog) {}

  ngOnInit() {
    this.users$ = this.store
      .select(selectUserEmailsSearchResults)
      .pipe(
        map((value) =>
          value.filter(
            (user) =>
              this.members.filter((member) => member.id === user.id).length !==
              1
          )
        )
      );
    this.initEmailAutocompleteOnChange();
  }

  private initEmailAutocompleteOnChange() {
    this.userIdGroup.controls['email'].valueChanges
      .pipe(
        debounceTime(25),
        distinctUntilChanged(),
        filter(
          (value: string | null): value is string =>
            value !== null && value !== ''
        ),
        map((value: string) => value.trim())
      )
      .subscribe((value: string) => {
        this.store.dispatch(searchUserEmails({ value: value }));
      });
  }

  addMember() {
    const id = this.userIdGroup.controls['id'].value;
    if (id) {
      const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
        data: {
          title: 'New Property Member',
          text: `Are you sure you want to add a new member to the property?`,
        },
      });

      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          this.store.dispatch(
            addPropertyMember({ propertyId: this.propertyId, userId: id })
          );
        }
      });
    }
  }
}
