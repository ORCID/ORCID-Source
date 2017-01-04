angular.module('orcidApp').filter("filterImportWizards", function(){ 
    return function(input, selectedWorkType, selectedGeoArea) {
        var output = [];        
        if(selectedWorkType == 'All' && selectedGeoArea == 'All'){
            output = input;
        }else{
            for(var i = 0; i < input.length; i ++) {
                for(var j = 0; j <  input[i].redirectUris.redirectUri.length; j ++) {
                    if (selectedWorkType == 'All'){
                        if (contains(input[i].redirectUris.redirectUri[j].geoArea['import-works-wizard'],selectedGeoArea)){
                            output.push(input[i]);
                        }
                    }else if(selectedGeoArea == 'All'){
                        if (contains(input[i].redirectUris.redirectUri[j].actType['import-works-wizard'],selectedWorkType)){
                            output.push(input[i]);
                        }                       
                    }else{                                      
                        if (contains(input[i].redirectUris.redirectUri[j].actType['import-works-wizard'],selectedWorkType) && contains(input[i].redirectUris.redirectUri[j].geoArea['import-works-wizard'],selectedGeoArea)){
                            output.push(input[i]);
                        }
                    }
                }
            }           
        }
        return output;
    };
});