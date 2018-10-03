<script type="text/ng-template" id="client-edit-ng2-template">
    <div id="member_developer_tools_header"> 
        <!-- Header -->         
        <div class="row">
            <div class="col-md-9 col-sm-10 col-xs-10">
                <h2><@orcid.msg 'manage.developer_tools.group.title'/></h2>
            </div>
            
            <div class="col-md-3 col-sm-2 col-xs-2">                
                <#if allow_more_clients?? && allow_more_clients>
                    <#if is_premium?? && is_premium>
                        <a class="pull-right"><span id="label btn-primary cboxElement" (click)="showAddClient()" class="btn btn-primary"><@orcid.msg 'manage.developer_tools.group.add'/></span></a>
                    <#else>
                        <a class="pull-right" ng-hide="clients.length > 0"><span id="label btn-primary cboxElement" (click)="showAddClient()" class="btn btn-primary"><@orcid.msg 'manage.developer_tools.group.add'/></span></a>
                    </#if>                      
                </#if>
            </div>              
        </div>
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <p class="developer-tools-instructions"><@orcid.msg 'manage.developer_tools.header_1' /><a href="<@orcid.msg 'manage.developer_tools.header_url' />" target="manage.developer_tools.header_link"><@orcid.msg 'manage.developer_tools.header_link' /></a><@orcid.msg 'manage.developer_tools.header_2' /></p>
            </div>
        </div>      
        <!-- View existing credentials -->
        <div class="listing-clients" *ngIf="listing" ng-cloack>
            <div class="row">
                <div class="col-md-12 client-api">
                    <p><@orcid.msg 'manage.developer_tools.group.description.1' />&nbsp;<a href="<@orcid.msg 'manage.developer_tools.group.description.link.url' />"><@orcid.msg 'manage.developer_tools.group.description.link.text' /></a><@orcid.msg 'manage.developer_tools.group.description.2' /></p>     
                    <div *ngIf="clients?.length == 0" >
                        <span><@orcid.msg 'manage.developer_tools.group.no_clients'/></span><br />
                        <span><@orcid.msg 'manage.developer_tools.group.register_now'/>&nbsp;<a (click)="showAddClient()"><@orcid.msg 'manage.developer_tools.group.add'/></a></span>
                    </div>  
                    <div *ngIf="clients?.length > 0" >
                        <table class="table sub-table">
                            <tbody>
                                <tr>
                                    <td colspan="12" class="table-header-dt">
                                        <@orcid.msg 'manage.developer_tools.group.group.id'/> ${(member_id)!} (${(member_type)!})
                                    </td>                       
                                </tr>   
                                <tr *ngFor="let client of clients">
                                    <td colspan="8">
                                        {{client.displayName.value}} (<a href="{{getClientUrl(client)}}" target="client.website.value">{{client.website.value}}</a>)
                                    </td>                                               
                                    <td colspan="4" class="pull-right">                                     
                                        <ul class="client-options">
                                            <li><a (click)="viewDetails(client)"><span class="glyphicon glyphicon-eye-open"></span><@orcid.msg 'manage.developer_tools.group.view_credentials_link' /></a></li>   
                                            <li><a (click)="showEditClient(client)"><span class="glyphicon glyphicon-pencil"></span><@orcid.msg 'manage.developer_tools.group.edit_credentials_link' /></a></li>                                              
                                        </ul>                                       
                                    </td>                                   
                                </tr>                                               
                            </tbody>
                        </table>
                    </div>                                                              
                </div>          
            </div>  
        </div>
        <!-- ---------------------- -->
        <!-- Create new credentials -->
        <!-- ---------------------- -->
        <div class="create-client" *ngIf="creating" > 
            <!-- Name -->
            <div class="row">                   
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="inner-row margin-left-fix">
                        <span><strong><@orcid.msg 'manage.developer_tools.group.display_name'/></strong></span>
                        <input type="text" placeholder="<@orcid.msg 'manage.developer_tools.group.display_name_placeholder'/>" class="input-xlarge" [(ngModel)]="newClient.displayName.value" />
                        <span class="orcid-error" *ngIf="newClient?.displayName?.errors?.length > 0">
                            <div *ngFor='let error of newClient.displayName.errors' [innerHTML]="error"></div>
                        </span>                 
                    </div>      
                </div>                                                              
            </div>
            <!-- Website -->
            <div class="row">   
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="inner-row margin-left-fix">
                        <span><strong><@orcid.msg 'manage.developer_tools.group.website'/></strong></span>
                        <input type="text" placeholder="<@orcid.msg 'manage.developer_tools.group.website_placeholder'/>" class="input-xlarge" [(ngModel)]="newClient.website.value" />
                        <span class="orcid-error" *ngIf="newClient?.website?.errors?.length > 0">
                            <div *ngFor='let error of newClient.website.errors' [innerHTML]="error"></div>
                        </span>                 
                    </div>      
                </div>  
            </div>
            <!-- Description -->
            <div class="row">                   
                <div class="col-md-12 col-sm-12 col-xs-12 dt-description">
                    <div class="inner-row margin-left-fix">
                        <span><strong><@orcid.msg 'manage.developer_tools.group.description'/></strong></span>
                        <textarea class="input-xlarge" placeholder="<@orcid.msg 'manage.developer_tools.group.description_placeholder'/>" [(ngModel)]="newClient.shortDescription.value"></textarea>                       
                        <span class="orcid-error" *ngIf="newClient?.shortDescription?.errors?.length > 0">
                            <div *ngFor='let error of newClient.shortDescription.errors' [innerHTML]="error"></div>
                        </span>
                    </div>                                                          
                </div>          
            </div>
            <!-- Allow auto deprecate -->
            <div class="row bottomBuffer">
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="inner-row margin-left-fix">
                        <span>
                            <strong class="middle"><@orcid.msg 'manage.developer_tools.group.allow_auto_deprecate' /></strong>
                            <input type="checkbox" class="small-element middle" [(ngModel)]="newClient.allowAutoDeprecate.value" />
                        </span>                                                         
                    </div>                                                          
                </div>
            </div>
            <!-- Redirect Uris -->              
            <div *ngFor="let rUri of newClient.redirectUris" class="margin-bottom-box">
                <!-- Header -->
                <div class="row" *ngIf="$first">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <div class="inner-row margin-left-fix">                 
                            <h4><@orcid.msg 'manage.developer_tools.redirect_uri'/></h4>
                        </div>
                    </div>
                </div>
                <!-- Value -->
                <div class="grey-box">
                    <div class="row">                       
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <div class="inner-row margin-left-fix">                         
                                <input type="text" placeholder="<@orcid.msg 'manage.developer_tools.group.redirect_uri_placeholder'/>" class="input-xlarge ruri" [(ngModel)]="rUri.value.value" />                                                         
                                <a (click)="deleteUriOnNewClient($index)" class="glyphicon glyphicon-trash grey"></a>
                                <span class="orcid-error" *ngIf="rUri?.errors?.length > 0">
                                    <div *ngFor='let error of rUri.errors' [innerHTML]="error"></div>
                                </span>                                 
                            </div>                                          
                        </div>  
                    </div>                      
                </div>
            </div>      
            <div class="row">
                <!-- Add redirect uris -->
                <div class="col-md-9 col-sm-9 col-xs-9 add-options">
                    <a class="icon-href-bg" (click)="addRedirectUriToNewClientTable()"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.add_redirect_uri' /></a>
                    <div class="add-options margin-bottom-box" *ngIf="!hideGoogleUri || !hideSwaggerUri || !hideSwaggerMemberUri">                                
                        <div>
                            <h4><@orcid.msg 'manage.developer_tools.test_redirect_uris.title' /></h4>
                            <ul class="pullleft-list">
                                <li *ngIf="!hideGoogleUri" id="google-ruir"><a class="icon-href" (click)="addTestRedirectUri('google','false')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.google'/></a></li>                                        
                                <li *ngIf="!hideSwaggerUri" id="swagger-ruir"><a class="icon-href" (click)="addTestRedirectUri('swagger','false')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.swagger'/></a></li>                                        
                                <li *ngIf="!hideSwaggerMemberUri" id="swagger-member-ruir"><a class="icon-href" (click)="addTestRedirectUri('swagger-member','false')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.swagger_member'/></a></li>                                     
                            </ul>                               
                        </div>
                    </div>                      
                </div>
                <div class="col-md-3 col-sm-3 col-xs-3 sso-api">                
                    <ul class="sso-options pull-right">                         
                        <li>
                            <a href="#member_developer_tools_header" (click)="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />">
                                <span class="glyphicon glyphicon-arrow-left"></span>
                            </a>
                        </li>
                        <li>
                            <a (click)="addClient()" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />">
                                <span class="glyphicon glyphicon-floppy-disk"></span>
                            </a>
                        </li>                           
                    </ul>                   
                </div>      
            </div>      
        </div>
        
        <!-- ---------------- -->
        <!-- View credentials -->
        <!-- ---------------- -->
        <div class="view-client" *ngIf="viewing" >        
            <!-- Client name -->
            <div class="row">
                <div class="col-md-12 col-sm-12 col-xs-12">             
                    <ul class="sso-options pull-right"> 
                        <li><a href="#member_developer_tools_header" (click)="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />"><span class="glyphicon glyphicon-arrow-left"></span></a></li>                        
                        <li><a (click)="showEditClient(clientDetails)" class="edit" title="<@orcid.msg 'manage.developer_tools.tooltip.edit' />"><span class="glyphicon glyphicon-pencil"></span></a></li>                            
                    </ul>                   
                </div>
            </div>
            <div class="row bottomBuffer">
                <div class="col-md-3 col-sm-3 col-xs-6">
                    <span><strong><@orcid.msg 'manage.developer_tools.group.display_name'/></strong></span>
                </div>                  
                <div class="col-md-9 col-sm-9 col-xs-6">
                    <span><strong>{{clientDetails.displayName.value}}</strong></span>                                               
                </div>
            
            </div>
            <!-- Client ID -->
            <div class="row bottomBuffer">
                <div class="col-md-3 col-sm-3 col-xs-12">
                    <span><strong><@orcid.msg 'manage.developer_tools.view.orcid'/></strong></span>
                </div>
                <div class="col-md-9 col-sm-9 col-xs-12"><span>{{clientDetails.clientId.value}}</span></div>
            </div>
            <!-- Client secret -->
            <div class="row bottomBuffer">
                <div class="col-md-3 col-sm-3 col-xs-12">
                    <span><strong><@orcid.msg 'manage.developer_tools.view.secret'/></strong></span>
                </div>
                <div class="col-md-9 col-sm-9 col-xs-12"><span>{{clientDetails.clientSecret.value}}</span></div>
            </div>          
            <div class="row bottomBuffer">
                <!-- Website -->
                <div class="col-md-3 col-sm-3 col-xs-12">
                    <span><strong><@orcid.msg 'manage.developer_tools.group.website'/></strong></span>
                </div>
                <div class="col-md-9 col-sm-9 col-xs-12 dt-website">
                    <p><a href="{{getClientUrl(clientDetails)}}" target="clientDetails.website.value">{{clientDetails.website.value}}</a></p>
                </div>                          
            </div>
            <div class="row bottomBuffer">
                <!-- Description -->
                <div class="col-md-3 col-sm-3 col-xs-12">
                    <span><strong><@orcid.msg 'manage.developer_tools.group.description'/></strong></span>
                </div>
                <div class="col-md-9 col-sm-9 col-xs-12 dt-description">
                    <p>{{clientDetails.shortDescription.value}}</p>                                                     
                </div>                          
            </div>  
            
            <div class="row bottomBuffer">
                <!-- Allow auto deprecate -->
                <div class="col-md-3 col-sm-3 col-xs-12">
                    <span><strong><@orcid.msg 'manage.developer_tools.group.allow_auto_deprecate'/></strong></span>
                </div>
                <div class="col-md-9 col-sm-9 col-xs-12">
                    <p><input type="checkbox" disabled="disabled" class="small-element middle" [(ngModel)]="clientDetails.allowAutoDeprecate.value" /></p>
                </div>                  
            </div>
                            
            <@security.authorize access="hasAnyRole('ROLE_PREMIUM_INSTITUTION', 'ROLE_BASIC_INSTITUTION')">                                                                                 
                <div class="row bottomBuffer">
                    <!-- Custom Emails -->
                    <div class="col-md-3 col-sm-3 col-xs-12">
                        <span><strong><@orcid.msg 'manage.developer_tools.group.custom_emails.th'/></strong></span>
                    </div>
                    <div class="col-md-9 col-sm-9 col-xs-12 dt-description">
                        <p><a href="<@orcid.rootPath "/group/custom-emails" />?clientId={{clientDetails.clientId.value}}" target="Edit custom emails">Edit custom emails</a></p>
                    </div>
                </div>  
            </@security.authorize>
            
            <!-- Slidebox -->
            <div class="slidebox grey-box" *ngIf="expanded == true">
                <div class="row">
                    <!-- Redirect URIS -->                      
                    <div  class="col-md-6 col-sm-6 col-xs-12">
                        <h4><@orcid.msg 'manage.developer_tools.redirect_uri'/>:</h4>
                        <select [(ngModel)]="selectedRedirectUri" (ngModelChange)="updateSelectedRedirectUri()">
                            <option *ngFor="let rUri.value.value of rUri in clientDetails.redirectUris | orderBy:'value.value'">{{rUri.value.value}}</option>
                        </select>
                    </div>
                    <div class="col-md-6 col-sm-6 col-xs-12 bottomBuffer">
                        <h4><@orcid.msg 'manage.developer_tools.view.scope' />:</h4>                            
                        <multiselect multiple="true" [(ngModel)]="selectedScope" options="scope as scope for scope in availableRedirectScopes" change="updateSelectedRedirectUri()"></multiselect>                         
                    </div>                      
                </div>                  
                <!-- Examples -->
                <div *ngIf="playgroundExample != ''">                                                                                 
                    <div class="row">
                        <span class="col-md-3 col-sm-3 col-xs-12"><strong><@orcid.msg 'manage.developer_tools.view.example.authorize'/></strong></span>
                        <span class="col-md-9 col-sm-9 col-xs-12">{{authorizeUrlBase}}</span>
                    </div>
                    <div class="row">
                        <span class="col-md-3 col-sm-3 col-xs-12"></span>
                        <span class="col-md-9 col-sm-9 col-xs-12">
                            <textarea class="input-xlarge authorizeURL" [(ngModel)]="authorizeURL" readonly="readonly" (focus)="inputTextAreaSelectAll($event)"></textarea>
                        </span>
                    </div>
                    <div class="row">
                        <span class="col-md-3 col-sm-3 col-xs-12"><strong><@orcid.msg 'manage.developer_tools.view.example.token'/></strong></span>
                        <span class="col-md-9 col-sm-9 col-xs-12">
                            {{tokenURL}}<br />
                            <@orcid.msg 'manage.developer_tools.view.example.curl' /><a href="<@orcid.msg 'manage.developer_tools.view.example.curl.url' />" target="curlWiki"><@orcid.msg 'manage.developer_tools.view.example.curl.text' /></a>
                        </span>
                    </div>
                    <div class="row">
                        <span class="col-md-3 col-sm-3 col-xs-12"></span>
                        <span class="col-md-9 col-sm-9 col-xs-12">
                            <textarea class="input-xlarge authorizeURL" [(ngModel)]="sampleAuthCurl" readonly="readonly" (focus)="inputTextAreaSelectAll($event)"></textarea>
                        </span>
                    </div>
                </div>
                <!-- Google playground example -->
                <div ng-hide="playgroundExample == ''">
                    <div class="row">
                        <span class="col-md-3 col-sm-3 col-xs-12"><strong><@orcid.msg 'manage.developer_tools.view.example.title'/></strong></span>
                        <span class="col-md-9 col-sm-9 col-xs-12"><a href="{{playgroundExample}}" target="playgroundExample">
                            <span *ngIf="selectedRedirectUri.value.value == googleUri"><@orcid.msg 'manage.developer_tools.view.example.google'/></span>
                            <span *ngIf="selectedRedirectUri.value.value == swaggerUri"><@orcid.msg 'manage.developer_tools.view.example.swagger'/></span>
                            <span *ngIf="selectedRedirectUri.value.value == swaggerMemberUri"><@orcid.msg 'manage.developer_tools.view.example.swagger_member'/></span>
                        </a></span>
                    </div>
                </div>
            </div>
        </div>          
        <!-- Slide button -->
        <div class="row slide" *ngIf="viewing" >
            <div class="col-md-12 col-sm-12 col-xs-12">
                <div class="tab-container" [ngClass]="{'expanded' : expanded == true}">
                    <a class="tab" (click)="expand()" *ngIf="expanded == false"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'common.details.show_details' /></a>
                    <a class="tab" (click)="collapse()" *ngIf="expanded == true"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'common.details.hide_details' /></a>
                </div>
            </div>          
        </div>
        <!-- ---------------- -->
        <!-- Edit credentials -->
        <!-- ---------------- -->
        <div class="edit-client" *ngIf="editing" >    
            <!-- Name -->
            <div class="row">                   
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="inner-row margin-left-fix">
                        <span><strong><@orcid.msg 'manage.developer_tools.group.display_name'/></strong></span>
                        <input type="text" class="input-xlarge" [(ngModel)]="clientToEdit.displayName.value" placeholder="<@orcid.msg 'manage.developer_tools.group.display_name_placeholder'/>"/>
                        <span class="orcid-error" *ngIf="clientToEdit?.displayName?.errors?.length > 0">
                            <div *ngFor='let error of clientToEdit.displayName.errors' [innerHTML]="error"></div>
                        </span>                 
                    </div>      
                </div>                                                              
            </div>
            <!-- Website -->
            <div class="row">   
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="inner-row margin-left-fix">
                        <span><strong><@orcid.msg 'manage.developer_tools.group.website'/></strong></span>
                        <input type="text" class="input-xlarge" [(ngModel)]="clientToEdit.website.value" placeholder="<@orcid.msg 'manage.developer_tools.group.website_placeholder'/>"/>
                        <span class="orcid-error" *ngIf="clientToEdit?.website?.errors?.length > 0">
                            <div *ngFor='let error of clientToEdit.website.errors' [innerHTML]="error"></div>
                        </span>                 
                    </div>      
                </div>  
            </div>
            <!-- Description -->
            <div class="row">                   
                <div class="col-md-12 col-sm-12 col-xs-12 dt-description">
                    <div class="inner-row margin-left-fix">
                        <span><strong><@orcid.msg 'manage.developer_tools.group.description'/></strong></span>
                        <textarea class="input-xlarge" [(ngModel)]="clientToEdit.shortDescription.value" placeholder="<@orcid.msg 'manage.developer_tools.group.description_placeholder'/>"></textarea>                        
                        <span class="orcid-error" *ngIf="clientToEdit?.shortDescription?.errors?.length > 0">
                            <div *ngFor='let error of clientToEdit.shortDescription.errors' [innerHTML]="error"></div>
                        </span>
                    </div>                                                          
                </div>          
            </div>
            <!-- Allow auto deprecate -->
            <div class="row bottomBuffer">
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="inner-row margin-left-fix">
                        <span>
                            <strong class="middle"><@orcid.msg 'manage.developer_tools.group.allow_auto_deprecate' /></strong>
                            <input type="checkbox" class="small-element middle" [(ngModel)]="clientToEdit.allowAutoDeprecate.value" />
                        </span>                                                         
                    </div>                                                          
                </div>
            </div>
            <!-- Client secret -->
            <div class="row bottomBuffer">
                <div class="col-md-3 col-sm-3 col-xs-4">
                    <span><strong><@orcid.msg 'manage.developer_tools.view.secret'/></strong></span>
                </div>
                <div class="col-md-9 col-sm-9 col-xs-8">
                    <span>{{clientToEdit.clientSecret.value}}</span>
                </div>                  
            </div>  
            <!-- Reset client secret button -->
            <div class="row">
                <div class="col-md-3 col-sm-3 col-xs-4">
                    <span></span>
                </div>
                <div class="col-md-9 col-sm-9 col-xs-8">
                    <a class="btn btn-danger" (click)="confirmResetClientSecret()">                                            
                        <@orcid.msg 'manage.developer_tools.edit.reset_client_secret' />
                    </a>
                </div>
            </div>
            <!-- Redirect Uris -->              
            <div *ngFor="let rUri of clientToEdit.redirectUris" class="margin-bottom-box">
                <!-- Header -->
                <div class="row" *ngIf="$first">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <div class="inner-row margin-left-fix">                 
                            <h4><@orcid.msg 'manage.developer_tools.redirect_uri'/></h4>
                        </div>
                    </div>
                </div>
                <!-- Value -->
                <div class="grey-box">
                    <div class="row">                       
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <div class="inner-row margin-left-fix">                         
                                <input type="text" class="input-xlarge ruri" [(ngModel)]="rUri.value.value" placeholder="<@orcid.msg 'manage.developer_tools.group.redirect_uri_placeholder'/>"/>
                                <a (click)="deleteUriOnExistingClient($index)" class="glyphicon glyphicon-trash grey pull-right"></a>
                                <span class="orcid-error" *ngIf="rUri?.errors?.length > 0">
                                    <div *ngFor='let error of rUri.errors' [innerHTML]="error"></div>
                                </span>                                                                                             
                            </div>                                          
                        </div>  
                    </div>                      
                </div>
            </div>                  
            <div class="row">
                <!-- Add redirect uris -->
                <div class="col-md-9 col-sm-9 col-xs-9 add-options">
                    <a class="icon-href-bg" (click)="addUriToExistingClientTable()"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.add_redirect_uri' /></a>
                    <div class="add-options margin-bottom-box" *ngIf="!hideGoogleUri || !hideSwaggerUri || !hideSwaggerMemberUri">                                
                        <div>
                            <h4><@orcid.msg 'manage.developer_tools.test_redirect_uris.title' /></h4>
                            <ul class="pullleft-list">
                                <li *ngIf="!hideGoogleUri" id="google-ruir"><a class="icon-href" (click)="addTestRedirectUri('google','true')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.google'/></a></li>                                     
                                <li *ngIf="!hideSwaggerUri" id="swagger-ruir"><a class="icon-href" (click)="addTestRedirectUri('swagger','true')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.swagger'/></a></li>                                     
                                <li *ngIf="!hideSwaggerMemberUri" id="swagger-member-ruir"><a class="icon-href" (click)="addTestRedirectUri('swagger-member','true')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.swagger_member'/></a></li>                                      
                            </ul>                               
                        </div>
                    </div>                      
                </div>
                <div class="col-md-3 col-sm-3 col-xs-3 sso-api">                
                    <ul class="sso-options pull-right">                         
                        <li><a href="#member_developer_tools_header" (click)="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />"><span class="glyphicon glyphicon-arrow-left"></span></a></li>
                        <li><a (click)="editClient()" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>                            
                    </ul>                   
                </div>      
            </div>      
        </div>
    </div>
</script>