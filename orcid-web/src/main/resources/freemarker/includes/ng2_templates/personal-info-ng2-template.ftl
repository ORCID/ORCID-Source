<script type="text/ng-template" id="personal-info-ng2-template">
    <!-- /data/orcid/git/ORCID-Source/ORCID-Source/orcid-web/src/main/resources/freemarker/print_public_record.ftl -->
    <div class="workspace-right" >
        <div class="workspace-inner-public workspace-public workspace-accordion">
            <#if (isProfileEmpty)?? && isProfileEmpty>
                <p class="margin-top-box"><b><@orcid.msg 'public_profile.empty_profile'/></b></p>
            <#else>             
                <#if (biography.content)?? && (biography.content)?has_content>                                              
                    <div class="workspace-accordion-content" *ngIf="displayInfo">
                        <div class="row bottomBuffer">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <h3 class="workspace-title">${springMacroRequestContext.getMessage("public_profile.labelBiography")}</h3>
                            </div>
                        </div>          
                        <div class="row bottomBuffer">                  
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="bio-content">${(biography.content)!}</div>                                  
                            </div>
                        </div>                          
                    </div>
                </#if>
                <#assign publicProfile = true />
                <#if !(affiliationsEmpty)??>
                    <!-- Education 
                    <public-edu-affiliation-ng2></public-edu-affiliation-ng2>
                    <!-- Employment 
                    <public-emp-affiliation-ng2></public-emp-affiliation-ng2>
                    -->
                </#if>
                  
                <!-- Funding -->
                <#if !(fundingEmpty)??>
                    <!-- 
                    <public-funding-ng2></public-funding-ng2>
                -->
                </#if>

                <!-- Works -->
                <!--
                <public-works-ng2></public-works-ng2>
                -->
                <!-- Peer Review -->

                <#if !(peerReviewsEmpty)??>
                    <!--
                    <public-peer-reviews-ng2></public-peer-reviews-ng2>
                -->
                </#if>                          
            </#if>
        </div>                     
    </div>

    <!-- /data/orcid/git/ORCID-Source/ORCID-Source/orcid-web/src/main/resources/freemarker/public_profile_v3.ftl -->

    <div class="workspace-right" >
        <div class="workspace-inner-public workspace-public workspace-accordion">
            <#if (isProfileEmpty)?? && isProfileEmpty>
                <p class="margin-top-box"><b><@orcid.msg 'public_profile.empty_profile'/></b></p>
            <#else>             
                <#if (biography.content)?? && (biography.content)?has_content>                                              
                    <div class="workspace-accordion-content" *ngIf="displayInfo">
                        <div class="row bottomBuffer">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <h3 class="workspace-title">${springMacroRequestContext.getMessage("public_profile.labelBiography")}</h3>
                            </div>
                        </div>          
                        <div class="row bottomBuffer">                  
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="bio-content">${(biography.content)!}</div>                                  
                            </div>
                        </div>                          
                    </div>
                </#if>
                <#assign publicProfile = true />
                <!-- ***
                include "workspace_preview_activities_v3.ftl"   
                ***
                -->                        
            </#if>
            <@orcid.checkFeatureStatus 'LAST_MOD'>
                <div id="public-last-modified">
                    <p class="small italic">${springMacroRequestContext.getMessage("public_profile.labelLastModified")} {{lastModifiedDate}}</p>                    
                </div>
            </@orcid.checkFeatureStatus>                   
        </div>
    </div>

    <!-- /data/orcid/git/ORCID-Source/ORCID-Source/orcid-web/src/main/resources/freemarker/workspace_v3.ftl -->

    <div id="workspace-personal" class="workspace-accordion-item workspace-accordion-active" >              
        <div class="workspace-accordion-content" *ngIf="displayInfo">
            <!-- ***
            include "workspace_personal_v3.ftl"
            -->
        </div>
    </div>
</script>