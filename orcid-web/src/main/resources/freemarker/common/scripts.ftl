<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<script type="text/javascript" src="//code.jquery.com/jquery-2.2.3.min.js"></script>
<script type="text/javascript">
if (typeof jQuery == 'undefined') {
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/jquery/2.2.3/jquery.min.js' type='text/javascript'%3E%3C/script%3E"));
}
</script>

<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.0/jquery-ui.min.js"></script>
<script type="text/javascript">
if (typeof jQuery.ui == 'undefined') {
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/jqueryui/1.10.0/jquery-ui.min.js' type='text/javascript'%3E%3C/script%3E"));
}
</script>

<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jquery-migrate/1.3.0/jquery-migrate.min.js"></script>
<script type="text/javascript">
if (typeof jQuery == 'undefined') {
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/jquery-migrate/1.3.0/jquery-migrate.min.js' type='text/javascript'%3E%3C/script%3E"));
}
</script>

<script type="text/javascript" >
    // CSRF
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");    
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


<script type="text/javascript" src="${staticCdn}/javascript/typeahead/0.9.3/typeahead.min.js"></script>

<script type="text/javascript" src="${staticCdn}/javascript/plugins.js?v=${ver}"></script>

<script type="text/javascript" src="${staticCdn}/javascript/orcid.js?v=${ver}"></script>


<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.5.2/angular.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.5.2/angular-cookies.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.5.2/angular-sanitize.min.js"></script>

<script type="text/javascript">
if (typeof angular == 'undefined') {
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/angularjs/1.5.2/angular.min.js' type='text/javascript'%3E%3C/script%3E"));
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/angularjs/1.5.2/angular-cookies.min.js' type='text/javascript'%3E%3C/script%3E"));
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/angularjs/1.5.2/angular-sanitize.min.js' type='text/javascript'%3E%3C/script%3E"));    
}
</script>
<script type="text/javascript" src="${staticCdn}/javascript/script.js?v=${ver}"></script>

<script type="text/javascript" src="${staticCdn}/javascript/angular-ui/typeahead/ui-bootstrap-custom-tpls-2.5.0.min.js"></script>

<script type="text/javascript">
    var lang = OrcidCookie.getCookie('locale_v3');
    var script = document.createElement("script");
    script.type = "text/javascript";
    script.src = "https://www.google.com/recaptcha/api.js?onload=vcRecaptchaApiLoaded&render=explicit&hl=" + lang;
    document.body.appendChild(script);
</script>

<script src="${staticCdn}/javascript/angular_orcid_generated.js?v=${ver}"></script>

<script type="text/javascript">
   var script = document.createElement("script");
   script.type = "text/javascript";
   script.src = "https://badges.mozillascience.org/widgets/paper-badger-widget.js";
   document.body.appendChild(script);
</script>

<script src="${staticCdn}/javascript/angular-recaptcha.min.js"></script>

<!-- Shibboleth -->
<#if request.requestURI?ends_with("signin") && (RequestParameters['newlogin'] )??>
	
	 
	<noscript>
	  <!-- If you need to care about non javascript browsers you will need to 
	       generate a hyperlink to a non-js DS.
	       
	       To build you will need:
	           - URL:  The base URL of the DS you use
	           - EI: Your entityId, URLencoded.  You can get this from the line that 
	             this page is called with.
	           - RET: Your return address dlib-adidp.ucs.ed.ac.uk. Again you can get
	             this from the page this is called with, but beware of the 
	             target%3Dcookie%253A5269905f bit..
	
	      < href={URL}?entityID={EI}&return={RET}
	   -->
	
	  Your Browser does not support javascript. Please use 
	  <a href="http://federation.org/DS/DS?entityID=https%3A%2F%2FyourentityId.edu.edu%2Fshibboleth&return=https%3A%2F%2Fyourreturn.edu%2FShibboleth.sso%2FDS%3FSAMLDS%3D1%26target%3Dhttps%3A%2F%2Fyourreturn.edu%2F">this link</a>.
	</noscript>
</#if>