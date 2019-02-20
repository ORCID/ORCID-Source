import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { CommonService } 
     from './common.service.ts'; 

@Injectable()
export class FeaturesService {
    
    private features = orcidVar.features;
    private done = false;
    
     constructor(private commonSrvc: CommonService){
         /*
         this.commonSrvc.configInfo$
         .subscribe(
             data => {
                 this.features = data.messages;  
                 this.done = true;
             },
             error => {
                 console.log('features.service.ts: unable to fetch configInfo', error);                
             } 
         );
         */
    }
    
    isFeatureEnabled(featureName: string) : boolean {
        /*         
         while(!done) {
            console.log('Awaiting for features');
            window.setTimeout(10)
        }
        */
        if (this.features[featureName]) {
            return true;
        }
        else {
            return false;
        }
    }

}
