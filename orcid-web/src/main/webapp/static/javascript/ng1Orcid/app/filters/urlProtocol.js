angular.module('orcidApp').filter('urlProtocol', function(){
    return function(url){
        if (url == null) return url;
        if(!url.startsWith('http')) {               
            if (url.startsWith('//')){              
                url = ('https:' == document.location.protocol ? 'https:' : 'http:') + url;
            } else {
                url = 'http://' + url;    
            }
        }
        return url;
    }
});