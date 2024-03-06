import { Injectable, OnDestroy } from '@angular/core';
import {
  BehaviorSubject,
  Observable,
  Subject,
  Subscription,
  timer,
} from 'rxjs';
import { share } from 'rxjs/operators';
import { Token } from './interface';
import { BaseToken } from './token';
import { TokenFactory } from './token-factory.service';
import { currentTimestamp, filterObject, timeLeft } from './helpers';
import { LocalStorageService } from '../shared/services/storage.service';
import { TOKEN_KEY, TOKEN_TYPE } from '../shared/util/system.constant';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';
import { HttpClient } from '@angular/common/http';
import { CoreUrl } from '../shared/util/url.constant';

@Injectable({
  providedIn: 'root',
})
export class TokenService implements OnDestroy {
  private key = `${TOKEN_KEY}`;

  private change$ = new BehaviorSubject<BaseToken | undefined>(undefined);
  private refresh$ = new Subject<BaseToken | undefined>();
  private timer$?: Subscription;

  private _token?: BaseToken;

  constructor(
    private store: LocalStorageService,
    private factory: TokenFactory,
    private permisisonService: PermisisonService,
    protected http: HttpClient
  ) { }

  private get token(): BaseToken | undefined {
    if (!this._token) {
      this._token = this.factory.create(this.store.get(this.key));
    }
    return this._token;
  }

  change(): Observable<BaseToken | undefined> {
    return this.change$.pipe(share());
  }

  refresh(): Observable<BaseToken | undefined> {
    this.buildRefresh();

    return this.refresh$.pipe(share());
  }
 
  set(token?: Token): TokenService {
    this.save(token);
    return this;
  }
 
  clear(): void {
    this.save();
  }

  valid(): boolean {
    return this.token?.valid() ?? false;
  }

  getBearerToken(): string {
    return this.token?.getBearerToken() ?? '';
  }

  getRefreshToken(): string | void {
    return this.token?.refresh_token;
  }
  getUserName() {
    return this._token?.user_name
  }
  ngOnDestroy(): void {
    this.clearRefresh();
  }

  getStoredToken() {
    return this._token;
  }
  refreshToken(param:any) {
    return this.http.post<Token>(
      `${CoreUrl.API_PATH}/${CoreUrl.AUTH}/${CoreUrl.REFRESH_TOKEN}`,
      param
    );
}
  private save(token?: Token): void {
    this._token = undefined;
    if (!token) {
      this.store.remove(this.key);
    } else {
      const value = Object.assign(
        {
          id: token.id,
          name: token.name,
          displayName: token.display_name,
          access_token: token.access_token, token_type: `${TOKEN_TYPE}`
        },
        token,
        {
          exp: token.token.expires_in ? currentTimestamp() + token.token.expires_in : null,
          refresh_token: token.token.refresh_token
        },
      );
      this.permisisonService.setPermission((token['permissions'] as Array<any>).map(m => m.code));
      this.store.set(this.key, filterObject(value));
    }
    this.change$.next(this.token);
    this.buildRefresh();
  }
  setNewAccessToken(token:any):void{
    this._token = undefined;
    const getLocalStorage = this.store.get(this.key);
    let value = this.store.get(this.key)
    if (!token) {
      this.store.remove(this.key);
    } else {
      value = {
        ...getLocalStorage,
        access_token: token.data.access_token, token_type: `${TOKEN_TYPE}`,
        token: {
          exp: token.data.expires_in ? currentTimestamp() + token.data.expires_in : null,
          refresh_token: token.data.refresh_token,
          expires_in: token.data.expires_in
        }
      }
      this.permisisonService.setPermission((value['permissions'] as Array<any>).map(m => m.code));
      this.store.set(this.key, filterObject(value));
    }
    this.buildRefresh();
  }
  private buildRefresh() {
    this.clearRefresh();
    if (this.token?.needRefresh()) {
      this.timer$ = timer((this.token.expires_in-5) * 1000).subscribe(() => {
        this.refresh$.next(this.token);
      });
    }
  }

  private clearRefresh() {
    if (this.timer$ && !this.timer$.closed) {
      this.timer$.unsubscribe();
    }
  }
}
