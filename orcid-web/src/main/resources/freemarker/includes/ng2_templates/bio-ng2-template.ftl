<script type="text/ng-template" id="bio-ng2-template">
    <div *ngIf="bio" class="workspace-accordion-content">
        <div class="row bottomBuffer">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <h3 class="workspace-title">${springMacroRequestContext.getMessage("public_profile.labelBiography")}</h3>
            </div>
        </div>          
        <div class="row bottomBuffer">                  
            <div class="col-md-12 col-sm-12 col-xs-12">
                <div class="bio-content">{{bio}}</div>                                  
            </div>
        </div>                          
    </div>
</script>