<script type="text/ng-template" id="admin-actions-ng2-template">
<a name="switch-user"></a>
<div class="workspace-accordion-item" ng-cloak>
    <p>
        <a  [hidden]="showSwitchUser" (click)="showSwitchUser = !showSwitchUser"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.switch_user' /></a>
        <a  [hidden]="!showSwitchUser" (click)="showSwitchUser = !showSwitchUser"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.switch_user' /></a>
    </p>
    <div class="collapsible bottom-margin-small admin-modal" id="switch_user_section" style="display:none;">
        <div class="form-group">
            <label for="orcidOrEmail"><@orcid.msg 'admin.switch_user.orcid.label' /></label>
            <input type="text" id="orcidOrEmail" (keyup.enter)="switchUser()" [(ngModel)]="switchId" placeholder="<@orcid.msg 'admin.switch_user.orcid.placeholder' />" class="input-xlarge" />
        </div>
        <div class="controls save-btns pull-left">
            <span id="switch-user" (click)="switchUser()" class="btn btn-primary"><@orcid.msg 'admin.switch_user.button'/></span>                     
        </div>
    </div>  
</div>
</script>