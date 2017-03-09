angular.module('orcidApp').controller('EmailEditCtrl', ['$scope', '$compile', 'emailSrvc' , 'bioBulkSrvc', 'initialConfigService', '$timeout', '$cookies', 'commonSrvc', function EmailEditCtrl($scope, $compile, emailSrvc, bioBulkSrvc, initialConfigService, $timeout, $cookies, commonSrvc) {
    bioBulkSrvc.initScope($scope);
    $scope.emailSrvc = emailSrvc;
    $scope.privacyHelp = {};
    $scope.verifyEmailObject;
    $scope.showElement = {};
    $scope.isPassConfReq = orcidVar.isPasswordConfirmationRequired;
    $scope.baseUri = orcidVar.baseUri;
    $scope.showDeleteBox = false;
    $scope.showConfirmationBox = false;
    $scope.showEmailVerifBox = false;
    $scope.showUnverifiedEmailSetPrimaryBox = false;
    $scope.commonSrvc = commonSrvc;
    $scope.scrollTop = 0;    

    $scope.toggleClickPrivacyHelp = function(key) {
        if (!document.documentElement.className.contains('no-touch'))
            $scope.privacyHelp[key]=!$scope.privacyHelp[key];
    };

    $scope.getEmails = function() {
        emailSrvc.getEmails(function() {
            if(isIE() == 7) $scope.fixZindexesIE7();
        });
    };
    
    $scope.$on('rebuildEmails', function(event, data) {
        emailSrvc.emails = data;
    });

    $scope.$on('unverifiedSetPrimary', function(event, data){
        if (data.newValue == true && configuration.showModalManualEditVerificationEnabled == true) {
            $scope.showUnverifiedEmailSetPrimaryBox = true;
            
        }
        else {
            $scope.showUnverifiedEmailSetPrimaryBox =false;
        }
        $scope.$apply(); 
    });

    //init
    $scope.password = null;
    $scope.curPrivToggle = null;
    emailSrvc.getEmails();
    emailSrvc.initInputEmail();
    //check if verify to edit manually is enabled
    var configuration = initialConfigService.getInitialConfiguration();

    $scope.fixZindexesIE7 =  function(){
        fixZindexIE7('.popover',2000);
        fixZindexIE7('.popover-help-container',3000);
        fixZindexIE7('#privacy-bar',500);
        fixZindexIE7('.emailVisibility',5000);
        fixZindexIE7('.col-md-3', 6000);
        fixZindexIE7('.row', 7000);
    };

    $scope.setPrivacy = function(email, priv, $event) {
        $event.preventDefault();
        email.visibility = priv;
        $scope.curPrivToggle = null;
        emailSrvc.saveEmail();
    };

    $scope.verifyEmail = function(email, popup) {
        
        $scope.verifyEmailObject = email;
        
        if(popup){
            emailSrvc.verifyEmail(email,function(data) {
                $scope.showEmailVerifBox = true;
                $scope.$apply();
                $.colorbox.resize();
           });    
        }else{
            emailSrvc.verifyEmail(email,function(data) {
                $.colorbox({
                    html : $compile($('#settings-verify-email-modal').html())($scope) 
                    //Name was changed to avoid conflicts with workspace verify email modal
                });
                $scope.$apply();
                $.colorbox.resize();
           });    
        }
        
    };

    $scope.closeModal = function() {
        
        angular.element('#cboxLoadedContent').css({         
            overflow: 'auto'
        });
        
        $.colorbox.close();
    };
    
    $scope.closeDeleteBox = function(){
        $scope.showDeleteBox = false;
    };
    
    $scope.closeVerificationBox = function(){
        $scope.showEmailVerifBox = false;
    };

    $scope.closeUnverifiedEmailSetPrimaryBox = function(){
        $scope.showUnverifiedEmailSetPrimaryBox = false;
    };

    $scope.submitModal = function (obj, $event) {
        emailSrvc.inputEmail.password = $scope.password;
        emailSrvc.addEmail();
        if(!$scope.emailSrvc.popUp){
            $.colorbox.close();    
        }
    };

    $scope.confirmDeleteEmail = function(email) {
        emailSrvc.delEmail = email;
        $.colorbox({
            html : $compile($('#delete-email-modal').html())($scope)
        });
        $.colorbox.resize();
    };
    
    $scope.confirmDeleteEmailInline = function(email, $event) {
        $event.preventDefault();
        $scope.showDeleteBox = true;
        emailSrvc.delEmail = email;
        
        $scope.$watch(
            function () {
                return document.getElementsByClassName('delete-email-box').length; 
            },
            function (newValue, oldValue) {             
                $.colorbox.resize();
            }
        );
    };

    $scope.deleteEmail = function () {
        emailSrvc.deleteEmail(function() {
            $scope.closeModal();
        });
    };
    
    $scope.deleteEmailInline = function () {
        emailSrvc.deleteEmail(function(){
            $scope.showDeleteBox = false;            
        });
    };

    $scope.checkCredentials = function(popup) {
        $scope.password = null;
        if(orcidVar.isPasswordConfirmationRequired){
            if (!popup){
                $.colorbox({
                    html: $compile($('#check-password-modal').html())($scope)
                });
                $.colorbox.resize();
            }else{
                $scope.showConfirmationBox = true;            
            }
        }else{
            $scope.submitModal();
        }
    };
    
    $scope.showTooltip = function(el, event){       
        $scope.position = angular.element(event.target.parentNode).parent().position();
        angular.element('.edit-record-emails .popover-help-container').css({            
            top: $scope.position.top + 33,
            left: $scope.position.left
        });
        angular.element('#cboxLoadedContent').css({         
            overflow: 'visible'
        });
        $scope.showElement[el] = true;
    };
    
    $scope.hideTooltip = function(el){
        $scope.showElement[el] = false;
    };
    
    $scope.setBulkGroupPrivacy = function(priv) {
        for (var idx in emailSrvc.emails.emails)            
            emailSrvc.emails.emails[idx].visibility = priv;
        emailSrvc.saveEmail();
    };
    
    /* Workaround for dealing with the Email table styles in Asian languages */
    $scope.asianEmailTableStyleFix = function(){
        if ($cookies.get('locale_v3') == 'zh_CN' || $cookies.get('locale_v3') == 'zh_TW' || $cookies.get('locale_v3') == 'ja' || $cookies.get('locale_v3') == 'ko'){     
            $scope.styleFix = false;
            $scope.$watch(
                function () {
                    return angular.element(document.getElementsByClassName("email-verified")[0]).length; 
                },
                function (newValue, oldValue) {
                    if(newValue > oldValue){
                        $scope.styleFix = true;                        
                    }        
                }
            );
            
         };
    };
    
    $scope.asianEmailTableStyleFix(); 
    
}]);