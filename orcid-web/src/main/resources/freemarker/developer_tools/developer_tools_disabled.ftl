<h1 id="manage-developer-tools">
    <span><@spring.message "manage.developer_tools.user.title"/></span>                 
</h1>
<#if hideRegistration>
   <@orcid.msg 'developer_tools.unavailable' />
<#else>                                 
    <div class="sso-api" *ngIf="!developerToolsEnabled">
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <p><i><@orcid.msg 'developer_tools.note' /> <a href="./my-orcid"><@orcid.msg 'developer_tools.note.link.text' /></a><@orcid.msg 'developer_tools.note.link.point' /></i></p>                                                                
                
                    <#if hasVerifiedEmail>
                        <div *ngIf="!showTerms" class="centered bottomBuffer">
                            <button class="btn btn-primary" (click)="showTerms = true" ><@orcid.msg 'developer_tools.public_member.turn_on' /></button>                        
                        </div>
                        <div *ngIf="showTerms" class="bottomBuffer">        
                            <div class="col-md-12 col-xs-12 col-sm-12">         
                                <div class="row bottomBuffer topBuffer">
                                    <div class="centered col-md-12 col-xs-12 col-sm-12">
                                        <h2 class="bottomBuffer"><@orcid.msg 'developer_tools.public_member.terms.title' /></h2>
                                    </div>
                                    <div class="col-md-12 col-xs-12 col-sm-12">
                                        <span>
                                           <@orcid.msg 'developer_tools.public_member.terms.description_1' /><a href="http://orcid.org/content/orcid-public-client-terms-service" target="terms_of_service"><@orcid.msg 'developer_tools.public_member.terms.description_2' /></a><@orcid.msg 'developer_tools.public_member.terms.description_3' />
                                        </span>             
                                    </div>              
                                </div>      
                                <div class="row bottomBuffer">
                                    <div class="col-md-12 col-xs-12 col-sm-12 bottomBuffer">
                                        <div class="row">
                                            <span class="col-md-1 col-xs-1 col-sm-1 vertical-align-middle"><input type="checkbox" name="accepted" [(ngModel)]="acceptedTerms" /></span> 
                                            <span class="col-md-8 col-xs-8 col-sm-8">
                                                <div class="row">
                                                    <span class="col-md-12 col-xs-12 col-sm-12">
                                                        <@orcid.msg 'developer_tools.public_member.terms.check_1' />
                                                        <a href="http://orcid.org/content/orcid-public-client-terms-service" target="terms_of_service">
                                                        <@orcid.msg 'developer_tools.public_member.terms.check_2' />
                                                        </a>
                                                    </span>
                                                    <span class="col-md-12 col-xs-12 col-sm-12 red" *ngIf="!acceptedTerms">
                                                        <@orcid.msg 'developer_tools.public_member.terms.must_accept' />
                                                    </span>
                                                </div>                                              
                                            </span>
                                            <span class="col-md-3 col-xs-3 col-sm-3">
                                                <a href (click)="showTerms = false" (click)="acceptedTerms = false"><@orcid.msg 'freemarker.btncancel' /></a>&nbsp;
                                                <button class="btn btn-primary" (click)="enableDeveloperTools()"><@orcid.msg 'freemarker.btncontinue' /></button>
                                            </span> 
                                        </div>                                        
                                    </div>                                    
                                </div>  
                            </div>
                        </div>
                    <#else>             
                        <div>
                            <button class="btn btn-primary bottomBuffer" (click)="verifyEmail('${primaryEmail?html}')"><@orcid.msg 'developer_tools.public_member.verify.button' /></button>     
                            <div class="red" *ngIf="verifyEmailSent">                                 
                                <h4><@orcid.msg 'workspace.sent'/></h4>
                                <@orcid.msg 'workspace.check_your_email'/><br />
                            </div>                                                                                      
                        </div>
                    </#if>
                
                    
                    
                <p><@orcid.msg 'developer_tools.client_types.description' /></p>
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
</#if>