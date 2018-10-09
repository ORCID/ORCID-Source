<@public nav="developer-tools">
	<div class="row">
		<div class="col-md-3 lhs col-sm-12 col-xs-12 padding-fix">
			<#include "../includes/id_banner.ftl"/>
		</div>
		<div class="col-md-9 col-sm-12 col-xs-12 developer-tools">
		    <script type="text/ng-template" id="developerTools-ng2-template">
                <h1 id="manage-developer-tools">
                    <span><@spring.message "manage.developer_tools.user.title"/></span>                 
                </h1>        
            </script>
			<developerTools-ng2></developerTools-ng2>
		</div>
	</div>	    
</@public>
