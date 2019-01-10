<script type="text/javascript" src="${staticCdn}/javascript/jquery/2.2.3/jquery.min.js"></script>
<script type="text/javascript" src="${staticCdn}/javascript/jqueryui/1.10.0/jquery-ui.min.js"></script>
<script type="text/javascript" src="${staticCdn}/javascript/jquery-migrate/1.3.0/jquery-migrate-1.3.0.min.js"></script>

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

<script type="text/javascript" src="${staticCdn}/javascript/plugins.js"></script>

<script type="text/javascript" src="${staticCdn}/javascript/orcid.js"></script>

<script type="text/javascript" src="${staticCdn}/javascript/script.js"></script>

<script src="${staticCdn}/javascript/ng1Orcid/angular_orcid_generated.js"></script>

<script type="text/javascript">
    var lang = OrcidCookie.getCookie('locale_v3');
</script>

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