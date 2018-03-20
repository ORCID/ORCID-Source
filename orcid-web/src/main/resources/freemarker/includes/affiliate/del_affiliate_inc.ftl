<script type="text/ng-template" id="delete-affiliation-modal">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3><@orcid.msg 'manage.deleteAffiliation.pleaseConfirm' /></h3>
				<p>{{fixedTitle}}</p>		
    			<div id="confirm_delete_affiliation" class="btn btn-danger" ng-click="deleteAff(deleAff)">
    				<@orcid.msg 'freemarker.btnDelete' />
    			</div>
    			<a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
			</div>
		</div>
    </div>
</script>