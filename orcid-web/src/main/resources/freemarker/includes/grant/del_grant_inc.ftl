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
<script type="text/ng-template" id="delete-grant-modal">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3><@orcid.msg 'manage.deleteGrant.pleaseConfirm' /></h3>
				<p>{{delGrant.title.value}}</p>		
    			<div class="btn btn-danger" ng-click="confirmDeleteGrant(delGrant)">
    				<@orcid.msg 'manage.deleteGrant.delete' />
    			</div>
    			<a href="" ng-click="closeModal()"><@orcid.msg 'manage.deleteGrant.cancel' /></a>
			</div>
		</div>
    </div>
</script>