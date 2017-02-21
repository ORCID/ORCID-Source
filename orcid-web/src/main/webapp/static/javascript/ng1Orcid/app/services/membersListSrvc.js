angular.module('orcidApp').factory("membersListSrvc", ['$rootScope', function ($rootScope) {
    var serv = {
        membersList: null,
        memberDetails: {},
        currentMemberDetails: null,
        consortiaList: null,
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
        },
        getDetails: function(memberId, consortiumLeadId) {
            if(serv.memberDetails[memberId] == null){
                var url = getBaseUri() + '/members/details.json?memberId=' + encodeURIComponent(memberId);
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
        getCurrentMemberDetailsBySlug: function(memberSlug) {
            if(serv.currentMemberDetails == null){
                var url = getBaseUri() + '/members/detailsBySlug.json?memberSlug=' + encodeURIComponent(memberSlug);
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
        getMemberPageUrl: function(slug) {
            return orcidVar.baseUri + '/members/' + slug;
        }
    };

    return serv; 
}]);
