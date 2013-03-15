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
			$('#addSelectedDelegateForm').submit(
				function(event)
				{
					if($('#addSelectedDelegateButton').hasClass("btn-primary"))
					{
						$.post('manage/confirm-delegate', $(this).serialize(),
							function(data)
							{
								$('#searchForDelegatesDialog').html(data);
								$('#searchForDelegatesDialog').dialog( "option", "title", 'Confirm Addition of Delegated Manager');
							}
						);
					}
					return false;
				}
			);
			$('#cancelAddDelegateButton').click(
				function(event){
					$('#searchForDelegatesDialog').dialog("close");
					return false;
				}
			);
			$('input[name="delegateOrcid"]').click(
				function(event){
					$('#addSelectedDelegateButton').addClass("btn-primary");
				}
			);
		}
	);
</script>
<#if (searchForDelegatesForm.results?size = 0)>
	<span>${springMacroRequestContext.getMessage("search_for_delegates_results.spanNoresultsfound")}</span>
<#else>
	<form id="addSelectedDelegateForm" action="manage/confirm-delegate" method="post">
		<table class="table">
			<#list searchForDelegatesForm.results as result>
				<tr>
					<td>${result.creditName}</td>
					<td>${result.email}</td>								
					<td><input type="radio" name="delegateOrcid" value="${result.orcid}"/></td>
				</tr>
			</#list>
		</table>
		<div class="profileDataCells">
			<span><button class="btn" id="cancelAddDelegateButton">${springMacroRequestContext.getMessage("freemarker.btncancel")}</button></span>
			<span><button class="btn" id="addSelectedDelegateButton">${springMacroRequestContext.getMessage("search_for_delegates_results.btnaddselect")}</button></span>
		</div>
	</form>
</#if>
