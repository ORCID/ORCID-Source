angular.module('orcidApp').controller('ExternalIdentifierCtrl', ['$scope', '$compile', 'bioBulkSrvc', 'commonSrvc', function ($scope, $compile, bioBulkSrvc, commonSrvc){
    bioBulkSrvc.initScope($scope);
    $scope.externalIdentifiersForm = null;
    $scope.orcidId = orcidVar.orcidId;
    $scope.primary = true;
    $scope.showElement = [];
    $scope.scrollTop = 0;
    $scope.commonSrvc = commonSrvc;
    
    $scope.getExternalIdentifiersForm = function(){
        $.ajax({
            url: getBaseUri() + '/my-orcid/externalIdentifiers.json',
            dataType: 'json',
            success: function(data) {
                $scope.externalIdentifiersForm = data;
                $scope.displayIndexInit();
                $scope.$apply();
            }
        }).fail(function(e){
            // something bad is happening!
            console.log("error fetching external identifiers");
            logAjaxError(e);
        });
    }

    $scope.setExternalIdentifiersForm = function(){     
        $scope.externalIdentifiersForm.visibility = null;
        $.ajax({
            url: getBaseUri() + '/my-orcid/externalIdentifiers.json',
            type: 'POST',
            data:  angular.toJson($scope.externalIdentifiersForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.externalIdentifiersForm = data;
                if ($scope.externalIdentifiersForm.errors.length == 0){                    
                    $scope.getExternalIdentifiersForm();                
                    $scope.closeEditModal();
                }else{
                    console.log($scope.externalIdentifiersForm.errors);
                }
                
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("ExternalIdentifierCtrl.serverValidate() error");
        });
    }
    
    
    $scope.setPrivacy = function(priv, $event) {
        $event.preventDefault();
        $scope.externalIdentifiersForm.visibility.visibility = priv;
    };
    
    $scope.setPrivacyModal = function(priv, $event, externalIdentifier) {        
        $event.preventDefault();        
        
        var externalIdentifiers = $scope.externalIdentifiersForm.externalIdentifiers;
        var len = externalIdentifiers.length;
        
        while (len--)
            if (externalIdentifiers[len] == externalIdentifier)            
                externalIdentifiers[len].visibility.visibility = priv;        
    };  
    
    
    $scope.openEditModal = function(){      
        $scope.bulkEditShow = false;
        $.colorbox({
            scrolling: true,
            html: $compile($('#edit-external-identifiers').html())($scope),
            onLoad: function() {
                $('#cboxClose').remove();
            },
            width: formColorBoxResize(),
            onComplete: function() {

            },
            onClosed: function() {
                $scope.getExternalIdentifiersForm();
            }
        });
        $.colorbox.resize();
    }
    
    $scope.deleteExternalIdentifierConfirmation = function(idx){
        $scope.removeExternalIdentifierIndex = idx;
        $scope.removeExternalModalText = $scope.externalIdentifiersForm.externalIdentifiers[idx].reference;
        if ($scope.externalIdentifiersForm.externalIdentifiers[idx].commonName != null)
            $scope.removeExternalModalText = $scope.externalIdentifiersForm.externalIdentifiers[idx].commonName + ' ' + $scope.removeExternalModalText;
        $.colorbox({
            html: $compile($('#delete-external-id-modal').html())($scope)
        });
        $.colorbox.resize();
    }
    
    $scope.removeExternalIdentifier = function() {
        var externalIdentifier = $scope.externalIdentifiersForm.externalIdentifiers[$scope.removeExternalIdentifierIndex];
        $.ajax({
            url: getBaseUri() + '/my-orcid/externalIdentifiers.json',
            type: 'DELETE',
            data: angular.toJson(externalIdentifier),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data.errors.length != 0){
                    console.log("Unable to delete external identifier.");
                } else {
                    $scope.externalIdentifiersForm.externalIdentifiers.splice($scope.removeExternalIdentifierIndex, 1);
                    $scope.removeExternalIdentifierIndex = null;
                    $scope.$apply();
                }
            }
        }).fail(function() {
            console.log("Error deleting external identifier.");
        });
        $.colorbox.close();
    };
    
    // Person 2
    $scope.deleteExternalIdentifier = function(externalIdentifier){
        var externalIdentifiers = $scope.externalIdentifiersForm.externalIdentifiers;
        var len = externalIdentifiers.length;
        while (len--) {
            if (externalIdentifiers[len] == externalIdentifier){
                externalIdentifiers.splice(len,1);
                $scope.externalIdentifiersForm.externalIdentifiers = externalIdentifiers;
            }       
        }
    };
    
    $scope.swapUp = function(index){
        if (index > 0) {
            var temp = $scope.externalIdentifiersForm.externalIdentifiers[index];
            var tempDisplayIndex = $scope.externalIdentifiersForm.externalIdentifiers[index]['displayIndex'];
            temp['displayIndex'] = $scope.externalIdentifiersForm.externalIdentifiers[index - 1]['displayIndex']
            $scope.externalIdentifiersForm.externalIdentifiers[index] = $scope.externalIdentifiersForm.externalIdentifiers[index - 1];
            $scope.externalIdentifiersForm.externalIdentifiers[index]['displayIndex'] = tempDisplayIndex;
            $scope.externalIdentifiersForm.externalIdentifiers[index - 1] = temp;
        }
    };

    $scope.swapDown = function(index){
        if (index < $scope.externalIdentifiersForm.externalIdentifiers.length - 1) {
            var temp = $scope.externalIdentifiersForm.externalIdentifiers[index];
            var tempDisplayIndex = $scope.externalIdentifiersForm.externalIdentifiers[index]['displayIndex'];
            temp['displayIndex'] = $scope.externalIdentifiersForm.externalIdentifiers[index + 1]['displayIndex']
            $scope.externalIdentifiersForm.externalIdentifiers[index] = $scope.externalIdentifiersForm.externalIdentifiers[index + 1];
            $scope.externalIdentifiersForm.externalIdentifiers[index]['displayIndex'] = tempDisplayIndex;
            $scope.externalIdentifiersForm.externalIdentifiers[index + 1] = temp;
        }
    };           
   
   // To fix displayIndex values that comes with -1
   $scope.displayIndexInit = function(){
       for (var idx in $scope.externalIdentifiersForm.externalIdentifiers) {            
           $scope.externalIdentifiersForm.externalIdentifiers[idx]['displayIndex'] = $scope.externalIdentifiersForm.externalIdentifiers.length - idx;
       }       
   };
    
   $scope.closeEditModal = function(){
       $.colorbox.close();
   };
   
   $scope.setBulkGroupPrivacy = function(priv) {
       for (var idx in $scope.externalIdentifiersForm.externalIdentifiers)     
           $scope.externalIdentifiersForm.externalIdentifiers[idx].visibility.visibility = priv;        
   };

   // init
   $scope.getExternalIdentifiersForm();  
}]);