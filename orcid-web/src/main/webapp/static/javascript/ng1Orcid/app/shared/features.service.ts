import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';



@Injectable()
export class FeaturesService {
    
    private features = orcidVar.features;
    
     constructor(){
        
    }
    
    isFeatureEnabled(featureName: string) : boolean {
        if (this.features[featureName]) {
            orcidGA.gaPush(['send', 'event', 'feature', featureName, 'enabled']);
            return true;
        }
        else {
            orcidGA.gaPush(['send', 'event', 'feature', featureName, 'disabled']);
            return false;
        }
    }

}
