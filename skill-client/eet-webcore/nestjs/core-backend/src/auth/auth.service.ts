import { Injectable } from '@nestjs/common';
import { UsersService } from '../users/users.service';
import { JwtService } from '@nestjs/jwt';
import { UserRequestModel } from 'src/users/users.model';
@Injectable()
export class AuthService {
  constructor(
    private usersService: UsersService,
    private jwtService: JwtService,
  ) {}

  async validateUser(username: string, pass: string): Promise<boolean | {username: string, password: string}> {
    const user = await this.usersService.findOne(username);
    if (user && user.password === pass) {
      const { password, ...result } = user;
      return result;
    }
    return false;
  }

  async findUser(username: string): Promise<{userId: Number, username: string, displayName: string}> {
    const user = await this.usersService.findOne(username);
    if (user) {
      const { ...result } = user;
      delete result.password;
      return result;
    }
    let emptyPromiseObject: Promise<{userId: Number, username: string, displayName: string}>;
    return await emptyPromiseObject;
  }

  async login(user: UserRequestModel) {
    const userInfo = await this.findUser(user.username)
    if (!this.validateUser(user.username, user.password)) {
      return {}
    }
    const payload = { username: userInfo.username, sub: userInfo.userId };
    return {
      access_token: this.jwtService.sign(payload),
      username: userInfo.username,
      displayName: userInfo.displayName
    };
  }

  async logout(user: any) {
    return {
      success: true,
    };
  }
}
