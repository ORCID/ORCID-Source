
<script type="text/ng-template" id="edit-language">
	<form id="language-form" action="#" ng-controller="languageCtrl">
		<select name="language-codes" id="language-codes"
			ng-model="language"
			ng-options="language.label for language in languages"
			ng-change="selectedLanguage()"></select>
	</form>
</script>