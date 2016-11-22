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
	<div class="lightbox-container" id="aka-popover">
		<div class="edit-record	edit-record-bulk-edit edit-aka">
			<!-- Title -->
			<div class="row">			
				<div class="col-md-12 col-sm-12 col-xs-12">	
					<h1 class="lightbox-title pull-left">
						<@orcid.msg 'manage_bio_settings.editOtherNames'/>
					</h1>
				</div>			
			</div>
				
				<div class="row bottomBuffer">							
					<div ng-include="'bulk-edit'"></div>					
				</div>				
				<div class="row">
					<div class="col-md-12 col-sm-12 col-xs-12 padding-right-reset">
						<span class="right"><@orcid.msg 'groups.common.edit_individual_privacy' /></span>	
					</div>
				</div>		

			<div class="row">
				<div class="col-md-12 col-xs-12 col-sm-12" style="position: static">
					<div class="fixed-area" scroll>
						<div class="scroll-area">
	        	      	   <div class="row aka-row" ng-repeat="otherName in otherNamesForm.otherNames" ng-cloak>							 								
								<div class="col-md-6 col-sm-6 col-xs-12">
									<div class="aka">		
										<input type="text" ng-model="otherName.content" ng-if="otherName.source == orcidId"  focus-me="newInput" />																				
										<span ng-bind="otherName.content" ng-if="otherName.source != orcidId && otherName.source != null"></span>										
									</div>									    
									<div class="source" ng-if="otherName.sourceName || otherName.sourceName == null"><@orcid.msg 'manage_bio_settings.source'/>: <span ng-if="otherName.sourceName">{{otherName.sourceName}}</span><span ng-if="otherName.sourceName == null">{{orcidId}}</span></div>
								</div>							
								<div class="col-md-6 col-sm-6 col-xs-12" style="position: static">																															
									<ul class="record-settings pull-right">
										<li>
											<div class="glyphicon glyphicon-arrow-up circle" ng-click="$first || swapUp($index)" ng-mouseover="commonSrvc.showTooltip('tooltip-aka-move-up-'+$index, $event, 37, -33, 44)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-aka-move-up-'+$index)"></div>											
											<@orcid.tooltip elementId="'tooltip-aka-move-up-'+$index" message="common.modals.move_up"/>
										</li>
										<li>																						
											<div class="glyphicon glyphicon-arrow-down circle" ng-click="$last || swapDown($index)" ng-mouseover="commonSrvc.showTooltip('tooltip-aka-move-down-'+$index, $event, 37, -2, 53)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-aka-move-down-'+$index)"></div>
											<@orcid.tooltip elementId="'tooltip-aka-move-down-'+$index" message="common.modals.move_down" />											
										</li>
										<li>
											<div class="glyphicon glyphicon-trash" ng-click="deleteOtherName(otherName)" ng-mouseover="commonSrvc.showTooltip('tooltip-aka-delete-'+$index, $event, 37, 50, 39)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-aka-delete-'+$index)"></div>
											<@orcid.tooltip elementId="'tooltip-aka-delete-'+$index" message="common.modals.delete" />
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
									<span class="created-date pull-right" ng-show="otherName.createdDate" ng-class="{'hidden-xs' : otherName.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{otherName.createdDate.year + '-' + otherName.createdDate.month + '-' + otherName.createdDate.day}}</span>
									<span class="created-date pull-left" ng-show="otherName.createdDate" ng-class="{'visible-xs' : otherName.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{otherName.createdDate.year + '-' + otherName.createdDate.month + '-' + otherName.createdDate.day}}</span>
								</div>
							</div>
						</div>
					</div>
					<div class="record-buttons">
						<a ng-click="addNewModal()"><span class="glyphicon glyphicon-plus pull-left">
							<div class="popover popover-tooltip-add top">
	    						<div class="arrow"></div>
	    						<div class="popover-content">
									<span><@orcid.msg 'common.modals.add' /></span>
	    						</div>
	   						</div> 
						</span></a>	        	      		
			            <button class="btn btn-primary pull-right" ng-click="setOtherNamesForm()"><@spring.message "freemarker.btnsavechanges"/></button>	        	      		
			            <a class="cancel-option pull-right" ng-click="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
					</div>					
				</div>
			</div>
		</div>
	</div>		
</script>

