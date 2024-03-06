import {
  Controller,
  Get,
  Post,
  Redirect,
  Request,
  UseGuards,
} from '@nestjs/common';
import { AuthService } from './auth/auth.service';
import { JwtAuthGuard } from './auth/jwt-auth.guard';
import { LocalAuthGuard } from './auth/local-auth.guard';
import { CoreUrl } from './constants/apiLocation.constant';
@Controller()
export class AppController {
  constructor(private authService: AuthService) {}

  @UseGuards(LocalAuthGuard)
  @Post(`${CoreUrl.API_PATH}/${CoreUrl.LOGIN}`)
  async login(@Request() req) {
    return this.authService.login(req.user);
  }

  @Get(`${CoreUrl.API_PATH}/${CoreUrl.LOGIN}`)
  @Redirect(`${CoreUrl.IP_PATH}`, 301)
  async fwLogin() {
    //no-op
  }

  @Get(`${CoreUrl.API_PATH}/${CoreUrl.REQUEST_ACCESS}`)
  @Redirect(`${CoreUrl.IP_PATH}`, 301)
  async fwLRegister() {
    //no-op
  }

  @UseGuards(JwtAuthGuard)
  @Post(`${CoreUrl.API_PATH}/${CoreUrl.LOGOUT}`)
  async logout(@Request() req) {
    return this.authService.logout(req.user);
  }

  // @UseGuards(JwtAuthGuard)
  @Get(`${CoreUrl.ADMIN}/${CoreUrl.MANAGE}`)
  async admin(@Request() req) {
    return [{ user1: 'user1' }, { user2: 'user2' }];
  }

  @UseGuards(JwtAuthGuard)
  @Get('user1')
  async user1(@Request() req) {
    return {
      user1: 'user1',
    };
  }

  @UseGuards(JwtAuthGuard)
  @Get(`${CoreUrl.API_PATH}/${CoreUrl.AUTH}/${CoreUrl.REFRESH}`)
  async refreshToken(@Request() req) {
    return this.authService.login(req.user);
  }

  @UseGuards(JwtAuthGuard)
  @Get(`${CoreUrl.API_PATH}/${CoreUrl.ME}`)
  async getProfile(@Request() req) {
    return this.authService.findUser(req.user.username);
  }
}
