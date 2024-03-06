import { Body, Controller, Get, Param, Post, Response } from '@nestjs/common';
import { GroupCreateDto } from './group.model';
import { GroupService } from './group.service';
import { CoreUrl } from 'src/constants/apiLocation.constant';
@Controller(`${CoreUrl.API_PATH}/${CoreUrl.GROUP}`)
export class GroupController {
  constructor(private groupService: GroupService) {}

  @Get()
  getFile() {
    return this.groupService.getAllGroups();
  }

  @Get(':groupId')
  getGroupDetail(@Param('groupId') groupId: number) {
    return this.groupService.getGroupDetail(groupId);
  }

  @Post()
  async createGroups(
    @Response({ passthrough: true }) res,
    @Body() createGroup: GroupCreateDto,
  ) {
    res?.set({
      'Content-Type': 'application/json',
    });
    return this.groupService.createGroup(createGroup);
  }
}
