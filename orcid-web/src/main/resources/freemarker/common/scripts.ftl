<script src="${staticCdn}/javascript/ng1Orcid/angular_orcid_generated.js"></script>

<script type="text/javascript">
    var lang = OrcidCookie.getCookie('locale_v3');
</script>


<script type="text/javascript">
    var head = document.getElementsByTagName('head')[0];
    var urlParams = new URLSearchParams(location.search);
    <!-- Zendesk Widget script -->
    <!--Documentation https://support.zendesk.com/hc/en-us/articles/115009522787-->
    if(window.location.pathname.indexOf("/print") < 0 && window.location.pathname.indexOf("/oauth") < 0 && (urlParams.get('oauth') == null || typeof(urlParams.get('oauth')) === 'undefined')){
        var supportWidget = document.createElement("script");
        supportWidget.src = 'https://static.zdassets.com/ekr/snippet.js?key=b8313acd-6439-4894-b431-8c5a2ae9e7cb';
        supportWidget.type = 'text/javascript';
        supportWidget.id = 'ze-snippet';
        head.appendChild(supportWidget);
    } 
</script>