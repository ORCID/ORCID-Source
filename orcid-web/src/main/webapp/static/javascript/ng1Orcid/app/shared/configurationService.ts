import { Location, LocationStrategy, PathLocationStrategy } 
    from '@angular/common';

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
    
    constructor( /*private location: Location*/ ){
//        console.log( 'location', this.location );

        this.configValues = {
            propertyManualEditVerificationEnabled: orcidVar.emailVerificationManualEditEnabled,
            showModalManualEditVerificationEnabled: true
        };
        this.paramVerifyEditRegex = /.*\?(.*\&)*(verifyEdit){1}(=true){0,1}(?!=false)((\&){1}.+)*/g;
        //this.paramVerifyEdit = this.paramVerifyEditRegex.test( $location.absUrl() ); 
    }


    getInitialConfiguration(): object{
        return this.configValues;
    }

}

/*
angular.module('orcidApp').factory("initialConfigService", ['$rootScope', '$location', function ($rootScope, $location) {
    //location requires param after # example: https://localhost:8443/orcid-web/my-orcid#?flag Otherwise it doesn't found the param and returns an empty object


    
    

    

    if( paramVerifyEdit == true ){
        configValues.showModalManualEditVerificationEnabled = true;
    } 

    return initialConfigService;
}]);
*/