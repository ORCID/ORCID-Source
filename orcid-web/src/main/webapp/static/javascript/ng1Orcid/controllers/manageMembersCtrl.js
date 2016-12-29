/**
 * Manage members controller
 * */
orcidNgModule.controller('manageMembersCtrl',['$scope', '$compile', function manageMembersCtrl($scope, $compile) {    
    $scope.showFindModal = false;
    $scope.success_message = null;
    $scope.client_id = null;
    $scope.client = null;
    $scope.showError = false;
    $scope.availableRedirectScopes = [];
    $scope.selectedScope = "";
    $scope.newMember = null;
    $scope.groups = [];
    $scope.importWorkWizard = {
        'actTypeList' : ['Articles','Books','Data','Student Publications'],
        'geoAreaList' : ['Global', 'Africa', 'Asia', 'Australia', 'Europe', 'North America', 'South America']
    };

    $scope.toggleGroupsModal = function() {
        $scope.showAdminGroupsModal = !$scope.showAdminGroupsModal;
        $('#admin_groups_modal').toggle();
    };
    
    $scope.toggleFindModal = function() {
        $scope.showAdminGroupsModal = !$scope.showAdminGroupsModal;
        $('#find_edit_modal').toggle();
    };
    
    /**
     * FIND
     * */
    $scope.findAny = function() {
        success_edit_member_message = null;
        success_message = null;
        $.ajax({
            url: getBaseUri()+'/manage-members/find.json?id=' + encodeURIComponent($scope.any_id),
            type: 'GET',
            dataType: 'json',
            success: function(data){
                $scope.$apply(function(){  
                    if(data.client == true) {
                        $scope.client = data.clientObject;
                        $scope.member = null;
                        for(var i = 0; i < $scope.client.redirectUris.length; i ++) {
                            $scope.client.redirectUris[i].actType.value = JSON.parse($scope.client.redirectUris[i].actType.value);
                            $scope.client.redirectUris[i].geoArea.value = JSON.parse($scope.client.redirectUris[i].geoArea.value);
                        }
                    } else {
                        $scope.client = null;
                        $scope.member = data.memberObject;
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error finding the information");
        });
    };
    
    /**
     * MEMBERS
     * */
    $scope.getMember = function() {
        $.ajax({
            url: getBaseUri()+'/manage-members/member.json',
            type: 'GET',
            dataType: 'json',
            success: function(data){
                $scope.$apply(function(){
                    $scope.newMember = data;
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error getting emtpy group");
        });
    };

    $scope.addMember = function() {
        $.ajax({
            url: getBaseUri()+'/manage-members/create-member.json',
            contentType: 'application/json;charset=UTF-8',
            type: 'POST',
            dataType: 'json',
            data: angular.toJson($scope.newMember),
            success: function(data){
                $scope.$apply(function(){
                    $scope.newMember = data;
                    if(data.errors.length != 0){

                    } else {
                        $scope.showSuccessModal();
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error deprecating the account");
        });
    };

    $scope.findMember = function() {
        $scope.success_edit_member_message = null;
        $.ajax({
            url: getBaseUri()+'/manage-members/find-member.json?orcidOrEmail=' + $scope.member_id,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                $scope.member = data;
                $scope.$apply();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error getting existing groups");
        });
    };

    $scope.updateMember = function() {
        $.ajax({
            url: getBaseUri()+'/manage-members/update-member.json',
            contentType: 'application/json;charset=UTF-8',
            type: 'POST',
            dataType: 'json',
            data: angular.toJson($scope.member),
            success: function(data){
                $scope.$apply(function(){
                    if(data.errors.length == 0){
                        $scope.member = null;
                        $scope.success_edit_member_message = om.get('manage_member.edit_member.success');
                        $scope.member_id = null;
                    } else {
                        $scope.member = data;
                    }
                });
                $scope.closeModal();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error deprecating the account");
        });
    };

    /**
     * CLIENTS
     * */
    $scope.searchClient = function() {
        $scope.showError = false;
        $scope.client = null;
        $scope.success_message = null;
        $.ajax({
            url: getBaseUri()+'/manage-members/find-client.json?orcid=' + $scope.client_id,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                $scope.client = data;
                $scope.$apply();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error getting existing groups");
        });
    };

    //Load empty redirect uri
    $scope.addRedirectUri = function() {
        $.ajax({
            url: getBaseUri() + '/manage-members/empty-redirect-uri.json',
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.client.redirectUris.push(data);
                $scope.$apply();
            }
        }).fail(function() {
            console.log("Unable to fetch redirect uri scopes.");
        });
    };

    $scope.deleteRedirectUri = function($index){
        $scope.client.redirectUris.splice($index,1);
    };

    //Load the default scopes based n the redirect uri type selected
    $scope.loadDefaultScopes = function(rUri) {
        //Empty the scopes to update the default ones
        rUri.scopes = [];
        //Fill the scopes with the default scopes
        if(rUri.type.value == 'grant-read-wizard'){
            rUri.scopes.push('/orcid-profile/read-limited');
        } else if (rUri.type.value == 'import-works-wizard'){
            rUri.scopes.push('/orcid-profile/read-limited');
            rUri.scopes.push('/orcid-works/create');
        } else if (rUri.type.value == 'import-funding-wizard'){
            rUri.scopes.push('/orcid-profile/read-limited');
            rUri.scopes.push('/funding/create');
        } else if (rUri.type.value == 'import-peer-review-wizard'){
            rUri.scopes.push('/orcid-profile/read-limited');
            rUri.scopes.push('/peer-review/create');
        } else if(rUri.type.value == 'institutional-sign-in') {
            rUri.scopes.push('/authenticate');
        }
    };

    //Load the list of scopes for client redirect uris
    $scope.loadAvailableScopes = function(){
        $.ajax({
            url: getBaseUri() + '/group/developer-tools/get-available-scopes.json',
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.availableRedirectScopes = data;
            }
        }).fail(function() {
            console.log("Unable to fetch redirect uri scopes.");
        });
    };

    //Update client
    $scope.updateClient = function() {
        var clientClone = JSON.parse(JSON.stringify($scope.client));
        for(var i = 0; i < clientClone.redirectUris.length; i ++) {
            clientClone.redirectUris[i].actType.value = JSON.stringify(clientClone.redirectUris[i].actType.value);
            clientClone.redirectUris[i].geoArea.value = JSON.stringify(clientClone.redirectUris[i].geoArea.value);
        }
        $.ajax({
            url: getBaseUri() + '/manage-members/update-client.json',
            type: 'POST',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            data: angular.toJson(clientClone),
            success: function(data) {
                if(data.errors.length == 0){
                    $scope.client = null;
                    $scope.client_id = "";
                    $scope.success_message = om.get('admin.edit_client.success');
                } else {
                    $scope.client.errors = data.errors;
                }
                $scope.$apply();
                $scope.closeModal();
            }
        }).fail(function() {
            console.log("Unable to update client.");
        });
    };

    //init
    $scope.loadAvailableScopes();
    $scope.getMember();

    /**
     * Colorbox
     * */
    //Confirm updating a client
    $scope.confirmUpdateClient = function() {
        $.colorbox({
            html : $compile($('#confirm-modal-client').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"450px" , height:"175px"});
    };

    //Confirm updating a member
    $scope.confirmUpdateMember = function() {
        $.colorbox({
            html : $compile($('#confirm-modal-member').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"450px" , height:"175px"});
    };

    //Display add member modal
    $scope.showAddMemberModal = function() {
        $scope.getMember();
        $.colorbox({
            html : $compile($('#add-new-member').html())($scope),
                onLoad: function() {
                $('#cboxClose').remove();
            }
        });

        $.colorbox.resize({width:"400px" , height:"500px"});
    };

    //Show success modal for groups
    $scope.showSuccessModal = function() {
        $.colorbox({
            html : $compile($('#new-group-info').html())($scope),
                onLoad: function() {
                $('#cboxClose').remove();
            }
        });

        $.colorbox.resize({width:"500px" , height:"500px"});
    };

    /**
     * General
     * */
    $scope.closeModal = function() {
        $.colorbox.close();
    };
    
    $scope.selectAll = function($event){
        $event.target.select();
    };
}]);