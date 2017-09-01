import { Injectable } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class ConfigurationService {
    private configValues: object;
    private paramVerifyEdit: boolean;
    private paramVerifyEditRegex: any;
    
    constructor(){
        this.configValues = {
            propertyManualEditVerificationEnabled: orcidVar.emailVerificationManualEditEnabled,
            showModalManualEditVerificationEnabled: true
        };
        this.paramVerifyEditRegex = /.*\?(.*\&)*(verifyEdit){1}(=true){0,1}(?!=false)((\&){1}.+)*/g;
        this.paramVerifyEdit = this.paramVerifyEditRegex.test( window.location );

        if( this.paramVerifyEdit == true ){
            this.configValues['showModalManualEditVerificationEnabled'] = true;
        } 
    }

    getInitialConfiguration(): object{
        return this.configValues;
    }

}