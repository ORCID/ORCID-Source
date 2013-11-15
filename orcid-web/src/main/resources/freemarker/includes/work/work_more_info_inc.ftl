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
<div class="more-info ie7-zi-fix-top">	
	<a class="glyphicon glyphicon-plus-sign grey"></a>	
	<div class="popover bottom more-info-container ">	
		<div class="arrow"></div>	
		<div class="lightbox-container">
			<div class="ie7fix">
			<div class="row bottomBuffer"></div>
			<div class="row bottomBuffer" ng-show="work.workTitle.title.value"
				ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg
						'manual_work_form_contents.labeltitle'/></strong>
					<div ng-bind="work.workTitle.title.value"></div>
				</div>
			</div>
			<div class="row bottomBuffer"
				ng-show="work.workTitle.translatedTitle.content" ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg
						'manual_work_form_contents.labeltranslatedtitle'/></strong>
					<div ng-bind="renderTranslatedTitleInfo($index)"></div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="work.workTitle.subtitle.value"
				ng-cloak>
				<div class="col-md-8">
					<strong> <@orcid.msg
						'manual_work_form_contents.labelsubtitle'/> </strong>
					<div ng-bind="work.workTitle.subtitle.value"></div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="work.journalTitle.value"
				ng-cloak>
				<div class="col-md-8">
					<strong> <@orcid.msg
						'manual_work_form_contents.journalTitle'/> </strong>
					<div ng-bind="work.journalTitle.value"></div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="work.workType.value" ng-cloak>
				<div class="col-md-8">
					<strong> <@orcid.msg
						'manual_work_form_contents.labelworktype'/> </strong>
					<div ng-bind="work.workType.value"></div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="work.citation.citation.value"
				ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg 'manual_work_form_contents.labelcitation'/></strong>
					<span ng-show="showBibtex && work.citation.citationType.value == 'bibtex'">
						<a ng-click="bibtexShowToggle()">Show in Bibtex</a>
					</span>
					<span ng-show="showBibtex == false && work.citation.citationType.value == 'bibtex'">
						<a ng-click="bibtexShowToggle()">Show in HTML</a>
					</span>
					<div ng-hide="showBibtex && work.citation.citationType.value == 'bibtex'" ng-bind="work.citation.citation.value" class="col-md-offset-1 col-md-11 col-sm-offset-1 col-sm-11 col-xs-12"></div>					
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
				ng-show="work.citation.citationType.value" ng-cloak>
				<div class="col-md-8">
					<strong> <@orcid.msg 'manual_work_form_contents.labelcitationtype'/> </strong>
					<div ng-bind="work.citation.citationType.value"></div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="work.publicationDate.year"
				ng-cloak>
				<div class="col-md-8">
					<strong> <@orcid.msg
						'manual_work_form_contents.labelPubDate'/> </strong>
					<div>
						<span
							ng-show="work.publicationDate.day && work.publicationDate.month">{{work.publicationDate.day}}-</span><span
							ng-show="work.publicationDate.month">{{work.publicationDate.month}}-</span><span
							ng-show="work.publicationDate.year">{{work.publicationDate.year}}</span>
					</div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="work.shortDescription.value"
				ng-cloak>
				<div class="col-md-8">
					<strong> <@orcid.msg
						'manual_work_form_contents.labeldescription'/> </strong>
					<div ng-bind="work.shortDescription.value"
						style="white-space: pre-wrap;"></div>
				</div>
			</div>
			<div class="row bottomBuffer"
				ng-show="work.workExternalIdentifiers.length > 0" ng-cloak>
				<div class="col-md-8">
					<strong> <@orcid.msg 'manual_work_form_contents.labelID'/>
					</strong>
					<div>
						<span ng-repeat='ie in work.workExternalIdentifiers'> <span
							ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length'></span>
						</span>
					</div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="work.url.value" ng-cloak>
				<div class="col-md-8">
					<strong> <@orcid.msg
						'manual_work_form_contents.labelURL'/> </strong>
					<div>
						<a href="{{work.url.value | urlWithHttp}}" target="_blank">{{work.url.value}}</a>
					</div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="work.contributors.length > 0"
				ng-cloak>
				<div class="col-md-8">
					<strong> Contributor </strong>
					<div ng-repeat="contributor in work.contributors">
						{{contributor.creditName.value}} <span
							ng-bind='contributor | contributorFilter'></span>
					</div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="work.languageCode.value"
				ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg
						'manual_work_form_contents.labellanguage'/></strong>
					<div ng-bind="work.languageName.value"></div>
				</div>
			</div>
			<div class="row bottomBuffer" ng-show="work.countryCode.value" ng-cloak>
				<div class="col-md-8">
					<strong><@orcid.msg
						'manual_work_form_contents.labelcountry'/></strong>
					<div ng-bind="work.countryName.value"></div>
				</div>
			</div>
			</div>
		</div><!-- .lightbox-container -->
	</div>
</div>