import { Controller, Get, Response, StreamableFile } from '@nestjs/common';
import { createReadStream } from 'fs';
import { join } from 'path';
import { AuthService } from '../auth/auth.service';

@Controller()
export class TreeViewController {
  constructor(private authService: AuthService) {}

  @Get('tree-data')
  getFile(@Response({ passthrough: true }) res): StreamableFile {
    const file = createReadStream(
      join(process.cwd(), './data/full-tree-data.json'),
    );
    res.set({
      'Content-Type': 'application/json',
    });
    return new StreamableFile(file);
  }
}
