<@base>
<div class="container">
<div class="row">
    <div class="col-md-offset-3 col-md-6 col-sm-offset-3 col-sm-6 col-xs-12">
        <div class="alert">${springMacroRequestContext.getMessage("session_expired.labelsessionexpired")} <a href="<@orcid.rootPath '/signin'/>">${springMacroRequestContext.getMessage("header.signin")}</a> ${springMacroRequestContext.getMessage("session_expired.labeltryagain")}</p>
    </div>
</div>
</div>
</@base>