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
	        	      	   <div class="row aka-row" ng-repeat="otherName in otherNamesForm.otherNames" ng-cloak> 								
								<div class="col-md-6">
									<div class="aka">
										<input type="text" ng-model="otherName.content" ng-show="otherName.source == orcidId || otherName.source == null"/>
										<span ng-bind="otherName.content" ng-show="otherName.source != orcidId && otherName.sourceName"></span>										
									</div>
									<div class="source" ng-show="otherName.sourceName">Source: {{otherName.sourceName}}</div>
								</div>							
								<div class="col-md-6">
									<ul class="record-settings pull-right">
										<li>												
											<span class="glyphicon glyphicon-arrow-up circle" ng-click=""></span>											
										</li>
										<li>																						
											<span class="glyphicon glyphicon-arrow-down circle" ng-click=""></span>											
										</li>
										<li>										
											<span class="glyphicon glyphicon-trash" ng-click="deleteKeyword(otherName)"></span>											
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
									<span class="created-date pull-right" ng-show="otherName.createdDate">Created: {{otherName.createdDate.year + '-' + otherName.createdDate.month + '-' + otherName.createdDate.day}}</span>
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
							<div class="row"><!-- ng-repeat="" -->
								<div class="col-md-6">
									<select id="country" name="country" ng-model="countryForm.iso2Country.value">
			    			 			<option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
						 				<#list isoCountries?keys as key>
								     		<option value="${key}">${isoCountries[key]}</option>
					 	 				</#list>
					 				</select>
								</div>
								<div class="col-md-6">
									<ul class="record-settings pull-right">
										<li>												
											<span class="glyphicon glyphicon-arrow-up circle" ng-click=""></span>											
										</li>
										<li>																						
											<span class="glyphicon glyphicon-arrow-down circle" ng-click=""></span>											
										</li>
										<li>										
											<span class="glyphicon glyphicon-trash" ng-click=""></span>											
										</li>
										<li>
											<@orcid.privacyToggle3  angularModel="countryForm.profileAddressVisibility.visibility"
				         						questionClick="toggleClickPrivacyHelp($index)"
				         						clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
				         						publicClick="setPrivacy('PUBLIC', $event)" 
	                 	     					limitedClick="setPrivacy('LIMITED', $event)" 
	                 	     					privateClick="setPrivacy('PRIVATE', $event)"
	                 	      					elementId="$index"/>	
										</li>
									</ul>
									<span class="created-date pull-right">Created: 2014-06-30</span>
								</div>					 				
							</div>											
						</div>
					</div>
					<div class="record-buttons">						
						<a ng-click="addNew()"><span class="glyphicon glyphicon-plus pull-left"></span></a>	        	    		
		            	<button class="btn btn-primary pull-right" ng-click="setCountryForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		            	<a class="cancel-option pull-right" ng-click="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>	
</script>


<script type="text/ng-template" id="edit-keyword">
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
							<div class="row"><!-- ng-repeat="" -->
								<div class="col-md-6">
									<input type="text" ng-model="keyword.value" ng-enter="setKeywordsForm()"></input>
								</div>
								<div class="col-md-6">
									<ul class="record-settings pull-right">
										<li>												
											<span class="glyphicon glyphicon-arrow-up circle" ng-click=""></span>											
										</li>
										<li>																						
											<span class="glyphicon glyphicon-arrow-down circle" ng-click=""></span>											
										</li>
										<li>										
											<span class="glyphicon glyphicon-trash" ng-click=""></span>											
										</li>
										<li>
											<@orcid.privacyToggle3  angularModel="keywordsForm.visibility.visibility"
		             	  						questionClick="toggleClickPrivacyHelp($index)"
		             	  						clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
		             	  						publicClick="setPrivacy('PUBLIC', $event)" 
                	      						limitedClick="setPrivacy('LIMITED', $event)" 
                	      						privateClick="setPrivacy('PRIVATE', $event)"
                	      						elementId="$index"/>	
										</li>
									</ul>
									<span class="created-date pull-right">Created: 2014-06-30</span>
								</div>					 				
							</div>											
						</div>
					</div>
					<div class="record-buttons">						
						<a ng-click="addNew()"><span class="glyphicon glyphicon-plus pull-left"></span></a>	        	    		
		            	<button class="btn btn-primary pull-right" ng-click="setCountryForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		            	<a class="cancel-option pull-right" ng-click="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>

</script>