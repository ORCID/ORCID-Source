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
<div class="more-info ie7-zi-fix-top" ng-mouseleave="closePopover(); $event.stopPropagation()">	
	<a class="glyphicon glyphicon-plus-sign grey" ng-mouseenter="moreInfoMouseEnter(work,$event);" ng-click="moreInfoClick(work,$event);"></a>	
	<div class="popover bottom work-more-info-container" >		
		<div class="arrow"></div>	
		<div class="lightbox-container">			
			<div class="ie7fix">		
			
			<div id="ajax-loader" ng-show="worksSrvc.details[group.getActive().putCode.value] == undefined">
				<span id="ajax-loader"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x4 green"></i></span>
			</div>
			
			<div id="content" ng-hide="worksSrvc.details[group.getActive().putCode.value] == undefined">			
				<div class="row bottomBuffer" ng-show="worksSrvc.details[work.putCode.value].title.value"
					ng-cloak>
					<div class="col-md-12">
						<strong><@orcid.msg
							'manual_work_form_contents.labeltitle'/></strong>
						<div ng-bind="worksSrvc.details[work.putCode.value].title.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer"
					ng-show="worksSrvc.details[work.putCode.value].translatedTitle.content" ng-cloak>
					<div class="col-md-12">
						<strong><@orcid.msg
							'manual_work_form_contents.labeltranslatedtitle'/></strong>
						<div ng-bind="renderTranslatedTitleInfo(work.putCode.value)"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksSrvc.details[work.putCode.value].subtitle.value"
					ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'manual_work_form_contents.labelsubtitle'/> </strong>
						<div ng-bind="worksSrvc.details[work.putCode.value].subtitle.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksSrvc.details[work.putCode.value].journalTitle.value"
					ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'manual_work_form_contents.journalTitle'/> </strong>
						<div ng-bind="worksSrvc.details[work.putCode.value].journalTitle.value"></div>		
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksSrvc.details[work.putCode.value].workType.value" ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'manual_work_form_contents.labelworktype'/> </strong>
						<div ng-bind="worksSrvc.details[work.putCode.value].workType.value"></div>
					</div>
				</div>				
				
				<div class="row bottomBuffer" ng-show="worksSrvc.details[work.putCode.value].citation.citation.value" ng-cloak>					
					
					
					<div class="col-md-12">					
						<strong><@orcid.msg 'manual_work_form_contents.labelcitation'/></strong>
						
						<div ng-show="worksSrvc.details[work.putCode.value].citation.citationType.value != 'bibtex'">
							<span>
								{{worksSrvc.details[work.putCode.value].citation.citation.value}}
							</span>
						</div>	
						
						<span ng-show="showBibtex[work.putCode.value] && worksSrvc.details[work.putCode.value].citation.citationType.value == 'bibtex'">
							<a class="toggle-tag-option" ng-click="bibtexShowToggle(work.putCode.value)">
								[<@orcid.msg 'work.show_in_bibtex'/>]
							</a>
						</span>
						<span ng-show="(showBibtex[work.putCode.value] == null || showBibtex[work.putCode.value] == false) && worksSrvc.details[work.putCode.value].citation.citationType.value == 'bibtex'">
							<a class="toggle-tag-option" ng-click="bibtexShowToggle(work.putCode.value)">
								[<@orcid.msg 'work.show_in_html'/>]
							</a>
						</span>
						<div ng-show="(showBibtex[work.putCode.value] == null || showBibtex[work.putCode.value] == false) && worksSrvc.details[work.putCode.value].citation.citationType.value == 'bibtex'" 
							 ng-bind="worksSrvc.details[work.putCode.value].citation.citation.value"
							 class="col-md-offset-1 col-md-11 col-sm-offset-1 col-sm-11 col-xs-12 citation-raw">
						</div>			
						<div class="row" ng-show="showBibtex[work.putCode.value] && (worksSrvc.bibtexJson[work.putCode.value]==null || worksSrvc.bibtexJson[work.putCode.value].length==0)">
							<div class="col-md-offset-1 col-md-6"><@orcid.msg 'work.unavailable_in_html'/></div>
						</div>
						<div class="row" ng-show="showBibtex[work.putCode.value]" ng-repeat='bibJSON in worksSrvc.bibtexJson[work.putCode.value]'>						
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
					ng-show="worksSrvc.details[work.putCode.value].citation.citationType.value" ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg 'manual_work_form_contents.labelcitationtype'/> </strong>
						<div ng-bind="worksSrvc.details[work.putCode.value].citation.citationType.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksSrvc.details[work.putCode.value].publicationDate.year"
					ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'manual_work_form_contents.labelPubDate'/> </strong>
						<div>
							<span ng-show="worksSrvc.details[work.putCode.value].publicationDate.year">{{worksSrvc.details[work.putCode.value].publicationDate.year}}</span><span ng-show="worksSrvc.details[work.putCode.value].publicationDate.month">-{{worksSrvc.details[work.putCode.value].publicationDate.month}}</span><span ng-show="worksSrvc.details[work.putCode.value].publicationDate.day && worksSrvc.details[work.putCode.value].publicationDate.month">-{{worksSrvc.details[work.putCode.value].publicationDate.day}}</span>							
						</div>
					</div>
				</div>
				
				<div class="row bottomBuffer" ng-show="worksSrvc.details[work.putCode.value].shortDescription.value"
					ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'manual_work_form_contents.labeldescription'/> </strong>
						<div ng-bind="worksSrvc.details[work.putCode.value].shortDescription.value"
							style="white-space: pre-wrap;"></div>
					</div>
				</div>
				<div class="row bottomBuffer"
					ng-show="worksSrvc.details[work.putCode.value].workExternalIdentifiers.length > 0" ng-cloak>
					<div class="col-md-8">
						<strong> <@orcid.msg 'manual_work_form_contents.labelID'/>
						</strong>
						<div>
							<span ng-repeat='ie in worksSrvc.details[work.putCode.value].workExternalIdentifiers'> <span
								ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length'></span>
							</span>
						</div>
					</div>
				</div>
				
				<div class="row bottomBuffer" ng-show="worksSrvc.details[work.putCode.value].url.value" ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'common.url'/> </strong>
						<div>
							<a href="{{worksSrvc.details[work.putCode.value].url.value | urlProtocol}}" target="_blank">{{worksSrvc.details[work.putCode.value].url.value}}</a>
						</div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksSrvc.details[work.putCode.value].contributors.length > 0"
					ng-cloak>
					<div class="col-md-12">
						<strong> Contributor </strong>
						<div ng-repeat="contributor in worksSrvc.details[work.putCode.value].contributors">
							{{contributor.creditName.value}} <span
								ng-bind='contributor | contributorFilter'></span>
						</div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksSrvc.details[work.putCode.value].languageCode.value"
					ng-cloak>
					<div class="col-md-12">
						<strong><@orcid.msg
							'manual_work_form_contents.labellanguage'/></strong>
						<div ng-bind="worksSrvc.details[work.putCode.value].languageName.value"></div>
					</div>
				</div>
				<div class="row bottomBuffer" ng-show="worksSrvc.details[work.putCode.value].countryCode.value" ng-cloak>
					<div class="col-md-12">
						<strong><@orcid.msg
							'manual_work_form_contents.labelcountry'/></strong>
						<div ng-bind="worksSrvc.details[work.putCode.value].countryName.value"></div>
					</div>
				</div>						
				<div class="row bottomBuffer" ng-show="worksSrvc.details[work.putCode.value].sourceName" ng-cloak>
					<div class="col-md-12">
						<strong> <@orcid.msg
							'manual_work_form_contents.labelWorkSource'/> </strong>
						<div ng-bind="worksSrvc.details[work.putCode.value].sourceName"></div>
					</div>
				</div>
			</div>
			</div>
		</div><!-- .lightbox-container -->
	</div>
</div>