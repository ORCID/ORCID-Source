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

<script type="text/ng-template" id="edit-aka">	
	<!-- Other Names -->
	<div class="lightbox-container">
		<div class="edit-record edit-aka">
			<!-- Title -->
			<div class="row">			
				<div class="col-md-12 col-sm-12 col-xs-12">	
					<h1 class="lightbox-title pull-left">
						<!-- <@orcid.msg ''/> -->
						Edit also known as
					</h1>
				</div>			
			</div>
			<div class="row">
				<div class="col-md-12 col-xs-12 col-sm-12">
					<div class="fixed-area">
						<div class="scroll-area">		
	        	      	   <div class="row aka-row" ng-repeat="otherName in otherNamesForm.otherNames | orderBy : 'displayIndex'" ng-cloak> 								
								<div class="col-md-6 col-sm-6 col-xs-12">
									<div class="aka">
										<input type="text" ng-model="otherName.content" ng-show="otherName.source == orcidId || otherName.source == null"  focus-me="newInput"/>
										<span ng-bind="otherName.content" ng-show="otherName.source != orcidId && otherName.sourceName"></span>										
									</div>
									<div class="source" ng-show="otherName.sourceName">Source: {{otherName.sourceName}}</div>
								</div>							
								<div class="col-md-6 col-sm-6 col-xs-12">
									<ul class="record-settings pull-right">
										<li>												
											<span class="glyphicon glyphicon-arrow-up circle" ng-click="$first || setPriorityUp(otherName.displayIndex)"></span>										
											
										</li>
										<li>																						
											<span class="glyphicon glyphicon-arrow-down circle" ng-click="$last || setPriorityDown(otherName.displayIndex)"></span>											
										</li>
										<li>										
											<span class="glyphicon glyphicon-trash" ng-click="deleteOtherName(otherName)"></span>											
										</li>
										<li>											
											<@orcid.privacyToggle3  angularModel="otherName.visibility.visibility"
				             					questionClick="toggleClickPrivacyHelp($index)"
				             					clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
				             					publicClick="setPrivacyModal('PUBLIC', $event, otherName)" 
		                	     				limitedClick="setPrivacyModal('LIMITED', $event, otherName)" 
		                	     				privateClick="setPrivacyModal('PRIVATE', $event, otherName)"
		                	     				elementId="$index" />	
										</li>
									</ul>
									<span class="created-date pull-right" ng-show="otherName.createdDate" ng-class="{'hidden-xs' : otherName.createdDate}">Created: {{otherName.createdDate.year + '-' + otherName.createdDate.month + '-' + otherName.createdDate.day}}</span>
									<span class="created-date pull-left" ng-show="otherName.createdDate" ng-class="{'visible-xs' : otherName.createdDate}">Created: {{otherName.createdDate.year + '-' + otherName.createdDate.month + '-' + otherName.createdDate.day}}</span>
								</div>
							</div>
						</div>
					</div>
					<div class="record-buttons">
						<a ng-click="addNewModal()"><span class="glyphicon glyphicon-plus pull-left"></span></a>	        	      		
			            <button class="btn btn-primary pull-right" ng-click="setOtherNamesForm(true)"><@spring.message "freemarker.btnsavechanges"/></button>	        	      		
			            <a class="cancel-option pull-right" ng-click="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>		
</script>

