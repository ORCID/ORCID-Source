<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
<hr class="logo" />
<p>
    <@emailMacros.msg "notification.header.hi" /><@emailMacros.space />${emailName}
    <a href="#">(${baseUri}/${orcidValue})</a>
</p>
<p>
    <@emailMacros.msg "notification.header.gotNewNotifications" />
    <a href="#"><@emailMacros.msg "notification.header.visit" /></a>
</p>
<br>
<h3><@emailMacros.msg "notification.header.newNotifications" /></h3>