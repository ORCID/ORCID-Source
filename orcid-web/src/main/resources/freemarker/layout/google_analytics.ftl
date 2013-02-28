<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#if googleAnalyticsTrackingId != ''>
 <script type="text/javascript">
	  var _gaq = _gaq || [];
	  _gaq.push(['_setAccount', '${googleAnalyticsTrackingId}']);
	  _gaq.push(['_setDomainName', 'orcid.org']);
	  _gaq.push(['_trackPageview']);
	  
	
	  (function() {
	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();
 
    </script>
 </#if>