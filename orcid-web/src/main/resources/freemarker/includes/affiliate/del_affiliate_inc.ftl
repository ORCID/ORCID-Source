<script type="text/ng-template" id="delete-affiliation-modal">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3>${springMacroRequestContext.getMessage("manage.deleteAffiliation.pleaseConfirm")}</h3>
				<p>{{fixedTitle}}</p>		
    			<div class="btn btn-danger" ng-click="deleteByPutCode()">
    				${springMacroRequestContext.getMessage("manage.deleteAffiliation.delete")}
    			</div>
    			<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteAffiliation.cancel")}</a>
			</div>
		</div>
    </div>
</script>