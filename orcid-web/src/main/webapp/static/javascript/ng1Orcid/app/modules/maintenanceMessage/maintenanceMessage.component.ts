import { AfterViewInit, Component, NgModule } 
    from '@angular/core';

import { CommonService } 
    from '../../shared/common.service.ts';    
    
@Component({
    selector: 'maintenance-ng2',
    template:  scriptTmpl("maintenance-message-ng2-template")
})
export class MaintenanceMessageComponent {
    
    maintenanceMessage: String;    
    visible: boolean = false;
    
    constructor(private commonSrvc: CommonService) {
        this.visible = commonSrvc.isPrintView(window.location.pathname);
        this.commonSrvc.configInfo$
        .subscribe(
            data => {
                this.maintenanceMessage = data.messages['MAINTENANCE_MESSAGE'];                
            },
            error => {
                console.log('maintenanceMessage.component.ts: unable to fetch configInfo', error);
            } 
        );
    }
}