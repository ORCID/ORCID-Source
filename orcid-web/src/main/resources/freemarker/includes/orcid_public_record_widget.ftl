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
<#if RequestParameters['widget']??>
	<div class="widget-container" ng-controller="widgetCtrl">
		<div class="widget-header">
			<a ng-click="toggleCopyWidget();showSampleWidget();"><@orcid.msg 'orcid_widget.header'/></a>
			<div class="popover-help-container">
   	     		<a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
   	     		<div id="widget-help" class="popover bottom">
					<div class="arrow"></div>
					<div class="popover-content">
						<p><@orcid.msg 'orcid_widget.tooltip'/></p>
					</div>
				</div>
			</div>
		</div>
		<div ng-show="showCode" ng-cloak class="widget-code-container">
			<p class="widget-instructions"><@orcid.msg 'orcid_widget.copy_message'/></p>
			<textarea id="widget-code-nd" name="widget-code" class="form-control widget-code" ng-model="widgetURLND" ng-click="inputTextAreaSelectAll($event)" readonly="readonly"></textarea>
			<p><@orcid.msg 'orcid_widget.widget_sample'/>:</p>
			<div class="orcid-summary-widget">
				<div style="width:100%;text-align:center">
					<iframe src="${baseUri}/static/html/widget.html?orcid=${(effectiveUserOrcid)!}&t=${(orcidIdHash[0..5])!}" frameborder="0" height="310" width="210px" vspace="0" hspace="0" marginheight="5" marginwidth="5" scrolling="auto" allowtransparency="true"></iframe>
				</div>
			</div>
			<a ng-click="hideWidgetCode()"><@orcid.msg 'orcid_widget.hide_code'/></a>
		</div>
	</div>
</#if>