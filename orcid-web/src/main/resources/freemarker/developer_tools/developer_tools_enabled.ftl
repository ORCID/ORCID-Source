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
                        <li><a (click)="createCredentials();" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>                                                                                                
                    </ul>                   
                </div>  
            </div>              
        </div>
        <div class="row slide" *ngIf="client.clientSecret && client.clientSecret.value && !editing" ng-cloak>
            <div class="col-md-12 col-sm-12 col-xs-12">
                <div class="tab-container" ng-class="{'expanded' : expanded == true}">
                    <a class="tab" (click)="expand()" *ngIf="expanded == false"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'common.details.show_details' /></a>
                    <a class="tab" (click)="collapse()" *ngIf="expanded == true"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'common.details.hide_details' /></a>
                </div>
            </div>          
        </div>
    </div>
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    <!-- View client -->
    <div *ngIf="!showForm">
    </div>
    
</div>