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
<div class="more-info" ng-if="moreInfo[group.groupId] && group.activePutCode == work.putCode.value">
	<div id="ajax-loader" ng-if="worksSrvc.details[work.putCode.value] == undefined">
		<span id="ajax-loader"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x4 green"></i></span>
	</div>
	
	<div class="content" ng-hide="worksSrvc.details[work.putCode.value] == undefined">	
		
		<span class="dotted-bar"></span>
		<div class="row">		
			<div class="col-md-6" ng-if="worksSrvc.details[work.putCode.value].translatedTitle.content" ng-cloak>
				<!-- Translated title -->
				<div class="bottomBuffer">
					<strong><@orcid.msg
						'manual_work_form_contents.labeltranslatedtitle'/></strong> <span><i>({{worksSrvc.details[work.putCode.value].translatedTitle.languageName}})</i></span>
					<div>{{worksSrvc.details[work.putCode.value].translatedTitle.content}}</div>				
				</div>
			</div>
			<div class="col-md-6" ng-if="worksSrvc.details[work.putCode.value].languageCode.value" ng-cloak>
				<!-- Language -->
				<div class="bottomBuffer">					
					<strong><@orcid.msg
						'manual_work_form_contents.labellanguage'/></strong>
					<div ng-bind="worksSrvc.details[work.putCode.value].languageName.value"></div>					
				</div>
			</div>
			<div class="col-md-6" ng-if="worksSrvc.details[work.putCode.value].subtitle.value" ng-cloak>
				<div class="bottomBuffer">
					<strong> <@orcid.msg 'manual_work_form_contents.labelsubtitle'/> </strong>
					<div ng-bind="worksSrvc.details[work.putCode.value].subtitle.value"></div>
				</div>
			</div>
			
			<div class="col-md-6" ng-if="worksSrvc.details[work.putCode.value].url.value" ng-cloak>
				<!-- URL -->				
				<div class="bottomBuffer">
					<strong>
						<@orcid.msg
						'common.url'/>
					</strong>
					<div>
						<a href="{{worksSrvc.details[work.putCode.value].url.value | urlProtocol}}" target="_blank">{{worksSrvc.details[work.putCode.value].url.value}}</a>					
					</div>				
				</div>
			</div>			
		</div>
		<!-- Citation -->                  
		<div class="row bottomBuffer" ng-if="worksSrvc.details[work.putCode.value].citation.citation.value" ng-cloak>
			<div class="col-md-12">				
				<strong><@orcid.msg 'manual_work_form_contents.labelcitation'/></strong> <span> (<span ng-if="worksSrvc.details[work.putCode.value].citation.citationType.value" ng-cloak><i>{{worksSrvc.details[work.putCode.value].citation.citationType.value}}</i></span>) 
				</span>
				<span ng-if="showBibtex[work.putCode.value] && worksSrvc.details[work.putCode.value].citation.citationType.value == 'bibtex'">
					<a class="toggle-tag-option" ng-click="bibtexShowToggle(work.putCode.value)">
						[<@orcid.msg 'work.switch_view'/>]
					</a>
				</span>
				<span ng-if="(showBibtex[work.putCode.value] == null || showBibtex[work.putCode.value] == false) && worksSrvc.details[work.putCode.value].citation.citationType.value == 'bibtex'">
					<a class="toggle-tag-option" ng-click="bibtexShowToggle(work.putCode.value)">
						[<@orcid.msg 'work.switch_view'/>]
					</a>
				</span>
				
			</div>
			<div class="col-md-12">
			
				<div ng-if="worksSrvc.details[work.putCode.value].citation.citationType.value != 'bibtex'">
					<span>
						{{worksSrvc.details[work.putCode.value].citation.citation.value}}
					</span>
				</div>
				
				<div ng-if="(showBibtex[work.putCode.value] == null || showBibtex[work.putCode.value] == false) && worksSrvc.details[work.putCode.value].citation.citationType.value == 'bibtex'" 
					 ng-bind="worksSrvc.details[work.putCode.value].citation.citation.value"
					 class="col-md-offset-1 col-md-11 col-sm-offset-1 col-sm-11 col-xs-12 citation-raw">
				</div>
									
				<div class="row" ng-if="showBibtex[work.putCode.value] && (worksSrvc.bibtexJson[work.putCode.value]==null || worksSrvc.bibtexJson[work.putCode.value].length==0)">
					<div class="col-md-offset-1 col-md-6"><@orcid.msg 'work.unavailable_in_html'/></div>
				</div>
				
				
				<div class="row" ng-if="showBibtex[work.putCode.value]" ng-repeat='bibJSON in worksSrvc.bibtexJson[work.putCode.value]'>						
					<div class="col-md-offset-1 col-md-2 col-sm-offset-1 col-sm-1 col-xs-offset-1 col-xs-4">{{bibJSON.entryType}}</div>
					<div class="col-md-8 col-sm-9 col-xs-offset-1 col-xs-6">{{bibJSON.citationKey}}</div>								
					<div ng-repeat="(entKey,entVal) in bibJSON.entryTags">
						<div class="col-md-offset-1 col-md-2 col-sm-offset-1 col-sm-1 col-xs-offset-1 col-xs-4">{{entKey}}</div>
						<div class="col-md-8 col-sm-9 col-xs-offset-1 col-xs-6">{{entVal | latex}}</div>
					</div>
				</div>						
			</div>
		</div>
		<!-- Description -->
		<div class="row bottomBuffer" ng-if="worksSrvc.details[work.putCode.value].shortDescription.value"
			ng-cloak>
			<div class="col-md-12">
				<strong> <@orcid.msg
					'manual_work_form_contents.labeldescription'/> </strong>
				<div ng-bind="worksSrvc.details[work.putCode.value].shortDescription.value"
					style="white-space: pre-wrap;"></div>
			</div>
		</div>
		
		<div class="row bottomBuffer">
			<div class="col-md-6" ng-if="worksSrvc.details[work.putCode.value].countryCode.value" ng-cloak>
				<!-- Country -->				
				<div class="bottomBuffer">
					<strong><@orcid.msg
						'manual_work_form_contents.labelcountry'/></strong>
					<div ng-bind="worksSrvc.details[work.putCode.value].countryName.value"></div>
				</div>
			</div>			
			<div class="col-md-6" ng-if="worksSrvc.details[work.putCode.value].contributors.length > 0" ng-cloak>
				<!-- Contributors -->
				<div class="bottomBuffer">			
					<strong> Contributor </strong>
					<div ng-repeat="contributor in worksSrvc.details[work.putCode.value].contributors">
						{{contributor.creditName.value}} <span
							ng-bind='contributor | contributorFilter'></span>
					</div>
				</div>										
			</div>
			<div class="col-md-6">
				<div class="bottomBuffer">
					<strong><@orcid.msg 'groups.common.created'/></strong><br />
					<div ng-bind="worksSrvc.details[work.putCode.value].createdDate | ajaxFormDateToISO8601"></div>
				</div>		
			</div>
			<div class="col-md-12">
				<div class="bottomBuffer">
					<div class="badge-container-{{work.putCode.value}}"></div>
				</div>                         
			</div>		
		</div>
		
	</div>	
</div>