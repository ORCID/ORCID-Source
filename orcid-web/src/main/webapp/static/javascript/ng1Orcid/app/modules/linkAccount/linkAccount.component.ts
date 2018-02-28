declare var orcidGA: any;

//Import all the angular components

import { NgFor, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { OauthService } 
    from '../../shared/oauth.service.ts'; 


@Component({
    selector: 'link-account-ng2',
    template:  scriptTmpl("link-account-ng2-template")
})
export class LinkAccountComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    @Input() entityId: any;

    //entityId: string;
    gaString: string;
    requestInfoForm: any;
   
    constructor(
        private oauthService: OauthService
    ) {
        //this.entityId = "";
        this.gaString = "";
        this.requestInfoForm = {};
    }

    /* Pending ***
    $scope.$watch(function() { return discoSrvc.feed; }, function(){
        $scope.idpName = discoSrvc.getIdPName($scope.entityId);
        if(discoSrvc.feed != null) {
            $scope.loadedFeed = true;
        }
    });
    */

    linkAccount(idp, linkType): boolean {
        let eventAction = linkType === 'shibboleth' ? 'Sign-In-Link-Federated' : 'Sign-In-Link-Social';
        orcidGA.gaPush(['send', 'event', 'Sign-In-Link', eventAction, idp]);
        return false;
    };

    setEntityId(entityId): void {
        this.entityId = entityId;
    };

    loadRequestInfoForm = function() {
        this.adminDelegatesService.getFormData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data){                     
                    this.requestInfoForm = data;              
                    this.gaString = orcidGA.buildClientString(this.requestInfoForm.memberName, this.requestInfoForm.clientName);
                }
            },
            error => {
                //console.log('getformDataError', error);
            } 
        );
    };
    

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.loadRequestInfoForm();
        this.setEntityId(this.entityId);
    }; 
}