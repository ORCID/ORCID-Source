var token = OrcidCookie.getCookie('XSRF-TOKEN');
var header = 'x-xsrf-token';    
if (header && token){
 $(document).ajaxSend(function(e, xhr, options) {
     if (options.type != "GET") {
        if (   options.url.startsWith(getBaseUri())            
            || options.url.startsWith('/')) {            
            xhr.setRequestHeader(header, token);
        };
     };
 });
}