<script type="text/ng-template" id="developerTools-ng2-template">
<div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <p><i><@orcid.msg 'developer_tools.note' /> <a href="./my-orcid"><@orcid.msg 'developer_tools.note.link.text' /></a><@orcid.msg 'developer_tools.note.link.point' /></i></p> 
        <!--DEVELOPER TOOLS DISABLED-->
        <!--Developer tools registration not allowed for trusted users except admins-->        
        <div *ngIf="userInfo.IN_DELEGATION_MODE=='true' && !(userInfo.DELEGATED_BY_ADMIN=='true')">
           <@orcid.msg 'developer_tools.unavailable' />
        </div>                               
        <div *ngIf="(userInfo.IN_DELEGATION_MODE!='true' || (userInfo.IN_DELEGATION_MODE=='true' && userInfo.ADMIN_MENU!='true')) && !developerToolsEnabled">
            <div *ngIf="userInfo.HAS_VERIFIED_EMAIL=='true'">
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
            </div>
            <div *ngIf="userInfo.HAS_VERIFIED_EMAIL!='true'">             
                <div class="centered">
                    <button class="btn btn-primary bottomBuffer" (click)="verifyEmail(userInfo.PRIMARY_EMAIL)" *ngIf="!verifyEmailSent"><@orcid.msg 'developer_tools.public_member.verify.button' /></button>     
                    <div class="red" *ngIf="verifyEmailSent">
                        <h4><@orcid.msg 'workspace.sent'/></h4>
                        <@orcid.msg 'workspace.check_your_email'/><br />
                    </div>                                                                                      
                </div>
            </div>
        </div>
        <!--DEVELOPER TOOLS ENABLED-->
            <div *ngIf="developerToolsEnabled">
            <div class="row">
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <p class="reset"><strong><@orcid.msg 'developer_tools.public_member.enabled' /></strong></p>
                    <p>
                        <@orcid.msg 'developer_tools.public_member.enabled.terms_1' />
                        <a href="http://orcid.org/content/orcid-public-client-terms-service" target="terms_of_service"><@orcid.msg 'developer_tools.public_member.enabled.terms_2' /></a><@orcid.msg 'developer_tools.public_member.enabled.terms_3' />
                    </p>                            
                    <p class="developer-tools-instructions"></p>
                </div>
            </div>       
            <!-- Create/Edit client -->
            <div *ngIf="showForm">
                <div class="create-client"> 
                    <!-- Name -->
                    <div class="row">                   
                        <div class="col-md-10 col-sm-10 col-xs-12">
                                <span><strong><@orcid.msg 'manage.developer_tools.generate.name'/></strong></span>
                                <input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.name.placeholder'/>" class="full-width-input" [(ngModel)]="client.displayName.value">
                                <span class="orcid-error" *ngIf="client.displayName.errors.length > 0">
                                    <div *ngFor='let error of client.displayName.errors' [innerHTML]="error"></div>
                                </span>
                        </div>  
                        <div class="col-md-2 col-sm-3"></div>                                           
                    </div>
                    <!-- Website -->
                    <div class="row">                   
                        <div class="col-md-10 col-sm-10 col-xs-12 dt-website">
                            <span><strong><@orcid.msg 'manage.developer_tools.generate.website'/></strong></span>
                            <input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.website.placeholder'/>" class="full-width-input" [(ngModel)]="client.website.value">
                            <span class="orcid-error" *ngIf="client.website.errors.length > 0">
                                <div *ngFor='let error of client.website.errors' [innerHTML]="error"></div>
                            </span>                                             
                        </div>          
                        <div class="col-md-2 col-sm-2"></div>                                   
                    </div>
                    <!-- Description -->                        
                    <div class="row">                   
                        <div class="col-md-10 col-sm-10 col-xs-12 dt-description">                      
                            <span><strong><@orcid.msg 'manage.developer_tools.generate.description'/></strong></span>
                            <textarea placeholder="<@orcid.msg 'manage.developer_tools.generate.description.placeholder'/>" [(ngModel)]="client.shortDescription.value"></textarea>                       
                            <span class="orcid-error" *ngIf="client.shortDescription.errors.length > 0">
                                <div *ngFor='let error of client.shortDescription.errors' [innerHTML]="error"></div>
                            </span>                                             
                        </div>          
                        <div class="col-md-2 col-sm-2"></div>                                   
                    </div>
                    <!-- Redirect URIS -->
                    <div class="row">
                        <!-- SLIDE BOX  -->                 
                        <div class="col-md-10 col-sm-10 col-xs-12">
                            <div class="redirectUris">
                                <h4><@orcid.msg 'manage.developer_tools.redirect_uri'/></h4>                        
                                <div *ngFor="let rUri of client.redirectUris; index as idx;">                            
                                    <input type="text" placeholder="<@orcid.msg 'manage.developer_tools.redirect_uri.placeholder'/>" [(ngModel)]="rUri.value.value">                   
                                    <a (click)="deleteRedirectUri(idx);" class="glyphicon glyphicon-trash blue"></a>
                                    <span class="orcid-error" *ngIf="rUri.errors.length > 0">
                                        <div *ngFor='let error of rUri.errors' [innerHTML]="error"></div>
                                    </span> 
                                </div>
                                <span class="orcid-error" *ngIf="client.redirectUris.length == 0">
                                    <div><@orcid.msg 'manage.developer_tools.at_least_one' /></div>
                                </span>
                            </div>
                        </div>  
                        <div class="col-md-2 col-sm-2"></div>                   
                    </div>
                    <!-- Options -->
                    <div class="row">
                        <div class="col-md-9 col-sm-9 col-xs-9 add-options">
                            <a class="icon-href-bg" (click)="addRedirectURI();"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.add_redirect_uri' /></a>
                            <div class="add-options margin-bottom-box" *ngIf="!hideGoogleUri || !hideSwaggerUri">
                                <div>
                                    <h4><@orcid.msg 'manage.developer_tools.test_redirect_uris.title' /></h4>
                                    <ul class="pullleft-list">
                                        <li *ngIf="!hideGoogleUri" id="google-ruir"><a class="icon-href" (click)="addTestRedirectUri('google')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.google'/></a></li>
                                        <li *ngIf="!hideSwaggerUri" id="swagger-ruir"><a class="icon-href" (click)="addTestRedirectUri('swagger')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.swagger'/></a></li>                                        
                                    </ul>                               
                                </div>
                            </div>                      
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">                
                            <ul class="sso-options pull-right">                                 
                                <li><a (click)="createOrUpdateCredentials();" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>                                                                                                
                            </ul>                   
                        </div>  
                    </div>              
                </div>
                <div class="slidebox" *ngIf="client.clientId.value.length > 0">
                    <div class="row" *ngIf="showResetClientSecret">
                        <div class="col-md-12 col-xs-12 col-sm-12">
                            <div class="grey-box">
                                <h3><@orcid.msg 'manage.developer_tools.edit.reset_key.title' /></h3>               
                                <p><strong>{{client.clientSecret.value}}</strong></p>       
                                <p><@orcid.msg 'manage.developer_tools.edit.reset_key.description' /></p>
                                <div class="btn btn-danger" (click)="resetClientSecret()">
                                    <@orcid.msg 'freemarker.btnReset' />
                                </div>
                                <a (click)="showResetClientSecret = false"><@orcid.msg 'freemarker.btncancel' /></a>               
                            </div>
                        </div>
                    </div>
                    <div class="row" *ngIf="!showResetClientSecret">
                        <div class="col-md-12 col-sm-12 col-xs-12">                     
                            <div class="grey-box">
                                <div class="row bottomBuffer">
                                    <div class="col-md-3 col-sm-3 col-xs-12">
                                        <strong><@orcid.msg 'manage.developer_tools.view.orcid'/></strong>
                                    </div>
                                    <div class="col-md-9 col-sm-9 col-xs-12">
                                        {{client.clientId.value}}
                                    </div>
                                </div>
                                <div class="row bottomBuffer">
                                    <div class="col-md-3 col-sm-3 col-xs-12">
                                        <strong><@orcid.msg 'manage.developer_tools.view.secret'/></strong>
                                    </div>
                                    <div class="col-md-9 col-sm-9 col-xs-12">
                                        {{client.clientSecret.value}}
                                    </div>
                                </div>
                                <div class="row bottomBuffer">
                                    <div class="col-md-3 col-sm-3 col-xs-12">
                                    </div>
                                    <div class="col-md-9 col-sm-9 col-xs-12">
                                        <a class="btn btn-primary" (click)="showResetClientSecret = true">
                                            <@orcid.msg 'manage.developer_tools.edit.reset_client_secret' />
                                        </a>
                                    </div>
                                </div>                                                       
                            </div>
                        </div>   
                    </div>            
                </div>        
            </div>    
            <!-- View client -->
            <div *ngIf="!showForm">
                <div class="details" *ngIf="!showForm">
                    <!-- Name and Edit/Delete options -->
                    <div class="row">                   
                        <div class="col-md-10 col-sm-10 col-xs-9">                      
                            <h4>{{client.displayName.value}}</h4>                                                  
                        </div>
                        <div class="col-md-2 col-sm-2 col-xs-3">                
                            <ul class="sso-options-light pull-right">                           
                                <li><a (click)="showForm = true" class="edit" title="<@orcid.msg 'manage.developer_tools.tooltip.edit' />"><span class="glyphicon glyphicon-pencil"></span></a></li>                         
                            </ul>                   
                        </div>              
                    </div>          
                    <div class="row">
                        <!-- Website -->
                        <div class="col-md-12 col-sm-12 col-xs-12 dt-website">
                            <p><a href="{{getClientUrl(client.website.value)}}">{{client.website.value}}</a></p>
                        </div>                          
                    </div>
                    <div class="row">
                        <!-- Description -->
                        <div class="col-md-12 col-sm-12 col-xs-12 dt-description">
                            <p>{{client.shortDescription.value}}</p>                                                     
                        </div>                          
                    </div>        
                    <div class="row" *ngIf="expandDetails == false">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <div class="tab-container">
                                <a class="tab" (click)="expandDetails = true"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'common.details.show_details' /></a>
                            </div>
                        </div>
                    </div>
                    <div *ngIf="expandDetails">
                        <div class="row">
                            <!-- Redirect URIS -->
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <h4><@orcid.msg 'manage.developer_tools.redirect_uri'/>:</h4>                                                                       
                                <select [(ngModel)]="selectedRedirectUri" (ngModelChange)="generateSamples($event)" class="input-xlarge-full input-xlarge">
                                    <option *ngFor="let rUrl of client.redirectUris" [ngValue]="rUrl.value.value" >{{rUrl.value.value}}</option>
                                </select>                                                       
                            </div>
                        </div>
                        <div class="row">                       
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="grey-box">
                                    <!-- Client details-->
                                    <div class="row bottomBuffer">
                                        <div class="col-md-3 col-sm-3 col-xs-12">
                                            <strong><@orcid.msg 'manage.developer_tools.view.orcid'/></strong>                                  
                                        </div>
                                        <div class="col-md-9 col-sm-9 col-xs-12">
                                            {{client.clientId.value}}                              
                                        </div>
                                    </div>                    
                                    <div class="row bottomBuffer">
                                        <div class="col-md-3 col-sm-3 col-xs-12">
                                            <strong><@orcid.msg 'manage.developer_tools.view.secret'/></strong>                             
                                        </div>
                                        <div class="col-md-9 col-sm-9 col-xs-12">
                                            {{client.clientSecret.value}}                          
                                        </div>
                                    </div>
                                     <!-- Authorize URL and Token URL -->
                                    <div class="row bottomBuffer">
                                        <div class="col-md-3 col-sm-3 col-xs-12">
                                            <strong><@orcid.msg 'manage.developer_tools.view.example.authorize'/></strong>                          
                                        </div>
                                        <div class="col-md-9 col-sm-9 col-xs-12">
                                            <div class="row">
                                                <div class="col-md-12 col-sm-12 col-xs-12">
                                                    <strong><@orcid.msg 'manage.developer_tools.view.endpoint'/>&nbsp;</strong>{{authorizeUrlBase}}
                                                </div>
                                            </div>
                                            <div class="row">                                                   
                                                <div class="col-md-12 col-sm-12 col-xs-12">
                                                    <strong><@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.scope'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate'/><br/>
                                                    <strong><@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.response_type'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.response_type.code'/><br/>
                                                    <strong><@orcid.msg 'manage.developer_tools.view.description'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.description'/>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-12 col-sm-12 col-xs-12">
                                                    <textarea class="input-xlarge-full authorizeURL" [(ngModel)]="authorizeURL" readonly="readonly"></textarea>
                                                </div>
                                            </div>                              
                                        </div>
                                    </div>
                                    <div class="row bottomBuffer">
                                        <div class="col-md-3 col-sm-3 col-xs-12">
                                            <strong><@orcid.msg 'manage.developer_tools.view.example.token'/></strong>                              
                                        </div>
                                        <div class="col-md-9 col-sm-9 col-xs-12">
                                                <strong><@orcid.msg 'manage.developer_tools.view.endpoint'/>&nbsp;</strong>{{tokenURL}}<br />
                                                <strong><@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.response_type'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.example.token.response_type'/><br/>
                                                <strong><@orcid.msg 'manage.developer_tools.view.description'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.example.token.description'/><br/>
                                                <@orcid.msg 'manage.developer_tools.view.example.curl' /><a href="<@orcid.msg 'manage.developer_tools.view.example.curl.url' />" target="curlWiki"><@orcid.msg 'manage.developer_tools.view.example.curl.text' /></a> 
                                                <textarea class="input-xlarge-full authorizeURL" [(ngModel)]="sampleAuthCurl" readonly="readonly"></textarea>                            
                                        </div>
                                    </div>                    
                                    <!-- Openid URL -->
                                    <div class="row bottomBuffer">
                                        <div class="col-md-3 col-sm-3 col-xs-12">
                                            <strong><@orcid.msg 'manage.developer_tools.view.example.openid'/></strong>                         
                                        </div>
                                        <div class="col-md-9 col-sm-9 col-xs-12">
                                            <div class="row">
                                                <div class="col-md-12 col-sm-12 col-xs-12">
                                                    <strong><@orcid.msg 'manage.developer_tools.view.endpoint'/>&nbsp;</strong>{{authorizeUrlBase}}
                                                </div>
                                            </div>
                                            <div class="row">                                                   
                                                <div class="col-md-12 col-sm-12 col-xs-12">
                                                    <strong><@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.scope'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.available_scopes.openid'/><br/>
                                                    <strong><@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.response_type'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.response_type.token'/><br/>
                                                    <strong><@orcid.msg 'manage.developer_tools.view.description'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.available_scopes.openid.description'/> (<a href="<@orcid.msg 'manage.developer_tools.view.example.opendid.url' />" target="openidWiki"><@orcid.msg 'manage.developer_tools.view.example.openid.text' /></a>)
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-12 col-sm-12 col-xs-12">
                                                    <textarea class="input-xlarge-full authorizeURL" [(ngModel)]="sampleOpenId" readonly="readonly"></textarea>
                                                </div>
                                            </div>                              
                                        </div>
                                    </div>                                                                 
                                </div>
                            </div>
                        </div>
                        <div class="row" *ngIf="expandDetails == true">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="tab-container">
                                    <a class="tab" (click)="expandDetails = false"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'common.details.hide_details' /></a>
                                </div>
                            </div>
                        </div>            
                    </div>
                </div>         
            </div>
        </div><!--END DEVELOPER TOOLS ENABLED-->
    </div>                
