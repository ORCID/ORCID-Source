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
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
<script type="text/javascript">
if (typeof jQuery == 'undefined') {
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/jquery/1.8.1/jquery.min.js' type='text/javascript'%3E%3C/script%3E"));
}
</script>

<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.0/jquery-ui.min.js"></script>
<script type="text/javascript">
if (typeof jQuery.ui == 'undefined') {
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/jqueryui/1.10.0/jquery-ui.min.js' type='text/javascript'%3E%3C/script%3E"));
}
</script>

<script type="text/javascript" src="${staticCdn}/javascript/typeahead/0.9.3/typeahead.min.js"></script>

<script type="text/javascript" src="${staticCdn}/javascript/plugins.js?v=${ver}"></script>

<script type="text/javascript" src="${staticCdn}/javascript/orcid.js?v=${ver}"></script>

<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.28/angular.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.28/angular-cookies.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.28/angular-sanitize.min.js"></script>

<script type="text/javascript">
if (typeof angular == 'undefined') {
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/angularjs/1.2.28/angular.min.js' type='text/javascript'%3E%3C/script%3E"));
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/angularjs/1.2.28/angular-cookies.min.js' type='text/javascript'%3E%3C/script%3E"));
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/angularjs/1.2.28/angular-sanitize.min.js' type='text/javascript'%3E%3C/script%3E"));    
}
</script>
<script type="text/javascript" src="${staticCdn}/javascript/script.js?v=${ver}"></script>

<script type="text/javascript">
	var lang = OrcidCookie.getCookie('locale_v3');
	var script = document.createElement("script");
	script.type = "text/javascript";
    script.src = "https://www.google.com/recaptcha/api.js?onload=vcRecaptchaApiLoaded&render=explicit&hl=" + lang;
    document.body.appendChild(script);
</script>



<script src="${staticCdn}/javascript/angularjs/1.2.28/angular-recaptcha.min.js"></script>

<script type="text/javascript" src="${staticCdn}/javascript/angularOrcid.js?v=${ver}"></script>



<script type="text/javascript">
    var MTIProjectId='078e0d2f-8275-4c25-8aa9-5d902d8e4491';
    (function() {
        var mtiTracking = document.createElement('script');
        mtiTracking.type='text/javascript';
        mtiTracking.async='true';
        mtiTracking.src=('https:'==document.location.protocol?'https:':'http:')+'//fast.fonts.net/t/trackingCode.js';
        (document.getElementsByTagName('head')[0]||document.getElementsByTagName('body')[0]).appendChild( mtiTracking );
   })();
</script>

<script type="text/javascript">
   var script = document.createElement("script");
   script.type = "text/javascript";
   script.src = "https://badges.mozillascience.org/widgets/paper-badger-widget.js";
   document.body.appendChild(script);
</script>
