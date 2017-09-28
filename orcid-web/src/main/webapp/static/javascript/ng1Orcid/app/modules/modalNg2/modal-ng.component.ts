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
              <div
                  
                  id="colorbox" 
                  role="dialog" 
                  tabindex="-1" 
                  style="display: block; visibility: visible; top: 127px; left: 703px; position: absolute; width: 500px; height: 247px;"
              >
                <div id="cboxWrapper" style="height: 247px; width: 500px;">
                  <div>
                    <div id="cboxTopLeft" style="float: left;"></div>
                    <div id="cboxTopCenter" style="float: left; width: 500px;"></div>
                    <div id="cboxTopRight" style="float: left;"></div>
                  </div>
                  <div style="clear: left;">
                    <div id="cboxMiddleLeft" style="float: left; height: 247px;"></div>
                    <div id="cboxContent" style="float: left; width: 500px; height: 247px;">
                      <div id="cboxLoadedContent" style="width: 500px; overflow: auto; height: 247px;">
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
    @Input() elementWidth: any;

    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    showModal: boolean;
    
    constructor( 
        private elementRef: ElementRef, 
        private emailService: EmailService, 
        private modalService: ModalService 
    ){
        this.elementId = elementRef.nativeElement.getAttribute('elementId');
        this.elementWidth = elementRef.nativeElement.getAttribute('elementWidth');
        this.showModal = false;
    }

    closeModal(): void{
        console.log('close modal');
        //$.colorbox.close();
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
        /*
        console.log('elementId', this.elementId);
        $.colorbox({
            html: $('#modal-email-unverified').html(),
            onComplete: function() {   
            },
            onClosed: function() {
            },            
            onLoad: function() {
                $('#cboxClose').remove();           
            },
            scrolling: true,
            width: this.elementWidth + 'px'
        });
        $.colorbox.resize();
        */
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