<script type="text/ng-template" id="edit-country">
	<!-- Country -->
	<div class="lightbox-container" id="country-popover">
		<div class="edit-record <#if RequestParameters['bulkEdit']??>
			edit-record-bulk-edit
			</#if> edit-country">
			<!-- Title -->
			<div class="row">			
				<div class="col-md-12 col-sm-12 col-xs-12">	
					<h1 class="lightbox-title pull-left">
						<@orcid.msg 'manage_bio_settings.editCountry'/>
					</h1>
				</div>			
			</div>
			<div class="row bottomBuffer">							
				<div ng-include="'bulk-edit'"></div>					
			</div>				
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12 padding-right-reset">
					<span class="right"><@orcid.msg 'groups.common.edit_individual_privacy' /></span>	
				</div>
			</div>		
			<div class="row">
				<div class="col-md-12 col-xs-12 col-sm-12" style="position: static">
					<div class="fixed-area" scroll>				
						<div class="scroll-area">		
							<div class="row aka-row" ng-repeat="country in countryForm.addresses">
								<div class="col-md-6">									
									<div class="aka">
			                 			<select  name="country" ng-model="country.iso2Country.value" ng-disabled="country.source != orcidId" ng-class="{'not-allowed': country.source != orcidId}" focus-me="newInput">
				    			 			<option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
								 			<#list isoCountries?keys as key>
								     			<option value="${key}">${isoCountries[key]}</option>
							 	 			</#list>
							 			</select>										
									</div>									
									<div class="source" ng-if="country.sourceName || country.sourceName == null"><@orcid.msg 'manage_bio_settings.source'/>: <span ng-if="country.sourceName">{{country.sourceName}}</span><span ng-if="country.sourceName == null">{{orcidId}}</span></div>
								</div> 
								<div class="col-md-6" style="position: static">
									<ul class="record-settings pull-right">																				
										<li>									
											<div class="glyphicon glyphicon-arrow-up circle" ng-click="$first || swapUp($index)" ng-mouseover="commonSrvc.showTooltip('tooltip-country-move-up-'+$index, $event, 37, -33, 44)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-country-move-up-'+$index)"></div>
											<@orcid.tooltip elementId="'tooltip-country-move-up-'+$index" message="common.modals.move_up"/>											
										</li>
										<li>
											<div class="glyphicon glyphicon-arrow-down circle" ng-click="$last || swapDown($index)" ng-mouseover="commonSrvc.showTooltip('tooltip-country-move-down-'+$index, $event, 37, -2, 53)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-country-move-down-'+$index)"></div>
											<@orcid.tooltip elementId="'tooltip-country-move-down-'+$index" message="common.modals.move_down" />
										</li>
										<li>
											<div class="glyphicon glyphicon-trash" ng-click="deleteCountry(country)" ng-mouseover="commonSrvc.showTooltip('tooltip-country-delete-'+$index, $event, 37, 50, 39)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-country-delete-'+$index)"></div>
											<@orcid.tooltip elementId="'tooltip-country-delete-'+$index" message="common.modals.delete" />											
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
									<span class="created-date pull-right" ng-show="country.createdDate" ng-class="{'hidden-xs' : country.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{country.createdDate.year + '-' + country.createdDate.month + '-' + country.createdDate.day}}</span>
									<span class="created-date pull-left" ng-show="country.createdDate" ng-class="{'visible-xs' : country.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{country.createdDate.year + '-' + country.createdDate.month + '-' + country.createdDate.day}}</span>
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
						<a ng-click="addNewModal()"><span class="glyphicon glyphicon-plus pull-left">
							<div class="popover popover-tooltip-add top">
	    						<div class="arrow"></div>
	    						<div class="popover-content">
									<span><@orcid.msg 'common.modals.add' /></span>
	    						</div>
	   						</div>
						</span></a>	        	    		
		            	<button class="btn btn-primary pull-right" ng-click="setCountryForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		            	<a class="cancel-option pull-right" ng-click="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>	
</script>

