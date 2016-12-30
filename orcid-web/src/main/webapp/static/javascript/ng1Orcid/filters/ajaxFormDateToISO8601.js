angular.module('orcidApp').filter('ajaxFormDateToISO8601', function(){
    return function(input){
        if (typeof input != 'undefined'){
            var str = '';
            if (input.year) str += input.year;
            if (input.month) {
                if (str.length > 0) str += '-';
                str += Number(input.month).pad(2);
            }
            if (input.day) {
                if (str.length > 0)
                    str += '-';
                str += Number(input.day).pad(2);
            }
            return str;
        } else {
            return false;
        }
    };
});