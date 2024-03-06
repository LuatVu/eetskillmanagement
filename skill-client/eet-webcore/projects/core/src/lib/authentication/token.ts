import { HEADER_TYP, TOKEN_TYPE } from '../shared/util/system.constant';
import { base64, capitalize, currentTimestamp, timeLeft } from './helpers';
import { Token } from './interface';

export abstract class BaseToken {
  constructor(protected attributes: Token) {}

  get access_token(): string {
    return this.attributes.access_token;
  }
  get user_name() {
    return this.attributes.name
  }
  get refresh_token(): string | void {
    return this.attributes.refresh_token;
  }

  get token_type(): string {
    if (!this.attributes.token_type) {
      return TOKEN_TYPE;
    }
    return this.attributes.token_type.toLowerCase() === 'jwt' ? TOKEN_TYPE : this.attributes.token_type;
  }

  get exp(): number | void {
    return this.attributes.exp;
  }
  get expires_in():number {
    return this.attributes.token?.expires_in
  }
  valid(): boolean {
    return this.hasAccessToken();
  }
  
  getBearerToken(): string {
    return this.access_token
      ? [capitalize(this.token_type), this.access_token].join(' ').trim()
      : '';
  }

  needRefresh(): boolean {
    return this.exp !== undefined && this.exp >= 0;
  }
  
  getRefreshTime(): number {
    return timeLeft((this.exp ?? 0) - 5);
  }
 
  private hasAccessToken(): boolean {
    return !!this.access_token;
  }

  private isExpired(): boolean {
    return this.exp !== undefined && this.exp - currentTimestamp() <= 0;
  }
}

export class SimpleToken extends BaseToken {}

export class JwtToken extends SimpleToken {
  private _payload?: { exp?: number | void };

  static is(accessToken: string): boolean {
    try {
      const [_header] = accessToken.split('.');
      const header = JSON.parse(base64.decode(_header));

      return header.typ.toUpperCase().includes(`${HEADER_TYP}`);
    } catch (e) {
      return false;
    }
  }

  override get exp(): number | void {
    return this.payload?.exp;
  }

  private get payload(): { exp?: number | void } {
    if (!this.access_token) {
      return {};
    }

    if (this._payload) {
      return this._payload;
    }

    const [, payload] = this.access_token.split('.');
    const data = JSON.parse(base64.decode(payload));
    if (!data.exp) {
      data.exp = this.attributes.exp;
    }

    return (this._payload = data);
  }
}
