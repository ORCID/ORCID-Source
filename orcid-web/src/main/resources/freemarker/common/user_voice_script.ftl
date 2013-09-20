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
