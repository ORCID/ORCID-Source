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
<#if RequestParameters['print']??>
	<div class="print-orcid-record">
		<a href="${baseUriHttp}/${(effectiveUserOrcid)!}" onClick="window.print();return false"><span class="glyphicon glyphicon-print"></span> Print your ORCID record</a>
		<#--<@orcid.msg 'id_banner.viewpublicprofile'/>-->
        <div class="popover-help-container">
            <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
            <div id="print-help" class="popover bottom">
                <div class="arrow"></div>
                <div class="popover-content">
                    <p>Print the public view of your ORCID record</p>
                    <#--<p><@orcid.msg 'workspace.qrcode.help'/></p>-->
                </div>
            </div>
        </div>
    </div>
    <div class="print-orcid-record">
        <a href="http://pdfmyurl.com/api?license=M9ldpIx9K1iU&url=${baseUriHttp}/${(effectiveUserOrcid)!}&page_size=Letter&orientation=portrait&css_media_type=print&filename=ORCID-record&javascript_time=600"><span class="glyphicon glyphicon-floppy-save"></span></span> Save PDF</a>
        <div class="popover-help-container">
            <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
            <div id="save-pdf-help" class="popover bottom">
                <div class="arrow"></div>
                <div class="popover-content">
                    <p>Save and download the public view of your ORCID record as a PDF file</p>
                    <#--<p><@orcid.msg 'workspace.qrcode.help'/></p>-->
                </div>
            </div>
        </div>
	</div>
</#if>


