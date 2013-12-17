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
<div class="more-info ie7-zi-fix-top" ng-mouseleave="closePopover(); $event.stopPropagation()">	
	<a class="glyphicon glyphicon-plus-sign grey" ng-mouseenter="moreInfoMouseEnter(work,$event);" ng-click="moreInfoClick(work,$event);"></a>	
	<div class="popover bottom work-more-info-container" >		
		<div class="arrow"></div>	
		<div class="lightbox-container">			
			<div class="ie7fix">		
			
			<div id="ajax-loader" ng-show="loadingInfo">
				<span id="ajax-loader"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x4 green"></i></span>
			</div>
			
			<div id="content" ng-hide="loadingInfo">			
				<div class="row bottomBuffer" ng-show="worksInfo[work.putCode.value].workTitle.title.value"
					ng-cloak>
					<div class="col-md-12">
						<strong><@orcid.msg
							'manual_work_form_contents.labeltitle'/></strong>
						<div ng-bind="worksInfo[work.putCode.value].workTitle.title.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer"
					ng-show="worksInfo[work.putCode.value].workTitle.translatedTitle.content" ng-cloak>
					<div class="col-md-12">
						<strong><@orcid.msg
							'manual_work_form_contents.labeltranslatedtitle'/></strong>
						<div ng-bind="renderTranslatedTitleInfo(work.putCode.value)"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksInfo[work.putCode.value].workTitle.subtitle.value"
					ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'manual_work_form_contents.labelsubtitle'/> </strong>
						<div ng-bind="worksInfo[work.putCode.value].workTitle.subtitle.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksInfo[work.putCode.value].journalTitle.value"
					ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'manual_work_form_contents.journalTitle'/> </strong>
						<div ng-bind="worksInfo[work.putCode.value].journalTitle.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksInfo[work.putCode.value].workType.value" ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'manual_work_form_contents.labelworktype'/> </strong>
						<div ng-bind="worksInfo[work.putCode.value].workType.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksInfo[work.putCode.value].citation.citation.value"
					ng-cloak>
					<div class="col-md-12">						
						<strong><@orcid.msg 'manual_work_form_contents.labelcitation'/></strong>
						<span ng-show="showBibtex && worksInfo[work.putCode.value].citation.citationType.value == 'bibtex'">
							<a ng-click="bibtexShowToggle()">Show in Bibtex</a>
						</span>
						<span ng-show="showBibtex == false && worksInfo[work.putCode.value].citation.citationType.value == 'bibtex'">
							<a ng-click="bibtexShowToggle()">Show in HTML</a>
						</span>
						<div ng-hide="showBibtex && worksInfo[work.putCode.value].citation.citationType.value == 'bibtex'" ng-bind="worksInfo[work.putCode.value].citation.citation.value" class="col-md-offset-1 col-md-11 col-sm-offset-1 col-sm-11 col-xs-12 citation-raw"></div>					
						<div class="row" ng-show="showBibtex" ng-repeat='bibJSON in bibtexCitations[work.putCode.value]'>
							<div class="col-md-offset-1 col-md-2 col-sm-offset-1 col-sm-1 col-xs-offset-1 col-xs-11">{{bibJSON.entryType}}</div>
							<div class="col-md-8 col-sm-9 col-xs-offset-1 col-xs-11">{{bibJSON.citationKey}}</div>
													
							<div ng-repeat="(entKey,entVal) in bibJSON.entryTags">
								<div class="col-md-offset-1 col-md-2 col-sm-offset-1 col-sm-1 col-xs-offset-1 col-xs-11">{{entKey}}</div>
								<div class="col-md-8 col-sm-9 col-xs-offset-1 col-xs-11">{{entVal}}</div>
							</div>
						</div>						
					</div>
				</div>
				
				<div class="row bottomBuffer"
					ng-show="worksInfo[work.putCode.value].citation.citationType.value" ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg 'manual_work_form_contents.labelcitationtype'/> </strong>
						<div ng-bind="worksInfo[work.putCode.value].citation.citationType.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksInfo[work.putCode.value].publicationDate.year"
					ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'manual_work_form_contents.labelPubDate'/> </strong>
						<div>
							<span
								ng-show="worksInfo[work.putCode.value].publicationDate.day && worksInfo[work.putCode.value].publicationDate.month">{{worksInfo[work.putCode.value].publicationDate.day}}-</span><span
								ng-show="worksInfo[work.putCode.value].publicationDate.month">{{worksInfo[work.putCode.value].publicationDate.month}}-</span><span
								ng-show="worksInfo[work.putCode.value].publicationDate.year">{{worksInfo[work.putCode.value].publicationDate.year}}</span>
						</div>
					</div>
				</div>
				
				<div class="row bottomBuffer" ng-show="worksInfo[work.putCode.value].shortDescription.value"
					ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'manual_work_form_contents.labeldescription'/> </strong>
						<div ng-bind="worksInfo[work.putCode.value].shortDescription.value"
							style="white-space: pre-wrap;"></div>
					</div>
				</div>
				<div class="row bottomBuffer"
					ng-show="worksInfo[work.putCode.value].workExternalIdentifiers.length > 0" ng-cloak>
					<div class="col-md-8">
						<strong> <@orcid.msg 'manual_work_form_contents.labelID'/>
						</strong>
						<div>
							<span ng-repeat='ie in worksInfo[work.putCode.value].workExternalIdentifiers'> <span
								ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length'></span>
							</span>
						</div>
					</div>
				</div>
				
				<div class="row bottomBuffer" ng-show="worksInfo[work.putCode.value].url.value" ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'manual_work_form_contents.labelURL'/> </strong>
						<div>
							<a href="{{worksInfo[work.putCode.value].url.value | urlWithHttp}}" target="_blank">{{worksInfo[work.putCode.value].url.value}}</a>
						</div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksInfo[work.putCode.value].contributors.length > 0"
					ng-cloak>
					<div class="col-md-12">
						<strong> Contributor </strong>
						<div ng-repeat="contributor in worksInfo[work.putCode.value].contributors">
							{{contributor.creditName.value}} <span
								ng-bind='contributor | contributorFilter'></span>
						</div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksInfo[work.putCode.value].languageCode.value"
					ng-cloak>
					<div class="col-md-12">
						<strong><@orcid.msg
							'manual_work_form_contents.labellanguage'/></strong>
						<div ng-bind="worksInfo[work.putCode.value].languageName.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksInfo[work.putCode.value].countryCode.value" ng-cloak>
					<div class="col-md-12">
						<strong><@orcid.msg
							'manual_work_form_contents.labelcountry'/></strong>
						<div ng-bind="worksInfo[work.putCode.value].countryName.value"></div>
					</div>
				</div>						
				<div class="row bottomBuffer" ng-show="worksInfo[work.putCode.value].workSourceName.value" ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'manual_work_form_contents.labelWorkSource'/> </strong>
						<div ng-bind="worksInfo[work.putCode.value].workSourceName.value"></div>
					</div>
				</div>
			</div>
			</div>
		</div><!-- .lightbox-container -->
	</div>
</div>