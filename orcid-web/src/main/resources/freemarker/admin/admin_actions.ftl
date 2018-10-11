<@public nav="admin_actions">
<div class="row">
    <!-- Left menu bar -->  
    <div class="col-md-3 col-sm-12 col-xs-12 lhs padding-fix">
        <#include "../includes/id_banner.ftl"/>
    </div>
    <!-- Right menu bar -->
    <div class="col-md-9 col-sm-12 col-xs-12 admin-options">
        <#include "../includes/ng2_templates/admin-actions-ng2-template.ftl">    
        <admin-actions-ng2></admin-actions-ng2>    
    </div>
</div>
</@public>