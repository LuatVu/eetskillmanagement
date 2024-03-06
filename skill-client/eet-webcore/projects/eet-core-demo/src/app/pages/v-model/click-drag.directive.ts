import { Directive, ElementRef, EventEmitter, HostListener, Input, Output } from '@angular/core';

const MIN_DRAG_DISTANCE = 4;
const DBL_CLICK_TIME = 500;

interface PointLike {
  x: number;
  y: number;
}


// Directive to handle pointer events and wrapping the logic to differentiate
// between a click, click drag, and double click.
// Emits a 'singleClick' event (opposed to the native 'click' event)
// when a click event without a drag move or followed by a double click.
// Emits a 'dragStart' when a pointer down is followed by a non-trivial mouse move.
// Emits a 'dragMove' on every pointer move following a 'dragStart'.
// Emits a 'dragEnd' following a 'dragStart' when the pointer is released.

@Directive({
  selector: '[appClickDrag]',
})
export class ClickDragDirective {

  @Output() singleClick = new EventEmitter<MouseEvent>();
  @Output() dragStart = new EventEmitter<MouseEvent>();
  @Output() dragMove = new EventEmitter<MouseEvent>();
  @Output() dragEnd = new EventEmitter<MouseEvent>();
  @Output() filter = new EventEmitter<{event: MouseEvent, ignore: boolean}>();

  private pointerDownEvent: PointerEvent | null;
  private dragging = false;
  private dragged = false;
  private doubleClicked = false;
  private clickPending = false;
  private ignored = false;

  constructor(private el: ElementRef) {
  }

  @HostListener('click', ['$event'])
  onClick(event: MouseEvent) {
    if (!this.dragged && !this.ignored) {
      const isFirstClick = event.detail === 1;
      if (isFirstClick) {
        // Wait for a double click duration in case this click is followed by a double.
        this.clickPending = true;
        setTimeout(() => {
          this.clickPending = false;
          if (!this.doubleClicked) {
            this.singleClick.emit(event);
          }
        }, DBL_CLICK_TIME);
      } else {
        this.doubleClicked = true;
      }
    }
  }

  @HostListener('pointerdown', ['$event'])
  onPointerDown(event: PointerEvent) {
    if (!this.clickPending) { // ignore rapid clicks
      this.pointerDownEvent = null;
      this.doubleClicked = this.dragged = this.dragging = this.ignored = false;
      const filterEvent = {event, ignore: false};
      this.filter.emit(filterEvent);
      this.ignored = filterEvent.ignore;
      if (event.button === 0 && !this.ignored) {
        this.pointerDownEvent = event;
        this.el.nativeElement.setPointerCapture(event.pointerId);
      }
    }
  }

  @HostListener('pointerup', ['$event'])
  onPointerUp(event: PointerEvent) {
    if (this.pointerDownEvent) {
      this.el.nativeElement.releasePointerCapture(this.pointerDownEvent.pointerId);
      this.pointerDownEvent = null;
      if (this.dragging) {
        this.dragging = false;
        this.dragEnd.emit(event);
      }
    }
  }

  @HostListener('pointermove', ['$event'])
  onPointerMove(event: PointerEvent) {
    if (this.pointerDownEvent) {
      if (this.dragging) {
        this.dragMove.emit(event);
      } else if (rectilinearDistance(this.pointerDownEvent, event) > MIN_DRAG_DISTANCE) {
        this.dragging = true;
        this.dragged = true;
        this.dragStart.emit(this.pointerDownEvent);
        this.dragMove.emit(event);
      }
    }
  }

}


function rectilinearDistance(a: PointLike, b: PointLike): number {
  return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
}
