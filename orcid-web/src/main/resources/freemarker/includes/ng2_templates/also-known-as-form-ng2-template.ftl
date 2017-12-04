<script type="text/ng-template" id="edit-aka">  
    <!-- Other Names -->    
    <div class="lightbox-container" id="aka-popover">
        <div class="edit-record edit-record-bulk-edit edit-aka">
            <!-- Title -->
            <div class="row">           
                <div class="col-md-12 col-sm-12 col-xs-12"> 
                    <h1 class="lightbox-title pull-left">
                        <@orcid.msg 'manage_bio_settings.editOtherNames'/>
                    </h1>
                </div>          
            </div>
                
                <div class="row bottomBuffer">                          
                    <div ng-include="'bulk-edit'"></div>                    
                </div>              
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12 padding-right-reset">
                        <span class="right"><@orcid.msg 'groups.common.edit_individual_privacy' /></span>   
                    </div>
                </div>      

            <div class="row">
                <div class="col-md-12 col-xs-12 col-sm-12" style="position: static">
                    <div class="fixed-area" scroll>
                        <div class="scroll-area">
                           <div class="row aka-row" ng-repeat="otherName in otherNamesForm.otherNames" ng-cloak>                                                            
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <div class="aka">       
                                        <input type="text" ng-model="otherName.content" ng-if="otherName.source == orcidId"  focus-me="newInput" />                                                                             
                                        <span ng-bind="otherName.content" ng-if="otherName.source != orcidId && otherName.source != null"></span>                                       
                                    </div>                                      
                                    <div class="source" ng-if="otherName.sourceName || otherName.sourceName == null"><@orcid.msg 'manage_bio_settings.source'/>: <span ng-if="otherName.sourceName">{{otherName.sourceName}}</span><span ng-if="otherName.sourceName == null">{{orcidId}}</span></div>
                                </div>                          
                                <div class="col-md-6 col-sm-6 col-xs-12" style="position: static">                                                                                                                          
                                    <ul class="record-settings pull-right">
                                        <li>
                                            <div class="glyphicon glyphicon-arrow-up circle" ng-click="$first || swapUp($index)" ng-mouseover="commonSrvc.showTooltip('tooltip-aka-move-up-'+$index, $event, 37, -33, 44)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-aka-move-up-'+$index)"></div>                                         
                                            <@orcid.tooltip elementId="'tooltip-aka-move-up-'+$index" message="common.modals.move_up"/>
                                        </li>
                                        <li>                                                                                        
                                            <div class="glyphicon glyphicon-arrow-down circle" ng-click="$last || swapDown($index)" ng-mouseover="commonSrvc.showTooltip('tooltip-aka-move-down-'+$index, $event, 37, -2, 53)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-aka-move-down-'+$index)"></div>
                                            <@orcid.tooltip elementId="'tooltip-aka-move-down-'+$index" message="common.modals.move_down" />                                            
                                        </li>
                                        <li>
                                            <div class="glyphicon glyphicon-trash" ng-click="deleteOtherName(otherName)" ng-mouseover="commonSrvc.showTooltip('tooltip-aka-delete-'+$index, $event, 37, 50, 39)" ng-mouseleave="commonSrvc.hideTooltip('tooltip-aka-delete-'+$index)"></div>
                                            <@orcid.tooltip elementId="'tooltip-aka-delete-'+$index" message="common.modals.delete" />
                                        </li>
                                        <li>
                                            <@orcid.privacyToggle3  angularModel="otherName.visibility.visibility"
                                                questionClick="toggleClickPrivacyHelp($index)"
                                                clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
                                                publicClick="setPrivacyModal('PUBLIC', $event, otherName)" 
                                                limitedClick="setPrivacyModal('LIMITED', $event, otherName)" 
                                                privateClick="setPrivacyModal('PRIVATE', $event, otherName)"
                                                elementId="$index" />   
                                        </li>
                                    </ul>
                                    <span class="created-date pull-right" ng-show="otherName.createdDate" ng-class="{'hidden-xs' : otherName.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{otherName.createdDate.year + '-' + otherName.createdDate.month + '-' + otherName.createdDate.day}}</span>
                                    <span class="created-date pull-left" ng-show="otherName.createdDate" ng-class="{'visible-xs' : otherName.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{otherName.createdDate.year + '-' + otherName.createdDate.month + '-' + otherName.createdDate.day}}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="record-buttons">
                        <a ng-click="addNewModal()"><span class="glyphicon glyphicon-plus pull-left">
                            <div class="popover popover-tooltip-add top">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <span><@orcid.msg 'common.modals.add' /></span>
                                </div>
                            </div> 
                        </span></a>                         
                        <button class="btn btn-primary pull-right" ng-click="setOtherNamesForm()"><@spring.message "freemarker.btnsavechanges"/></button>                           
                        <a class="cancel-option pull-right" ng-click="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
                    </div>                  
                </div>
            </div>
        </div>
    </div>      
</script>