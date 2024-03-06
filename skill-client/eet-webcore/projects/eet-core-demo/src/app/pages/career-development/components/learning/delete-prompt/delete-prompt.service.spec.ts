import { TestBed } from '@angular/core/testing';

import { DeletePromptService } from './delete-prompt.service';

describe('DeletePromptService', () => {
  let service: DeletePromptService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DeletePromptService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
