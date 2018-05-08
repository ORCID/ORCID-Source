import {Directive, Input, EventEmitter, ElementRef, Renderer, Inject, OnInit} from '@angular/core';
 
@Directive({
  selector: '[focusMe]'
})
export class FocusMe {
  @Input('focusMe') focusEvent: EventEmitter<boolean>;
 
  constructor(@Inject(ElementRef) private element: ElementRef, private renderer: Renderer) {
  }
 
  ngOnInit() {
    console.log("directive oninit");
    this.focusEvent.subscribe(event => {
      console.log(event);
      this.renderer.invokeElementMethod(this.element.nativeElement, 'focus', []);
    });
  }
}