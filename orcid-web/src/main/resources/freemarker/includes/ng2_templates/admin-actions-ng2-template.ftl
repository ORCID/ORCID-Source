<script type="text/ng-template" id="admin-actions-ng2-template">
<div class="workspace-accordion-item" id="switch-user">
    <p>
        <a *ngIf="showSwitchUser" (click)="showSwitchUser = !showSwitchUser"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.switch_user' /></a>
        <a *ngIf="!showSwitchUser" (click)="showSwitchUser = !showSwitchUser"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.switch_user' /></a>
    </p>
    <div class="collapsible bottom-margin-small admin-modal" id="switch_user_section" *ngIf="showSwitchUser">
        <div class="form-group">
            <label for="orcidOrEmail"><@orcid.msg 'admin.switch_user.orcid.label' /></label>
            <input type="text" id="orcidOrEmail" (keyup.enter)="switchUser()" [(ngModel)]="switchId" placeholder="<@orcid.msg 'admin.switch_user.orcid.placeholder' />" class="input-xlarge" />
            <span class="orcid-error" *ngIf="switchUserError">
                <@spring.message "orcid.frontend.web.invalid_switch_orcid"/>
            </span>    
        </div>
        <div class="controls save-btns pull-left">
            <span id="switch-user" (click)="switchUser()" class="btn btn-primary"><@orcid.msg 'admin.switch_user.button'/></span>                     
        </div>
    </div>  
</div>


<div class="workspace-accordion-item" id="find-ids">
    <div ng-controller="findIdsCtrl" class="workspace-accordion-item" ng-cloak>
        <p>
            <a *ngIf="showFindIds" (click)="showFindIds = !showFindIds"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.find_ids' /></a>
            <a *ngIf="!showFindIds" (click)="showFindIds = !showFindIds"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.find_ids' /></a>
        </p>                
        <div class="collapsible bottom-margin-small admin-modal" id="find_ids_section" *ngIf="showFindIds">
            <div class="form-group">
                <label for="emails"><@orcid.msg 'admin.find_ids.label' /></label>
                <input type="text" id="emails" (keyup.enter)="findIds()" [(ngModel)]="emailIds" placeholder="<@orcid.msg 'admin.find_ids.placeholder' />" class="input-xlarge" />
            </div>
            <div class="controls save-btns pull-left">
                <span id="find-ids" (click)="findIds()" class="btn btn-primary"><@orcid.msg 'admin.find_ids.button'/></span>                       
            </div>
        </div>  
</div>
</div>

</script>