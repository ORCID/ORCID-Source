<script type="text/ng-template" id="emails-ng2-template">
    <div class="workspace-section">
        <div class="workspace-section-header">
            <div class="workspace-section-title">                  
                <div class="edit-websites edit-option popover-help-container" (click)="openEditModal()" aria-label="<@orcid.msg 'common.edit' />">
                    <div class="glyphicon glyphicon-pencil" >
                    </div>
                    <div class="popover top tooltip-edit tooltip-text">
                        <div class="arrow"></div>
                        <div class="popover-content">
                            <span><@orcid.msg 'groups.common.edit_my' /></span>
                        </div>
                    </div>
                </div>
                <h3 class="workspace-section-label"><@orcid.msg 'manage.emails'/></h3>
            </div>
        </div> 
        <div class="workspace-section-content">
            <div *ngFor="let email of formData.emails | orderBy:'value'" class="mobile-box emails-box">
                <span >{{email.value}}</span>
            </div>
        </div>
    </div>
</script>