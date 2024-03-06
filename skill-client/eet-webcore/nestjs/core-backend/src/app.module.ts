import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { AuthModule } from './auth/auth.module';
import { TreeViewController } from './tree-view/TreeViewController';
import { GroupController } from './groups/GroupController';
import { UsersModule } from './users/users.module';
import { GroupService } from './groups/group.service';

@Module({
  imports: [AuthModule, UsersModule],
  controllers: [AppController, TreeViewController, GroupController],
  providers: [AppService, GroupService],
})
export class AppModule {}
