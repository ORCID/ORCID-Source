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
		
	
		<div class="row bottomBuffer">		
			<div class="col-md-12">
				<!-- Work Title -->
				<div class="bottomBuffer" ng-show="worksSrvc.details[group.getActive().putCode.value].workTitle.title.value" ng-cloak>
					<strong><@orcid.msg
						'manual_work_form_contents.labeltitle'/></strong>
					<div ng-bind="worksSrvc.details[group.getActive().putCode.value].workTitle.title.value"></div>
				</div>
				
				<!-- Translated title -->
				<div class="bottomBuffer" ng-show="worksSrvc.details[group.getActive().putCode.value].workTitle.translatedTitle.content" ng-cloak>
					<strong><@orcid.msg
						'manual_work_form_contents.labeltranslatedtitle'/></strong>
					<div ng-bind="renderTranslatedTitleInfo(group.getActive().putCode.value)"></div>				
				</div>
				
				<!-- Subtitle -->		
				<div class="bottomBuffer" ng-show="worksSrvc.details[group.getActive().putCode.value].workTitle.subtitle.value" ng-cloak>
					<strong> <@orcid.msg 'manual_work_form_contents.labelsubtitle'/> </strong>
					<div ng-bind="worksSrvc.details[group.getActive().putCode.value].workTitle.subtitle.value"></div>
				</div>
				
				<!-- Journal Title -->
				<div ng-show="worksSrvc.details[group.getActive().putCode.value].journalTitle.value" ng-cloak>
					<strong> <@orcid.msg 'manual_work_form_contents.journalTitle'/> </strong>
					<div ng-bind="worksSrvc.details[group.getActive().putCode.value].journalTitle.value"></div>
				</div>
				
				<!-- Work type -->		
				<div class="bottomBuffer" ng-show="worksSrvc.details[group.getActive().putCode.value].workType.value" ng-cloak>			
					<strong> <@orcid.msg
						'manual_work_form_contents.labelworktype'/> </strong>
					<div ng-bind="worksSrvc.details[group.getActive().putCode.value].workType.value"></div>			
				</div>
			</div>	
		</div>
		
		
		<!-- Citation -->
		<div class="row bottomBuffer" ng-show="worksSrvc.details[group.getActive().putCode.value].citation.citation.value"
			ng-cloak>
			<div class="col-md-12 col-sm-12 col-xs-12">
												
				<strong><@orcid.msg 'manual_work_form_contents.labelcitation'/></strong>
				<!-- Bibtex -->
				<span ng-show="showBibtex && worksSrvc.details[group.getActive().putCode.value].citation.citationType.value == 'bibtex'">
					<a ng-click="bibtexShowToggle()"><@orcid.msg 'group.getActive().show_in_bibtex'/></a>
				</span>
				<!-- Show in HTML/Bibtex -->
				<span ng-show="showBibtex == false && worksSrvc.details[group.getActive().putCode.value].citation.citationType.value == 'bibtex'">
					<a ng-click="bibtexShowToggle()"><@orcid.msg 'group.getActive().show_in_html'/></a>
				</span>				
				<div ng-hide="showBibtex && worksSrvc.details[group.getActive().putCode.value].citation.citationType.value == 'bibtex'" ng-bind="worksSrvc.details[group.getActive().putCode.value].citation.citation.value" class="col-md-offset-1 col-md-11 col-sm-offset-1 col-sm-11 col-xs-12 citation-raw"></div>
									
				<div class="row" ng-show="showBibtex && (worksSrvc.bibtexJson[group.getActive().putCode.value]==null || worksSrvc.bibtexJson[group.getActive().putCode.value].length==0)">
					<div class="col-md-offset-1 col-md-6"><@orcid.msg 'group.getActive().unavailable_in_html'/></div>				
				</div>
				
				<div class="row" ng-show="showBibtex" ng-repeat='bibJSON in worksSrvc.bibtexJson[group.getActive().putCode.value]'>						
					
					<div class="col-md-offset-1 col-md-2 col-sm-offset-1 col-sm-2 col-xs-offset-1 col-xs-5">{{bibJSON.entryType}}</div>
					<div class="col-md-8 col-sm-8 col-xs-6">{{bibJSON.citationKey}}</div>								
					
					<div ng-repeat="(entKey,entVal) in bibJSON.entryTags">
						<div class="col-md-offset-1 col-md-2 col-sm-offset-1 col-sm-2 col-xs-offset-1 col-xs-5">{{entKey}}</div>
						<div class="col-md-8 col-sm-8 col-xs-6">{{entVal}}</div>
					</div>
					
				</div>						
			</div>
		</div>
		<!-- Citation type -->
		<div class="row bottomBuffer"
			ng-show="worksSrvc.details[group.getActive().putCode.value].citation.citationType.value" ng-cloak>
			<div class="col-md-12">
				<strong> <@orcid.msg 'manual_work_form_contents.labelcitationtype'/> </strong>
				<div ng-bind="worksSrvc.details[group.getActive().putCode.value].citation.citationType.value"></div>
			</div>
		</div>
		<!-- Publication date -->
		<div class="row bottomBuffer" ng-show="worksSrvc.details[group.getActive().putCode.value].publicationDate.year"
			ng-cloak>
			<div class="col-md-12">
				<strong> <@orcid.msg
					'manual_work_form_contents.labelPubDate'/> </strong>
				<div>
					<span ng-show="worksSrvc.details[group.getActive().putCode.value].publicationDate.year">{{worksSrvc.details[group.getActive().putCode.value].publicationDate.year}}</span><span ng-show="worksSrvc.details[group.getActive().putCode.value].publicationDate.month">-{{worksSrvc.details[group.getActive().putCode.value].publicationDate.month}}</span><span ng-show="worksSrvc.details[group.getActive().putCode.value].publicationDate.day && worksSrvc.details[group.getActive().putCode.value].publicationDate.month">-{{worksSrvc.details[group.getActive().putCode.value].publicationDate.day}}</span>							
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
		
		<!-- Identifier Value -->
		<div class="row bottomBuffer"
			ng-show="worksSrvc.details[group.getActive().putCode.value].workExternalIdentifiers.length > 0" ng-cloak>
			<div class="col-md-8">
				<strong> <@orcid.msg 'manual_work_form_contents.labelID'/>
				</strong>
				<div>
					<span ng-repeat='ie in worksSrvc.details[group.getActive().putCode.value].workExternalIdentifiers'> <span
						ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:group.getActive().workExternalIdentifiers.length'></span>
					</span>
				</div>
			</div>
		</div>
		
		<!-- URL -->
		<div class="row bottomBuffer" ng-show="worksSrvc.details[group.getActive().putCode.value].url.value" ng-cloak>
			<div class="col-md-12">
				<strong> <@orcid.msg
					'manual_work_form_contents.labelURL'/> </strong>
				<div>
					<a href="{{worksSrvc.details[group.getActive().putCode.value].url.value | urlWithHttp}}" target="_blank">{{worksSrvc.details[group.getActive().putCode.value].url.value}}</a>
				</div>
			</div>
		</div>
		
		<!-- Contributors -->
		
		<div class="row bottomBuffer" ng-show="worksSrvc.details[group.getActive().putCode.value].contributors.length > 0"
			ng-cloak>
			<div class="col-md-12">
				<strong> Contributor </strong>
				<div ng-repeat="contributor in worksSrvc.details[group.getActive().putCode.value].contributors">
					{{contributor.creditName.value}} <span
						ng-bind='contributor | contributorFilter'></span>
				</div>
			</div>
		</div>
		
		<!-- Language -->
		<div class="row bottomBuffer" ng-show="worksSrvc.details[group.getActive().putCode.value].languageCode.value"
			ng-cloak>
			<div class="col-md-12">
				<strong><@orcid.msg
					'manual_work_form_contents.labellanguage'/></strong>
				<div ng-bind="worksSrvc.details[group.getActive().putCode.value].languageName.value"></div>
			</div>
		</div>
		<!-- Country of publication -->
		<div class="row bottomBuffer" ng-show="worksSrvc.details[group.getActive().putCode.value].countryCode.value" ng-cloak>
			<div class="col-md-12">
				<strong><@orcid.msg
					'manual_work_form_contents.labelcountry'/></strong>
				<div ng-bind="worksSrvc.details[group.getActive().putCode.value].countryName.value"></div>
			</div>
		</div>
		<div class="show-more-info-tab hide-tab">
			<a href="" ng-show="moreInfo[group.getActive().putCode.value]" ng-click="showDetailsMouseClick(group.getActive().putCode.value,$event);"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'manage.developer_tools.hide_details'/></a>
		</div>
	</div>	
