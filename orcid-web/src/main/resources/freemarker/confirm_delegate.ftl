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
	<div class="row-fluid">
		<div class="span12">
			Are you sure that you want to add this user as a delegated manager to your ORCID record?
			Doing so will enable the person to manage all elements of your ORCID record <strong>except</strong> your password and your e-mail address
		</div>
	</div>
	<form id="addDelegateForm" action="<@spring.url '/account/add-delegate'/>" method="post">
		<div class="well">
			<input type="hidden" name="delegateOrcid" value="${delegateOrcid}"/>
			<span>${delegateProfile.orcidBio.personalDetails.creditName.content}</span>
			<span>${delegateProfile.orcidBio.contactDetails.email.value}</span>
		</div>
		<div class="regFieldData">
			<button class="btn" id="cancelAddDelegateButton" href="">No</button>
			<button class="btn btn-primary" id="addDelegateLink">Yes</button>
		</div>
	</form>
</div>