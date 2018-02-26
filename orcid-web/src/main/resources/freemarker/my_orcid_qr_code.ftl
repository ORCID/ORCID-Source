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
<@protected nav="record">
<#escape x as x?html>
<div class="row">
    <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12"> 
        <h1><@orcid.msg 'workspace.qrcode.heading'/></h1>
        <div>
            <a href="<@orcid.rootPath "/ORCID.png"/>" download="ORCID.png" type="image/png">
                <img class="qrcode-image" src="<@orcid.rootPath "/my-orcid-qr-code.png"/>"></img>
            </a>
        </div>
    </div>
</div>
</#escape>
</@protected>  