<@public nav="admin_actions">
    <div class="row">
        <div class="col-md-offset-3 col-md-9 col-sm-offset-3 col-sm-9 col-xs-12">
            <h2><@orcid.msg 'claim.claimyourrecord' /></h2>
            <h4><@orcid.msg 'claim.almostthere' /></h4>
            <p><@orcid.msg 'claim.completefields' /></p>                           
            <#include "/includes/ng2_templates/claim-ng2-template.ftl">
            <claim-ng2></claim-ng2>
        </div>
    </div>
</@public>