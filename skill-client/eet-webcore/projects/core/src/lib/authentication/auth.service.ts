import { Injectable } from '@angular/core';
import { BehaviorSubject, delay, finalize, iif, merge, Observable, of } from 'rxjs';
import { catchError, map, share, switchMap, tap } from 'rxjs/operators';
import { TokenService } from './token.service';
import { LoginService } from './login.service';
import { filterObject, isEmptyObject } from './helpers';
import { Token, User } from './interface';
import { CoreUrl } from '../shared/util/url.constant';
import { HttpClient } from '@angular/common/http';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private user$ = new BehaviorSubject<User>({});
  private change$ = merge(
    this.tokenService.change(),
    this.tokenService.refresh().pipe(switchMap(() => this.refresh()))
  ).pipe(
    switchMap((token) => this.assignUser(token)),
    share()
  );

  constructor(
    private loginService: LoginService,
    private tokenService: TokenService,
    protected http: HttpClient,
    private comLoader:LoadingService,
  ) { }

  init() {
    return new Promise<void>((resolve) =>
      this.change$.subscribe(() => resolve())
    );
  }

  change() {
    return this.change$;
  }

  check() {
    return this.tokenService.valid();
  }

  loginSso(): Observable<any> {
    const loader = this.comLoader.showProgressBar()
    return this.loginService.loginSso().pipe(
      map((token: any) => {
        if (token !== null && token?.data) {
          const userSession = token.data.token;
          if (token.data.access_token == null && userSession) {
            token.data.access_token = userSession.access_token;
            token.data.token_type = userSession.token_type;
          }
        }
        return token;
      }),
      tap((token: any) => {
        if (token?.code == 'SUCCESS') {
          this.tokenService.set(token?.data);
        }
      }
      ),
      finalize(() => {
        this.comLoader.hideProgressBar(loader)
      })
    );
  }
  refresh() {
    return this.loginService
      .refresh(
        filterObject({ refreshToken: this.tokenService.getRefreshToken(), username: this.tokenService.getUserName() })
      )
      .pipe(
        catchError(() => of(undefined)),
        tap((token) => {
          if(token?.code==="SUCCESS") {
            this.tokenService.setNewAccessToken(token)
          }
        }),
        map(() => this.check())
      );
  }
  login(username: string, password: string) {
    return this.loginService.login(username, password).pipe(
      map((token) => {
        if (token !== null) {
          const userSession = token.data.token;
          if (token.data.access_token == null && userSession) {
            token.data.access_token = userSession.access_token;
            token.data.token_type = userSession.token_type;

          }
        }
        return token.data;
      }),
      tap((token) => {
        this.tokenService.set(token);
      }),
      map(() => this.check())
    );
  }

  logout() {
    return this.loginService.logout().pipe(
      tap(() => this.tokenService.clear()),
      map(() => !this.check())
    );
  }

  user() {
    return this.user$.pipe(share());
  }

  private assignUser(token?: any): Observable<User> {
    if (!this.check()) {
      return of({}).pipe(tap((user) => this.user$.next(user)));
    }

    if (!isEmptyObject(this.user$.getValue())) {
      return of(this.user$.getValue());
    }

    if (token == null) {
      token = this.tokenService.getStoredToken();
    }
    const userSession = token.attributes;
    if (typeof token === 'object' && userSession) {
      const user: User = {
        id: userSession.id,
        name: userSession.name,
        displayName: userSession.displayName,
        email: userSession.email,
        permissions: userSession.permissions
      };

      return of(user).pipe(delay(100)); // For UI transition to be smoother
      // Blueprint had this line by default.
    }

    return this.loginService.me().pipe(tap((user) => this.user$.next(user)));
  }
}