<script type="text/ng-template" id="edit-country">
	<!-- Country -->
	<div class="lightbox-container">
		<div class="edit-record edit-country">
			<!-- Title -->
			<div class="row">			
				<div class="col-md-12 col-sm-12 col-xs-12">	
					<h1 class="lightbox-title pull-left">
						<!-- <@orcid.msg ''/> -->
						Edit Country
					</h1>
				</div>			
			</div>
			<div class="row">
				<div class="col-md-12 col-xs-12 col-sm-12">
					<div class="fixed-area">
						<div class="scroll-area">		
							<div class="row aka-row" ng-repeat="country in countryForm.addresses | orderBy: 'displayIndex'">
								<div class="col-md-6">
									<div class="aka">
			                 			<select id="country" name="country" ng-model="country.iso2Country.value" ng-disabled="{{country.source != orcidId && country.source != null}}" ng-class="{'not-allowed': country.source != orcidId && country.source != null}" focus-me="newInput">
				    			 			<option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
								 			<#list isoCountries?keys as key>
								     			<option value="${key}">${isoCountries[key]}</option>
							 	 			</#list>
							 			</select>
									</div> 
						 			<div class="source" ng-show="country.sourceName">Source: {{country.sourceName}}</div>
								</div> 
								<div class="col-md-6">
									<ul class="record-settings pull-right">
										<li>
											<input name="priority" type="radio" ng-model="primary" ng-value="country.primary" ng-click="setPrimary(country)">
										</li>										
										<li ng-init="">												
											<span class="glyphicon glyphicon-arrow-up circle" ng-click="$first || setPriorityUp(country.displayIndex)"></span>											
										</li>
										<li>
											<span class="glyphicon glyphicon-arrow-down circle" ng-click="$last || setPriorityDown(country.displayIndex)"></span>
										</li>
										<li>
											<span class="glyphicon glyphicon-trash" ng-click="deleteCountry(country)"></span>											
										</li>
										<li>
											<@orcid.privacyToggle3  angularModel="country.visibility.visibility"
				         						questionClick="toggleClickPrivacyHelp($index)"
				         						clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
				         						publicClick="setPrivacyModal('PUBLIC', $event, country)" 
	                 	     					limitedClick="setPrivacyModal('LIMITED', $event, country)" 
	                 	     					privateClick="setPrivacyModal('PRIVATE', $event, country)"
	                 	      					elementId="$index"/>	
										</li>
									</ul>
									<span class="created-date pull-right" ng-show="country.createdDate" ng-class="{'hidden-xs' : country.createdDate}">Created: {{country.createdDate.year + '-' + country.createdDate.month + '-' + country.createdDate.day}}</span>
									<span class="created-date pull-left" ng-show="country.createdDate" ng-class="{'visible-xs' : country.createdDate}">Created: {{country.createdDate.year + '-' + country.createdDate.month + '-' + country.createdDate.day}}</span>
								</div>					 				
							</div>											
						</div>
						<div ng-show="countryForm.errors.length > 0">
							<div ng-repeat="error in countryForm.errors">
								<span ng-bind="error" class="red"></span>
							</div>
						</div>
					</div>					
					<div class="record-buttons">						
						<a ng-click="addNewModal()"><span class="glyphicon glyphicon-plus pull-left"></span></a>	        	    		
		            	<button class="btn btn-primary pull-right" ng-click="setCountryForm(true)"><@spring.message "freemarker.btnsavechanges"/></button>
		            	<a class="cancel-option pull-right" ng-click="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>	
</script>


<script type="text/ng-template" id="edit-keyword">
	<!-- Keywords -->
	<div class="lightbox-container">
		<div class="edit-record edit-keyword">
			<!-- Title -->
			<div class="row">			
				<div class="col-md-12 col-sm-12 col-xs-12">	
					<h1 class="lightbox-title pull-left">
						<!-- <@orcid.msg ''/> -->
						Edit Keywords
					</h1>
				</div>			
			</div>
			<div class="row">
				<div class="col-md-12 col-xs-12 col-sm-12">
					<div class="fixed-area">
						<div class="scroll-area">		
							<div class="row aka-row" ng-repeat="keyword in keywordsForm.keywords | orderBy:'displayIndex'">		
								<div class="col-md-6">
									<div class="aka">
										<input type="text" ng-model="keyword.content" ng-show="keyword.source == orcidId || keyword.source == null" focus-me="newInput"></input>
										<span ng-bind="keyword.content" ng-show="keyword.source != orcidId && keyword.sourceName"></span>
									</div>
									<div class="source" ng-show="keyword.sourceName">Source: {{keyword.sourceName}}</div>										
								</div>
								
								<div class="col-md-6">
									<ul class="record-settings pull-right">
										<li>												
											<span class="glyphicon glyphicon-arrow-up circle" ng-click="$first || setPriorityUp(keyword.displayIndex)"></span>
										</li>
										<li>																						
											<span class="glyphicon glyphicon-arrow-down circle" ng-click="$last || setPriorityDown(keyword.displayIndex)"></span>											
										</li>
										<li>										
											<span class="glyphicon glyphicon-trash" ng-click="deleteKeyword(keyword)"></span>											
										</li>
										<li>
											<@orcid.privacyToggle3  angularModel="keyword.visibility.visibility"
		             	  						questionClick="toggleClickPrivacyHelp($index)"
		             	  						clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
		             	  						publicClick="setPrivacyModal('PUBLIC', $event, keyword)" 
                	      						limitedClick="setPrivacyModal('LIMITED', $event, keyword)" 
                	      						privateClick="setPrivacyModal('PRIVATE', $event, keyword)"
                	      						elementId="$index"/>	
										</li>
									</ul>
									<span class="created-date pull-right" ng-show="keyword.createdDate" ng-class="{'hidden-xs' : keyword.createdDate}">Created: {{keyword.createdDate.year + '-' + keyword.createdDate.month + '-' + keyword.createdDate.day}}</span>
									<span class="created-date pull-left" ng-show="keyword.createdDate" ng-class="{'visible-xs' : keyword.createdDate}">Created: {{keyword.createdDate.year + '-' + keyword.createdDate.month + '-' + keyword.createdDate.day}}</span>
								</div>					 				
							</div>											
						</div>
					</div>
					<div class="record-buttons">						
						<a ng-click="addNewModal()"><span class="glyphicon glyphicon-plus pull-left"></span></a>	        	    		
		            	<button class="btn btn-primary pull-right" ng-click="setKeywordsForm(true)"><@spring.message "freemarker.btnsavechanges"/></button>
		            	<a class="cancel-option pull-right" ng-click="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>

