<@public nav="developer-tools">
	<div class="row">
		<div class="col-md-3 lhs col-sm-12 col-xs-12 padding-fix">
		    <#include "/includes/ng2_templates/id-banner-ng2-template.ftl"/>
            <id-banner-ng2> </id-banner-ng2>
		</div>
		<div class="col-md-9 col-sm-12 col-xs-12 developer-tools">
		    <h1 id="manage-developer-tools">
	            <span><@spring.message "manage.developer_tools.user.title"/></span>                 
	        </h1>
	        <div class="sso-api">                                           
                <#include "/includes/ng2_templates/developer-tools-ng2-template.ftl"/>           
                <developer-tools-ng2></developer-tools-ng2>                 
            </div>
		</div>
	</div>	    
</@public>
