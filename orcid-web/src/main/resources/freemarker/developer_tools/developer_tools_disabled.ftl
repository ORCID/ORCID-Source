<#if hideRegistration>
   <@orcid.msg 'developer_tools.unavailable' />
<#else>                                 
    <div *ngIf="!developerToolsEnabled">
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
            <div class="centered">
                <button class="btn btn-primary bottomBuffer" (click)="verifyEmail('${primaryEmail?html}')" *ngIf="!verifyEmailSent"><@orcid.msg 'developer_tools.public_member.verify.button' /></button>     
                <div class="red" *ngIf="verifyEmailSent">                                 
                    <h4><@orcid.msg 'workspace.sent'/></h4>
                    <@orcid.msg 'workspace.check_your_email'/><br />
                </div>                                                                                      
            </div>
        </#if>
    </div>
</#if>