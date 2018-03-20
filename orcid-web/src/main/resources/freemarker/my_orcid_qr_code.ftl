<@protected nav="record">
<#escape x as x?html>
<div class="row">
    <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12"> 
        <h1><@orcid.msg 'workspace.qrcode.heading'/></h1>
        <p><@orcid.msg 'workspace.qrcode.detail_1'/></p>
        <p><@orcid.msg 'workspace.qrcode.detail_2'/></p>
        <div>
            <a href="<@orcid.rootPath "/ORCID.png"/>" download="ORCID.png" type="image/png">
                <img class="qrcode-image" src="<@orcid.rootPath "/my-orcid-qr-code.png"/>"></img>
            </a>
            <p>
                <a href="<@orcid.rootPath "/ORCID.png"/>" download="ORCID.png" type="image/png"><@orcid.msg 'workspace.qrcode.download'/></a>
            </p>
        </div>
        
    </div>
</div>
</#escape>
</@protected>  