</div>
<div class="row">
	<div class="col-md-12 col-sm-12">
		<!-- Sources -->			
		<div class="sources-container-header">			
			<div class="row">					 
				
				<div class="col-md-5" ng-hide="editSources[group.groupId] == true">
					<span>
						<strong >Source:</strong> {{group.getActive().workSourceName.value}}
					</span>						
				</div>					
				
				<div ng-class="editSources[group.groupId] == true ? 'col-md-12' : 'col-md-7">						
					<ul class="sources-options" ng-cloak>
						<li ng-hide="group.activitiesCount == 1 || editSources[group.groupId] == true">
							<span>
							 	<a ng-click="editSources[group.groupId] = !editSources[group.groupId]">View <span class="badge">{{group.activitiesCount - 1 }}</span> additional source<span ng-show="group.activitiesCount > 2">s</span></a>
							</span>
						</li>
				        <li ng-show="editSources[group.groupId] == true">
				            <a ng-click="deleteWorkConfirm(group.getActive().putCode.value, true)">
				                <span class="glyphicon glyphicon-trash"></span> Delete all
				            </a>
				        </li>
				        <li ng-show="editSources[group.groupId] == true">
				            <a ng-click="editSources[group.groupId] = false">
				                <span class="glyphicon glyphicon-remove"></span> Hide aditional sources
				            </a>
				        </li>
				        <li>
					        <div class="show-more-info-tab">			
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
				<table class="sources-edit-table" ng-show="editSources[group.groupId] == true" ng-cloak>
				    
				    <tr ng-repeat="work in group.activities" ng-show="moreInfo[work.putCode.value] == moreInfo[group.activePutCode] && group.activePutCode == work.putCode.value" class="no-border-top">				    	
				       <td>
				       		<span
				       		    ng-show="work.putCode.value == group.activePutCode"
				           		ng-click="moreInfo[work.putCode.value] = moreInfo[group.activePutCode]; group.activePutCode = work.putCode.value ">
				           			<strong >Source:</strong> {{work.workSourceName.value}}
				           		</span>
				       </td>
				       <td>Last modified: 12/31/2013</td>
				       <td>
				           <span class="glyphicon glyphicon-check" ng-show="work.putCode.value == group.defaultPutCode"></span> <!-- <span ng-show="work.putCode.value == group.defaultPutCode">Default</span> --> 
				           <a ng-click="worksSrvc.makeDefault(group, work.putCode.value); group.activePutCode = work.putCode.value" ng-show="work.putCode.value != group.defaultPutCode">
			            	 <span class="glyphicon glyphicon-unchecked"></span> Keep on top
			               </a>
			           </td>
				       <td>
				           <a ng-click="deleteWorkConfirm(group.getActive().putCode.value, false)">
			            	   <span class="glyphicon glyphicon-trash"></span>
			               </a>
				       </td>
				    </tr>
				    <!-- No default values -->
				    <tr ng-repeat="work in group.activities" ng-hide="moreInfo[work.putCode.value] == moreInfo[group.activePutCode] && group.activePutCode == work.putCode.value">				    	
				       <td><!-- Source name -->				       		
				           	<a ng-hide="work.putCode.value == group.activePutCode"
				           		ng-click="moreInfo[work.putCode.value] = moreInfo[group.activePutCode]; group.activePutCode = work.putCode.value">
				           			{{work.workSourceName.value}}
				           		</a> 
				       </td>
				       <td><!-- Date -->
				       		05/02/2010
				       	</td>
				       <td> <!-- Make Default -->
				           <span class="glyphicon glyphicon-check" ng-show="work.putCode.value == group.defaultPutCode"></span><!-- <span ng-show="work.putCode.value == group.defaultPutCode">Default</span> --> 
				           <a ng-click="worksSrvc.makeDefault(group, work.putCode.value); group.activePutCode = work.putCode.value" ng-show="work.putCode.value != group.defaultPutCode">
			            	 <span class="glyphicon glyphicon-unchecked"></span> Keep on top
			               </a>
			           </td>
				       <td><!-- Delete -->
				           <a ng-click="deleteWorkConfirm(group.getActive().putCode.value, false)">
			            	   <span class="glyphicon glyphicon-trash"></span>
			               </a>
				       </td>
				    </tr>
				    
				    
				</table>						
			</div>
		</div>
	</div>
</div>