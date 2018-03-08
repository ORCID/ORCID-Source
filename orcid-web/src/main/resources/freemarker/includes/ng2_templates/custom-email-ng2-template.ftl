<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->

<script type="text/ng-template" id="custom-email-ng2-template">
    <div>    
        <!-- Top content, instructions -->
        <div class="row">               
            <div class="col-md-10 col-sm-10 col-xs-8">
                <div>
                    <h2><@orcid.msg 'custom_email.template.title' /></h2>
                </div>                  
            </div>
            <div class="col-md-2 col-sm-2 col-xs-4" *ngIf="showCreateButton" >
                <a (click)="displayCreateForm()" class="pull-right"><span class="label btn-primary"><@orcid.msg 'custom_email.template.create_button' /></span></a>
            </div>  
        </div>              
        <div class="row bottom-line">
            <div class="col-md-12 col-sm-12 col-xs-12 instructions">                
                <p><@orcid.msg 'custom_email.template.description.1'/></p>
                <p><@orcid.msg 'custom_email.template.description.2'/></p>                                      
            </div>                  
        </div>
        
        <!-- Show existing emails -->
        <div class="row view bottom-line" *ngIf="showEmailList">              
            <div class="col-md-12 col-sm-12 col-xs-12 small-padding">   
                <h3><@orcid.msg 'custom_email.template.existing_custom_emails.title' /></h3>                    
            </div>
            
            <div class="row" *ngFor="let existingEmail of customEmailList">  
                <div class="inner-row email-list" *ngIf="$first">             
                    <div class="col-md-1 col-sm-1 col-xs-1 list-header"><@orcid.msg 'custom_email.custom_emails.header.type' /></div>
                    <div class="col-md-9 col-sm-9 col-xs-9 list-header"><@orcid.msg 'custom_email.custom_emails.header.subject' /></div>
                    <div class="col-md-1 col-sm-1 col-xs-1 list-header">&nbsp;</div>
                    <div class="col-md-1 col-sm-1 col-xs-1 list-header">&nbsp;</div>
                </div>              
                <div class="inner-row email-list">              
                    <div class="col-md-1 col-sm-1 col-xs-1">{{existingEmail.emailType.value}}</div>
                    <div class="col-md-9 col-sm-9 col-xs-9">{{existingEmail.subject.value}}</div>
                    <div class="col-md-1 col-sm-1 col-xs-1"><a href (click)="showEditLayout($index)" class="edit" title="<@orcid.msg 'custom_email.common.edit' />"><span class="glyphicon glyphicon-pencil blue"></span></a></div>
                    <div class="col-md-1 col-sm-1 col-xs-1"><a href (click)="confirmDeleteCustomEmail($index)" class="edit" title="<@orcid.msg 'custom_email.common.remove' />"><span class="glyphicon glyphicon-trash blue"></span></a></div>
                </div>
            </div>
        </div>
        
        <!-- Create form -->
        <div *ngIf="showCreateForm" class="create bottom-line">   
            <div class="col-md-12 col-sm-12 col-xs-12 small-padding">   
                <h3><@orcid.msg 'custom_email.template.create_custom_emails.title' /></h3>                  
            </div>      
            
            <div class="row">
                <!-- Sender -->
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="inner-row">
                        <span><strong><@orcid.msg 'custom_email.template.create.sender.label'/></strong></span>
                        <input type="text" placeholder="<@orcid.msg 'custom_email.template.create.sender.placeholder'/>" class="input-xlarge" [(ngModel)]="customEmail.sender.value">
                        <span class="orcid-error" *ngIf="customEmail?.sender?.errors?.length > 0">
                            <div *ngFor='let error of customEmail.sender.errors' [innerHTML]="error"></div>
                        </span>
                    </div>                      
                </div>
                <!-- Subject -->
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="inner-row">
                        <span><strong><@orcid.msg 'custom_email.template.create.subject.label'/></strong></span>
                        <input type="text" placeholder="<@orcid.msg 'custom_email.template.create.subject.placeholder'/>" class="input-xlarge" [(ngModel)]="customEmail.subject.value">
                        <span class="orcid-error" *ngIf="customEmail.subject.errors.length > 0">
                            <div *ngFor='let error of customEmail?.subject?.errors' [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <!-- Content -->
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="inner-row content">
                        <span><strong><@orcid.msg 'custom_email.template.create.content.label'/></strong></span>
                        <textarea placeholder="<@orcid.msg 'custom_email.template.create.content.placeholder'/>" [(ngModel)]="customEmail.content.value"></textarea>
                        <span class="orcid-error" *ngIf="customEmail.content.errors.length > 0">
                            <div *ngFor='let error of customEmail?.content?.errors' [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <!-- Is Html -->
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="row">
                        <div class="inner-row">                         
                            <div class="col-md-2 col-sm-2 col-xs-2"><strong><@orcid.msg 'custom_email.template.create.is_html'/></div>
                            <div class="col-md-10 col-sm-10 col-xs-10"><input type="checkbox" [(ngModel)]="customEmail.html"></div>
                        </div>                          
                    </div>
                </div>
                <!-- Actions -->
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="inner-row content">             
                        <ul class="pull-right actions">                         
                            <li><a href (click)="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />"><span class="glyphicon glyphicon-arrow-left"></span></a></li>
                            <li><a href (click)="saveCustomEmail()" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>                           
                        </ul>                   
                    </div>
                </div>  
            </div>
        </div>

        <!-- Edit form -->              
        <div *ngIf="showEditForm" class="edit bottom-line">       
            <div class="col-md-12 col-sm-12 col-xs-12 small-padding">   
                <h3><@orcid.msg 'custom_email.template.edit_custom_emails.title' /></h3>                    
            </div>  
            <div class="row">
                <!-- Sender -->
                <div class="col-md-10 col-sm-10 col-xs-12">
                    <div class="inner-row">
                        <span><strong><@orcid.msg 'custom_email.template.create.sender.label'/></strong></span>
                        <input type="text" placeholder="<@orcid.msg 'custom_email.template.create.sender.placeholder'/>" class="input-xlarge" [(ngModel)]="editedCustomEmail.sender.value">
                        <span class="orcid-error" *ngIf="editedCustomEmail?.sender?.errors?.length > 0">
                            <div *ngFor='let error of editedCustomEmail.sender.errors' [innerHTML]="error"></div>
                        </span>
                    </div>                      
                </div>
                <!-- Subject -->
                <div class="col-md-10 col-sm-10 col-xs-12">
                    <div class="inner-row">
                        <span><strong><@orcid.msg 'custom_email.template.create.subject.label'/></strong></span>
                        <input type="text" placeholder="<@orcid.msg 'custom_email.template.create.subject.placeholder'/>" class="input-xlarge" [(ngModel)]="editedCustomEmail.subject.value">
                        <span class="orcid-error" *ngIf="editedCustomEmail.subject.errors.length > 0">
                            <div *ngFor='let error of editedCustomEmail?.subject?.errors' [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <!-- Content -->
                <div class="col-md-10 col-sm-10 col-xs-12">
                    <div class="inner-row content">
                        <span><strong><@orcid.msg 'custom_email.template.create.content.label'/></strong></span>
                        <textarea placeholder="<@orcid.msg 'custom_email.template.create.content.placeholder'/>" [(ngModel)]="editedCustomEmail.content.value"></textarea>
                        <span class="orcid-error" *ngIf="editedCustomEmail?.content?.errors?.length > 0">
                            <div *ngFor='let error of editedCustomEmail.content.errors' [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <!-- Is Html -->
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="row">
                        <div class="inner-row">                         
                            <div class="col-md-2 col-sm-2 col-xs-2"><strong><@orcid.msg 'custom_email.template.create.is_html'/></div>
                            <div class="col-md-10 col-sm-10 col-xs-10"><input type="checkbox" [(ngModel)]="editedCustomEmail.html"></div>
                        </div>                          
                    </div>
                </div>
                <!-- Actions -->
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="inner-row content">             
                        <ul class="pull-right actions">                         
                            <li><a href (click)="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />"><span class="glyphicon glyphicon-arrow-left"></span></a></li>
                            <li><a href (click)="editCustomEmail()" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>                           
                        </ul>                   
                    </div>
                </div>  
            </div>
        </div>          
        
        <!-- Learn more -->         
        <div class="row learn-more">
            <div class="col-md-12 col-sm-12 col-xs-12">                 
                <p> 
                    <@orcid.msg 'custom_email.template.description.learn_more.1'/>
                    <a href="<@orcid.msg 'custom_email.template.description.learn_more.link.url'/>" target="custom_email.template.description.learn_more.link.text"><@orcid.msg 'custom_email.template.description.learn_more.link.text'/></a>
                    <@orcid.msg 'custom_email.template.description.learn_more.2'/>
                </p>                            
            </div>
        </div>
            
    </div>
</script>