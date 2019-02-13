<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
    <title>IDP select test bed</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-5" />
    <link rel="stylesheet" href="${staticCdn}/css/fonts.css"/>
    <link rel="stylesheet" type="text/css" href="${staticCdn}/css/idpselect.css" />
    <script type="text/javascript">
        var orcidVar = {};
        orcidVar.recaptchaKey = '${recaptchaWebKey}';             
        orcidVar.pubBaseUri = '${pubBaseUri}';
        orcidVar.orcidId = '${(effectiveUserOrcid)!}';
        orcidVar.orcidIdHash = '${(orcidIdHash)!}';
        orcidVar.realOrcidId = '${realUserOrcid!}';
        orcidVar.jsMessages = JSON.parse("${jsMessagesJson}");
        orcidVar.searchBaseUrl = "${searchBaseUrl}";
    </script>    
</head>

<body>
  	<div id="idpSelectContainer">
		<div id="idpSelectContainerHeader">
			<div id="orcid-logo">
				
			</div>
			<div id="header">
				<span>Institution sign in</span>
			</div>
		</div>
		<div id="idpSelectInner">
			
			
			<!-- Where the widget is going to be injected -->
			<div id="idpSelect"></div>
			
			
		</div>
		<div id="idpSelectContainerFooter">
			<div>
				<a class="idpSelectContainerFooterReturn" href="">...or, return to ORCID sign in &nbsp;<span class="orcid-mini"></span></a>
			</div>
			<div class="idpSelectContainerFooterBar">
				<ul>
					<li><a href="http://orcid.org/help/contact-us" target="About ORCID">About ORCID</a></li>
					<li>|</li>
					<li><a href="http://orcid.org/footer/privacy-policy" target="Privacy Policy">Privacy Policy</a></li>
					<li>|</li>
					<li><a href="http://orcid.org/content/orcid-terms-use" target="Terms of use">Terms of use</a></li>
					<li class="orcid-link"><a href="http://orcid.org/" target="orcid.org">orcid.org</a></li>
				</ul>
			</div>
		</div>		
	</div>

  <script src="${staticCdn}/javascript/shibboleth-embedded-ds/1.1.0/idpselect_config.js" type="text/javascript" language="javascript"></script>

  <script src="${staticCdn}/javascript/shibboleth-embedded-ds/1.1.0/idpselect.js" type="text/javascript" language="javascript"></script>


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
</body>
</html>
