import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { HighlightTextPipe } from './highlight-text.pipe';

@NgModule({
  declarations: [HighlightTextPipe],
  imports: [CommonModule],
  exports: [HighlightTextPipe],
})
export class EetPipeModule {}
