/**
* External consortium controller
*/
angular.module('orcidApp').controller('externalConsortiumCtrl',['$scope', '$compile', 'utilsService', 'membersListSrvc', function manageConsortiumCtrl($scope, $compile, utilsService, membersListSrvc) { 
    $scope.addContactDisabled = false;
    $scope.addSubMemberDisabled = false;
    $scope.addSubMemberShowLoader = false;
    $scope.membersListSrvc = membersListSrvc;
    $scope.consortium = null;
    /**
    * Not needed if contacts only added by email
    $scope.results = new Array();
    $scope.numFound = 0;
    */
    $scope.input = {};
    /**
    * Not needed if contacts only added by email
    $scope.input.start = 0;
    $scope.input.rows = 10;
    */
    $scope.showInitLoader = true;
    $scope.updateConsortiumDisabled = false;
    $scope.updateConsortiumShowLoader = false;
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
    /**
    * Not needed if contacts only added by email
    $scope.getResults = function(rows){
        $.ajax({
            url: orcidSearchUrlJs.buildUrl($scope.input)+'&callback=?',
            dataType: 'json',
            headers: { Accept: 'application/json'},
            success: function(data) {
                var resultsContainer = data['orcid-search-results'];
                $scope.numFound = resultsContainer['num-found'];
                if(resultsContainer['orcid-search-result']){
                    $scope.numFound = resultsContainer['num-found'];
                    $scope.results = $scope.results.concat(resultsContainer['orcid-search-result']);
                }
                var tempResults = $scope.results;
                for(var index = 0; index < tempResults.length; index ++) {
                    if($scope.results[index]['orcid-profile']['orcid-bio']['personal-details'] == null) {
                        $scope.results.splice(index, 1);
                    } 
                }
                $scope.numFound = $scope.results.length;
                if(!$scope.numFound){
                    $('#no-results-alert').fadeIn(1200);
                }
                $scope.areMoreResults = $scope.numFound >= ($scope.start + $scope.rows);
                $scope.showLoader = false;
                $scope.$apply();
                var newSearchResults = $('.new-search-result');
                if(newSearchResults.length > 0){
                    newSearchResults.fadeIn(1200);
                    newSearchResults.removeClass('new-search-result');
                    var newSearchResultsTop = newSearchResults.offset().top;
                    var showMoreButtonTop = $('#show-more-button-container').offset().top;
                    var bottom = $(window).height();
                    if(showMoreButtonTop > bottom){
                        $('html, body').animate(
                            {
                                scrollTop: newSearchResultsTop
                            },
                            1000,
                            'easeOutQuint'
                        );
                    }
                }
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error doing search for contacts");
        });
    };

    $scope.getMoreResults = function(){
        $scope.showLoader = true;
        $scope.start += 10;
        $scope.getResults();
    };

    $scope.concatPropertyValues = function(array, propertyName){
        if(typeof array === 'undefined'){
            return '';
        }
        else{
            return $.map(array, function(o){ return o[propertyName]; }).join(', ');
        }
    };

    $scope.areResults = function(){
        return $scope.numFound != 0;
    };

    $scope.getDisplayName = function(result){
        var personalDetails = result['orcid-profile']['orcid-bio']['personal-details'];
        var name = "";
        if(personalDetails != null) {
            var creditName = personalDetails['credit-name'];
            if(creditName != null){
                return creditName.value;
            }
            name = personalDetails['given-names'].value;
            if(personalDetails['family-name'] != null) {
                name = name + ' ' + personalDetails['family-name'].value;
            }
        }
        return name;
    };*/

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
    /**
    * Not needed if contacts only added by email
    $scope.confirmAddContact = function(contactName, contactId, contactIdx){
        $scope.errors = [];
        $scope.contactNameToAdd = contactName;
        $scope.contactToAdd = contactId;
        $scope.contactIdx = contactIdx;
        $.colorbox({
            html : $compile($('#confirm-add-contact-modal').html())($scope),
            transition: 'fade',
            close: '',
            onLoad: function() {
                $('#cboxClose').remove();
            },
            onComplete: function() {$.colorbox.resize();},
            scrolling: true
        });
    };*/

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
                $scope.getConsortium();
                $scope.addContactDisabled = false;
                $scope.input.text = "";
                $scope.$apply();
                $scope.closeModal();
            }
        }).fail(function() {
            console.log("Error adding contact.");
        });
    };
    /**
    * Not needed if contacts only added by email    
    $scope.addContact = function() {
        var addContact = {};
        addContact.orcid = $scope.contactToAdd;
        addContact.name = $scope.contactNameToAdd;
        $scope.contactNameToAdd
        $.ajax({
            url: getBaseUri() + '/manage-consortium/add-contact.json',
            type: 'POST',
            data: angular.toJson(addContact),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                if(data.errors.length === 0){
                    $scope.getConsortium();
                    $scope.$apply();
                    $scope.closeModal();
                }
                else{
                    $scope.errors = data.errors;
                    $scope.$apply();
                }
            }
        }).fail(function() {
            console.log("Error adding contact.");
        });
    };*/

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
                $scope.getConsortium();
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
    
}]);
