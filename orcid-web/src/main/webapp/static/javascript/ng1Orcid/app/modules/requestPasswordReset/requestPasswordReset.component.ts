declare var om: any;

import { NgFor, NgIf } 
    from '@angular/common';

import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit } 
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
    selector: 'request-password-reset-ng2',
    template:  scriptTmpl("request-password-reset-ng2-template"),
})
export class RequestPasswordResetComponent {
    resetPasswordToggleText: any;

    constructor(
    ) { 
        this.resetPasswordToggleText = om.get("login.forgotten_password");
    }

    
}