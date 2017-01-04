angular.module('orcidApp').controller('SocialCtrl',['$scope', '$compile', 'discoSrvc', function SocialCtrl($scope, $compile, discoSrvc){
    $scope.showLoader = false;
    $scope.sort = {
        column: 'providerUserId',
        descending: false
    };
    $scope.isPasswordConfirmationRequired = orcidVar.isPasswordConfirmationRequired;

    $scope.changeSorting = function(column) {
        var sort = $scope.sort;
        if (sort.column === column) {
            sort.descending = !sort.descending;
        } else {
            sort.column = column;
            sort.descending = false;
        }
    };

    $scope.confirmRevoke = function(socialAccount) {
        $scope.errors = [];
        $scope.socialAccount = socialAccount;
        $scope.idToManage = socialAccount.id;
        $.colorbox({
            html : $compile($('#revoke-social-account-modal').html())($scope),            
            onComplete: function() {
                $.colorbox.resize({height:"200px", width:"500px"});        
            }
        });
        
    };

    $scope.revoke = function () {
        var revokeSocialAccount = {};
        revokeSocialAccount.idToManage = $scope.idToManage;
        revokeSocialAccount.password = $scope.password;
        $.ajax({
            url: getBaseUri() + '/account/revokeSocialAccount.json',
            type: 'POST',
            data:  angular.toJson(revokeSocialAccount),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                if(data.errors.length === 0){
                    $scope.getSocialAccounts();
                    $scope.$apply();
                    $scope.closeModal();
                    $scope.password = "";
                }
                else{
                    $scope.errors = data.errors;
                    $scope.$apply();
                }
            }
        }).fail(function() {
            // something bad is happening!
            console.log("$SocialCtrl.revoke() error");
        });
    };

    $scope.getSocialAccounts = function() {
        $.ajax({
            url: getBaseUri() + '/account/socialAccounts.json',
            dataType: 'json',
            success: function(data) {
                $scope.socialAccounts = data;
                $scope.populateIdPNames();
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error getting social accounts");
        });
    };
    
    $scope.$watch(function() { return discoSrvc.feed; }, function(){
        $scope.populateIdPNames();
        
    });
    
    $scope.populateIdPNames = function() {
        if(discoSrvc.feed != null) {
            for(i in $scope.socialAccounts){
                var account = $scope.socialAccounts[i];
                var name = discoSrvc.getIdPName(account.id.providerid);
                account.idpName = name;
            }
        }
    }

    $scope.closeModal = function() {
        $.colorbox.close();
    };
    // init
    $scope.getSocialAccounts();

}]);