<script type="text/ng-template" id="edit-keyword">
	<!-- Keywords -->
	<div class="lightbox-container" id="keyword-popover">
		<div class="edit-record <#if RequestParameters['bulkEdit']??>
			edit-record-bulk-edit
			</#if> edit-keyword">
			<!-- Title -->
			<div class="row">			
				<div class="col-md-12 col-sm-12 col-xs-12">	
					<h1 class="lightbox-title pull-left">
						<@orcid.msg 'manage_bio_settings.editKeywords'/>		
					</h1>					
				</div>			
			</div>
			<div class="row bottomBuffer">							
				<div ng-include="'bulk-edit'"></div>					
			</div>				
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12 padding-right-reset">
					<span class="right"><@orcid.msg 'groups.common.edit_individual_privacy' /></span>	
				</div>
			</div>		
			<div class="row">
				<div class="col-md-12 col-xs-12 col-sm-12" style="position: static">
					<div class="fixed-area" scroll>				
						<div class="scroll-area">		
							<div class="row aka-row" ng-repeat="keyword in keywordsForm.keywords">		
								<div class="col-md-6">
									<div class="aka">										
										<input type="text" ng-model="keyword.content" ng-show="keyword.source == orcidId" focus-me="newInput"></input>
										<span ng-bind="keyword.content" ng-show="keyword.source != orcidId"></span>										
									</div>
									<div class="source" ng-if="keyword.sourceName || keyword.sourceName == null"><@orcid.msg 'manage_bio_settings.source'/>: <span ng-if="keyword.sourceName">{{keyword.sourceName}}</span><span ng-if="keyword.sourceName == null">{{orcidId}}</span></div>																			
								</div>
								
								<div class="col-md-6" style="position: static">
									<ul class="record-settings pull-right">
										<li>							
											<div class="glyphicon glyphicon-arrow-up circle" ng-click="$first || swapUp($index)" ng-mouseover="commonSrvc.showTooltip('tooltip-keyword-move-up-'+$index, $event, 37, -33, 44)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-keyword-move-up-'+$index)"></div>
											<@orcid.tooltip elementId="'tooltip-keyword-move-up-'+$index" message="common.modals.move_up"/>
										</li>
										<li>																						
											<div class="glyphicon glyphicon-arrow-down circle" ng-click="$last || swapDown($index)" ng-mouseover="commonSrvc.showTooltip('tooltip-keyword-move-down-'+$index, $event, 37, -2, 53)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-keyword-move-down-'+$index)"></div>
											<@orcid.tooltip elementId="'tooltip-keyword-move-down-'+$index" message="common.modals.move_down" />											
										</li>
										<li>										
											<div class="glyphicon glyphicon-trash" ng-click="deleteKeyword(keyword)" ng-mouseover="commonSrvc.showTooltip('tooltip-keyword-delete-'+$index, $event, 37, 50, 39)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-keyword-delete-'+$index)"></div>
											<@orcid.tooltip elementId="'tooltip-keyword-delete-'+$index" message="common.modals.delete" />											
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
									<span class="created-date pull-right" ng-show="keyword.createdDate" ng-class="{'hidden-xs' : keyword.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{keyword.createdDate.year + '-' + keyword.createdDate.month + '-' + keyword.createdDate.day}}</span>
									<span class="created-date pull-left" ng-show="keyword.createdDate" ng-class="{'visible-xs' : keyword.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{keyword.createdDate.year + '-' + keyword.createdDate.month + '-' + keyword.createdDate.day}}</span>
								</div>					 				
							</div>											
						</div>
					</div>
					<div class="record-buttons">						
						<a ng-click="addNewModal()"><span class="glyphicon glyphicon-plus pull-left">
							<div class="popover popover-tooltip-add top">
	    						<div class="arrow"></div>
	    						<div class="popover-content">
									<span><@orcid.msg 'common.modals.add' /></span>
	    						</div>
	   						</div>
						</span></a>	        	    		
		            	<button class="btn btn-primary pull-right" ng-click="setKeywordsForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		            	<a class="cancel-option pull-right" ng-click="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>
</script>

