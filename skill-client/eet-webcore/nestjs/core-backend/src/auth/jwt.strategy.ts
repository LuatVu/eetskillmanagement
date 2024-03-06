import { Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { Request } from 'express';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { jwtConstants } from './constants';

const AUTH_HEADER = 'CSRF-TOKEN'.toLowerCase();
const BEARER_AUTH_SCHEME = 'bearer';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor() {
    super({
      jwtFromRequest: ExtractJwt.fromExtractors([localExtractor]),
      ignoreExpiration: false,
      secretOrKey: jwtConstants.secret,
    });
  }

  async validate(payload: any) {
    return { userId: payload.sub, username: payload.username };
  }
}

function parseAuthHeader(hdrValue) {
  const re = /(\S+)\s+(\S+)/;
  if (typeof hdrValue !== 'string') {
    return null;
  }
  const matches = hdrValue.match(re);
  return matches && { scheme: matches[1], value: matches[2] };
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
function localExtractor(request: Request): string | null {
  const auth_scheme_lower = BEARER_AUTH_SCHEME;

  let token = null;
  if (request.headers[AUTH_HEADER]) {
    const auth_params = parseAuthHeader(request.headers[AUTH_HEADER]);
    if (auth_params && auth_scheme_lower === auth_params.scheme.toLowerCase()) {
      token = auth_params.value;
    }
  }
  return token;
}
