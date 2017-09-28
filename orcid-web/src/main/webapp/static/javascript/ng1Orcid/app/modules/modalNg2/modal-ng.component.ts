declare var $: any;

import { NgFor, NgIf } 
    from '@angular/common'; 
 
import { AfterViewInit, Component, ElementRef, Input, OnInit, OnDestroy, Output/*Component, EventEmitter, Input, NgModule, */ } 
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
            <div class="lightbox-container">
                ++{{elementId}}++
                <ng-content></ng-content>
            </div>
        `
    }
)
export class ModalNgComponent implements AfterViewInit, OnDestroy, OnInit {
    @Input() elementId: any;
    @Input() elementWidth: any;

    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    emailPrimary: string;
    showModal: boolean;
    
    constructor( private elementRef: ElementRef, private emailService: EmailService, private modalService: ModalService ){
        this.emailPrimary = '';
        this.elementId = elementRef.nativeElement.getAttribute('elementId');
        this.elementWidth = elementRef.nativeElement.getAttribute('elementWidth');
        this.showModal = false;
    }

    closeModal(): void{
        console.log('close modal');
        $.colorbox.close();
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

    getEmails(): any {
        this.emailService.getEmails()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.emailPrimary = this.emailService.getEmailPrimary().value;
            },
            error => {
                console.log('getEmails', error);
            } 
        );
    }

    openModal(): void{
        this.showModal = true;
        console.log('showmodal');
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
                if ( res.action === "open" 
                    && res.moduleId == this.elementId ) {
                    this.openModal();
                }
            }
        );
        
        this.getEmails();
    };

    ngOnInit() {
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}