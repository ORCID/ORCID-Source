<@public nav="self-service">
    <#include "/includes/ng2_templates/modal-ng2-template.ftl">
    <#include "/includes/ng2_templates/self-service-add-contact-ng2-template.ftl">
    <#include "/includes/ng2_templates/self-service-existing-sub-member-ng2-template.ftl">
    <#include "/includes/ng2_templates/self-service-remove-contact-ng2-template.ftl">
    <#include "/includes/ng2_templates/self-service-remove-sub-member-ng2-template.ftl">
    <#include "/includes/ng2_templates/self-service-ng2-template.ftl">
    
    <div class="row">
        <div class="col-md-3 lhs col-sm-12 col-xs-12 padding-fix">
            <#include "includes/id_banner.ftl"/>
        </div>
        <self-service-ng2></self-service-ng2>
    </div>
    
    <modalngcomponent elementHeight="550" elementId="modalSelfServiceAddContact" elementWidth="616">
        <self-service-add-contact-ng2></self-service-add-contact-form-ng2>
    </modalngcomponent><!-- Ng2 component -->
    
    <modalngcomponent elementHeight="550" elementId="modalSelfServiceExistingSubMember" elementWidth="616">
        <self-service-existing-sub-member-ng2></self-service-existing-sub-member-ng2>
    </modalngcomponent><!-- Ng2 component --> 
    
    <modalngcomponent elementHeight="550" elementId="modalSelfServiceRemoveContact" elementWidth="616">
        <self-service-remove-contact-ng2></self-service-remove-contact-ng2>
    </modalngcomponent><!-- Ng2 component -->
    
    <modalngcomponent elementHeight="550" elementId="modalSelfServiceRemoveSubMember" elementWidth="616">
        <self-service-remove-sub-member-ng2></self-service-remove-sub-member-ng2>
    </modalngcomponent><!-- Ng2 component --> 
    
</@public>
