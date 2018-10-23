<@protected nav="notifications">
<#escape x as x?html> 
<div class="row">
    <div class="col-md-3 col-sm-12 col-xs-12 padding-fix">
        <#include "admin_menu.ftl"/>
    </div>
    <#include "/includes/ng2_templates/notifications-ng2-template.ftl">
    <notifications-ng2></notifications-ng2>
</div>

</#escape>
</@protected>
