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
    document.write(unescape("%3Cscript src='<@spring.url "/static/javascript/jquery-1.8.1.min.js?v=${ver}" />' type='text/javascript'%3E%3C/script%3E"));
}
</script>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.0/jquery-ui.min.js"></script>

<script type="text/javascript" src="<@spring.url "/static/javascript/plugins.js?v=${ver}" />"></script>
<script type="text/javascript" src="<@spring.url "/static/javascript/orcid.js?v=${ver}" />"></script>
<script type="text/javascript" src="<@spring.url "/static/javascript/plugins.js?v=${ver}" />"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.0.4/angular.min.js"></script>
<script type="text/javascript" src="<@spring.url "/static/javascript/script.js?v=${ver}" />"></script>
<script type="text/javascript" src="<@spring.url "/static/javascript/aprilFools.js?v=${ver}" />"></script>
<script type="text/javascript" src="<@spring.url "/static/javascript/angularOrcid.js?v=${ver}" />"></script>

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