</script>


<script type="text/ng-template" id="edit-websites">
	<div class="lightbox-container">
		<div class="edit-record edit-websites">
			<!-- Title -->
			<div class="row">			
				<div class="col-md-12 col-sm-12 col-xs-12">	
					<h1 class="lightbox-title pull-left">
						<!-- <@orcid.msg ''/> -->
						Edit Websites
					</h1>
				</div>			
			</div>
			<div class="row">
				<div class="col-md-12 col-xs-12 col-sm-12">
					<div class="fixed-area">
						<div class="scroll-area">		
							<div class="row aka-row websites" ng-repeat="website in websitesForm.websites | orderBy:'displayIndex'">
								<div class="col-md-6">
									<div class="aka">										
										<input type="text" ng-model="website.urlName" ng-show="website.source == orcidId || website.source == null" focus-me="newInput" placeholder="${springMacroRequestContext.getMessage('manual_work_form_contents.labeldescription')}"></input>
										<input type="text" ng-model="website.url" ng-show="website.source == orcidId || website.source == null" placeholder="${springMacroRequestContext.getMessage('common.url')}"></input>
 										<a href="{{website.url}}" target="_blank" rel="me nofollow" ng-show="website.source != orcidId && website.sourceName" ng-cloak>{{website.urlName != null? website.urlName : website.url}}</a>										
									</div>
									<div class="source" ng-show="website.sourceName">Source: {{website.sourceName}}</div>										
								</div>
								
								<div class="col-md-6">
									<ul class="record-settings pull-right">
										<li>												
											<span class="glyphicon glyphicon-arrow-up circle" ng-click="$first || setPriorityUp(website.displayIndex)"></span>											
										</li>
										<li>																						
											<span class="glyphicon glyphicon-arrow-down circle" ng-click="$last || setPriorityDown(website.displayIndex)"></span>											
										</li>
										<li>										
											<span class="glyphicon glyphicon-trash" ng-click="deleteWebsite(website)"></span>											
										</li>
										<li>
											<@orcid.privacyToggle3  angularModel="website.visibility.visibility"
		             	  						questionClick="toggleClickPrivacyHelp($index)"
		             	  						clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
		             	  						publicClick="setPrivacyModal('PUBLIC', $event, website)" 
                	      						limitedClick="setPrivacyModal('LIMITED', $event, website)" 
                	      						privateClick="setPrivacyModal('PRIVATE', $event, website)"
                	      						elementId="$index"/>	
										</li>
									</ul>
									<span class="created-date pull-right" ng-show="website.createdDate" ng-class="{'hidden-xs' : website.createdDate}">Created: {{website.createdDate.year + '-' + website.createdDate.month + '-' + website.createdDate.day}}</span>
									<span class="created-date pull-left" ng-show="website.createdDate" ng-class="{'visible-xs' : website.createdDate}">Created: {{website.createdDate.year + '-' + website.createdDate.month + '-' + website.createdDate.day}}</span>
								</div>								
								<div ng-show="website.errors.length > 0" class="col-md-12">									
									<div ng-repeat="error in website.errors">
										<span ng-bind="error" class="red"></span>
									</div>
								</div>					 				
							</div>																								
						</div>
					</div>
					
					<div class="record-buttons">						
						<a ng-click="addNewModal()"><span class="glyphicon glyphicon-plus pull-left"></span></a>	        	    		
		            	<button class="btn btn-primary pull-right" ng-click="setWebsitesForm(true)"><@spring.message "freemarker.btnsavechanges"/></button>
		            	<a class="cancel-option pull-right" ng-click="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>
