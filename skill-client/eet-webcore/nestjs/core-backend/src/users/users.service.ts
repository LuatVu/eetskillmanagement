import { Injectable } from '@nestjs/common';
// This should be a real class/interface representing a user entity
export type User = any;

@Injectable()
export class UsersService {
  private readonly users = [
    {
      userId: 1,
      username: 'user1',
      password: 'user1',
      displayName: 'User 1',
    },
    {
      userId: 2,
      username: 'user2',
      password: 'user2',
      displayName: 'User 2',
    },
    {
      userId: 3,
      username: 'UTN7HC',
      password: 'ASDFGHJKL;\'',
      displayName: 'FIXED-TERM NGUYEN TIEN THIEN THANH',
    },
  ];

  async findOne(username: string): Promise<User | undefined> {
    return this.users.find((user) => user.username === username);
  }
}
