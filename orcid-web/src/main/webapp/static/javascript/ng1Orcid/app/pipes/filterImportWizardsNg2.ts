import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "filterImportWizards"
})

@Injectable()
export class FilterImportWizardsPipe implements PipeTransform {
    transform(input: any, selectedWorkType: any, selectedGeoArea: any): any {
        var output = [];   
        var all = om.get('workspace.works.import_wizzard.all');
        if(selectedWorkType == all && selectedGeoArea == all) {
            output = input;
        } else {
            for(var i = 0; i < input.length; i ++) {
                if (selectedWorkType == all){
                    if (contains(input[i].geoAreas, selectedGeoArea)){
                        output.push(input[i]);
                    }
                }else if(selectedGeoArea == all){
                    if (contains(input[i].actTypes, selectedWorkType)){
                        output.push(input[i]);
                    }                       
                }else{                                      
                    if (contains(input[i].actTypes, selectedWorkType) && contains(input[i].geoAreas, selectedGeoArea)){
                        output.push(input[i]);
                    }
                }                
            }           
        }
        return output;
    }
}
