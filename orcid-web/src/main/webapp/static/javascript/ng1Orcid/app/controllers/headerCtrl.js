angular.module('orcidApp').controller('headerCtrl',['$scope', '$window', function ($scope, $window){ 
    
    $scope.searchFilterChanged = false;
    $scope.filterActive = false;
    $scope.conditionsActive = false;
    $scope.menuVisible = false;
    $scope.secondaryMenuVisible = {};
    $scope.tertiaryMenuVisible = {};
    $scope.searchVisible = false;
    $scope.settingsVisible = false;
    
    $scope.searchFocus = function(){
        $scope.filterActive = true;
        $scope.conditionsActive = true;
    }
    
    $scope.searchBlur = function(){     
        $scope.hideSearchFilter();
        $scope.conditionsActive = false;        
    }
    
    $scope.filterChange = function(){
        $scope.searchFilterChanged = true;
    }
    
    $scope.hideSearchFilter = function(){
        var searchInputValue = document.getElementById("search-input").value;
        if (searchInputValue === ""){
            setTimeout(function() {
                if ($scope.searchFilterChanged === false) {
                    $scope.filterActive = false;
                }
            }, 3000);
        }
    }
    
    
    $scope.toggleMenu = function(){
        $scope.menuVisible = !$scope.menuVisible;
        $scope.searchVisible = false;
        $scope.settingsVisible = false;     
    }
    
    $scope.toggleSecondaryMenu = function(submenu){
        $scope.secondaryMenuVisible[submenu] = !$scope.secondaryMenuVisible[submenu];
    }
    
    $scope.toggleTertiaryMenu = function(submenu){
        $scope.tertiaryMenuVisible[submenu] = !$scope.tertiaryMenuVisible[submenu];
    }
    
    $scope.toggleSearch = function(){
        $scope.searchVisible = !$scope.searchVisible;
        $scope.menuVisible = false;     
        $scope.settingsVisible = false;
    }
    
    $scope.toggleSettings = function(){
        $scope.settingsVisible = !$scope.settingsVisible;
        $scope.menuVisible = false;
        $scope.searchVisible = false;
    }   
    
    $scope.handleMobileMenuOption = function($event){
        $event.preventDefault();
        var w = getWindowWidth();           
        if(w > 767) {               
            window.location = $event.target.getAttribute('href');
        }
    }
    
}]);