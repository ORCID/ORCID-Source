<script type="text/ng-template" id="delete-funding-modal">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3><@orcid.msg 'manage.deleteFunding.pleaseConfirm' /></h3>
				<p>{{fixedTitle}}</p>		
    			<div id="confirm-delete-funding" class="btn btn-danger" ng-click="deleteFundingByPut(deletePutCode, deleteGroup)">
    				<@orcid.msg 'freemarker.btnDelete' />
    			</div>
    			<a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
			</div>
		</div>
    </div>
</script>