import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthToken } from '../model/auth-token.model';
import { APP_SERVICE_CONFIG, AppConfig } from '../../app-config/app-config';

@Injectable({
  providedIn: 'root',
})
export class AuthHttpService {
  private LOGIN_FIRST_STEP = 'auth/login-details-exist';
  private LOGIN_SECOND_STEP = 'auth/login';
  private LOGOUT = 'auth/logout';
  private SIGN_UP = 'auth/register';
  private EMAIL_CONFIRMATION = 'auth/activateEmail/';

  constructor(
    @Inject(APP_SERVICE_CONFIG) private config: AppConfig,
    private http: HttpClient
  ) {}

  sendFirstStepLoginRequest(email: string, password: string) {
    return this.http.post<number>(
      this.config.apiEndpoint + this.LOGIN_FIRST_STEP,
      {
        email,
        password,
      }
    );
  }

  sendSecondStepLoginRequest(email: string, password: string, pin: string) {
    return this.http.post<AuthToken>(
      this.config.apiEndpoint + this.LOGIN_SECOND_STEP,
      {
        email,
        password,
        pin,
      },
      {
        withCredentials: true,
      }
    );
  }

  sendLogoutRequest() {
    return this.http.post(this.config.apiEndpoint + this.LOGOUT, {});
  }

  sendSignUpRequest(email: string, password: string, role: string) {
    return this.http.post(this.config.apiEndpoint + this.SIGN_UP, {
      email,
      password,
      role,
    });
  }

  sendEmailConfirmationRequest(token: string) {
    return this.http.put(
      this.config.apiEndpoint + this.EMAIL_CONFIRMATION + token,
      {}
    );
  }
}
