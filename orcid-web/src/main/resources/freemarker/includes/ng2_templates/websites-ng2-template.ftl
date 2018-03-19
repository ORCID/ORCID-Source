<script type="text/ng-template" id="websites-ng2-template">  
    <!-- Websites  -->          
    <div class="workspace-section websites">
        <div class="workspace-section-header">
            <div class="workspace-section-title">
                <div id="open-edit-websites" class="edit-websites edit-option" (click)="openEditModal()">
                    <div class="glyphicon glyphicon-pencil">
                        <div class="popover popover-tooltip top">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <span><@orcid.msg 'manage_bio_settings.editWebsites' /></span>
                            </div>                
                        </div>
                    </div>         
                </div>
                <div class="workspace-section-label"><@orcid.msg 'public_profile.labelWebsites'/></div>
            </div>
        </div>  
        <div class="workspace-section-content">
            <div *ngFor="let website of formData.websites" class="wrap">
                <a href="{{website.url.value}}" target="website.urlName" rel="me nofollow">{{website.urlName != null? website.urlName : website.url.value}}</a>
            </div>
        </div>
    </div>
</script>
