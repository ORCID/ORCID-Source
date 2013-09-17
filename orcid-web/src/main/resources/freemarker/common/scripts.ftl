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

<script type="text/javascript" src="${staticCdn}/javascript/plugins.js?v=${ver}"></script>

<script type="text/javascript" src="${staticCdn}/javascript/orcid.js?v=${ver}"></script>

<script type="text/javascript" src="${staticCdn}/javascript/plugins.js?v=${ver}"></script>

<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.0.8/angular.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.0.8/angular-cookies.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.0.8/angular-sanitize.min.js"></script>
<script type="text/javascript">
if (typeof angular == 'undefined') {
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/angularjs/1.0.8/angular.min.js' type='text/javascript'%3E%3C/script%3E"));
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/angularjs/1.0.8/angular-cookies.min.js' type='text/javascript'%3E%3C/script%3E"));
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/angularjs/1.0.8/angular-sanitize.min.js' type='text/javascript'%3E%3C/script%3E"));
}
</script>

<script type="text/javascript" src="${staticCdn}/javascript/script.js?v=${ver}"></script>

<script type="text/javascript" src="${staticCdn}/javascript/aprilFools.js?v=${ver}"></script>

<script type="text/javascript" src="${staticCdn}/javascript/angularOrcid.js?v=${ver}"></script>

<script type="text/javascript">

if (location == parent.location) {
    var userVoiceUrl = '://${springMacroRequestContext.getMessage("common.uservoice.url")}';
    if ("https:" == document.location.protocol) userVoiceUrl = 'https' + userVoiceUrl;
    else userVoiceUrl = 'http' + userVoiceUrl;
	$.getScript(userVoiceUrl, function(data, textStatus, jqxhr) {
	   console.log('user voice script load was performed.');
	});
}

</script>
