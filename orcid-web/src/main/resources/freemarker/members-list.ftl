<@public classes=['home'] nav="members-list">	
    <#include "/includes/ng2_templates/members-list-ng2-template.ftl">
    <div class="member-list row">        
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
        	<div class="row">
        		<div class="col-md-12 col-sm-12 col-xs-12">
		            <h1><@orcid.msg 'member_list.orcid_member_organizations'/></h1>
		            <p><@orcid.msg 'member_list.orcid_is_a'/> <a href="<@orcid.rootPath '/about/membership'/>"><@orcid.msg 'developer_tools.member_api.description.1'/></a></p>
	            	
	            </div>
	            <members-list-ng2></members-list-ng2>
	    	</div>
        </div>	    
    </div>
</@public>