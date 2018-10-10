<@public nav="developer-tools">
	<div class="row">
		<div class="col-md-3 lhs col-sm-12 col-xs-12 padding-fix">
			<#include "../includes/id_banner.ftl"/>
		</div>
		<div class="col-md-9 col-sm-12 col-xs-12 developer-tools">
		    <script type="text/ng-template" id="developerTools-ng2-template">
		        <#include "./developer_tools_disabled.ftl"/>       
            </script>            
			<developer-tools-ng2></developer-tools-ng2>
		</div>
	</div>	    
</@public>
