<div class="workspace-accordion-header clearfix">
    <div class="row">
        <div class="col-md-3 col-sm-3 col-xs-12">
            <a name='workspace-educations' />
            <a href="" ng-click="workspaceSrvc.toggleEducation($event)" ng-click="workspaceSrvc.toggleEducation($event)" class="toggle-text">
                <i class="glyphicon-chevron-down glyphicon x075" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayEducation==false}"></i>
                <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.education'/> (<span ng-bind="affiliationsSrvc.educations.length"></span>)
            </a>
            <#if !(isPublicProfile??)> 
                <div class="popover-help-container">
                    <i class="glyphicon glyphicon-question-sign"></i>
                    <div id="education-help" class="popover bottom">
                        <div class="arrow"></div>
                        <div class="popover-content">
                            <p><@orcid.msg 'manage_affiliations_settings.helpPopoverEducation'/> <a href="<@orcid.msg 'common.kb_uri_default'/>360006971493" target="manage_affiliations_settings.helpPopoverEducation"><@orcid.msg 'common.learn_more'/></a></p>
                        </div>
                    </div>
                </div>  
            </#if>
        </div>
        <div class="col-md-9 col-sm-9 col-xs-12 action-button-bar" ng-if="workspaceSrvc.displayEducation">
            <#include "../workspace/workspace_act_sort_menu.ftl"/>                    
            <#if !(isPublicProfile??)>
                <ul class="workspace-bar-menu">                         
                    <!-- Link Manually -->
                    <li class="hidden-xs">                  
                        <div class="menu-container" id="add-education-container">
                            <ul class="toggle-menu">
                                <li ng-class="{'green-bg' : showBibtexImportWizard == true}">       
                                    <span class="glyphicon glyphicon-plus"></span>
                                    <@orcid.msgCapFirst 'manual_affiliation_form_contents.add_education'/>    
                                    <ul class="menu-options education">                         
                                        <!-- Add Manually -->
                                        <li>          
                                            <a id="add-education" href="" class="action-option manage-button two-options" ng-click="addAffiliationModal('education')">
                                                <span class="glyphicon glyphicon-plus"></span>
                                                <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                            </a>
                                       </li>
                                    </ul>
                                 </li>
                          </ul>
                        </div>         
                    </li>
                    <!-- Mobile Workaround -->                    
                    <li class="hidden-md hidden-sm visible-xs-inline">          
                       <a href="" class="action-option manage-button two-options" ng-click="addAffiliationModal('education')">
                           <span class="glyphicon glyphicon-plus"></span>
                           <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                       </a>
                   </li>                                            
                </ul>
            </#if>
        </div>
    </div>
</div>
