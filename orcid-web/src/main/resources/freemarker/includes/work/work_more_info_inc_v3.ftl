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
<div class="more-info" ng-show="moreInfo[group.getActive().putCode.value]">
	<div id="ajax-loader" ng-show="worksSrvc.details[group.getActive().putCode.value] == undefined">
		<span id="ajax-loader"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x4 green"></i></span>
	</div>
	
	<div class="content" ng-hide="worksSrvc.details[group.getActive().putCode.value] == undefined">	
		
		<span class="dotted-bar"></span>
		<div class="row">		
			<div class="col-md-6" ng-show="worksSrvc.details[group.getActive().putCode.value].workTitle.translatedTitle.content" ng-cloak>
				<!-- Translated title -->
				<div class="bottomBuffer">
					<strong><@orcid.msg
						'manual_work_form_contents.labeltranslatedtitle'/></strong> <span><i>({{worksSrvc.details[group.getActive().putCode.value].workTitle.translatedTitle.languageName}})</i></span>
					<div>{{worksSrvc.details[group.getActive().putCode.value].workTitle.translatedTitle.content}}</div>				
				</div>
			</div>
			<div class="col-md-6" ng-show="worksSrvc.details[group.getActive().putCode.value].languageCode.value" ng-cloak>
				<!-- Language -->
				<div class="bottomBuffer">					
					<strong><@orcid.msg
						'manual_work_form_contents.labellanguage'/></strong>
					<div ng-bind="worksSrvc.details[group.getActive().putCode.value].languageName.value"></div>					
				</div>
			</div>
			<div class="col-md-6" ng-show="worksSrvc.details[group.getActive().putCode.value].journalTitle.value" ng-cloak>
				<!-- Journal Title -->
				<div class="bottomBuffer">
					<strong> <@orcid.msg 'manual_work_form_contents.journalTitle'/> </strong>
					<div ng-bind="worksSrvc.details[group.getActive().putCode.value].journalTitle.value"></div>
				</div>
			</div>
			<div class="col-md-6" ng-show="worksSrvc.details[group.getActive().putCode.value].url.value" ng-cloak>
				<!-- URL -->				
				<div class="bottomBuffer">
					<strong>
						<@orcid.msg
						'manual_work_form_contents.labelURL'/>
					</strong>
					<div>
						<a href="{{worksSrvc.details[group.getActive().putCode.value].url.value | urlWithHttp}}" target="_blank">{{worksSrvc.details[group.getActive().putCode.value].url.value}}</a>					
					</div>				
				</div>
			</div>			
		</div>
		<!-- Citation -->                  
		<div class="row bottomBuffer" ng-show="worksSrvc.details[group.getActive().putCode.value].citation.citation.value" ng-cloak>
			<div class="col-md-12">				
				<strong><@orcid.msg 'manual_work_form_contents.labelcitation'/></strong <span> (<span ng-show="worksSrvc.details[group.getActive().putCode.value].citation.citationType.value" ng-cloak><i>{{worksSrvc.details[group.getActive().putCode.value].citation.citationType.value}}</i></span>) 
				</span>
			</div>
			<div class="col-md-12">
				<span ng-show="showBibtex[group.getActive().putCode.value] && worksSrvc.details[group.getActive().putCode.value].citation.citationType.value == 'bibtex'">
					<a class="toggle-tag-option" ng-click="bibtexShowToggle(group.getActive().putCode.value)">
						[<@orcid.msg 'work.show_in_bibtex'/>]
					</a>
				</span>
				
				<span ng-show="(showBibtex[group.getActive().putCode.value] == null || showBibtex[group.getActive().putCode.value] == false) && worksSrvc.details[group.getActive().putCode.value].citation.citationType.value == 'bibtex'">
					<a class="toggle-tag-option" ng-click="bibtexShowToggle(group.getActive().putCode.value)">
						[<@orcid.msg 'work.show_in_html'/>]
					</a>
				</span>
				
				<div ng-show="(showBibtex[group.getActive().putCode.value] == null || showBibtex[group.getActive().putCode.value] == false) && worksSrvc.details[group.getActive().putCode.value].citation.citationType.value == 'bibtex'" 
					 ng-bind="worksSrvc.details[group.getActive().putCode.value].citation.citation.value"
					 class="col-md-offset-1 col-md-11 col-sm-offset-1 col-sm-11 col-xs-12 citation-raw">
				</div>
									
				<div class="row" ng-show="showBibtex[group.getActive().putCode.value] && (worksSrvc.bibtexJson[group.getActive().putCode.value]==null || worksSrvc.bibtexJson[group.getActive().putCode.value].length==0)">
					<div class="col-md-offset-1 col-md-6"><@orcid.msg 'work.unavailable_in_html'/></div>
				</div>
				
				<div class="row" ng-show="showBibtex[group.getActive().putCode.value]" ng-repeat='bibJSON in worksSrvc.bibtexJson[group.getActive().putCode.value]'>						
					<div class="col-md-offset-1 col-md-2 col-sm-offset-1 col-sm-1 col-xs-offset-1 col-xs-11">{{bibJSON.entryType}}</div>
					<div class="col-md-8 col-sm-9 col-xs-offset-1 col-xs-11">{{bibJSON.citationKey}}</div>								
					<div ng-repeat="(entKey,entVal) in bibJSON.entryTags">
						<div class="col-md-offset-1 col-md-2 col-sm-offset-1 col-sm-1 col-xs-offset-1 col-xs-11">{{entKey}}</div>
						<div class="col-md-8 col-sm-9 col-xs-offset-1 col-xs-11">{{entVal}}</div>
					</div>
				</div>						
			</div>
		</div>
		<!-- Description -->
		<div class="row bottomBuffer" ng-show="worksSrvc.details[group.getActive().putCode.value].shortDescription.value"
			ng-cloak>
			<div class="col-md-12">
				<strong> <@orcid.msg
					'manual_work_form_contents.labeldescription'/> </strong>
				<div ng-bind="worksSrvc.details[group.getActive().putCode.value].shortDescription.value"
					style="white-space: pre-wrap;"></div>
			</div>
		</div>
		
		<div class="row bottomBuffer">
			<div class="col-md-6" ng-show="worksSrvc.details[group.getActive().putCode.value].countryCode.value" ng-cloak>
				<!-- Country -->				
				<div class="bottomBuffer">
					<strong><@orcid.msg
						'manual_work_form_contents.labelcountry'/></strong>
					<div ng-bind="worksSrvc.details[group.getActive().putCode.value].countryName.value"></div>
				</div>
			</div>			
			<div class="col-md-6" ng-show="worksSrvc.details[group.getActive().putCode.value].contributors.length > 0" ng-cloak>
				<!-- Contributors -->
				<div class="bottomBuffer">			
					<strong> Contributor </strong>
					<div ng-repeat="contributor in worksSrvc.details[group.getActive().putCode.value].contributors">
						{{contributor.creditName.value}} <span
							ng-bind='contributor | contributorFilter'></span>
					</div>
				</div>										
			</div>
		</div>		
	</div>	
