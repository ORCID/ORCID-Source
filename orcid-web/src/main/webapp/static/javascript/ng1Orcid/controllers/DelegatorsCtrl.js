// Controller for delegate permissions that have been granted TO the current user
angular.module('orcidApp').controller('DelegatorsCtrl',['$scope', '$compile', function ($scope, $compile){

    $scope.sort = {
            column: 'delegateSummary.creditName.content',
            descending: false
    };

    $scope.changeSorting = function(column) {
        var sort = $scope.sort;
        if (sort.column === column) {
            sort.descending = !sort.descending;
        } else {
            sort.column = column;
            sort.descending = false;
        }
    };

    $scope.getDelegators = function() {
        $.ajax({
            url: getBaseUri() + '/delegators/delegators-and-me.json',
            dataType: 'json',
            success: function(data) {
                $scope.delegators = data.delegators;
                $scope.$apply();
            }
        }).fail(function(e) {
            // something bad is happening!
            console.log("error with delegates");
            logAjaxError(e);
        });
    };

    $scope.selectDelegator = function(datum) {
        window.location.href = getBaseUri() + '/switch-user?j_username=' + datum.orcid;
    };

    $("#delegatorsSearch").typeahead({
        name: 'delegatorsSearch',
        remote: {
            url: getBaseUri()+'/delegators/search-for-data/%QUERY?limit=' + 10
        },
        template: function (datum) {
            var forDisplay;
            if(datum.noResults){
                forDisplay = "<span class=\'no-delegator-matches\'>" + om.get('delegators.nomatches') + "</span>";
            }
            else{
                forDisplay =
                    '<span style=\'white-space: nowrap; font-weight: bold;\'>' + datum.value + '</span>'
                    +'<span style=\'font-size: 80%;\'> (' + datum.orcid + ')</span>';
            }
            return forDisplay;
        }
    });
    $("#delegatorsSearch").bind("typeahead:selected", function(obj, datum) {
        if(!datum.noResults){
            $scope.selectDelegator(datum);
        }
        $scope.$apply();
    });

    // init
    $scope.getDelegators();

}]);
