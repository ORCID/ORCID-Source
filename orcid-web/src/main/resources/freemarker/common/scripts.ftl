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

<script type="text/javascript">
    var head = document.getElementsByTagName('head')[0];
    var urlParams = new URLSearchParams(location.search);
    <!-- Zendesk Widget script -->
    <!--Documentation https://support.zendesk.com/hc/en-us/articles/115009522787-->
    if(!(window.location.pathname.indexOf("/print") > -1) && !(window.location.pathname.indexOf("/oauth") > -1) && (urlParams.get('oauth')!= null && urlParams.get('oauth')!== 'undefined' && urlParams.get('oauth')!= true && urlParams.get('oauth')!= 'true')){
        console.log("show support widget");
        var supportWidget = document.createElement("script");
        supportWidget.src = 'https://static.zdassets.com/ekr/snippet.js?key=b8313acd-6439-4894-b431-8c5a2ae9e7cb';
        supportWidget.type = 'text/javascript';
        supportWidget.id = 'ze-snippet';
        console.log(supportWidget);
        head.appendChild(supportWidget);
    } 
</script>