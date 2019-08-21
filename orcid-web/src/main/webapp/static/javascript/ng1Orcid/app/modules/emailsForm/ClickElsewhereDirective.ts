import { Directive, EventEmitter, ElementRef, HostListener, Output } from '@angular/core';
 
@Directive({ selector: '[clickElsewhere]' })
export class ClickElsewhereDirective {
  @Output() clickElsewhere = new EventEmitter<MouseEvent>(); 
 
  constructor(private elementRef: ElementRef) {}
 
  @HostListener('document:click', ['$event'])
  public onDocumentClick(event: MouseEvent): void {
    const targetElement = event.target as HTMLElement;
 
      // Check if the click was outside the element
      if (targetElement && !this.elementRef.nativeElement.contains(targetElement) && targetElement.classList &&  targetElement.classList["value"]
      && targetElement.classList["value"].indexOf("glyphicon glyphicon-pencil") == -1) {
 
         this.clickElsewhere.emit(event);
      }
  }
}