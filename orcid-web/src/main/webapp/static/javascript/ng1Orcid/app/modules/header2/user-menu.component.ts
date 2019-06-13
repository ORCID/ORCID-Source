declare var getWindowWidth: any;

//Import all the angular components


import { AfterViewInit, Component, OnDestroy, OnInit, ChangeDetectorRef, HostListener } 
    from '@angular/core';

import { Subject } 
    from 'rxjs';
    
import { takeUntil } 
    from 'rxjs/operators';
    
import { NotificationsService } 
    from '../../shared/notifications.service'; 

import { CommonService } 
    from '../../shared/common.service';
    
import { FeaturesService }
    from '../../shared/features.service';

@Component({
    selector: 'user-menu',
    template: scriptTmpl("user-menu-template")
})
export class UserMenuComponent  {
    state = false
    assetsPath = ''
    constructor(private commonSrvc: CommonService) {
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                this.assetsPath = data.messages['STATIC_PATH'];     
            },
            error => {
                console.log('header.component.ts: unable to fetch configInfo', error);                
            } 
        );

    }

}
