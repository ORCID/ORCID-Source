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
   				<div class="row">
	   			    <div class="col-md-12 col-sm-12 col-xs-12">
	   			    	<textarea id="biography" name="biography" class="input-xlarge" maxlength="5000" rows="20" ng-model="biographyForm.biography.value">
	   			    	</textarea>
	   			    </div>
   			    </div>
   			    <div class="row">
	   			    <div class="col-md-12 col-sm-12 col-xs-12">
	   			    	<span class="orcid-error" ng-show="website.url.errors.length > 0">
						     <div ng-repeat='error in biographyForm.biography.errors' ng-bind-html="error"></div>
						</span>
					</div>   		
				</div>
				<div class="row">
		        	<div class="col-md-4 col-sm-4 col-xs-12">
		        		<@orcid.privacyToggle  angularModel="biographyForm.visiblity.visibility"
					             questionClick="toggleClickPrivacyHelp()"
					             clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
					             publicClick="setPrivacy('PUBLIC', $event)" 
		                 	     limitedClick="setPrivacy('LIMITED', $event)" 
		                 	     privateClick="setPrivacy('PRIVATE', $event)" />
					</div>				
					<div class="col-md-8 col-sm-8 col-xs-12">
						<div class="pull-right">
		   			    	<button class="btn btn-primary" ng-click="setBiographyForm()"><@spring.message "freemarker.btnsavechanges"/></button>
			        		<button class="btn" ng-click="cancel()"><@spring.message "freemarker.btncancel"/></button>
		        		</div>
	   			    </div>
   			    </div>													        
   			</div>
   	</div>
   	<br />
</#escape>