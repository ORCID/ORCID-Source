angular.module('orcidApp').controller('RecordCorrectionsCtrl', ['$scope', '$compile', 'utilsService', function RecordCorrectionsCtrl($scope, $compile, utilsService) {
    $scope.currentPage = null;
    $scope.currentElement = null;
    
    $scope.getNextPage = function() {
    	var nextPageUrl = getBaseUri() + '/record-corrections/next';
    	if($scope.currentPage != null) {
    		nextPageUrl += '/' + $scope.currentPage.lastElementId
    	}    	
        $.ajax({
            url: nextPageUrl,
            dataType: 'json',
            success: function(data) {
            	$scope.currentPage = data;
                $scope.$apply();
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching next page");
        });
    };

    $scope.getPreviousPage = function() {
    	var previousPageUrl = getBaseUri() + '/record-corrections/previous';
    	if($scope.currentPage != null) {
    		previousPageUrl += '/' + $scope.currentPage.firstElementId
    	}
        $.ajax({
            url: previousPageUrl,            
            dataType: 'json',
            success: function(data) {            	
            	$scope.currentPage = data;
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
        	console.log("error fetching previous page");
        });
    };    

    $scope.getNextPage();
    
    $scope.moreInfo = function(element) {
        $.colorbox({
            scrolling: true,
            html: $compile($('#record-correction-more-info').html())($scope),
            onLoad: function() {
            	$scope.currentElement = element;
                $('#cboxClose').remove();                
            },            
            onComplete: function() {
                    
            },
            onClosed: function() {
                $scope.closeMoreInfo();
            }            
        });
        $.colorbox.resize({width:"600px"});        
    }
    
    $scope.closeMoreInfo = function() {  
    	$scope.currentElement = null;
        $.colorbox.close();
    }        
}]);