<div ng-controller="OtherNamesCtrl" class="workspace-section other-names" id="other-names-section">
    <div class="workspace-section-header">
        <div class="workspace-section-title">
            <div class="edit-other-names edit-option" id="open-edit-other-names" ng-click="openEditModal()">                      
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
        <span ng-repeat="otherName in otherNamesForm.otherNames" ng-cloak>
        {{ $last?otherName.content:otherName.content + ", "}}
        </span>
    </div>
</div>