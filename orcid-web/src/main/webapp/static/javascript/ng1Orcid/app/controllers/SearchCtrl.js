angular.module('orcidApp').controller('SearchCtrl',['$scope', '$compile', function ($scope, $compile){
    $scope.hasErrors = false;
    $scope.results = new Array();
    $scope.numFound = 0;
    $scope.input = {};
    $scope.input.start = 0;
    $scope.input.rows = 10;
    $scope.input.text = $('#SearchCtrl').data('search-query');

    $scope.getResults = function(){
        $.ajax({
            url: orcidSearchUrlJs.buildUrl($scope.input),
            dataType: 'json',
            headers: { Accept: 'application/json'},
            success: function(data) {
                $('#ajax-loader').hide();
                var resultsContainer = data['orcid-search-results'];
                $scope.numFound = resultsContainer['num-found'];
                if(resultsContainer['orcid-search-result']){
                    $scope.results = $scope.results.concat(resultsContainer['orcid-search-result']);
                }
                if(!$scope.numFound){
                    $('#no-results-alert').fadeIn(1200);
                }
                $scope.areMoreResults = $scope.numFound > ($scope.input.start + $scope.input.rows);
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
            console.log("error doing search");
        });
    };

    $scope.getFirstResults = function(){
        $('#no-results-alert').hide();
        $scope.results = new Array();
        $scope.numFound = 0;
        $scope.input.start = 0;
        $scope.input.rows = 10;
        $scope.areMoreResults = false;
        if($scope.isValid()){
            $scope.hasErrors = false;
            $('#ajax-loader').show();
            $scope.getResults();
        }
        else{
            $scope.hasErrors = true;
        }
    };

    $scope.getMoreResults = function(){
        $('#ajax-loader').show();
        $scope.input.start += 10;
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
        return $scope.results.length > 0;
    };

    $scope.isValid = function(){
        return orcidSearchUrlJs.isValidInput($scope.input);
    };

    $scope.isValidOrcidId = function(){
        if(typeof $scope.input.text === 'undefined' || $scope.input.text === null || $scope.input.text === '' || orcidSearchUrlJs.isValidOrcidId($scope.input.text)){
            return true;
        }
        return false;
    }

    // init
    if(typeof $scope.input.text !== 'undefined'){
        $('#ajax-loader').show();
        $scope.getResults();
    }
}]);