<script type="text/ng-template" id="bio-ng2-template">

    <ng-container *ngIf="userInfo && userInfo.IS_LOCKED === 'true'">
        <div class="alert alert-error readme">
            <p><b id="error_locked"><@orcid.msg 'public-layout.locked'/></b></p>
        </div>   
    </ng-container>

    <ng-container *ngIf="userInfo && !userInfo.PRIMARY_RECORD && userInfo.IS_LOCKED !== 'true' &&  userInfo.IS_DEACTIVATED === 'true'">
            <p class="margin-top-box"><b><@orcid.msg 'public_profile.empty_profile'/></b></p>
    </ng-container>

    <ng-container *ngIf="userInfo && userInfo.PRIMARY_RECORD  && userInfo.IS_LOCKED !== 'true' &&  userInfo.IS_DEACTIVATED === 'true'   ">
        <div class="alert alert-error readme">
            <@orcid.checkFeatureStatus featureName='HTTPS_IDS'>
                <p><b><@orcid.msg 'public_profile.deprecated_account.1'/>&nbsp;<a href="{{baseUrl + '/' + userInfo.PRIMARY_RECORD}}">{{baseUrl + '/' + userInfo.PRIMARY_RECORD}}</a>&nbsp;<@orcid.msg 'public_profile.deprecated_account.2'/></b></p>
            </@orcid.checkFeatureStatus> 
            <@orcid.checkFeatureStatus featureName='HTTPS_IDS' enabled=false> 
                <p><b><@orcid.msg 'public_profile.deprecated_account.1'/>&nbsp;<a href="{{baseUrl + '/' + userInfo.PRIMARY_RECORD}}">{{baseUrl + '/' + userInfo.PRIMARY_RECORD}}</a>&nbsp;<@orcid.msg 'public_profile.deprecated_account.2'/></b></p>
            </@orcid.checkFeatureStatus>
        </div> 
    </ng-container>


    <ng-container *ngIf="userInfo && userInfo.IS_LOCKED !== 'true' && userInfo.IS_DEACTIVATED !== 'true'">

    <#if (peerReviewEmpty)?? && (affiliationsEmpty)?? && (fundingEmpty)?? && (researchResourcesEmpty)?? && (worksEmpty)??>
        <p *ngIf="!bio" class="margin-top-box"><b><@orcid.msg 'public_profile.empty_profile'/></b></p>
    </#if> 
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

    </ng-container>
</script>