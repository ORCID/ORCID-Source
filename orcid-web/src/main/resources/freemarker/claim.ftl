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
                            <div class="relative">
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
                                    <div *ngFor='let error of claim.password.errors' [innerHTML]="error"></div>
                                </span>
                            <div>
                        </div>
                    </script>
                    <claim-ng2></claim-ng2>
                </div>
            </div>
        </div>
    </div>
</@public>