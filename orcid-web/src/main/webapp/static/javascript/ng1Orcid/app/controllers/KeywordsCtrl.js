angular.module('orcidApp').controller(
    'KeywordsCtrl', 
    [
    '$scope', 
    '$rootScope', 
    '$compile', 
    'bioBulkSrvc', 
    'commonSrvc', 
    'emailSrvc', 
    'initialConfigService',
    'utilsService',  
    function ($scope, $rootScope, $compile, bioBulkSrvc, commonSrvc, emailSrvc, initialConfigService, utilsService) {
    bioBulkSrvc.initScope($scope);
    $scope.commonSrvc = commonSrvc;
    $scope.defaultVisibility = null;
    $scope.emailSrvc = emailSrvc;
    $scope.keywordsForm = null;
    $scope.modal = false;
    $scope.newElementDefaultVisibility = null;
    $scope.orcidId = orcidVar.orcidId; //Do not remove
    $scope.privacyHelp = false;
    $scope.scrollTop = 0;    
    $scope.showEdit = false;
    $scope.showElement = {};
    
    /////////////////////// Begin of verified email logic for work
    var configuration = initialConfigService.getInitialConfiguration();
    var emailVerified = false;
    var emails = {};
    var utilsService = utilsService;


    var showEmailVerificationModal = function(){
        $rootScope.$broadcast('emailVerifiedObj', {flag: emailVerified, emails: emails});
    };
    
    $scope.emailSrvc.getEmails(
        function(data) {
            emails = data.emails;
            if( $scope.emailSrvc.getEmailPrimary().verified == true ) {
                emailVerified = true;
            }
        }
    );
    /////////////////////// End of verified email logic for work

    $scope.openEdit = function() {
        $scope.addNew();
        $scope.showEdit = true;
    };

    $scope.close = function() {
        $scope.getKeywordsForm();
        $scope.showEdit = false;
    };

    $scope.updateDisplayIndex = function(){
        for (var idx in $scope.keywordsForm.keywords)
            $scope.keywordsForm.keywords[idx]['displayIndex'] = $scope.keywordsForm.keywords.length - idx;
    };
    
    $scope.addNew = function() {
        $scope.keywordsForm.keywords.push({content: "", displayIndex: "1"});        
        $scope.updateDisplayIndex();
    };
    
    $scope.addNewModal = function() {                
        var tmpObj = {"errors":[],"putCode":null,"content":"","visibility":{"errors":[],"required":true,"getRequiredMessage":null,"visibility":$scope.newElementDefaultVisibility},"displayIndex":1,"source":$scope.orcidId,"sourceName":""};
        $scope.keywordsForm.keywords.push(tmpObj);
        $scope.updateDisplayIndex();
        $scope.newInput = true;
    };

    $scope.getKeywordsForm = function(){
        $.ajax({
            url: getBaseUri() + '/my-orcid/keywordsForms.json',
            dataType: 'json',
            success: function(data) {
                $scope.keywordsForm = data;
                $scope.newElementDefaultVisibility = $scope.keywordsForm.visibility.visibility;
                //If there is at least one element, iterate over them to see if they have the same visibility, to set the default  visibility element                
                if($scope.keywordsForm != null && $scope.keywordsForm.keywords != null && $scope.keywordsForm.keywords.length > 0) {
                    for(var i = 0; i < $scope.keywordsForm.keywords.length; i ++) {
                        var itemVisibility = null;
                        if($scope.keywordsForm.keywords[i].visibility != null && $scope.keywordsForm.keywords[i].visibility.visibility) {
                            itemVisibility = $scope.keywordsForm.keywords[i].visibility.visibility;
                        }
                        /**
                         * The default visibility should be set only when all elements have the same visibility, so, we should follow this rules: 
                         * 
                         * Rules: 
                         * - If the default visibility is null:
                         *  - If the item visibility is not null, set the default visibility to the item visibility
                         * - If the default visibility is not null:
                         *  - If the default visibility is not equals to the item visibility, set the default visibility to null and stop iterating 
                         * */
                        if($scope.defaultVisibility == null) {
                            if(itemVisibility != null) {
                                $scope.defaultVisibility = itemVisibility;
                            }                           
                        } else {
                            if(itemVisibility != null) {
                                if($scope.defaultVisibility != itemVisibility) {
                                    $scope.defaultVisibility = null;
                                    break;
                                }
                            } else {
                                $scope.defaultVisibility = null;
                                break;
                            }
                        }                       
                    }
                } else {
                    $scope.defaultVisibility = $scope.keywordsForm.visibility.visibility;
                }
                                                                
                $scope.$apply();
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching keywords");
        });
    };

    $scope.deleteKeyword = function(keyword){
        var keywords = $scope.keywordsForm.keywords;
        var len = keywords.length;
        while (len--) {
            if (keywords[len] == keyword){
                keywords.splice(len,1);
            }
        }
    };

    $scope.setKeywordsForm = function(){        
        $scope.keywordsForm.visibility = null;        
        $.ajax({
            url: getBaseUri() + '/my-orcid/keywordsForms.json',
            type: 'POST',
            data:  angular.toJson($scope.keywordsForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.keywordsForm = data;
                
                if(data.errors.length == 0){
                    $scope.close();
                    $.colorbox.close();
                }                   
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("KeywordsCtrl.serverValidate() error");
        });
    };

    $scope.setPrivacy = function(priv, $event) {
        $event.preventDefault();
        $scope.defaultVisibility = priv;
    };
    
    $scope.setPrivacyModal = function(priv, $event, keyword) {        
        $event.preventDefault();
        
        var keywords = $scope.keywordsForm.keywords;        
        var len = keywords.length;
        
        while (len--) {
            if (keywords[len] == keyword){
                keywords[len].visibility.visibility = priv;
                $scope.keywordsForm.keywords = keywords;
            }
        }
    };
    
    $scope.openEditModal = function(){
        if(emailVerified === true || configuration.showModalManualEditVerificationEnabled == false){
            $scope.bulkEditShow = false;
            $scope.modal = true;        
            $.colorbox({
                scrolling: true,
                html: $compile($('#edit-keyword').html())($scope),
                onLoad: function() {
                    $('#cboxClose').remove();
                    if ($scope.keywordsForm.keywords.length == 0){
                        $scope.addNewModal();
                        $scope.newInput = true;
                        
                    } else{
                        $scope.updateDisplayIndex();
                    }
                },
                width: utilsService.formColorBoxResize(),
                onComplete: function() {
                        
                },
                onClosed: function() {
                    $scope.getKeywordsForm();
                }            
            });
            $.colorbox.resize();
        }else{
            showEmailVerificationModal();
        }
    }
    
    $scope.closeEditModal = function(){        
        $.colorbox.close();
    }
    
    $scope.swapUp = function(index){
        if (index > 0) {
            var temp = $scope.keywordsForm.keywords[index];
            var tempDisplayIndex = $scope.keywordsForm.keywords[index]['displayIndex'];
            temp['displayIndex'] = $scope.keywordsForm.keywords[index - 1]['displayIndex']
            $scope.keywordsForm.keywords[index] = $scope.keywordsForm.keywords[index - 1];
            $scope.keywordsForm.keywords[index]['displayIndex'] = tempDisplayIndex;
            $scope.keywordsForm.keywords[index - 1] = temp;
        }
    };

    $scope.swapDown = function(index){
        if (index < $scope.keywordsForm.keywords.length - 1) {
            var temp = $scope.keywordsForm.keywords[index];
            var tempDisplayIndex = $scope.keywordsForm.keywords[index]['displayIndex'];
            temp['displayIndex'] = $scope.keywordsForm.keywords[index + 1]['displayIndex']
            $scope.keywordsForm.keywords[index] = $scope.keywordsForm.keywords[index + 1];
            $scope.keywordsForm.keywords[index]['displayIndex'] = tempDisplayIndex;
            $scope.keywordsForm.keywords[index + 1] = temp;
        }
    };
    
    $scope.setBulkGroupPrivacy = function(priv) {
        for (var idx in $scope.keywordsForm.keywords){
            $scope.keywordsForm.keywords[idx].visibility.visibility = priv;        
        }
    };
    
    $scope.getKeywordsForm();
}]);