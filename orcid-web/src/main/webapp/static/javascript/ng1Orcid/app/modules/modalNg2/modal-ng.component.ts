declare var $: any;

import { NgFor, NgIf } 
    from '@angular/common'; 
 
import { AfterViewInit, Component, ElementRef, Input, OnInit, OnDestroy, Output } 
    from '@angular/core';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription } 
    from 'rxjs/Subscription';

import { EmailService } 
    from '../../shared/emailService.ts';

import { ModalService } 
    from '../../shared/modalService.ts'; 

@Component(
    {
        selector: '[modalngcomponent]',
        template: `
          <div [hidden]="!showModal" >
              <div class="popover-ng2-bck"></div>
              <div
                  class="popover-ng2-content"
                  id="colorbox" 
                  role="dialog" 
                  style="transition: width 2s, height 2s;"
                  tabindex="-1" 
                  [ngStyle]="{
                      'height': this.elementHeight + 'px',
                      'left': 'calc(50% - ' + this.elementWidth/2 + 'px)',
                      'top': 'calc(50% - ' + this.elementHeight/2 + 'px)',
                      'width': this.elementWidth + 'px'
                  }"
              >
                <div id="cboxWrapper" 
                [ngStyle]="{
                      'height': this.elementHeight + 'px',
                      'width': this.elementWidth + 'px'
                  }">
                  <div>
                    <div id="cboxTopLeft" style="float: left;"></div>
                    <div id="cboxTopCenter" style="float: left;"
                        [ngStyle]="{
                              'width': this.elementWidth + 'px'
                          }"
                    ></div>
                    <div id="cboxTopRight" style="float: left;"></div>
                  </div>
                  <div style="clear: left;">
                    <div id="cboxMiddleLeft" style="float: left;"
                        [ngStyle]="{
                              'height': this.elementHeight + 'px'
                          }"
                    ></div>
                    <div id="cboxContent" style="float: left;"
                        [ngStyle]="{
                              'height': this.elementHeight + 'px',
                              'width': this.elementWidth + 'px'
                          }">
                      <div id="cboxLoadedContent" style=" overflow: auto;"
                          [ngStyle]="{
                              'height': this.elementHeight + 'px',
                              'width': this.elementWidth + 'px'
                          }"
                      >
                        <div class="lightbox-container">

                          <ng-content></ng-content>
                
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
          </div> 
        `
    }
)
export class ModalNgComponent implements AfterViewInit, OnDestroy, OnInit {
    @Input() elementId: any;
    @Input() elementHeight: any;
    @Input() elementWidth: any;

    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    showModal: boolean;
    
    constructor( 
        private elementRef: ElementRef, 
        private emailService: EmailService, 
        private modalService: ModalService 
    ){
        this.elementHeight = elementRef.nativeElement.getAttribute('elementHeight');
        this.elementId = elementRef.nativeElement.getAttribute('elementId');
        this.elementWidth = elementRef.nativeElement.getAttribute('elementWidth');
        this.showModal = false;
    }

    closeModal(): void{
        console.log('close modal');
        this.showModal = false;
    };

    formColorBoxWidth(): string {
        console.log("isMobile()? '100%': '800px'", isMobile()? '100%': '800px');
        return isMobile()? '100%': '800px';
    };

    formColorBoxResize(): void {
        if ( isMobile() ) {
            console.log('isMobile');
            $.colorbox.resize(
                {
                    height: '100%',
                    width: this.formColorBoxWidth()
                }
            );
        }
        else {
            console.log('notmobile');
            $.colorbox.resize(
                {
                    width:'800px'
                }
            );
            
        }
    };

    openModal(): void{
        this.showModal = true;
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.modalService.notifyObservable$.subscribe(
            (res) => {
                //console.log('res.value',res, this.elementId);
                if ( res.moduleId == this.elementId ) {
                    if ( res.action === "close") {
                        this.closeModal();
                    }

                    if ( res.action === "open") {
                        this.openModal();
                    }

                }
            }
        );
    };

    ngOnInit() {
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}