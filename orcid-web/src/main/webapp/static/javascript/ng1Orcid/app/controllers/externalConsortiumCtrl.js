/**
* External consortium controller
*/
angular.module('orcidApp').controller('externalConsortiumCtrl',['$scope', '$compile', 'utilsService', 'membersListSrvc', function manageConsortiumCtrl($scope, $compile, utilsService, membersListSrvc) { 
    $scope.addContactDisabled = false;
    $scope.addSubMemberDisabled = false;
    $scope.addSubMemberShowLoader = false;
    $scope.membersListSrvc = membersListSrvc;
    $scope.consortium = null;
    $scope.contacts = null;
    $scope.input = {};
    $scope.showInitLoader = true;
    $scope.updateConsortiumDisabled = false;
    $scope.updateConsortiumShowLoader = false;
    $scope.updateContactsDisabled = false;
    $scope.updateContactsShowLoader = false;
    $scope.effectiveUserOrcid = orcidVar.orcidId;
    $scope.realUserOrcid = orcidVar.realOrcidId;
    $scope.toggleFindConsortiumModal = function() {
         $scope.showFindModal = !$scope.showFindModal;
    };
    
    /**
     * GET
     * */
    $scope.getConsortium = function() {
         $.ajax({
              url: getBaseUri()+'/manage-consortium/get-consortium.json',
              type: 'GET',
              dataType: 'json',
              success: function(data){
                    $scope.consortium = data;
                    $scope.$apply();
              }
         }).fail(function(error) {
              // something bad is happening!
              console.log("Error getting the consortium");
         });
    };
    
    $scope.updateConsortium = function() {
        $scope.updateConsortiumShowLoader = true;
        $scope.updateConsortiumDisabled = true;
         $.ajax({
              url: getBaseUri()+'/manage-consortium/update-consortium.json',
              contentType: 'application/json;charset=UTF-8',
              type: 'POST',
              dataType: 'json',
              data: angular.toJson($scope.consortium),
              success: function(data){
                    $scope.updateConsortiumShowLoader = false;
                    $scope.updateConsortiumDisabled = false;
                    $scope.$apply(function(){
                         if(data.errors.length == 0){
                              $scope.success_edit_member_message = om.get('manage_member.edit_member.success');
                         } else {
                              $scope.consortium = data;
                         }
                    });
              }
         }).fail(function(error) {
              // something bad is happening!
              console.log("Error updating the consortium");
         });
    };
    
    $scope.closeModal = function() {
         $.colorbox.close();
    };

    $scope.closeModalReload = function() {
         $.colorbox.close();
         window.location.reload();
    };
    
    /**
     * GET
     * */
    $scope.getContacts = function() {
         $.ajax({
              url: getBaseUri()+'/manage-consortium/get-contacts.json',
              type: 'GET',
              dataType: 'json',
              success: function(data){
                    $scope.contacts = data;
                    $scope.$apply();
              }
         }).fail(function(error) {
              // something bad is happening!
              console.log("Error getting the contacts");
         });
    };
    
    $scope.validateContacts = function() {
         $.ajax({
              url: getBaseUri()+'/manage-consortium/validate-contacts.json',
              contentType: 'application/json;charset=UTF-8',
              type: 'POST',
              dataType: 'json',
              data: angular.toJson($scope.contacts),
              success: function(data){
                    $scope.$apply(function(){
                        $scope.contacts.errors = data.errors;
                    });
              }
         }).fail(function(error) {
              // something bad is happening!
              console.log("Error validating the contacts");
         });
    };
    
    $scope.updateContacts = function() {
        $scope.updateContactsShowLoader = true;
        $scope.updateContactsDisabled = true;
         $.ajax({
              url: getBaseUri()+'/manage-consortium/update-contacts.json',
              contentType: 'application/json;charset=UTF-8',
              type: 'POST',
              dataType: 'json',
              data: angular.toJson($scope.contacts),
              success: function(data){
                    $scope.updateContactsShowLoader = false;
                    $scope.updateContactsDisabled = false;
                    $scope.$apply(function(){
                         if(data.errors.length == 0){
                              $scope.success_edit_member_message = om.get('manage_member.edit_member.success');
                         }
                         $scope.contacts = data;
                    });
              }
         }).fail(function(error) {
              // something bad is happening!
              console.log("Error updating the contacts");
         });
    };
    
    $scope.search = function(){
        $('#invalid-email-alert').hide();
        if(utilsService.isEmail($scope.input.text)){
            $scope.searchByEmail();
        }
        else{
            $('#invalid-email-alert').show();
        }
    };

    $scope.searchByEmail = function(){
        $.ajax({
            url: getBaseUri() + '/manage/search-for-delegate-by-email/' + encodeURIComponent($scope.input.text) + '/',
            dataType: 'json',
            headers: { Accept: 'application/json'},
            success: function(data) {
                $scope.confirmAddContactByEmail(data);
                $scope.$apply();
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error doing search for contact by email");
        });

    };

    $scope.confirmAddContactByEmail = function(emailSearchResult){
        $scope.errors = [];
        $scope.emailSearchResult = emailSearchResult;
        $.colorbox({
            html : $compile($('#confirm-add-contact-by-email-modal').html())($scope),
            transition: 'fade',
            close: '',
            onLoad: function() {
                $('#cboxClose').remove();
            },
            onComplete: function() {$.colorbox.resize();},
            scrolling: true
        });
    };

    $scope.addContactByEmail = function(contactEmail) {
        $scope.addContactDisabled = true;
        $scope.errors = [];
        var addContact = {};
        addContact.email = $scope.input.text;
        $.ajax({
            url: getBaseUri() + '/manage-consortium/add-contact-by-email.json',
            type: 'POST',
            data: angular.toJson(addContact),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                $scope.getContacts();
                $scope.addContactDisabled = false;
                $scope.input.text = "";
                $scope.$apply();
                $scope.closeModal();
            }
        }).fail(function() {
            console.log("Error adding contact.");
        });
    };

    $scope.confirmRevoke = function(contact) {
        $scope.contactToRevoke = contact;
        $.colorbox({
            html : $compile($('#revoke-contact-modal').html())($scope),
            transition: 'fade',
            close: '',
            onLoad: function() {
                $('#cboxClose').remove();
            },
            onComplete: function() {$.colorbox.resize();},
            scrolling: true

        });
        $.colorbox.resize();
    };
    
    $scope.revoke = function () {
        $.ajax({
            url: getBaseUri() + '/manage-consortium/remove-contact.json',
            type: 'POST',
            data:  angular.toJson($scope.contactToRevoke),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                $scope.getContacts();
                $scope.$apply();
                $scope.closeModal();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("$ContactCtrl.revoke() error");
        });
    };
    
    $scope.update = function (contact) {
        var done = false;
        if(contact.mainContact){
            for(var i in $scope.consortium.contactsList){
                var other = $scope.consortium.contactsList[i];
                if(other.id !== contact.id && other.mainContact){
                    other.mainContact = false;
                    other.role = null;
                    var nextContact = contact;
                    $scope.updateCall(other, function() {  $scope.updateCall(nextContact); })
                    done = true;
                }
            }
        }
        if(!done){
            $scope.updateCall(contact);
        }
        
    };
    
    $scope.updateCall = function(contact, nextFunction){
        $.ajax({
            url: getBaseUri() + '/manage-consortium/update-contact.json',
            type: 'POST',
            data:  angular.toJson(contact),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                contact.role.id = data.role.id;
                $scope.$apply();
                if(nextFunction){
                    nextFunction();
                }
            }
        }).fail(function() {
            // something bad is happening!
            console.log("$ContactCtrl.update() error");
        });
    }
    
    $scope.addSubMember = function() {
        $scope.addSubMemberDisabled = true;
        $scope.addSubMemberShowLoader = true;
        $.ajax({
            url: getBaseUri() + '/manage-consortium/add-sub-member.json',
            type: 'POST',
            data: angular.toJson($scope.newSubMember),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                if(data.errors.length === 0){
                    $scope.getConsortium();
                    $scope.addSubMemberShowLoader = false;
                    $scope.addSubMemberDisabled = false;
                    $scope.newSubMember.name = "";
                    $scope.newSubMember.website = "";
                    $scope.$apply();
                }
                else{
                    $scope.errors = data.errors;
                    $scope.$apply();
                }
            }
        }).fail(function() {
            console.log("Error adding submember.");
        });
    };
    
    $scope.confirmRemoveSubMember = function(subMember) {
        $scope.subMemberToRemove = subMember;
        $.colorbox({
            html : $compile($('#remove-sub-member-modal').html())($scope),
            transition: 'fade',
            close: '',
            onLoad: function() {
                $('#cboxClose').remove();
            },
            onComplete: function() {$.colorbox.resize();},
            scrolling: true

        });
        $.colorbox.resize();
    };
    
    $scope.removeSubMember = function () {
        $.ajax({
            url: getBaseUri() + '/manage-consortium/remove-sub-member.json',
            type: 'POST',
            data:  angular.toJson($scope.subMemberToRemove),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                $scope.getConsortium();
                $scope.$apply();
                $scope.closeModal();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("Problem removing sub member");
        });
    };
    
    $scope.buildOrcidUri = function(orcid){
        return orcidVar.baseUri + '/' + orcid;
    }
    
    // Init
    $scope.getConsortium();
    $scope.getContacts();
    
}]);
