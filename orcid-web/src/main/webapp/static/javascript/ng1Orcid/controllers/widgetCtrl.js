orcidNgModule.controller('widgetCtrl',['$scope', 'widgetSrvc', function ($scope, widgetSrvc){
    $scope.hash = orcidVar.orcidIdHash.substr(0, 6);
    $scope.showCode = false;
    $scope.widgetSrvc = widgetSrvc;
    
    $scope.widgetURLND = '<div style="width:100%;text-align:center"><iframe src="'+ getBaseUri() + '/static/html/widget.html?orcid=' + orcidVar.orcidId + '&t=' + $scope.hash + '&locale=' + $scope.widgetSrvc.locale + '" frameborder="0" height="310" width="210px" vspace="0" hspace="0" marginheight="5" marginwidth="5" scrolling="no" allowtransparency="true"></iframe></div>';
    
    $scope.inputTextAreaSelectAll = function($event){
        $event.target.select();
    }
    
    $scope.toggleCopyWidget = function(){
        $scope.showCode = !$scope.showCode;
    }
    
    $scope.hideWidgetCode = function(){
        $scope.showCode = false;
    }
    
}]);