</div><!--END ROW-->
<!--WHAT CAN YOU DO WITH THE PUBLIC API-->
<div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <div *ngIf="!developerToolsEnabled">
            <p *ngIf="!developerToolsEnabled"><@orcid.msg 'developer_tools.client_types.description' /></p>
        </div>
        <div *ngIf="developerToolsEnabled">
            <h3><@orcid.msg 'developer_tools.public_member.what_can_you_do' /></h3>
            <p><@orcid.msg 'developer_tools.public_member.what_can_you_do.description' /></p>
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
<!--WANT TO DO MORE WITH THE ORCID API-->
<div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <h3><@orcid.msg 'developer_tools.client_types.description.differences' /></h3>
        <p><a href="https://orcid.org/about/membership/comparison" target="developer_tools.client_types.description.differences.link"><@orcid.msg 'developer_tools.client_types.description.differences.link' /></a></p>
    </div>
</div>
<!--ADDITIONAL RESOURCES-->
<div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">                             
        <h3><@orcid.msg 'developer_tools.public_member.additional_resources' /></h3>                                                                    
        <ul class="dotted">
            <li *ngIf="userInfo.HAS_VERIFIED_EMAIL!='true'"><button class="btn btn-white-no-border text-14" (click)="verifyEmail(userInfo.PRIMARY_EMAIL)"><@orcid.msg 'developer_tools.public_member.verify.link' /></button> <@orcid.msg 'developer_tools.public_member.verify.description' /></li>
            <li><a href="<@orcid.msg 'common.kb_uri_default'/>360006897174" target="developer_tools.public_member.read_more"><@orcid.msg 'developer_tools.public_member.read_more' /></a></li>
        </ul>
    </div>
</div> 
</script>            