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
         
    }
    
    isFeatureEnabled(featureName: string) : boolean {
        if (this.features[featureName]) {
            return true;
        }
        else {
            return false;
        }
    }

}
