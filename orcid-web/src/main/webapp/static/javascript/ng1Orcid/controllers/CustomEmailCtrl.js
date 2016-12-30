angular.module('orcidApp').controller('CustomEmailCtrl',['$scope', '$compile',function ($scope, $compile) {
    $scope.customEmail = null;
    $scope.editedCustomEmail = null;
    $scope.customEmailList = [];
    $scope.showCreateButton = false;
    $scope.showEmailList = false;
    $scope.showCreateForm = false;
    $scope.showEditForm = false;
    $scope.clientId = null;
    
    $scope.init = function(client_id) {
        $scope.clientId = client_id;
        $scope.getCustomEmails();
    };
    
    $scope.getCustomEmails = function() {
        $.ajax({
            url: getBaseUri() + '/group/custom-emails/get.json?clientId=' + $scope.clientId,
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.customEmailList = [];
                $scope.showEmailList = false;
                $scope.showCreateForm = false;
                $scope.showEditForm = false;
                $scope.customEmail = null;
                $scope.editedCustomEmail = null;
                if(data != null && data.length > 0){
                    $scope.customEmailList = data;
                    $scope.showCreateForm = false;
                    $scope.showEditForm = false;
                    $scope.showEmailList = true;
                    $scope.showCreateButton = false;
                }  else {
                    $scope.showCreateButton = true;
                }
                $scope.$apply();
            }
        });
    };

    $scope.displayCreateForm = function() {
        $.ajax({
            url: getBaseUri() + '/group/custom-emails/get-empty.json?clientId=' + $scope.clientId,
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data.errors == null || data.errors.length == 0){
                    $scope.customEmail = data;
                    $scope.showCreateForm = true;
                    $scope.showEditForm = false;
                    $scope.showCreateButton = false;
                    $scope.showEmailList = false;
                    $scope.$apply();
                }
            }
        }).fail(function() {
            console.log("Error getting empty custom email.");
        });
    };

    $scope.saveCustomEmail = function() {
        $.ajax({
            url: getBaseUri() + '/group/custom-emails/create.json',
            type: 'POST',
            data: angular.toJson($scope.customEmail),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data.errors != null && data.errors.length > 0){
                    $scope.customEmail = data;
                    $scope.$apply();
                } else {
                    //If everything worked fine, reload the list of clients
                    $scope.getCustomEmails();
                }
            }
        }).fail(function() {
            alert("An error occured creating the custom email");
            console.log("An error occured creating the custom email.");
        });
    };

    $scope.showEditLayout = function(index) {
        $scope.showCreateForm = false;
        $scope.showEditForm = true;
        $scope.showCreateButton = false;
        $scope.showEmailList = false;
        $scope.editedCustomEmail = $scope.customEmailList[index];
    };

    $scope.editCustomEmail = function() {
        $.ajax({
            url: getBaseUri() + '/group/custom-emails/update.json',
            type: 'POST',
            data: angular.toJson($scope.editedCustomEmail),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data.errors != null && data.errors.length > 0){
                    $scope.editedCustomEmail = data;
                    $scope.$apply();
                } else {
                    //If everything worked fine, reload the list of clients
                    $scope.getCustomEmails();
                }
            }
        }).fail(function() {
            alert("An error occured creating the custom email");
            console.log("An error occured creating the custom email.");
        });
    };

    $scope.showViewLayout = function() {
        $scope.getCustomEmails();
    };

    $scope.confirmDeleteCustomEmail = function(index) {
        $scope.toDelete = $scope.customEmailList[index];
        $.colorbox({
            html : $compile($('#delete-custom-email').html())($scope),
            scrolling: true,
            onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"415px" , height:"175px"});
    };

    $scope.deleteCustomEmail = function(index) {
        $.ajax({
            url: getBaseUri() + '/group/custom-emails/delete.json',
            type: 'POST',
            data: angular.toJson($scope.toDelete),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data){
                    //If everything worked fine, reload the list of clients
                    $scope.getCustomEmails();
                    $scope.closeModal();
                } else {
                    console.log("Error deleting custom email");
                }
            }
        }).fail(function() {
            alert("An error occured creating the custom email");
            console.log("An error occured creating the custom email.");
        });
    };

    $scope.closeModal = function(){
        $.colorbox.close();
    };
}]);
