<script type="text/ng-template" id="delete-work-modal">
	<div style="padding: 20px;">
		<h3 style="margin-bottom: 0px;">${springMacroRequestContext.getMessage("manage.deleteWork.pleaseConfirm")}</h3>
		{{fixedTitle}}<br />
		<br />
    	<div class="btn btn-danger" ng-click="deleteByPutCode()">
    		${springMacroRequestContext.getMessage("manage.deleteWork.delete")}
    	</div>
    	<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteWork.cancel")}</a>
    <div>
</script>
