<script type="text/ng-template" id="also-known-as-ng2-template">
    <div class="workspace-section other-names" id="other-names-section">
        <div class="workspace-section-header">
            <div class="workspace-section-title">
                <div class="edit-other-names edit-option" id="open-edit-other-names" (click)="openEditModal()">                      
                    <div class="glyphicon glyphicon-pencil">
                        <div class="popover popover-tooltip top"> 
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <span><@orcid.msg 'manage_bio_settings.editOtherNames' /></span>
                            </div>                
                        </div>                  
                    </div>
                </div>
            <div class="workspace-section-label"><@orcid.msg 'workspace.Alsoknownas'/></div>
            </div>                
        </div>
        <div class="workspace-section-content">
            <span *ngFor="let otherName of formData.otherNames; let index = index; let first = first; let last = last;">
            {{ last?otherName.content:otherName.content + ", "}}
            </span>
        </div>
    </div>
</script>