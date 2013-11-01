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
<script>
	$(document).ready(
		function(){
			$('#cancelAddDelegateButton').click(
				function(event){
					$('#searchForDelegatesDialog').dialog("close");
					return false;
				}
			);
		}
	);
</script>
<div>
	<div class="row">
		<div class="col-md-12 col-sm-12 col-xs-12">
			${springMacroRequestContext.getMessage("confirm_delegate.areyousuredelegatedmanager")} <strong>${springMacroRequestContext.getMessage("confirm_delegate.except")}</strong> ${springMacroRequestContext.getMessage("confirm_delegate.yourpasswordandemail")}
		</div>
	</div>
	<form id="addDelegateForm" action="<@spring.url '/account/add-delegate'/>" method="post">
		<div class="well">
			<input type="hidden" name="delegateOrcid" value="${delegateOrcid}"/>
			<span>${delegateProfile.orcidBio.personalDetails.creditName.content}</span>
			<span>${delegateProfile.orcidBio.contactDetails.email.value}</span>
		</div>
		<div class="regFieldData">
			<button class="btn" id="cancelAddDelegateButton" href="">${springMacroRequestContext.getMessage("freemarker.btnNo")}</button>
			<button class="btn btn-primary" id="addDelegateLink">${springMacroRequestContext.getMessage("freemarker.btnYes")}</button>
		</div>
	</form>
</div>