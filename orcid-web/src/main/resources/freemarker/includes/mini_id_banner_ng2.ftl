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
<div class="id-banner <#if inDelegationMode>delegation-mode</#if>">
	<div class="full-name pull-right" *ngIf="requestInfoForm?.userName != null">
		{{requestInfoForm?.userName}}		
	</div>
	<div class="oid">
		<#if (locked)?? && !locked>
			<!-- SWITCH USER -->
            <switch-user-ng2 [requestInfoForm]="requestInfoForm"></switch-user-ng2>
		</#if>
	</div>
	<div class="clearfix pull-right">
		<span><a href="" onclick="logOffReload('show_login'); return false;">(<@orcid.msg'confirm-oauth-access.notYou'/>?)</a></span>
	</div>
</div>