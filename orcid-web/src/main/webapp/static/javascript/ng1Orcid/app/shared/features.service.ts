import { Injectable } 
    from '@angular/core';

@Injectable()
export class FeaturesService {
    
    private features = orcidVar.features;
    
     constructor(){
        
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