<script type="text/ng-template" id="edit-websites">
	<div class="lightbox-container" id="websites-popover">
		<div class="edit-record edit-record-bulk-edit edit-websites">
			<!-- Title -->
			<div class="row">			
				<div class="col-md-12 col-sm-12 col-xs-12">	
					<h1 class="lightbox-title pull-left">
						<@orcid.msg 'manage_bio_settings.editWebsites'/>
					</h1>
				</div>			
			</div>
			<div class="row bottomBuffer">							
				<div ng-include="'bulk-edit'"></div>					
			</div>				
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12 padding-right-reset">
					<span class="right"><@orcid.msg 'groups.common.edit_individual_privacy' /></span>	
				</div>
			</div>		
			<div class="row">
				<div class="col-md-12 col-xs-12 col-sm-12" style="position: static">
					<div class="fixed-area" scroll>				
						<div class="scroll-area">		
							<div class="row aka-row websites" ng-repeat="website in websitesForm.websites">
								<div class="col-md-6">
									<div class="aka">										
										<input type="text" ng-model="website.urlName" ng-show="website.source == orcidId" focus-me="newInput" placeholder="${springMacroRequestContext.getMessage('manual_work_form_contents.labeldescription')}"></input>
										<input type="text" ng-model="website.url" ng-show="website.source == orcidId" placeholder="${springMacroRequestContext.getMessage('common.url')}"></input>
 										<a href="{{website.url}}" target="_blank" rel="me nofollow" ng-show="website.source != orcidId" ng-cloak>{{website.urlName != null? website.urlName : website.url}}</a>										
									</div>
									<div class="source" ng-if="website.sourceName || website.sourceName == null"><@orcid.msg 'manage_bio_settings.source'/>: <span ng-if="website.sourceName">{{website.sourceName}}</span><span ng-if="website.sourceName == null">{{orcidId}}</span></div>																			
								</div>
								
								<div class="col-md-6" style="position: static">
									<ul class="record-settings pull-right">
										<li>											
											<div class="glyphicon glyphicon-arrow-up circle" ng-click="swapUp($index)" ng-mouseover="commonSrvc.showTooltip('tooltip-websites-move-up-'+$index, $event, 37, -33, 44)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-websites-move-up-'+$index)"></div>
											<@orcid.tooltip elementId="'tooltip-websites-move-up-'+$index" message="common.modals.move_up"/>
										</li>
										<li>																						
											<div class="glyphicon glyphicon-arrow-down circle" ng-click="swapDown($index)" ng-mouseover="commonSrvc.showTooltip('tooltip-websites-move-down-'+$index, $event, 37, -2, 53)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-websites-move-down-'+$index)"></div>
											<@orcid.tooltip elementId="'tooltip-websites-move-down-'+$index" message="common.modals.move_down" />
										</li>
										<li>										
											<div class="glyphicon glyphicon-trash" ng-click="deleteWebsite(website)" ng-mouseover="commonSrvc.showTooltip('tooltip-websites-delete-'+$index, $event, 37, 50, 39)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-websites-delete-'+$index)"></div>
											<@orcid.tooltip elementId="'tooltip-websites-delete-'+$index" message="common.modals.delete" />
										</li>
										<li>
											<@orcid.privacyToggle3  angularModel="website.visibility.visibility"
		             	  						questionClick="toggleClickPrivacyHelp($index)"
		             	  						clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
		             	  						publicClick="setPrivacyModal('PUBLIC', $event, website)" 
                	      						limitedClick="setPrivacyModal('LIMITED', $event, website)" 
                	      						privateClick="setPrivacyModal('PRIVATE', $event, website)"
                	      						elementId="$index"
											/>
										</li>
									</ul>
									<span class="created-date pull-right" ng-show="website.createdDate" ng-class="{'hidden-xs' : website.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{website.createdDate.year + '-' + website.createdDate.month + '-' + website.createdDate.day}}</span>
									<span class="created-date pull-left" ng-show="website.createdDate" ng-class="{'visible-xs' : website.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{website.createdDate.year + '-' + website.createdDate.month + '-' + website.createdDate.day}}</span>
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
						<a ng-click="addNewModal()"><span class="glyphicon glyphicon-plus pull-left">
							<div class="popover popover-tooltip-add top">
	    						<div class="arrow"></div>
	    						<div class="popover-content">
									<span><@orcid.msg 'common.modals.add' /></span>
	    						</div>
	   						</div>
						</span></a>	        	    		
		            	<button class="btn btn-primary pull-right" ng-click="setWebsitesForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		            	<a class="cancel-option pull-right" ng-click="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>
</script>

