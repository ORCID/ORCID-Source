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
<#escape x as x?html>
   	<div class="biography-controller" ng-controller="BiographyCtrl">
   			<strong ng-click="toggleEdit()">${springMacroRequestContext.getMessage("manage_bio_settings.labelbiography")}</strong>
   			<span class="glyphicon glyphicon-pencil edit-country edit-option" ng-click="toggleEdit()" ng-hide="showEdit == true" title=""></span><br />
   			<div style="white-space: pre-wrap" ng-hide="showEdit == true" ng-bind="biographyForm.biography.value" ng-click="toggleEdit()"></div>
   			<div ng-hide="showEdit == false"  class="biography-edit" ng-cloak>
   			    <textarea id="biography" name="biography" class="input-xlarge" maxlength="5000" rows="20" ng-model="biographyForm.biography.value">
   			    </textarea>
   			    <span class="orcid-error" ng-show="website.url.errors.length > 0">
						     <div ng-repeat='error in biographyForm.biography.errors' ng-bind-html="error"></div>
				</span>
   			    <br />
   	            <button class="btn btn-primary" ng-click="setBiographyForm()"><@spring.message "freemarker.btnsavechanges"/></button>
	        	<button class="btn" ng-click="cancel()"><@spring.message "freemarker.btncancel"/></button>
	        	
   			</div>
   	</div>
   	<br />
</#escape>