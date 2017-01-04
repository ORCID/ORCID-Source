angular.module('orcidApp').filter('latex', function(){
    return function(input){
        if (input == null) return "";
        return latexParseJs.decodeLatex(input);
    };
});