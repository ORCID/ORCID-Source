<script type="text/javascript" src="${staticCdn}/javascript/jquery/2.2.3/jquery.min.js"></script>
<script type="text/javascript" src="${staticCdn}/javascript/jqueryui/1.10.0/jquery-ui.min.js"></script>
<script type="text/javascript" src="${staticCdn}/javascript/jquery-migrate/1.3.0/jquery-migrate-1.3.0.min.js"></script>

<script type="text/javascript" src="${staticCdn}/javascript/typeahead/0.9.3/typeahead.min.js"></script>

<script type="text/javascript" src="${staticCdn}/javascript/plugins.js"></script>

<script type="text/javascript" src="${staticCdn}/javascript/orcid.js"></script>

<script type="text/javascript" src="${staticCdn}/javascript/script.js"></script>

<script src="${staticCdn}/javascript/ng1Orcid/angular_orcid_generated.js"></script>

<script type="text/javascript">
    var lang = OrcidCookie.getCookie('locale_v3');
</script>

<script type="text/javascript" >
//CSRF
var token = OrcidCookie.getCookie('XSRF-TOKEN');
var header = 'x-xsrf-token';    
if (header && token){
 $(document).ajaxSend(function(e, xhr, options) {
     if (options.type != "GET") {
        if (   options.url.startsWith(orcidVar.baseUri)
            || options.url.startsWith(orcidVar.baseUriHttp)
            || options.url.startsWith('/')) {            
            xhr.setRequestHeader(header, token);
        };
     };
 });
}
</script>