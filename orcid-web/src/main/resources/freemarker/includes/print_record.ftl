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
<#if RequestParameters['print']??>
	<div class="print-orcid-record" ng-controller="PrintRecordCtrl">
		<#if ((isPublicProfile)?? && isPublicProfile == true | (locked)?? && locked | (deprecated)?? && deprecated)>
			<a id="printRecord" ng-click="printRecord('${baseUriHttp}/${(effectiveUserOrcid)!}/print')">	
		<#else>
			<a id="printRecord" ng-click="printRecord('${baseUri}/${(effectiveUserOrcid)!}/print')">	
		</#if>
        <span class="glyphicon glyphicon-print"></span> ${springMacroRequestContext.getMessage("public_record.printView")}</a>
        <div class="popover-help-container">
            <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
            <div id="print-help" class="popover bottom">
                <div class="arrow"></div>
                <div class="popover-content">
                    <p>${springMacroRequestContext.getMessage("public_record.printHelpText")}</p>
                </div>
            </div>
        </div>
    </div>
</#if>
