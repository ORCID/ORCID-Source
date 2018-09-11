<@public classes=['home'] nav="consortia-list">
    <#include "/includes/ng2_templates/consortia-list-ng2-template.ftl">
    <div class="member-list row">        
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
            <h1><@orcid.msg 'member_list.orcid_consortia_members'/></h1>
            <p><@orcid.msg 'member_list.consortia_are_groups'/> <a href="<@orcid.rootPath '/about/membership'/>"><@orcid.msg 'developer_tools.member_api.description.1'/></a></p>
            <consortia-list-ng2></consortia-list-ng2>
        </div>
    </div>
</@public>