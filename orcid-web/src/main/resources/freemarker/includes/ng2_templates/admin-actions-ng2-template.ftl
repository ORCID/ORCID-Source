<script type="text/ng-template" id="admin-actions-ng2-template">
    <!-- Switch user -->
    <div class="workspace-accordion-item" id="switch-user">
        <p>
            <a *ngIf="showSwitchUser" (click)="showSwitchUser = false"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.switch_user' /></a>
            <a *ngIf="!showSwitchUser" (click)="showSwitchUser = true"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.switch_user' /></a>
        </p>
        <div class="collapsible bottom-margin-small admin-modal" id="switch_user_section" *ngIf="showSwitchUser">
            <div class="form-group">
                <label for="orcidOrEmail"><@orcid.msg 'admin.switch_user.orcid.label' /></label>
                <input type="text" id="orcidOrEmail" [(ngModel)]="switchId" (keyup.enter)="switchUser(switchId)" placeholder="<@orcid.msg 'admin.switch_user.orcid.placeholder' />" class="input-xlarge" />
                <span class="orcid-error" *ngIf="switchUserError">
                    <@spring.message "orcid.frontend.web.invalid_switch_orcid"/>
                </span>    
            </div>
            <div class="controls save-btns pull-left">
                <span id="switch-user" (click)="switchUser(switchId)" class="btn btn-primary"><@orcid.msg 'admin.switch_user.button'/></span>                     
            </div>
        </div>  
    </div>
</script>
