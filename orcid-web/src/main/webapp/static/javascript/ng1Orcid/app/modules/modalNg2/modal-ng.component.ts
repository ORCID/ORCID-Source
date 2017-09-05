declare var $: any;
 
import { Component, OnInit, OnDestroy/*Component, EventEmitter, Input, NgModule, Output*/ } 
    from '@angular/core';

import { Subscription } 
    from 'rxjs/Subscription';

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
export class ModalNgComponent implements OnInit, OnDestroy {
    //@Output() onOpen = new EventEmitter<void>();

    private subscription: Subscription;
    
    constructor( private modalService: ModalService ){
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

    openModal(): void{
        $.colorbox({
            //html: $('#edit-country').html(),
            html: '<div class="lightbox-container" id="modal-email-unverified"><div class="row"><div class="col-md-12 col-xs-12 col-sm-12"><h4>' + om.get("orcid.frontend.workspace.your_primary_email") + '</h4><p>' + om.get("orcid.frontend.workspace.ensure_future_access") + '</p><p>' + om.get("orcid.frontend.workspace.ensure_future_access2") + '<br /><strong>' + 'scope.emailPrimary' + '</strong></p><p>' + om.get("orcid.frontend.workspace.ensure_future_access3") + ' <a target="orcid.frontend.link.url.knowledgebase" href="' + om.get("orcid.frontend.link.url.knowledgebase") + '">' + om.get("orcid.frontend.workspace.ensure_future_access4") + '</a> ' + om.get("orcid.frontend.workspace.ensure_future_access5") + ' <a target="orcid.frontend.link.email.support" href="mailto:' + om.get("orcid.frontend.link.email.support") + '">' + om.get("orcid.frontend.link.email.support") + '</a>.</p><div class="topBuffer"><button class="btn btn-primary" id="modal-close" ng-click="verifyEmail()">' + om.get("orcid.frontend.workspace.send_verification") + '</button><a class="cancel-option inner-row" ng-click="closeColorBox()">' + om.get("orcid.frontend.freemarker.btncancel") + '</a></div></div></div></div>',
            onComplete: function() {   
            },
            onClosed: function() {
            },            
            onLoad: function() {
                $('#cboxClose').remove();           
            },
            scrolling: true,
            width: this.formColorBoxWidth(),
        });
        $.colorbox.resize();
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
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}