<@protected>
<div class="alert alert-error">
    ${springMacroRequestContext.getMessage("wrong_user.Wronguser")}
</div>
<div><a href="<@orcid.rootPath '/signout'/>">${springMacroRequestContext.getMessage("public-layout.sign_out")}</a> ${springMacroRequestContext.getMessage("wrong_user.andtryagain")}</div>
</@protected>
