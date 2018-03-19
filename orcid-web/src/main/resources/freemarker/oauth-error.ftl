<@public classes=['home'] nav="signin">

<div class="row">
    <div class="col-md-offset-3 col-md-9 col-sm-12 col-xs-12">
        <#if error??>
        	<p>${error}</p>
        <#else>
        	<p><@orcid.msg 'oauth.errors.other' /></p>
        </#if>        
    </div>
</div>
</@public>