</script>


<script type="text/ng-template" id="edit-external-identifiers">
	<div class="lightbox-container">
		<div class="edit-record edit-external-identifiers">
			<!-- Title -->
			<div class="row">			
				<div class="col-md-12 col-sm-12 col-xs-12">	
					<h1 class="lightbox-title pull-left">
						<!-- <@orcid.msg ''/> -->
						Edit External Identifiers
					</h1>
				</div>			
			</div>
			<div class="row">
				<div class="col-md-12 col-xs-12 col-sm-12">
					<div class="fixed-area">
						<div class="scroll-area">		
							<div class="row aka-row external-identifiers" ng-repeat="externalIdentifier in externalIdentifiersForm.externalIdentifiers | orderBy:'displayIndex'">
								<div class="col-md-6">
									<div class="aka">										
										<p>
											<span ng-hide="externalIdentifier.url">{{externalIdentifier.commonName}} {{externalIdentifier.reference}}</span>
			        						<span ng-show="externalIdentifier.url"><a href="{{externalIdentifier.url}}" target="_blank">{{externalIdentifier.commonName}} {{externalIdentifier.reference}}</a></span>
										</p>										
									</div>
									<div class="source" ng-show="externalIdentifier.sourceName">Source: {{externalIdentifier.sourceName}}</div>										
								</div>
								
								<div class="col-md-6">
									<ul class="record-settings pull-right">
										<li>												
											<span class="glyphicon glyphicon-arrow-up circle" ng-click="$first || setPriorityUp(externalIdentifier.displayIndex)"></span>											
										</li>
										<li>																						
											<span class="glyphicon glyphicon-arrow-down circle" ng-click="$last || setPriorityDown(externalIdentifier.displayIndex)"></span>											
										</li>
										<li>										
											<span class="glyphicon glyphicon-trash" ng-click="deleteExternalIdentifier(externalIdentifier)"></span>											
										</li>
										<li>
											<@orcid.privacyToggle3  angularModel="externalIdentifier.visibility.visibility"
		             	  						questionClick="toggleClickPrivacyHelp($index)"
		             	  						clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
		             	  						publicClick="setPrivacyModal('PUBLIC', $event, externalIdentifier)" 
                	      						limitedClick="setPrivacyModal('LIMITED', $event, externalIdentifier)" 
                	      						privateClick="setPrivacyModal('PRIVATE', $event, externalIdentifier)"
                	      						elementId="$index"/>	
										</li>
									</ul>
									<span class="created-date pull-right" ng-show="externalIdentifier.createdDate" ng-class="{'hidden-xs' : externalIdentifier.createdDate}">Created: {{externalIdentifier.createdDate.year + '-' + externalIdentifier.createdDate.month + '-' + externalIdentifier.createdDate.day}}</span>
									<span class="created-date pull-left" ng-show="externalIdentifier.createdDate" ng-class="{'visible-xs' : externalIdentifier.createdDate}">Created: {{externalIdentifier.createdDate.year + '-' + externalIdentifier.createdDate.month + '-' + externalIdentifier.createdDate.day}}</span>
								</div>								
								<div ng-show="website.errors.length > 0" class="col-md-12">									
									<div ng-repeat="error in externalIdentifier.errors">
										<span ng-bind="error" class="red"></span>
									</div>
								</div>					 				
							</div>																								
						</div>
					</div>
					<div class="record-buttons">	
		            	<button class="btn btn-primary pull-right" ng-click="setExternalIdentifiersForm(true)"><@spring.message "freemarker.btnsavechanges"/></button>
		            	<a class="cancel-option pull-right" ng-click="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>
</script>