import { Injectable } from '@angular/core';
import { AuthHttpService } from '../services/auth-http.service';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import * as AuthActions from './auth.actions';
import { autoLoginFail, loginSuccess } from './auth.actions';
import { map, switchMap, tap } from 'rxjs';
import { Router } from '@angular/router';
import { NotifierService } from '../../core/notifier.service';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthEffects {
  loginFirstStep$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.loginFirstStep.type),
      switchMap((action) => {
        return this.httpService
          .sendFirstStepLoginRequest(action.email, action.password)
          .pipe(
            map((pinValidityTime) =>
              AuthActions.loginFirstStepSuccess({
                email: action.email,
                password: action.password,
                pinValidityTime,
              })
            )
          );
      })
    );
  });

  loginFirstStepSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AuthActions.loginFirstStepSuccess.type),
        tap(() => {
          this.router.navigate(['/auth/auth-confirm']);
        })
      );
    },
    { dispatch: false }
  );

  loginSecondStep$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.loginSecondStep.type),
      switchMap((action) => {
        return this.httpService
          .sendSecondStepLoginRequest(action.email, action.password, action.pin)
          .pipe(
            map((authToken) => {
              return AuthActions.loginSuccess({
                token: authToken.token,
              });
            })
          );
      })
    );
  });

  autoLogin$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.autoLogin.type),
      map(() => {
        const token = sessionStorage.getItem('token');
        if (!token) {
          return autoLoginFail();
        }
        return loginSuccess({ token });
      })
    )
  );

  loginSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AuthActions.loginSuccess.type),
        map((action) => {
          sessionStorage.setItem('token', action.token);
          this.authService.setLogoutTimer();
          this.router.navigate(['/']);
        })
      );
    },
    { dispatch: false }
  );

  logout$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.logout.type),
      switchMap(() => {
        return this.httpService
          .sendLogoutRequest()
          .pipe(map(() => AuthActions.logoutSuccess()));
      })
    );
  });

  logoutSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AuthActions.logoutSuccess.type),
        tap(() => {
          sessionStorage.clear();
          this.authService.clearLogoutTimer();
          this.router.navigate(['/auth/login']);
        })
      );
    },
    { dispatch: false }
  );

  signUp$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.signUp.type),
      switchMap((action) => {
        return this.httpService
          .sendSignUpRequest(action.email, action.password, action.role)
          .pipe(map(() => AuthActions.signUpSuccess()));
      })
    );
  });

  signUpSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AuthActions.signUpSuccess.type),
        map(() => {
          const message =
            'Your sign up request has been sent. Please go check your email.';
          this.notifierService.notifySuccess(message);
        })
      );
    },
    { dispatch: false }
  );

  confirmEmail$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.confirmEmail.type),
      switchMap((action) => {
        return this.httpService
          .sendEmailConfirmationRequest(action.token)
          .pipe(map(() => AuthActions.confirmEmailSuccess()));
      })
    );
  });

  confirmEmailSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AuthActions.confirmEmailSuccess.type),
        map(() => {
          const message =
            'You have successfully confirmed your email address. You may log in now.';
          this.notifierService.notifySuccess(message);
          this.router.navigate(['/auth/login']);
        })
      );
    },
    { dispatch: false }
  );

  constructor(
    private notifierService: NotifierService,
    private router: Router,
    private actions$: Actions<AuthActions.AuthActionsUnion>,
    private httpService: AuthHttpService,
    private authService: AuthService
  ) {}
}