<script type="text/ng-template" id="edit-external-identifiers">
	<div class="lightbox-container" id="extids-popover">
		<div class="edit-record <#if RequestParameters['bulkEdit']??>
			edit-record-bulk-edit
			</#if> edit-external-identifiers">
			<!-- Title -->
			<div class="row">			
				<div class="col-md-12 col-sm-12 col-xs-12">	
					<h1 class="lightbox-title pull-left">
						<@orcid.msg 'manage_bio_settings.editExternalIdentifiers'/>
					</h1>
				</div>			
			</div>
			<div class="row bottomBuffer">							
				<div ng-include="'bulk-edit'"></div>					
			</div>				
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12 padding-right-reset">
					<span class="right"><@orcid.msg 'groups.common.edit_individual_privacy' /></span>	
				</div>
			</div>		
			<div class="row">
				<div class="col-md-12 col-xs-12 col-sm-12" style="position: static">
					<div class="fixed-area" scroll>				
						<div class="scroll-area">		
							<div class="row aka-row external-identifiers" ng-repeat="externalIdentifier in externalIdentifiersForm.externalIdentifiers">
								<div class="col-md-6">
									<div class="aka">										
										<p>
											<span ng-hide="externalIdentifier.url">{{externalIdentifier.commonName}} {{externalIdentifier.reference}}</span>
			        						<span ng-show="externalIdentifier.url"><a href="{{externalIdentifier.url}}" target="_blank">{{externalIdentifier.commonName}} {{externalIdentifier.reference}}</a></span>
										</p>										
									</div>
									<div class="source"><@orcid.msg 'manage_bio_settings.source'/>: <span ng-if="externalIdentifier.sourceName">{{externalIdentifier.sourceName}}</span><span ng-if="externalIdentifier.sourceName == null">{{orcidId}}</span></div>																			
								</div>
								
								<div class="col-md-6" style="position: static">
									<ul class="record-settings pull-right">
										<li>										
											<div class="glyphicon glyphicon-arrow-up circle" ng-click="$first || swapUp($index)" ng-mouseover="commonSrvc.showTooltip('tooltip-external-identifiers-move-up-'+$index, $event, 37, -33, 44)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-external-identifiers-move-up-'+$index)"></div>
											<@orcid.tooltip elementId="'tooltip-external-identifiers-move-up-'+$index" message="common.modals.move_up"/>											
										</li>
										<li>																						
											<div class="glyphicon glyphicon-arrow-down circle" ng-click="$last || swapDown($index)" ng-mouseover="commonSrvc.showTooltip('tooltip-external-identifiers-move-down-'+$index, $event, 37, -2, 53)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-external-identifiers-move-down-'+$index)"
 ></div>
											<@orcid.tooltip elementId="'tooltip-external-identifiers-move-down-'+$index" message="common.modals.move_down" />											
										</li>
										<li>										
											<div class="glyphicon glyphicon-trash" ng-click="deleteExternalIdentifier(externalIdentifier)" ng-mouseover="commonSrvc.showTooltip('tooltip-external-identifiers-delete-'+$index, $event, 37, 50, 39)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-external-identifiers-delete-'+$index)"></div>
											<@orcid.tooltip elementId="'tooltip-external-identifiers-delete-'+$index" message="common.modals.delete" />
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
									<span class="created-date pull-right" ng-show="externalIdentifier.createdDate" ng-class="{'hidden-xs' : externalIdentifier.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{externalIdentifier.createdDate.year + '-' + externalIdentifier.createdDate.month + '-' + externalIdentifier.createdDate.day}}</span>
									<span class="created-date pull-left" ng-show="externalIdentifier.createdDate" ng-class="{'visible-xs' : externalIdentifier.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{externalIdentifier.createdDate.year + '-' + externalIdentifier.createdDate.month + '-' + externalIdentifier.createdDate.day}}</span>
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
		            	<button class="btn btn-primary pull-right" ng-click="setExternalIdentifiersForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		            	<a class="cancel-option pull-right" ng-click="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>
</script>

<script type="text/ng-template" id="bulk-edit">					
	<div class="row bulk-edit-modal">
		<div class="pull-right bio-edit-modal">				
			<span class="right"><@spring.message "groups.common.bulk_edit_privacy"/></span>
			<div class="bulk-privacy-bar">
				<@orcid.privacyToggle3  angularModel="bioModel"
		        	questionClick="toggleClickPrivacyHelp($index)"
		        	clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
		            publicClick="setBulkGroupPrivacy('PUBLIC', $event, bioModel)" 
                	limitedClick="setBulkGroupPrivacy('LIMITED', $event, bioModel)" 
                	privateClick="setBulkGroupPrivacy('PRIVATE', $event, bioModel)"
                	elementId="bulkEdit" />		

			</div>
			<div class="bulk-help popover-help-container">
            	<a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                <div id="bulk-help" class="popover bottom">
                	<div class="arrow"></div>
                    <div class="popover-content">
                    	<p><@orcid.msg "groups.common.bulk_edit_privacy_help" /></p>
                    </div>
               </div>
            </div>
		</div>			
	</div>
</script>

	
