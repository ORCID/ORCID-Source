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
<footer class="footer">
    <p class="pull-right"><a href="#">${springMacroRequestContext.getMessage("footer.backtotop")}</a></p>
    <ul class="nav nav-pills">
        <li class=""><a href="${aboutUri}/help/contact-us">${springMacroRequestContext.getMessage("footer.contactus")}</a></li>
	    <li class=""><a href="${aboutUri}/footer/privacy-policy">${springMacroRequestContext.getMessage("footer.privacypolicy")}</a></li>
	    <li class=""><a href="${aboutUri}/content/orcid-terms-use">${springMacroRequestContext.getMessage("footer.termsofuse")}</a></li>
    </ul>
    <p id="copyright">${springMacroRequestContext.getMessage("footer.copyright")}</p>
</footer>
