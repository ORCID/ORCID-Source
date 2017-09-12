declare var $: any;
 
import { AfterViewInit, Component, OnInit, OnDestroy/*Component, EventEmitter, Input, NgModule, Output*/ } 
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
                <ng-content></ng-content>
            </div>
        `
    }
)
export class ModalNgComponent implements AfterViewInit, OnDestroy, OnInit {
    //@Output() onOpen = new EventEmitter<void>();
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    emailPrimary: string;
    
    constructor( private emailService: EmailService, private modalService: ModalService ){
        this.emailPrimary = '';
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
                console.log('getEmailPrimary()', this.emailService.getEmailPrimary().value);
                this.emailPrimary = this.emailService.getEmailPrimary().value;
            },
            error => {
                console.log('getEmails', error);
            } 
        );
    }

    openModal(): void{
        $.colorbox({
            //html: $('#edit-country').html(),
            html: '<div class="lightbox-container" id="modal-email-unverified"><div class="row"><div class="col-md-12 col-xs-12 col-sm-12"><h4>' + om.get("orcid.frontend.workspace.your_primary_email") + '</h4><p>' + om.get("orcid.frontend.workspace.ensure_future_access") + '</p><p>' + om.get("orcid.frontend.workspace.ensure_future_access2") + '<br /><strong>' + this.emailPrimary + '</strong></p><p>' + om.get("orcid.frontend.workspace.ensure_future_access3") + ' <a target="orcid.frontend.link.url.knowledgebase" href="' + om.get("orcid.frontend.link.url.knowledgebase") + '">' + om.get("orcid.frontend.workspace.ensure_future_access4") + '</a> ' + om.get("orcid.frontend.workspace.ensure_future_access5") + ' <a target="orcid.frontend.link.email.support" href="mailto:' + om.get("orcid.frontend.link.email.support") + '">' + om.get("orcid.frontend.link.email.support") + '</a>.</p><div class="topBuffer"><button class="btn btn-primary" id="modal-close" ng-click="verifyEmail()" onClick="$.colorbox.close()">' + om.get("orcid.frontend.workspace.send_verification") + '</button><a class="cancel-option inner-row" (click)="this.closeModal(); console.log("closemodalbtn");" onClick="$.colorbox.close()">' + om.get("orcid.frontend.freemarker.btncancel") + '</a></div></div></div></div>',
            onComplete: function() {   
            },
            onClosed: function() {
            },            
            onLoad: function() {
                $('#cboxClose').remove();           
            },
            scrolling: true,
            //width: this.formColorBoxWidth(),
            width: '500px'
        });
        $.colorbox.resize();
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnInit() {
        this.subscription = this.modalService.notifyObservable$.subscribe(
            (res) => {
                console.log('res.value',res);
                if ( res === "open") {
                    this.openModal();
                }
            }
        );
        
        this.getEmails();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}