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
<script type="text/ng-template" id="delete-affiliation-modal">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3>${springMacroRequestContext.getMessage("manage.deleteAffiliation.pleaseConfirm")}</h3>
				<p>{{fixedTitle}}</p>		
    			<div class="btn btn-danger" ng-click="deleteAff()">
    				${springMacroRequestContext.getMessage("manage.deleteAffiliation.delete")}
    			</div>
    			<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteAffiliation.cancel")}</a>
			</div>
		</div>
    </div>
</script>