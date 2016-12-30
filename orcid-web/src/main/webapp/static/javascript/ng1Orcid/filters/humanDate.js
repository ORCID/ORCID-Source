angular.module('orcidApp').filter('humanDate', function($filter){
    var standardDateFilter = $filter('date');
    return function(input){
        var inputDate = new Date(input);
        var dateNow = new Date();
        var dateFormat = (inputDate.getYear() === dateNow.getYear() && inputDate.getMonth() === dateNow.getMonth() && inputDate.getDate() === dateNow.getDate())  ? 'HH:mm' : 'yyyy-MM-dd';
        return standardDateFilter(input, dateFormat);
    };
});