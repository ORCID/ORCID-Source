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
<div class="more-info" ng-show="moreInfo[work.putCode.value]">
	<div id="ajax-loader" ng-show="loadingInfo">
		<span id="ajax-loader"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x4 green"></i></span>
	</div>
	
	<div class="content" ng-hide="loadingInfo">	
		
		<!-- Title -->			
		<div class="row bottomBuffer">		
			<!-- Left column -->	
			<div class="col-md-9">
				<!-- Work Title -->
				<div class="bottomBuffer" ng-show="worksSrvc.worksInfo[work.putCode.value].workTitle.title.value" ng-cloak>
					<strong><@orcid.msg
						'manual_work_form_contents.labeltitle'/></strong>
					<div ng-bind="worksSrvc.worksInfo[work.putCode.value].workTitle.title.value"></div>
				</div>
				
				<!-- Translated title -->
				<div class="bottomBuffer" ng-show="worksSrvc.worksInfo[work.putCode.value].workTitle.translatedTitle.content" ng-cloak>
					<strong><@orcid.msg
						'manual_work_form_contents.labeltranslatedtitle'/></strong>
					<div ng-bind="renderTranslatedTitleInfo(work.putCode.value)"></div>				
				</div>
				
				<!-- Subtitle -->		
				<div class="bottomBuffer" ng-show="worksSrvc.worksInfo[work.putCode.value].workTitle.subtitle.value" ng-cloak>
					<strong> <@orcid.msg 'manual_work_form_contents.labelsubtitle'/> </strong>
					<div ng-bind="worksSrvc.worksInfo[work.putCode.value].workTitle.subtitle.value"></div>
				</div>
				
				<!-- Journal Title -->
				<div ng-show="worksSrvc.worksInfo[work.putCode.value].journalTitle.value" ng-cloak>
					<strong> <@orcid.msg 'manual_work_form_contents.journalTitle'/> </strong>
					<div ng-bind="worksSrvc.worksInfo[work.putCode.value].journalTitle.value"></div>
				</div>
				
				<!-- Work type -->		
				<div class="bottomBuffer" ng-show="worksSrvc.worksInfo[work.putCode.value].workType.value" ng-cloak>			
					<strong> <@orcid.msg
						'manual_work_form_contents.labelworktype'/> </strong>
					<div ng-bind="worksSrvc.worksInfo[work.putCode.value].workType.value"></div>			
				</div>
			</div>
			<!-- Right column -->
			<div class="col-md-3">
				<!-- Validations -->
				<div class="validations bottomBuffer">
					<strong>Validations</strong>
					<ul>
						<li><a href="">Validation #1</a></li>
						<li><a href="">Validation #2</a></li>
					</ul>
				</div>
				<!-- Versions -->
				<div class="versions bottomBuffer">
					<strong>Versions</strong>
					<ul>
						<li class="current-version"><span class="glyphicon glyphicon-chevron-right"></span><a href="">My Version</a><span class="glyphicon glyphicon-globe privacy"></span></li>
						<li><span class="glyphicon glyphicon-chevron-right"></span><a href="">ResearcherID</a></li>
						<li><span class="glyphicon glyphicon-chevron-right"></span><a href="">DataCite</a></li>
						<li><span class="glyphicon glyphicon-chevron-right"></span><a href="">Harvard University</a></li>
					</ul>
				</div>
				<!-- Work Source -->
				<div class="work-source bottombuffer" ng-show="worksSrvc.worksInfo[work.putCode.value].workSourceName.value" ng-cloak>
					<strong> <@orcid.msg
					'manual_work_form_contents.labelWorkSource'/></strong>
					<div ng-bind="worksSrvc.worksInfo[work.putCode.value].workSourceName.value"></div>
				</div>
			</div>			
		</div>
		
		<span class="dotted-bar"></span>
		<!-- Citation -->
		<div class="row bottomBuffer" ng-show="worksSrvc.worksInfo[work.putCode.value].citation.citation.value"
			ng-cloak>
			<div class="col-md-12 col-sm-12 col-xs-12">
												
				<strong><@orcid.msg 'manual_work_form_contents.labelcitation'/></strong>
				<!-- Bibtex -->
				<span ng-show="showBibtex && worksSrvc.worksInfo[work.putCode.value].citation.citationType.value == 'bibtex'">
					<a ng-click="bibtexShowToggle()"><@orcid.msg 'work.show_in_bibtex'/></a>
				</span>
				<!-- Show in HTML/Bibtex -->
				<span ng-show="showBibtex == false && worksSrvc.worksInfo[work.putCode.value].citation.citationType.value == 'bibtex'">
					<a ng-click="bibtexShowToggle()"><@orcid.msg 'work.show_in_html'/></a>
				</span>				
				<div ng-hide="showBibtex && worksSrvc.worksInfo[work.putCode.value].citation.citationType.value == 'bibtex'" ng-bind="worksSrvc.worksInfo[work.putCode.value].citation.citation.value" class="col-md-offset-1 col-md-11 col-sm-offset-1 col-sm-11 col-xs-12 citation-raw"></div>
									
				<div class="row" ng-show="showBibtex && (worksSrvc.bibtexJson[work.putCode.value]==null || worksSrvc.bibtexJson[work.putCode.value].length==0)">
					<div class="col-md-offset-1 col-md-6"><@orcid.msg 'work.unavailable_in_html'/></div>				
				</div>
				
				<div class="row" ng-show="showBibtex" ng-repeat='bibJSON in worksSrvc.bibtexJson[work.putCode.value]'>						
					
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
			ng-show="worksSrvc.worksInfo[work.putCode.value].citation.citationType.value" ng-cloak>
			<div class="col-md-12">
				<strong> <@orcid.msg 'manual_work_form_contents.labelcitationtype'/> </strong>
				<div ng-bind="worksSrvc.worksInfo[work.putCode.value].citation.citationType.value"></div>
			</div>
		</div>
		<!-- Publication date -->
		<div class="row bottomBuffer" ng-show="worksSrvc.worksInfo[work.putCode.value].publicationDate.year"
			ng-cloak>
			<div class="col-md-12">
				<strong> <@orcid.msg
					'manual_work_form_contents.labelPubDate'/> </strong>
				<div>
					<span ng-show="worksSrvc.worksInfo[work.putCode.value].publicationDate.year">{{worksSrvc.worksInfo[work.putCode.value].publicationDate.year}}</span><span ng-show="worksSrvc.worksInfo[work.putCode.value].publicationDate.month">-{{worksSrvc.worksInfo[work.putCode.value].publicationDate.month}}</span><span ng-show="worksSrvc.worksInfo[work.putCode.value].publicationDate.day && worksSrvc.worksInfo[work.putCode.value].publicationDate.month">-{{worksSrvc.worksInfo[work.putCode.value].publicationDate.day}}</span>							
				</div>
			</div>
		</div>
		<!-- Description -->
		<div class="row bottomBuffer" ng-show="worksSrvc.worksInfo[work.putCode.value].shortDescription.value"
			ng-cloak>
			<div class="col-md-12">
				<strong> <@orcid.msg
					'manual_work_form_contents.labeldescription'/> </strong>
				<div ng-bind="worksSrvc.worksInfo[work.putCode.value].shortDescription.value"
					style="white-space: pre-wrap;"></div>
			</div>
		</div>
		
		<!-- Identifier Value -->
		<div class="row bottomBuffer"
			ng-show="worksSrvc.worksInfo[work.putCode.value].workExternalIdentifiers.length > 0" ng-cloak>
			<div class="col-md-8">
				<strong> <@orcid.msg 'manual_work_form_contents.labelID'/>
				</strong>
				<div>
					<span ng-repeat='ie in worksSrvc.worksInfo[work.putCode.value].workExternalIdentifiers'> <span
						ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length'></span>
					</span>
				</div>
			</div>
		</div>
		
		<!-- URL -->
		<div class="row bottomBuffer" ng-show="worksSrvc.worksInfo[work.putCode.value].url.value" ng-cloak>
			<div class="col-md-12">
				<strong> <@orcid.msg
					'manual_work_form_contents.labelURL'/> </strong>
				<div>
					<a href="{{worksSrvc.worksInfo[work.putCode.value].url.value | urlWithHttp}}" target="_blank">{{worksSrvc.worksInfo[work.putCode.value].url.value}}</a>
				</div>
			</div>
		</div>
		
		<!-- Contributors -->
		<span class="dotted-bar"></span>
		<div class="row bottomBuffer" ng-show="worksSrvc.worksInfo[work.putCode.value].contributors.length > 0"
			ng-cloak>
			<div class="col-md-12">
				<strong> Contributor </strong>
				<div ng-repeat="contributor in worksSrvc.worksInfo[work.putCode.value].contributors">
					{{contributor.creditName.value}} <span
						ng-bind='contributor | contributorFilter'></span>
				</div>
			</div>
		</div>
		
		<!-- Language -->
		<div class="row bottomBuffer" ng-show="worksSrvc.worksInfo[work.putCode.value].languageCode.value"
			ng-cloak>
			<div class="col-md-12">
				<strong><@orcid.msg
					'manual_work_form_contents.labellanguage'/></strong>
				<div ng-bind="worksSrvc.worksInfo[work.putCode.value].languageName.value"></div>
			</div>
		</div>
		<!-- Country of publication -->
		<div class="row bottomBuffer" ng-show="worksSrvc.worksInfo[work.putCode.value].countryCode.value" ng-cloak>
			<div class="col-md-12">
				<strong><@orcid.msg
					'manual_work_form_contents.labelcountry'/></strong>
				<div ng-bind="worksSrvc.worksInfo[work.putCode.value].countryName.value"></div>
			</div>
		</div>
	</div>
</div>