</div>
<div class="row sources-details">
	<div class="col-md-12 col-sm-12">
		<!-- Sources -->			
		<div class="sources-container-header">			
			<div class="row">					 
				
				<div class="col-md-5" ng-hide="editSources[group.groupId] == true">
					<span>
						<strong >Source:</strong> {{group.getActive().workSourceName.value}}
					</span>						
				</div>
				
				<div ng-class="editSources[group.groupId] == true ? 'col-md-12' : 'col-md-7'">						
					<ul class="sources-options" ng-cloak>
						<li ng-hide="group.activitiesCount == 1 || editSources[group.groupId] == true">							
							<span class="view-sources-details">
							 	<a ng-click="editSources[group.groupId] = !editSources[group.groupId]">View <span class="badge">{{group.activitiesCount - 1 }}</span> additional source<span ng-show="group.activitiesCount > 2">s</span></a>							 	
							</span>
							<a ng-click="editSources[group.groupId] = !editSources[group.groupId]">
			            	   <span class="glyphicon glyphicon-trash"></span>
			        		</a>
						</li>
						<li ng-show="group.activitiesCount == 1">
							<a ng-click="deleteWorkConfirm(group.getActive().putCode.value, false)">
			            	   <span class="glyphicon glyphicon-trash"></span>
			               </a>
						</li>
				        <li ng-show="editSources[group.groupId] == true">
				            <a ng-click="deleteWorkConfirm(group.getActive().putCode.value, true)">
				                <span class="glyphicon glyphicon-trash"></span> Delete all
				            </a>
				        </li>
				        <li ng-show="editSources[group.groupId] == true">
				            <a ng-click="editSources[group.groupId] = false">
				                <span class="glyphicon glyphicon-remove"></span> Hide additional sources
				            </a>
				        </li>
				        <li>
					        <div class="show-more-info-tab work-tab">			
								<a href="" ng-show="!moreInfo[group.getActive().putCode.value]" ng-click="showDetailsMouseClick(group.getActive().putCode.value,$event);"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'manage.developer_tools.show_details'/></a>									
								<a href="" ng-show="moreInfo[group.getActive().putCode.value]" ng-click="showDetailsMouseClick(group.getActive().putCode.value,$event);"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'manage.developer_tools.hide_details'/></a>
							</div>							
				        </li>                               
				    </ul>
				</div>
			</div>
		</div>
	</div>
	<div class="col-md-12 col-sm-12">
		<div class="sources-container">
			<div class="sources-edit">	
				<ul class="sources-edit-list" ng-show="editSources[group.groupId] == true" ng-cloak>
					<li class="first-source">
						<div class="col-sm-4">
							<span>
					        	<strong >Source:</strong> {{group.getActive().workSourceName.value}}
					        </span>
				        </div>
				        <div class="col-sm-4">
				        	Last modified: {{group.getActive().lastModified | ajaxFormDateToISO8601}}
				        </div>
				        <div class="col-sm-3">
				        	   <span class="glyphicon glyphicon-check" ng-show="group.getActive().putCode.value == group.defaultPutCode"></span> 
					           <a ng-click="worksSrvc.makeDefault(group, group.getActive().putCode.value); group.activePutCode = group.getActive().putCode.value" ng-show="group.getActive().putCode.value != group.defaultPutCode">
				            	 <span class="glyphicon glyphicon-unchecked"></span> Make Preferred
				               </a>
				        </div>
				        <div class="col-sm-1">
				        		<a ng-click="deleteWorkConfirm(group.getActive().putCode.value, false)">
			            	   <span class="glyphicon glyphicon-trash"></span>
			               </a>
				        </div>
					</li>
					<li ng-repeat="work in group.activities" ng-hide="group.activePutCode == work.putCode.value">
						<div class="col-sm-4">
							<a ng-click="worksSrvc.showSpinner($event); moreInfo[work.putCode.value] = moreInfo[group.activePutCode]; group.activePutCode = work.putCode.value">
				           		{{work.workSourceName.value}}
				           	</a> 
						</div>
						<div class="col-sm-4">
							{{work.lastModified | ajaxFormDateToISO8601}}
						</div>
						<div class="col-sm-3">
							<span class="glyphicon glyphicon-check" ng-show="work.putCode.value == group.defaultPutCode"></span> 
				           <a ng-click="worksSrvc.makeDefault(group, work.putCode.value); " ng-show="work.putCode.value != group.defaultPutCode">
			            	 <span class="glyphicon glyphicon-unchecked"></span> Make Preferred
			               </a>
						</div>
						<div class="col-sm-1">
							<a ng-click="deleteWorkConfirm(group.getActive().putCode.value, false)">
			            	   <span class="glyphicon glyphicon-trash"></span>
			               </a>
						</div>
					</li>
				</ul>				
			</div>
		</div>
	</div>
</div>