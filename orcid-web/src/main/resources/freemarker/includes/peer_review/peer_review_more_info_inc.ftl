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

<div class="more-info" ng-show="showDetails[group.groupId] && group.activePutCode == peerReview.putCode.value">
	<div class="content" ng-hide="worksSrvc.details[work.putCode.value] == undefined">
		<span class="dotted-bar"></span>
		<div class="row">
			<!-- Translated title -->
			<div class="col-md-6" ng-show="worksSrvc.details[work.putCode.value].translatedTitle.content" ng-cloak>
				<div class="bottomBuffer">
					<strong><@orcid.msg
						'manual_work_form_contents.labeltranslatedtitle'/></strong> <span><i>({{worksSrvc.details[work.putCode.value].translatedTitle.languageName}})</i></span>
					<div>{{worksSrvc.details[work.putCode.value].translatedTitle.content}}</div>				
				</div>
			</div>
		
		
		</div>		
	</div>
</div>