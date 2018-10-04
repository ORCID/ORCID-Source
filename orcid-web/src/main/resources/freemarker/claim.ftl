<@public nav="admin_actions">
    <div class="row">
        <div class="col-md-offset-3 col-md-9 col-sm-offset-3 col-sm-9 col-xs-12">
            <h2><@orcid.msg 'claim.claimyourrecord' /></h2>
            <div>
                <h4><@orcid.msg 'claim.almostthere' /></h4>
                <p><@orcid.msg 'claim.completefields' /></p>                           
                <div class="control-group">
                    <script type="text/ng-template" id="claim-ng2-template">
                        <div>
                            <label class="control-label"><@orcid.msg 'claim.password' /></label>
                            <div class="form-group clear-fix">
                                <input type="password" name="password" class="input-xlarge" [(ngModel)]="claim.password.value" (ngModelChange)="serverValidate('Password')"/>
                                <span class="required" [ngClass]="isValidClass(claim.password)">*</span>
                                <div class="popover-help-container" style="display: inline;float: none;">
                                    <i class="glyphicon glyphicon-question-sign"></i>
                                    <div id="name-help" class="popover bottom">
                                        <div class="arrow"></div>
                                        <div class="popover-content">
                                            <p><@orcid.msg 'password_one_time_reset.labelmust8more' /></p>
                                            <ul>
                                                <li><@orcid.msg 'password_one_time_reset.labelatleast09' /></li>
                                                <li><@orcid.msg 'password_one_time_reset.labelatleast1following' />
                                                    <ul>
                                                        <li><@orcid.msg 'password_one_time_reset.labelalphacharacter' /></li>
                                                        <li><@orcid.msg 'password_one_time_reset.labelanyoffollow' /><br /> ! @ # $ % ^ * ( ) ~ {{ '{' }} } [ ] | \ &amp; _ &#96;</li>
                                                    </ul>
                                                </li>
                                                <li>
                                                    <@orcid.msg 'password_one_time_reset.labeloptionallyspace_1' /><br/>
                                                    <@orcid.msg 'password_one_time_reset.labeloptionallyspace_2' />
                                                </li>
                                            </ul>                         
                                            <p><@orcid.msg 'password_one_time_reset.commonpasswords' /><a href="https://github.com/danielmiessler/SecLists/blob/master/Passwords/Common-Credentials/10-million-password-list-top-1000.txt" target="password_one_time_reset.commonpasswordslink"><@orcid.msg 'password_one_time_reset.commonpasswordslink' /></a></p>
                                            <p><strong><@orcid.msg 'password_one_time_reset.examplesunmoon' /></strong></p>
                                        </div>                
                                    </div>
                                </div>
                                <span class="orcid-error" *ngIf="claim.password?.errors?.length > 0">
                                    <div *ngFor="let error of claim.password.errors" [innerHTML]="error"></div>
                                </span>                                                                                                
                            </div>
                        </div>
                        <div class="form-group clear-fix">                            
                            <label class="control-label"><@orcid.msg 'password_one_time_reset.labelconfirmpassword' /></label>
                            <div class="relative">
                                <input type="password" name="confirmPassword" class="input-xlarge" [(ngModel)]="claim.passwordConfirm.value" (ngModelChange)="serverValidate('PasswordConfirm')"/>
                                <span class="required" [ngClass]="isValidClass(claim.passwordConfirm)">*</span>
                                <span class="orcid-error" *ngIf="claim.passwordConfirm.errors.length > 0">
                                    <div *ngFor="let error of claim.passwordConfirm.errors" [innerHTML]="error"></div>
                                </span>
                            </div>                            
                        </div>
                        <div class="form-group clear-fix">  
                            <h4><@orcid.msg 'register.privacy_settings' /></h4>         
                            <p><@orcid.msg 'privacy_preferences.activitiesVisibilityDefault' /></p> 
                            <p><b><@orcid.msg 'privacy_preferences.activitiesVisibilityDefault.who_can_see_this' /></b></p>
                            <div class="visibilityDefault">
                                <div class="radio">
                                  <label><input type="radio" name="defaultVisibility" [(ngModel)]="claim.activitiesVisibilityDefault.visibility" value="PUBLIC" (ngModelChange)="serverValidate('ActivitiesVisibilityDefault')"><span class="public"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.lipublic'/></b> <@orcid.msg 'register.privacy_everyone_text'/></span></label>
                                </div>
                                <div class="radio">
                                  <label><input type="radio" name="defaultVisibility" [(ngModel)]="claim.activitiesVisibilityDefault.visibility" value="LIMITED" (ngModelChange)="serverValidate('ActivitiesVisibilityDefault')"><span class="limited"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.lilimited'/></b> <@orcid.msg 'register.privacy_limited_text'/></span></label>
                                </div>
                                <div class="radio">
                                  <label><input type="radio" name="defaultVisibility" [(ngModel)]="claim.activitiesVisibilityDefault.visibility" value="PRIVATE" (ngModelChange)="serverValidate('ActivitiesVisibilityDefault')"><span class="private"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.liprivate'/></b> <@orcid.msg 'register.privacy_private_text'/></span></label>
                                </div>
                            </div>
                            <div class="visibilityHelp">
                                <span class="required" ng-class="isValidClass(register.activitiesDefaultVisibility)">*</span>
                                <div class="popover-help-container">
                                    <i class="glyphicon glyphicon-question-sign"></i>
                                    <div id="name-help" class="popover bottom">
                                        <div class="arrow"></div>
                                        <div class="popover-content">
                                            <strong><@orcid.msg 'privacyToggle.help.who_can_see' /></strong>
                                            <ul class="privacyHelp">
                                                <li class="public" style="color: #009900;"><@orcid.msg 'privacyToggle.help.everyone' /></li>
                                                <li class="limited"style="color: #ffb027;"><@orcid.msg 'privacyToggle.help.trusted_parties' /></li>
                                                <li class="private" style="color: #990000;"><@orcid.msg 'privacyToggle.help.only_me' /></li>
                                            </ul>
                                            <a href="<@orcid.msg 'common.kb_uri_default'/>360006897614" target="privacyToggle.help.more_information"><@orcid.msg 'privacyToggle.help.more_information' /></a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <span class="orcid-error" *ngIf="claim.activitiesVisibilityDefault.errors.length > 0">
                                <div *ngFor="let error of claim.activitiesVisibilityDefault.errors" [innerHTML]="error"></div>
                            </span>
                        </div>
                        <div class="form-group clear-fix">
                            <h4 class="dark-label"><@orcid.msg 'register.label.notification_settings' /></h4>                
                            <p><@orcid.msg 'register.paragraph.1' /></p>
                            <p><@orcid.msg 'register.paragraph.2' /></p>
                            <div class="control-group">
                                <input id="send-orcid-news" type="checkbox" name="sendOrcidNews" tabindex="9" [(ngModel)]="claim.sendOrcidNews.value" />
                                <label for="send-orcid-news"><@orcid.msg 'manage.email.email_frequency.notifications.news.checkbox.label' /></label>
                            </div>
                            <p><@orcid.msg 'register.paragraph.3' /></p>                            
                        </div>                          
                        
                        
                        
                        
                        
                        
                        
                    </script>
                    <claim-ng2></claim-ng2>
                </div>
            </div>
        </div>
    </div>
</@public>