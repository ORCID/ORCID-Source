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

	<div class="print-orcid-record">
		<a href="${baseUriHttp}/${(profile.orcidIdentifier.path)!}" onClick="window.print();return false"><span class="glyphicon glyphicon-print"></span> Print your ORCID record</a>
		<#--<@orcid.msg 'id_banner.viewpublicprofile'/>-->
    </div>
    <div class="print-orcid-record">
        <a href="http://pdfmyurl.com/api?license=M9ldpIx9K1iU&url=${baseUriHttp}/${(profile.orcidIdentifier.path)!}&page_size=Letter&orientation=portrait&css_media_type=print&filename=ORCID-record&javascript_time=600"><span class="glyphicon glyphicon-floppy-save"></span></span> Save PDF</a>
	</div>



