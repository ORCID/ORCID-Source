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
<script type="text/ng-template" id="edit-language">
	<form id="language-form" action="#" ng-controller="languageCtrl">
		<select name="language-codes" id="language-codes"
			ng-model="language"
			ng-options="language.label for language in languages"
			ng-change="selectedLanguage()"></select>
	</form>
</script>