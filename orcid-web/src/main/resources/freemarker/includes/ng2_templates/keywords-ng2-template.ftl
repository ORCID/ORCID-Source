<script type="text/ng-template" id="keywords-ng2-template">
    <div class="workspace-section keywords">
        <div class="workspace-section-header">
            <div class="workspace-section-title">
                <div id="open-edit-keywords" class="edit-keywords edit-option" (click)="openEditModal()">
                    <div class="glyphicon glyphicon-pencil">
                        <div class="popover popover-tooltip top">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <span><@orcid.msg 'manage_bio_settings.editKeywords' /></span>
                            </div>                
                        </div>
                    </div>
                </div>
                <div class="workspace-section-label"><@orcid.msg 'public_profile.labelKeywords'/></div>
            </div>
            <div class="workspace-section-content">
                <span *ngFor="let keyword of formData.keywords; let index = index; let first = first; let last = last;">
                {{ last?keyword.content:keyword.content+ ", "}}
                </span>
            </div>
        </div>
    </div>
</script>