<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#include "includes/affiliate/del_affiliate_inc.ftl"/>

<#include "includes/affiliate/add_affiliate_inc.ftl"/>
<div ng-controller="AffiliationCtrl">
    <!-- Education -->
    <div id="workspace-education" class="workspace-accordion-item workspace-accordion-active" >
        <#include "includes/affiliate/edu_section_header_inc.ftl" />
                <!-- Bulk Edit START -->          
        <div ng-if="bulkEditShow && workspaceSrvc.displayWorks" ng-cloak><!-- bulkEditShow && workspaceSrvc.displayWorks -->
            <div class="bulk-edit">
                <div class="row">
                    <div class="col-md-7 col-sm-7 col-xs-12">
                        <h4><@orcid.msg 'workspace.bulkedit.title'/></h4><span class="hide-bulk" ng-click="toggleBulkEdit()"><@orcid.msg 'workspace.bulkedit.hide'/></span>
                        <ol>
                            <li><@orcid.msg 'workspace.bulkedit.selectWorks'/></li>
                            <li><@orcid.msg 'workspace.bulkedit.selectAction'/></li>
                        </ol>
                    </div>
                    <div class="col-md-5 col-sm-5 col-xs-12">
                        <ul class="bulk-edit-toolbar">

                            <li class="bulk-edit-toolbar-item work-multiple-selector"><!-- Select all -->
                                <label><@orcid.msg 'workspace.bulkedit.select'/></label>
                                <div id="custom-control-x">
                                    <div class="custom-control-x" > 
                                        <div class="dropdown-custom-menu" id="dropdown-custom-menu" ng-click="toggleSelectMenu();$event.stopPropagation()">                   
                                            <span class="custom-checkbox-parent">
                                                <div class="custom-checkbox" id="custom-checkbox" ng-click="swapbulkChangeAll();$event.stopPropagation();" ng-class="{'custom-checkbox-active':bulkChecked}"></div>
                                            </span>                   
                                            <div class="custom-control-arrow" ng-click="toggleSelectMenu(); $event.stopPropagation()"></div>                            
                                        </div>
                                        <div>
                                            <ul class="dropdown-menu" role="menu" id="special-menu" ng-class="{'block': bulkDisplayToggle}">
                                                <li><a ng-click="bulkChangeAll(true)"><@orcid.msg 'workspace.bulkedit.selected.all'/></a></li>
                                                <li><a ng-click="bulkChangeAll(false)"><@orcid.msg 'workspace.bulkedit.selected.none'/></a></li>                                                
                                            </ul>     
                                        </div>
                                    </div>
                                </div>
                            </li>
                            <li class="bulk-edit-toolbar-item"><!-- Privacy control -->
                                <label><@orcid.msg 'workspace.bulkedit.edit'/></label>
                                <div class="bulk-edit-privacy-control">
                                    <@orcid.privacyToggle2 angularModel="groupPrivacy()" 
                                    questionClick=""
                                    clickedClassCheck=""
                                    publicClick="setBulkGroupPrivacy('PUBLIC', $event)" 
                                    limitedClick="setBulkGroupPrivacy('LIMITED', $event)" 
                                    privateClick="setBulkGroupPrivacy('PRIVATE', $event)"/>
                                </div>                      
                            </li>                   
                        </ul>
                        <div class="bulk-edit-delete">
                            <div class="centered">
                                <a ng-click="deleteBulkConfirm()" class="ignore toolbar-button edit-item-button" ng-mouseenter="showTooltip('Bulk-Edit')" ng-mouseleave="hideTooltip('Bulk-Edit')">
                                    <span class="edit-option-toolbar glyphicon glyphicon-trash"></span>
                                </a>
                                <div class="popover popover-tooltip top bulk-edit-popover" ng-if="showElement['Bulk-Edit']">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span><@orcid.msg 'workspace.bulkedit.delete'/></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>              
                </div>              
            </div>
        </div>
        <!-- Bulk Edit END -->     
        <div ng-if="workspaceSrvc.displayEducation" class="workspace-accordion-content">
            <#include "includes/affiliate/edu_body_inc.ftl" />
        </div>
    </div>
    <!-- Employment -->
    <div id="workspace-employment" class="workspace-accordion-item workspace-accordion-active" >
        <#include "includes/affiliate/emp_section_header_inc.ftl" />
                <!-- Bulk Edit START -->          
        <div ng-if="bulkEditShow && workspaceSrvc.displayWorks" ng-cloak><!-- bulkEditShow && workspaceSrvc.displayWorks -->
            <div class="bulk-edit">
                <div class="row">
                    <div class="col-md-7 col-sm-7 col-xs-12">
                        <h4><@orcid.msg 'workspace.bulkedit.title'/></h4><span class="hide-bulk" ng-click="toggleBulkEdit()"><@orcid.msg 'workspace.bulkedit.hide'/></span>
                        <ol>
                            <li><@orcid.msg 'workspace.bulkedit.selectWorks'/></li>
                            <li><@orcid.msg 'workspace.bulkedit.selectAction'/></li>
                        </ol>
                    </div>
                    <div class="col-md-5 col-sm-5 col-xs-12">
                        <ul class="bulk-edit-toolbar">

                            <li class="bulk-edit-toolbar-item work-multiple-selector"><!-- Select all -->
                                <label><@orcid.msg 'workspace.bulkedit.select'/></label>
                                <div id="custom-control-x">
                                    <div class="custom-control-x" > 
                                        <div class="dropdown-custom-menu" id="dropdown-custom-menu" ng-click="toggleSelectMenu();$event.stopPropagation()">                   
                                            <span class="custom-checkbox-parent">
                                                <div class="custom-checkbox" id="custom-checkbox" ng-click="swapbulkChangeAll();$event.stopPropagation();" ng-class="{'custom-checkbox-active':bulkChecked}"></div>
                                            </span>                   
                                            <div class="custom-control-arrow" ng-click="toggleSelectMenu(); $event.stopPropagation()"></div>                            
                                        </div>
                                        <div>
                                            <ul class="dropdown-menu" role="menu" id="special-menu" ng-class="{'block': bulkDisplayToggle}">
                                                <li><a ng-click="bulkChangeAll(true)"><@orcid.msg 'workspace.bulkedit.selected.all'/></a></li>
                                                <li><a ng-click="bulkChangeAll(false)"><@orcid.msg 'workspace.bulkedit.selected.none'/></a></li>                                                
                                            </ul>     
                                        </div>
                                    </div>
                                </div>
                            </li>
                            <li class="bulk-edit-toolbar-item"><!-- Privacy control -->
                                <label><@orcid.msg 'workspace.bulkedit.edit'/></label>
                                <div class="bulk-edit-privacy-control">
                                    <@orcid.privacyToggle2 angularModel="groupPrivacy()" 
                                    questionClick=""
                                    clickedClassCheck=""
                                    publicClick="setBulkGroupPrivacy('PUBLIC', $event)" 
                                    limitedClick="setBulkGroupPrivacy('LIMITED', $event)" 
                                    privateClick="setBulkGroupPrivacy('PRIVATE', $event)"/>
                                </div>                      
                            </li>                   
                        </ul>
                        <div class="bulk-edit-delete">
                            <div class="centered">
                                <a ng-click="deleteBulkConfirm()" class="ignore toolbar-button edit-item-button" ng-mouseenter="showTooltip('Bulk-Edit')" ng-mouseleave="hideTooltip('Bulk-Edit')">
                                    <span class="edit-option-toolbar glyphicon glyphicon-trash"></span>
                                </a>
                                <div class="popover popover-tooltip top bulk-edit-popover" ng-if="showElement['Bulk-Edit']">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span><@orcid.msg 'workspace.bulkedit.delete'/></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>              
                </div>              
            </div>
        </div>
        <!-- Bulk Edit END -->     
        <div ng-if="workspaceSrvc.displayEmployment" class="workspace-accordion-content">
            <#include "includes/affiliate/emp_body_inc.ftl" />
        </div>
    </div>
</div>
    