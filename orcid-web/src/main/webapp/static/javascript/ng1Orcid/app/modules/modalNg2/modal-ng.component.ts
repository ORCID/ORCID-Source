declare var $: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 
 
import { AfterViewInit, Component, ElementRef, Input, OnInit, OnDestroy, Output } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component(
    {
        selector: 'modalngcomponent',
        template:  scriptTmpl("modal-ng2-template")
    }
)
export class ModalNgComponent implements AfterViewInit, OnDestroy, OnInit {
    @Input() elementId: any;
    @Input() elementHeight: any;
    @Input() elementWidth: any;
    @Input() setFocus?: any;

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
        this.setFocus = elementRef.nativeElement.getAttribute('setFocus');
        this.showModal = false;
    }

    closeModal(): void{
        //$('body').removeClass('overflow-hidden');
        this.showModal = false;
    };

    formColorBoxWidth(): string {
        //console.log("isMobile()? '100%': '800px'", isMobile()? '100%': '800px');
        return isMobile()? '100%': '800px';
    };

    formColorBoxResize(): void {
        if ( isMobile() ) {
            ////console.log('isMobile');
            $.colorbox.resize(
                {
                    height: '100%',
                    width: this.formColorBoxWidth()
                }
            );
        }
        else {
            ////console.log('notmobile');
            $.colorbox.resize(
                {
                    width:'800px'
                }
            );
            
        }
    };

    openModal(): void{
        //$('body').addClass('overflow-hidden');
        this.showModal = true;
        if (this.setFocus == 'true') {
            setTimeout(
                function(){ 
                    $('#cboxContent input.focusInput').focus();
                },
                1000
            );
        }
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