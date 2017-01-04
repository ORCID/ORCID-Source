/*
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */

angular.module('orcidApp').directive('appFileTextReader', function($q){
    var slice = Array.prototype.slice;
    return {
        restrict: 'A',
        require: 'ngModel',
        scope: {
            updateFn: '&'
        },
        link: function(scope, element, attrs, ngModelCtrl){
            console.log("directive loaded");
            if(!ngModelCtrl) return;
            ngModelCtrl.$render = function(){};
            element.bind('change', function(event){
                var element = event.target;
                $q.all(slice.call(element.files, 0).map(readFile))
                .then(function(values){
                    if(element.multiple){
                        for(v in values){
                            ngModelCtrl.$viewValue.push(values[v]);
                        }
                    }
                    else{
                        ngModelCtrl.$setViewValue(values.length ? values[0] : null);
                    }
                    scope.updateFn(scope);
                    element.value = null;
                });
                function readFile(file) {
                    var deferred = $q.defer();
                    var reader = new FileReader();
                    reader.onload = function(event){
                        deferred.resolve(event.target.result);
                    };
                    reader.onerror = function(event) {
                        deferred.reject(event);
                    };
                    reader.readAsText(file);
                    return deferred.promise;
                }
            });//change
        }//link
    };//return
});//appFilereader