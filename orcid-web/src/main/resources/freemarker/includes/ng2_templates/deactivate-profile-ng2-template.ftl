<script type="text/ng-template" id="deactivate-profile-ng2-template">
    <div class="workspace-accordion-item" >
        <p>
            <a  *ngIf="showSection" (click)="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.profile_deactivation' /></a>
            <a  ng-hide="showSection" (click)="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.profile_deactivation' /></a>
        </p>                
        
        <div class="collapsible bottom-margin-small admin-modal" id="deactivation_modal" style="display:none;">
            <div class="alert alert-success" *ngIf="result?.deactivateSuccessfulList?.length || result?.notFoundList?.length || result?.alreadyDeactivatedList?.length" style="overflow-x:auto;">
                <div *ngIf="result?.deactivateSuccessfulList?.length"><@spring.message "admin.profile_deactivation.deactivation_success"/>
                    <br>{{result.deactivateSuccessfulList}}
                </div>
                <div *ngIf="result?.alreadyDeactivatedList?.length"><br><@spring.message "admin.profile_deactivation.already_deactivated"/>
                    <br>{{result.alreadyDeactivatedList}}
                </div>
                <div *ngIf="result?.notFoundList?.length"><br><@spring.message "admin.profile_deactivation.not_found"/>
                    <br>{{result.notFoundList}}
                </div>
            </div>
            <div class="control-group">
                <label for="orcid_to_deactivate" class="control-label"><@orcid.msg 'admin.profile_deactivation.to_deactivate' /></label>
                <div class="controls">
                    <input type="text" id="orcid_to_deactivate" class="input-xlarge" [(ngModel)]="orcidsToDeactivate" placeholder="<@orcid.msg 'admin.profile_deactivation.placeholder.to_deactivate' />" />
                </div>
                <span class="btn btn-primary" (click)="deactivateOrcids()"><@orcid.msg 'admin.profile_deactivation.deactivate_account'/></span>
            </div>
        </div>
    </div>
</script>