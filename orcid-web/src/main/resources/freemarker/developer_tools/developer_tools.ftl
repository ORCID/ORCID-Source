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
    	        <div class="row">
    	            <div class="col-md-12 col-sm-12 col-xs-12">
    	                <p><i><@orcid.msg 'developer_tools.note' /> <a href="./my-orcid"><@orcid.msg 'developer_tools.note.link.text' /></a><@orcid.msg 'developer_tools.note.link.point' /></i></p>                                                                
    	                <script type="text/ng-template" id="developerTools-ng2-template">
    	                    <#include "./developer_tools_disabled.ftl"/> 
    	                    <#include "./developer_tools_enabled.ftl"/> 
    	                </script>            
    	                <developer-tools-ng2></developer-tools-ng2>
    	            </div>                
                </div>
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <div>
                            <#if !developerToolsEnabled>
                                <p><@orcid.msg 'developer_tools.client_types.description' /></p>
                            <#else>
                                <h3><@orcid.msg 'developer_tools.public_member.what_can_you_do' /></h3>
                                <p><@orcid.msg 'developer_tools.public_member.what_can_you_do.description' /></p>
                            </#if>
                        </div>                        
                        <ul class="dotted">
                            <li><@orcid.msg 'developer_tools.client_types.description.bullet.1' /></li>
                            <li><@orcid.msg 'developer_tools.client_types.description.bullet.2' /></li>
                            <li><@orcid.msg 'developer_tools.client_types.description.bullet.3' /></li>
                            <li><@orcid.msg 'developer_tools.client_types.description.bullet.4' /></li>
                        </ul>
                        <p>
                            <@orcid.msg 'developer_tools.client_types.description.oauth2_1' /><a href="http://oauth.net/2/" target="oauth2"><@orcid.msg 'developer_tools.client_types.description.oauth2_2' /></a><@orcid.msg 'developer_tools.client_types.description.oauth2_3' />
                        </p>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h3><@orcid.msg 'developer_tools.client_types.description.differences' /></h3>
                        <p><a href="https://orcid.org/about/membership/comparison" target="developer_tools.client_types.description.differences.link"><@orcid.msg 'developer_tools.client_types.description.differences.link' /></a></p>
                    </div>
                </div>                                                              
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">                             
                        <h3><@orcid.msg 'developer_tools.public_member.additional_resources' /></h3>                                                                    
                        <ul class="dotted">
                            <#if !hasVerifiedEmail>
                                <li><a href (click)="verifyEmail('${primaryEmail?html}')"><@orcid.msg 'developer_tools.public_member.verify.link' /></a> <@orcid.msg 'developer_tools.public_member.verify.description' /></li>
                            </#if>
                            <li><a href="<@orcid.msg 'common.kb_uri_default'/>360006897174" target="developer_tools.public_member.read_more"><@orcid.msg 'developer_tools.public_member.read_more' /></a></li>
                        </ul>
                    </div>
                </div> 
            </div>
		</div>
	</div>	    
</@public>
