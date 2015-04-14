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
<script type="text/ng-template" id="add-peer-review-modal"> 
	<div class="add-peer-review colorbox-content">
		<fn-form update-fn="">
				<div class="lightbox-container-ie7">		
				<!-- Title -->
				<div class="row">			
					<div class="col-md-9 col-sm-8 col-xs-9">	
						<h1 class="lightbox-title pull-left">						
							<div ng-show="editPeerReview.putCode.value != ''" ng-cloak>
								Edit Peer Review
							</div>						 
							<div ng-show="editPeerReview.putCode.value == ''" ng-cloak>
								Add Peer Review
							</div>
						</h1>
					</div>			
				</div>
		
				<!-- Main content -->		
				<div class="row">
					<!-- Left Column -->			
					<div class="col-md-6 col-sm-6 col-xs-12">	
						<!-- ROLE -->
						<div class="control-group">
				    		<label class="relative">Role</label>			    		
				    		<div class="relative">
					    		<select id="peerReviewRole" class="input-xlarge" name="peerReviewRole" ng-model="editPeerReview.role.value" ng-change="serverValidate('peer-reviews/roleValidate.json');">
	                            	<option value=""><@orcid.msg 'org.orcid.jaxb.model.record.Role.empty' /></option>
	                            	<#list peerReviewRoles?keys as key>
	                                	<option value="${key}">${peerReviewRoles[key]}</option>
	                            	</#list>
	                        	</select> 
								<span class="required" ng-class="isValidClass(editPeerReview.role)">*</span>
								<span class="orcid-error" ng-show="editPeerReview.role.errors.length > 0">
									<div ng-repeat='error in editPeerReview.role.errors' ng-bind-html="error"></div>
								</span>
							</div>
						</div>
						<!-- TYPE -->
						<div class="control-group">
				    		<label class="relative">Type</label>			    		
				    		<div class="relative">
					    		<select id="peerReviewType" class="input-xlarge" name="peerReviewType" ng-model="editPeerReview.type.value" ng-change="serverValidate('peer-reviews/typeValidate.json');">
	                            	<option value=""><@orcid.msg 'org.orcid.jaxb.model.record.PeerReviewType.empty' /></option>
	                            	<#list peerReviewTypes?keys as key>
	                                	<option value="${key}">${peerReviewTypes[key]}</option>
	                            	</#list>
	                        	</select> 
								<span class="required" ng-class="isValidClass(editPeerReview.type)">*</span>
								<span class="orcid-error" ng-show="editPeerReview.type.errors.length > 0">
									<div ng-repeat='error in editPeerReview.type.errors' ng-bind-html="error"></div>
								</span>
							</div>
						</div>
	
						<!-- ORGANIZATION -->
						<div class="control-group">
		                	<div class="control-group no-margin-bottom">
	    	                	<strong>FUNDING AGENCY</strong>
		    	            </div>
	    	    	        <div class="control-group" ng-show="editPeerReview.disambiguatedOrganizationSourceId">
	        	    	        <label>Institution</label>
	            	    	    <span id="remove-disambiguated" class="pull-right">
	                	    	    <a ng-click="removeDisambiguatedOrganization()">
	                    	    	    <span class="glyphicon glyphicon-remove-sign"></span><@orcid.msg 'common.remove'/>
		                        	</a>
			                    </span>
	    		                <div class="relative" style="font-weight: strong;">
	        		                <span ng-bind="disambiguatedOrganization.value"></span>
	            		        </div>
	                		</div>
		                	<div class="control-group">
	    	                	<span ng-hide="disambiguatedOrganization">
	        	                	   <label>Institution</label>
	            	        	</span>
		                	    <span ng-show="disambiguatedOrganization">
	    	                    	<label>Display institution</label>
	        	            	</span>
	            	    	    <div class="relative">
		                	        <input id="organizationName" class="input-xlarge" name="organizationName" type="text" ng-model="editPeerReview.orgName.value" placeholder="Type name. Select from the list to fill other fields" ng-change="serverValidate('peer-reviews/orgNameValidate.json')" ng-model-onblur/>
	    		                    <span class="required" ng-class="isValidClass(editPeerReview.orgName)">*</span>
	            		            <span class="orcid-error" ng-show="editPeerReview.orgName.errors.length > 0">
	                    	        	<div ng-repeat='error in editPeerReview.orgName.errors' ng-bind-html="error"></div>
	                        		</span>
		    	                </div>
	    	    	        </div>
	                		<div class="control-group">
			                    <label ng-hide="disambiguatedOrganization">City</label>
	        		            <label ng-show="disambiguatedOrganization">Display city</label>
	                		    <div class="relative">
			                        <input name="city" type="text" class="input-xlarge"  ng-model="editPeerReview.city.value" placeholder="Add city" ng-change="serverValidate('peer-reviews/cityValidate.json')" ng-model-onblur/>
	        		                <span class="required" ng-class="isValidClass(editPeerReview.city)">*</span>
	                		        <span class="orcid-error" ng-show="editPeerReview.city.errors.length > 0">
	                        		    <div ng-repeat='error in editPeerReview.city.errors' ng-bind-html="error"></div>
			                        </span>
	        		            </div>
			                </div>
	        		        <div class="control-group">
	                		    <label ng-hide="disambiguatedOrganization">Region</label>
			                    <label ng-show="disambiguatedOrganization">Display region</label>
	        		            <div class="relative">
	                		        <input name="region" type="text" class="input-xlarge"  ng-model="editPeerReview.region.value" placeholder="Add region" ng-change="serverValidate('peer-reviews/regionValidate.json')" ng-model-onblur/>
			                        <span class="orcid-error" ng-show="editPeerReview.region.errors.length > 0">
	        	        	            <div ng-repeat='error in editPeerReview.region.errors' ng-bind-html="error"></div>
	            		            </span>
	                    		</div>
	                		</div>
			                <div class="control-group">
	        		            <label ng-hide="disambiguatedOrganization">Country</label>
	                		    <label ng-show="disambiguatedOrganization">Display country</label>
			                    <div class="relative">
	        		                <select id="country" class="input-xlarge" name="country" ng-model="editPeerReview.country.value" ng-change="serverValidate('peer-reviews/countryValidate.json')">
	                		            <option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
										<#list isoCountries?keys as key>
	                                    	<option value="${key}">${isoCountries[key]}</option>
										</#list>
	                        		</select>
			                        <span class="required" ng-class="isValidClass(editPeerReview.country)">*</span>
	        		                <span class="orcid-error" ng-show="editPeerReview.country.errors.length > 0">
	                	        	    <div ng-repeat='error in editPeerReview.country.errors' ng-bind-html="error"></div>
	                    		    </span>
	                    		</div>
	                		</div>
						</div>
						<!-- DATE -->				
						<span><strong>COMPLETION DATE</strong></span>	
						<div class="control-group">			    		
				    		<div class="relative">					    
								<select id="year" class="col-md-3 col-sm-3 col-xs-3 inline-input" name="year" ng-model="editPeerReview.completionDate.year">
									<#list years?keys as key>
										<option value="${key}">${years[key]}</option>
									</#list>
					    		</select>				    	
								<select id="month" class="col-md-4 col-sm-4 col-xs-4 inline-input" name="month" ng-model="editPeerReview.completionDate.month">
									<#list months?keys as key>
										<option value="${key}">${months[key]}</option>
									</#list>
					    		</select>
								<select id="day" class="col-md-3 col-sm-3 col-xs-3 inline-input" name="day" ng-model="editPeerReview.completionDate.day">
									<#list days?keys as key>
										<option value="${key}">${days[key]}</option>
									</#list>
					    		</select>								    
				    		</div>
						</div>
						<!-- External identifiers -->
					    <span><strong>EXTERNAL IDENTIFIERS</strong></span>
						<div ng-repeat="extId in editPeerReview.externalIdentifiers"> 
							<div class="control-group">
								<label class="relative">Identifier type</label>
								<div class="relative">
				    				<select id="extIdType" class="input-xlarge" name="extIdType" ng-model="extId.workExternalIdentifierType.value">																					 
										<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.WorkExternalIdentifierType.empty' /></option>
										<#list idTypes?keys as key>
											<option value="${idTypes[key]}">${key}</option>
										</#list>
									</select> 
									<a href ng-click="deleteExternalIdentifier(extId)" class="glyphicon glyphicon-trash grey"></a>
									<span class="orcid-error" ng-show="extId.workExternalIdentifierType.errors.length > 0">
	                	        	    <div ng-repeat='error in extId.workExternalIdentifierType.errors' ng-bind-html="error"></div>
	                    		    </span>
								</div>	
							</div>								
							
							<div class="control-group">
								<label class="relative">Identifier value</label>
						    	<div class="relative">
									<input id="extIdValue" name="extIdValue" type="text" class="input-xlarge"  ng-model="extId.workExternalIdentifierId.value"/>
									<span class="orcid-error" ng-show="workExternalIdentifier.workExternalIdentifierId.errors.length > 0">
										<div ng-repeat='error in workExternalIdentifier.workExternalIdentifierId.errors' ng-bind-html="error"></div>
									</span>
								</div>
								<div class="add-item-link" ng-show="$last">			
									<span><a href ng-click="addExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign"></i> Add external identifier</a></span>
								</div>
							</div>
						</div>
					</div>
		
					
					
					
					<!-- Right column -->
					<div class="col-md-6 col-sm-6 col-xs-12">
						<!-- URL -->	
						<div class="control-group">
					    	<label class="relative">URL</label>
					    	<div class="relative">
								<input id="url" class="input-xlarge" name="url" type="text" ng-model="editPeerReview.url.value" placeholder="Type url." ng-change="serverValidate('peer-reviews/urlValidate.json')" ng-model-onblur/>
								<span class="required" ng-class="isValidClass(editPeerReview.url)">*</span>
	            		        <span class="orcid-error" ng-show="editPeerReview.url.errors.length > 0">
	                    	    	<div ng-repeat='error in editPeerReview.url.errors' ng-bind-html="error"></div>
	                        	</span>
							</div>
						</div>
						
						<!-- Subject -->
						<!-- Subject External ids -->
						<span><strong>SUBJECT</strong></span>
						<span>EXTERNAL IDENTIFIERS</span>
						<div ng-repeat="extId in editPeerReview.subjectForm.workExternalIdentifiers"> 
							<!-- Ext id type-->
							<div class="control-group">
								<label class="relative">Identifier type</label>
								<div class="relative">
									<select id="extIdType" class="input-xlarge" name="extIdType" ng-model="extId.workExternalIdentifierType.value">																					 
										<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.WorkExternalIdentifierType.empty' /></option>
										<#list idTypes?keys as key>
											<option value="${idTypes[key]}">${key}</option>
										</#list>
									</select> 
									<a href ng-click="deleteSubjectExternalIdentifier(extId)" class="glyphicon glyphicon-trash grey"></a>
									<span class="orcid-error" ng-show="extId.workExternalIdentifierType.errors.length > 0">
										<div ng-repeat='error in extId.workExternalIdentifierType.errors' ng-bind-html="error"></div>
									</span>
								</div>	
							</div>
							
							<!-- Ext id value-->
							<div class="control-group">
								<label class="relative">Identifier value</label>
								<div class="relative">
									<input id="extIdValue" name="extIdValue" type="text" class="input-xlarge"  ng-model="extId.workExternalIdentifierId.value"/>
									<span class="orcid-error" ng-show="workExternalIdentifier.workExternalIdentifierId.errors.length > 0">
										<div ng-repeat='error in workExternalIdentifier.workExternalIdentifierId.errors' ng-bind-html="error"></div>
									</span>
								</div>
								<div class="add-item-link" ng-show="$last">			
									<span><a href ng-click="addSubjectExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign"></i> Add external identifier</a></span>
								</div>
							</div>	
						</div>										 
											
						<!-- Subject Type -->
						<div class="control-group">
				    		<label class="relative"><@orcid.msg 'manual_work_form_contents.labelworktype'/></label>
							<div class="relative">
								<select id="peerReviewSubjectType" class="input-xlarge" name="peerReviewSubjectType" ng-model="editPeerReview.subjectForm.workType.value" ng-change="serverValidate('peer-reviews/subject/typeValidate.json');">
									<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.WorkType.empty' /></option>
									<#list workTypes?keys as key>
										<option value="${key}">${workTypes[key]}</option>
									</#list>
								</select> 
								<span class="required" ng-class="isValidClass(editPeerReview.subjectForm.workType)">*</span>
								<span class="orcid-error" ng-show="editPeerReview.subjectForm.workType.errors.length > 0">
									<div ng-repeat='error in editPeerReview.subjectForm.workType.errors' ng-bind-html="error"></div>
								</span>
							</div>
						</div>
						
						<!-- Subject Title -->
						<div class="control-group">
						   <label><@orcid.msg 'manual_work_form_contents.labeltitle'/></label>
						   <div class="relative">
						      <input name="title" type="text" class="input-xlarge"  ng-model="editPeerReview.subjectForm.title.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_title'/>" ng-change="serverValidate('peer-reviews/subject/titleValidate.json')" ng-model-onblur/>						
						      <span class="required" ng-class="isValidClass(editPeerReview.subjectForm.title)">*</span>						
						      <span class="orcid-error" ng-show="editPeerReview.subjectForm.title.errors.length > 0">
						         <div ng-repeat='error in editPeerReview.subjectForm.title.errors' ng-bind-html="error"></div>
						      </span>
						      <div class="add-item-link">
						         <span ng-hide="editTranslatedTitle"><a ng-click="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_work_form_contents.labelshowtranslatedtitle'/></a></span>
						         <span ng-show="editTranslatedTitle"><a ng-click="toggleTranslatedTitleModal()"><i class="glyphicon glyphicon-minus-sign"></i> <@orcid.msg 'manual_work_form_contents.labelhidetranslatedtitle'/></a></span>
						      </div>
						   </div>
						</div>
						<div id="translatedTitle">
						   <span class="orcid-error" ng-show="editPeerReview.subjectForm.translatedTitle.errors.length > 0">
						      <div ng-repeat='error in editPeerReview.translatedTitle.errors' ng-bind-html="error"></div>
						   </span>
						   <div class="control-group">
						      <label><@orcid.msg 'manual_work_form_contents.labeltranslatedtitle'/></label>
						      <div class="relative">
						         <input name="translatedTitle" type="text" class="input-xlarge" ng-model="editPeerReview.subjectForm.translatedTitle.content" placeholder="<@orcid.msg 'manual_work_form_contents.add_translated_title'/>" ng-model-onblur/>
						      </div>
						   </div>
						   <div class="control-group">
						      <label class="relative"><@orcid.msg 'manual_work_form_contents.labeltranslatedtitlelanguage'/></label>
						      <div class="relative">
						         <select id="language" name="language" ng-model="editPeerReview.subjectForm.translatedTitle.languageCode">
						            <#list languages?keys as key>
						            <option value="${languages[key]}">${key}</option>
						            </#list>
						         </select>
						      </div>
						   </div>
						</div>
						
						<div id="translatedTitle">
							<span class="orcid-error" ng-show="editWork.translatedTitle.errors.length > 0">
								<div ng-repeat='error in editWork.translatedTitle.errors' ng-bind-html="error"></div>
							</span>
							<div class="control-group">
								<label><@orcid.msg 'manual_work_form_contents.labeltranslatedtitle'/></label>
								<div class="relative">
									<input name="translatedTitle" type="text" class="input-xlarge" ng-model="editWork.translatedTitle.content" placeholder="<@orcid.msg 'manual_work_form_contents.add_translated_title'/>" ng-change="serverValidate('works/work/translatedTitleValidate.json')" ng-model-onblur/>														
								</div>						
							</div>
		
							<div class="control-group">
								<label class="relative"><@orcid.msg 'manual_work_form_contents.labeltranslatedtitlelanguage'/></label>
								<div class="relative">						
									<select id="language" name="language" ng-model="editWork.translatedTitle.languageCode" ng-change="serverValidate('works/work/translatedTitleValidate.json')">			
										<#list languages?keys as key>
											<option value="${languages[key]}">${key}</option>
										</#list>
									</select>				
								</div>
							</div>					
						</div>
					
					
					<div class="control-group">
						<label><@orcid.msg 'manual_work_form_contents.journalTitle'/></label>
					    <div class="relative">
							<input name="journalTitle" type="text" class="input-xlarge"  ng-model="editPeerReview.subjectForm.journalTitle.value" placeholder="<@orcid.msg 'manual_work_form_contents.add_journalTitle'/>" ng-model-onblur/>
							<span class="orcid-error" ng-show="editPeerReview.subjectForm.journalTitle.errors.length > 0">
								<div ng-repeat='error in editPeerReview.subjectForm.journalTitle.errors' ng-bind-html="error"></div>
							</span>						
						</div>
					</div>	
					
					<!-- Subject URL -->	
						<div class="control-group">
					    	<label class="relative">Subject URL</label>
					    	<div class="relative">
								<input name="subjectUrl" type="text" class="input-xlarge"  ng-model="editPeerReview.subjectForm.url.value" placeholder="Type a url" ng-change="serverValidate('peer-reviews/subject/urlValidate.json')" ng-model-onblur/>
								<span class="orcid-error" ng-show="editPeerReview.subjectForm.url.errors.length > 0">
									<div ng-repeat='error in editPeerReview.subjectForm.url.errors' ng-bind-html="error"></div>
								</span>							
							</div>
						</div>
	
						<div class="control-group">
	                    	<button class="btn btn-primary" ng-click="addAPeerReview()" ng-disabled="addingPeerReview" ng-class="{disabled:addingPeerReview}">
		                        <!--<span ng-show="" class="">Add to list</span>-->
								<!--<span ng-show="" class="">Save changes</span>-->
	                        	<span>Add to list</span>
	                    	</button>
	                    	<button id="" class="btn close-button" ng-click="closeModal()" type="reset">Cancel</button>
	                	</div>
					</div>
				</div>
			</fn-form>			
		</div>		
	</div>
 </script>