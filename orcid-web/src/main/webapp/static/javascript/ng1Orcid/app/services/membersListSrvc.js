angular.module('orcidApp').factory("membersListSrvc", ['$rootScope', function ($rootScope) {
    var serv = {
        communityTypes: {},
        consortiaList: null,
        currentMemberDetails: null,
        membersList: null,
        memberDetails: {},

        getCommunityTypes: function() {
            var url = "";
            if(serv.currentMemberDetails == null){
                url = getBaseUri() + '/members/communityTypes.json';
                $.ajax({
                    url: url,
                    dataType: 'json',
                    cache: true,
                    success: function(data) {
                        for(var i in data){
                            serv.communityTypes[i] = data[i];
                        }
                        $rootScope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with community types");
                    serv.feed = [];
                    $rootScope.$apply();
                });
            }
        },

        getConsortiaList: function() {
            $.ajax({
                url: getBaseUri() + '/consortia/consortia.json',
                dataType: 'json',
                cache: true,
                success: function(data) {
                    serv.consortiaList = data;
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with consortia list");
                serv.feed = [];
                $rootScope.$apply();
            });
        },

        getCurrentMemberDetailsBySlug: function(memberSlug) {
            var url = "";
            if(serv.currentMemberDetails == null){
                url = getBaseUri() + '/members/detailsBySlug.json?memberSlug=' + encodeURIComponent(memberSlug);
                $.ajax({
                    url: url,
                    dataType: 'json',
                    cache: true,
                    success: function(data) {
                        serv.currentMemberDetails = data;
                        $rootScope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with member details by slug");
                    serv.feed = [];
                    $rootScope.$apply();
                });
            }
        },

        getDetails: function(memberId, consortiumLeadId) {
            var url = "";
            if(serv.memberDetails[memberId] == null){
                url = getBaseUri() + '/members/details.json?memberId=' + encodeURIComponent(memberId);
                if(consortiumLeadId != null){
                    url += '&consortiumLeadId=' + encodeURIComponent(consortiumLeadId);
                }
                $.ajax({
                    url: url,
                    dataType: 'json',
                    cache: true,
                    success: function(data) {
                        serv.memberDetails[memberId] = data;
                        $rootScope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with member details");
                    serv.feed = [];
                    $rootScope.$apply();
                });
            }
        },

        getMemberPageUrl: function(slug) {
            return orcidVar.baseUri + '/members/' + slug;
        },

        getMembersList: function() {
            $.ajax({
                url: getBaseUri() + '/members/members.json',
                dataType: 'json',
                cache: true,
                success: function(data) {
                    serv.membersList = data;
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with members list");
                serv.feed = [];
                $rootScope.$apply();
            });
        }
        
    };
    serv.getCommunityTypes();
    return serv; 
}]);
