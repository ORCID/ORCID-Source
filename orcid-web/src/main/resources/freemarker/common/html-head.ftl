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
<head>
    <meta charset="utf-8" />
    <title>${title!"ORCID"}</title>
    <meta name="description" content="">
    <meta name="author" content="ORCID">
    <meta name="_csrf" content="${(_csrf.token)!}"/>
    <meta name="_csrf_header" content="${(_csrf.headerName)!}"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    
    <#if (noIndex)??>
    <meta name="googlebot" content="noindex">
    <meta name="robots" content="noindex">
    <meta name="BaiduSpider" content="noindex">
    </#if>
    
    <#include "/layout/google_analytics.ftl">
    
    <script type="text/javascript">
        var orcidVar = {};
        orcidVar.recaptchaKey = '${recaptchaWebKey}';
        orcidVar.baseDomainRmProtocall = '${baseDomainRmProtocall}';
        orcidVar.baseUri = '${baseUri}';
        orcidVar.baseUriHttp = '${baseUriHttp}';
        orcidVar.pubBaseUri = '${pubBaseUri}';
        
        <#if (workIdsJson)??>
        orcidVar.workIds = JSON.parse("${workIdsJson}");
        </#if>
      
        <#if (affiliationIdsJson)??>
        orcidVar.affiliationIdsJson = JSON.parse("${affiliationIdsJson}");
        </#if>
      
        <#if (fundingIdsJson)??>
        orcidVar.fundingIdsJson = JSON.parse("${fundingIdsJson}");
        </#if>
      
        <#if (peerReviewIdsJson)??>       
        orcidVar.PeerReviewIds = JSON.parse("${peerReviewIdsJson}");
        </#if>      
      
        <#if (showLogin)??>
        orcidVar.showLogin = ${showLogin};
        </#if>

        orcidVar.orcidId = '${(effectiveUserOrcid)!}';
        orcidVar.lastModified = '${(lastModifiedTime)!}';
        orcidVar.orcidIdHash = '${(orcidIdHash)!}';
        orcidVar.realOrcidId = '${realUserOrcid!}';
        orcidVar.jsMessages = JSON.parse("${jsMessagesJson}");
        orcidVar.searchBaseUrl = "${searchBaseUrl}";
        orcidVar.isPasswordConfirmationRequired = ${isPasswordConfirmationRequired?c};
        orcidVar.emailVerificationManualEditEnabled = ${emailVerificationManualEditEnabled?c};
        orcidVar.version = "${ver}";
        orcidVar.knowledgeBaseUri = "${knowledgeBaseUri}";
      
        <#if (oauth2Screens)??>
        orcidVar.oauth2Screens = true;
        <#else>
        
        orcidVar.oauth2Screens = false;
        </#if>
      
        <#if (originalOauth2Process)??>
        orcidVar.originalOauth2Process = true;
        <#else>
        orcidVar.originalOauth2Process = false;
        </#if>     
      
        orcidVar.oauthUserId = "${(oauth_userId?js_string)!}";
        orcidVar.memberSlug = "${(memberSlug?js_string)!}";
    </script>

    <#include "/macros/orcid_ga.ftl">
    
    <link rel="stylesheet" href="${staticLoc}/css/noto-sans-googlefonts.css?v=${ver}"/> <!-- Src: //fonts.googleapis.com/css?family=Noto+Sans:400,700 -->
    <link rel="stylesheet" href="${staticLoc}/css/glyphicons.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticLoc}/css/social.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticLoc}/css/filetypes.css?v=${ver}"/>    
    
    <!-- Always remember to remove Glyphicons font reference when bootstrap is updated -->
    <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.3.6/css/bootstrap.min.css?v=${ver}"/>
    
    <#if locale?? && (locale == 'rl' || locale == 'ar' )>
    <!-- just a prototype to show what RTL, expect to switch the cdn to ours -->
    <!-- Load Bootstrap RTL theme from RawGit -->
    <link rel="stylesheet" href="${staticCdn}/css/bootstrap-rtl.min.css?v=${ver}"> <!-- Src: //cdn.rawgit.com/morteza/bootstrap-rtl/v3.3.4/dist/css/bootstrap-rtl.min.css -->
    </#if>

    <link rel="stylesheet" href="${staticCdn}/css/orcid.new.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticCdn}/css/idpselect.css" />
    
    <#if springMacroRequestContext.requestUri?contains("/print")>
    <link rel="stylesheet" href="${staticCdn}/css/orcid-print.css"/>
    </#if>

    <link rel="stylesheet" href="${staticCdn}/css/jquery-ui-1.10.0.custom.min.css?v=${ver}"/>
    
    <!-- this is a manually patched version, we should update when they accept our changes -->
    <script src="${staticCdn}/javascript/respond.src.js?v=${ver}"></script>
    
    <!-- Respond.js proxy on external server -->
    <link href="${staticCdn}/html/respond-proxy.html" id="respond-proxy" rel="respond-proxy" />
    <link href="${staticCdn}/img/respond.proxy.gif" id="respond-redirect" rel="respond-redirect" />
    <script src="${staticCdn}/javascript/respond.proxy.js"></script>
        
    <style type="text/css">
        /* 
        Allow angular.js to be loaded in body, hiding cloaked elements until 
        templates compile.  The !important is important given that there may be 
        other selectors that are more specific or come later and might alter display.  
         */
        [ng\:cloak], [ng-cloak], .ng-cloak {
            display: none !important;
        }
    </style>    

    <link rel="shortcut icon" href="${staticCdn}/img/favicon.ico"/>
    <link rel="apple-touch-icon" href="${staticCdn}/img/apple-touch-icon.png" />  
    <link rel="stylesheet" href="${staticLoc}/css/noto-font.css?v=${ver}"/> 

    <!-- ***************************************************** -->
    <!-- Ng2 Templates - BEGIN -->

    <#if springMacroRequestContext.requestUri?contains("/my-orcid") >
    <script type="text/ng-template" id="affiliation-ng2-template">
        <div>
            <div id="workspace-education" class="workspace-accordion-item workspace-accordion-active">
                <div class="workspace-accordion-header clearfix">
                    <div class="row">
                        <div class="col-md-3 col-sm-3 col-xs-12">
                            <a name='workspace-educations'></a>
                            <a href="" (click)="workspaceSrvc.toggleEducation($event)" class="toggle-text">
                                <i
                                    class="glyphicon-chevron-down glyphicon x075"
                                    [ngClass]="{ 'glyphicon-chevron-right': workspaceSrvc.displayEducation==false }"
                                ></i>
                                    
                                <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.education'/> (<span>{{affiliationsSrvc.educations.length}}</span>)
                            </a>
                            
                            <#if !(isPublicProfile??)> 
                            <div class="popover-help-container">
                                <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                                <div id="education-help" class="popover bottom">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <p><@orcid.msg 'manage_affiliations_settings.helpPopoverEducation'/> <a href="${knowledgeBaseUri}/articles/1807522" target="manage_affiliations_settings.helpPopoverEducation"><@orcid.msg 'common.learn_more'/></a></p>
                                    </div>
                                </div>
                            </div>  
                            </#if>

                        </div>
                        <div class="col-md-9 col-sm-9 col-xs-12 action-button-bar" *ngIf="workspaceSrvc.displayEducation">
                            
                            <#escape x as x?html>
                            
                            <div class="menu-container">   
                                <ul class="toggle-menu">
                                    <li>
                                        <span class="glyphicon glyphicon-sort"></span>                          
                                        <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                        <ul class="menu-options sort">
                                            <li [ngClass]="{'checked':sortState.predicateKey=='date'}" *ngIf="!sortHideOption">                                         
                                                <a (click)="sort('date');" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_date'/>
                                                    <span *ngIf="sortState.reverseKey['date']" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='date'}"></span>
                                                    <span *ngIf="sortState.reverseKey['date'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='date'}"></span>
                                                </a>                       
                                            </li>
                                            <li [ngClass]="{'checked':sortState.predicateKey=='groupName'}" *ngIf="!sortHideOption == null">
                                                <a (click)="sort('groupName');" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                    <span *ngIf="sortState.reverseKey['groupName']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='groupName'}" ></span>
                                                    <span *ngIf="sortState.reverseKey['groupName'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='groupName'}" ></span>
                                                </a>                                            
                                            </li>
                                            <li [ngClass]="{'checked':sortState.predicateKey=='title'}" *ngIf="!sortHideOption">                                            
                                                <a (click)="sort('title');" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                    <span *ngIf="sortState.reverseKey['title']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='title'}" ></span>
                                                    <span *ngIf="sortState.reverseKey['title'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='title'}" ></span>
                                                </a>
                                            </li>
                                            <li *ngIf="sortState.type != 'affiliation'" [ngClass]="{'checked':sortState.predicateKey=='type'}" *ngIf="!sortHideOption">
                                                <a (click)="sort('type');" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_type'/>
                                                    <span *ngIf="sortState.reverseKey['type']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='type'}"></span>
                                                    <span *ngIf="sortState.reverseKey['type'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='type'}"></span>
                                                </a>
                                            </li>
                                        </ul>
                                    </li>
                                </ul>                                   
                            </div>
                            </#escape>               
                            
                            <#if !(isPublicProfile??)>
                            <ul class="workspace-bar-menu">      
                                <li class="hidden-xs">                  
                                    <div class="menu-container" id="add-education-container">
                                        <ul class="toggle-menu">
                                            <li [ngClass]="{'green-bg' : showBibtexImportWizard == true}">       
                                                <span class="glyphicon glyphicon-plus"></span>
                                                <@orcid.msgCapFirst 'manual_affiliation_form_contents.add_education'/>    
                                                <ul class="menu-options education">
                                                    <li>          
                                                        <a id="add-education" href="" class="action-option manage-button two-options" (click)="addAffiliationModal('education')">
                                                            <span class="glyphicon glyphicon-plus"></span>
                                                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                                        </a>
                                                   </li>
                                                </ul>
                                             </li>
                                        </ul>
                                    </div>         
                                </li>
                                                   
                                <li class="hidden-md hidden-sm visible-xs-inline">          
                                    <a href="" class="action-option manage-button two-options" (click)="addAffiliationModal('education')">
                                        <span class="glyphicon glyphicon-plus"></span>
                                        <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                    </a>
                                </li>                                        
                            </ul>
                            </#if>
                        </div>
                    </div>
                </div>
                <div *ngIf="workspaceSrvc.displayEducation" class="workspace-accordion-content">
                    <ul id="educations-list" *ngIf="affiliationsSrvc.educations.length" class="workspace-affiliations workspace-body-list bottom-margin-medium">
                        <li class="bottom-margin-small workspace-border-box affiliation-box card ng-scope" *ngFor="let group of affiliationsSrvc.educations | orderBy:sortState.predicate:sortState.reverse" education-put-code="{{group.getActive().putCode.value}}">
                            <div class="row"> 
         
                                
                                <div class="col-md-9 col-sm-9 col-xs-7">
                                    <h3 class="workspace-title">            
                                        <span>{{group.getActive().affiliationName.value}}</span>:
                                        <span>{{group.getActive().city.value}}</span><span *ngIf="group.getActive().region.value">, </span><span>{{group.getActive().region.value}}</span>, <span>{{group.getActive().countryForDisplay}}</span>                                               
                                    </h3>
                                    <div class="info-detail">
                                        <div class="info-date">                     
                                            <span class="affiliation-date" *ngIf="group.getActive().startDate">
                                                <span *ngIf="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span *ngIf="group.getActive().startDate.month">-{{group.getActive().startDate.month}}</span><span *ngIf="group.getActive().startDate.day">-{{group.getActive().startDate.day}}</span>
                                                <span><@orcid.msg 'workspace_affiliations.dateSeparator'/></span>
                                                <span ng-hide="group.getActive().endDate.year"><@orcid.msg 'workspace_affiliations.present'/></span>
                                                <span *ngIf="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span *ngIf="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span><span *ngIf="group.getActive().endDate.day">-{{group.getActive().endDate.day}}</span>
                                            </span>
                                            <span class="affiliation-date" *ngIf="!group.getActive().startDate && group.getActive().endDate">
                                                 <span *ngIf="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span *ngIf="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span><span *ngIf="group.getActive().endDate.day">-{{group.getActive().endDate.day}}</span>
                                            </span>
                                            <span *ngIf="(group.getActive().startDate || group.getActive().endDate) && (group.getActive().roleTitle.value || group.getActive().departmentName.value)"> | </span> <span *ngIf="group.getActive().roleTitle.value">{{group.getActive().roleTitle.value}}</span>        
                                            <span *ngIf="group.getActive().departmentName.value">
                                                <span *ngIf="group.getActive().roleTitle.value && !printView">&nbsp;</span>(<span>{{group.getActive().departmentName.value}}</span>)
                                            </span>
                                        </div>
                                    </div>
                                </div>

                                
                                <div class="col-md-3 col-sm-3 col-xs-5 padding-left-fix">          
                                    <div class="workspace-toolbar">         
                                        <ul class="workspace-private-toolbar"> 
                                            <@orcid.checkFeatureStatus 'AFFILIATION_ORG_ID'> 
                                            <li class="works-details">
                                                <a (click)="showDetailsMouseClick(group,$event);showMozillaBadges(group.activePutCode)" ng-mouseenter="showTooltip(group.groupId+'-showHideDetails')" ng-mouseleave="hideTooltip(group.groupId+'-showHideDetails')">
                                                    <span [ngClass]="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                                    </span>
                                                </a>
                                                <div class="popover popover-tooltip top show-hide-details-popover" *ngIf="showElement[group.groupId+'-showHideDetails']">
                                                     <div class="arrow"></div>
                                                    <div class="popover-content">   
                                                        <span *ngIf="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details'/></span>   
                                                        <span *ngIf="moreInfo[group.groupId]"><@orcid.msg 'common.details.hide_details'/></span>
                                                    </div>
                                                </div>
                                            </li>
                                            </@orcid.checkFeatureStatus>
                                            <#if !(isPublicProfile??)> 
                                            <li>
                                                <@orcid.privacyToggle2  angularModel="group.getActive().visibility.visibility"
                                                questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
                                                clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}" 
                                                publicClick="setPrivacy(group.getActive(), 'PUBLIC', $event)" 
                                                    limitedClick="setPrivacy(group.getActive(), 'LIMITED', $event)" 
                                                    privateClick="setPrivacy(group.getActive(), 'PRIVATE', $event)" />
                                            </li>
                                            </#if>
                                        </ul>
                                    </div>
                                </div>  
                            </div>
                            <div class="row" *ngIf="group.activePutCode == group.getActive().putCode.value">
                                <div class="col-md-12 col-sm-12 bottomBuffer">
                                    <ul class="id-details">
                                        <li class="url-work">
                                            <ul class="id-details">
                                                <li *ngFor='let extID of group.getActive().affiliationExternalIdentifiers | orderBy:["-relationship.value", "type.value"] track by $index' class="url-popover">
                                                    <span *ngIf="group.getActive().affiliationExternalIdentifiers[0].value.value.length > 0" bind-html-compile='extID | affiliationExternalIdentifierHtml:group.getActive().putCode.value:$index'></span>
                                                </li>
                                            </ul>                                   
                                        </li>
                                    </ul>
                                </div>
                            </div>  

                            <@orcid.checkFeatureStatus 'AFFILIATION_ORG_ID'>
                            
                            <div class="more-info content" *ngIf="moreInfo[group.groupId]">
                                <div class="row bottomBuffer">
                                    <div class="col-md-12"></div>
                                </div>
                                <span class="dotted-bar"></span>    
                                <div class="row">
                                    <div class="org-ids" *ngIf="group.getActive().orgDisambiguatedId.value">
                                        <div class="col-md-12">   
                                            <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                                            <span bind-html-compile='group.getActive().disambiguatedAffiliationSourceId.value | orgIdentifierHtml:group.getActive().disambiguationSource.value:group.getActive().putCode.value:group.getActive().disambiguationSource' class="url-popover"> 
                                            </span>
                                        </div>
                                        <div class="col-md-11 bottomBuffer info-detail leftBuffer clearfix">
                                            <span *ngIf="group.getActive().orgDisambiguatedName">{{group.getActive().orgDisambiguatedName}}</span><span *ngIf="group.getActive().orgDisambiguatedCity || group.getActive().orgDisambiguatedRegion || group.getActive().orgDisambiguatedCountry">: </span><span *ngIf="group.getActive().orgDisambiguatedCity">{{group.getActive().orgDisambiguatedCity}}</span><span *ngIf="group.getActive().orgDisambiguatedCity && group.getActive().orgDisambiguatedRegion">, </span><span *ngIf="group.getActive().orgDisambiguatedRegion">{{group.getActive().orgDisambiguatedRegion}}</span><span *ngIf="group.getActive().orgDisambiguatedCountry && (group.getActive().orgDisambiguatedCity || group.getActive().orgDisambiguatedRegion)">, </span><span *ngIf="group.getActive().orgDisambiguatedCountry">{{group.getActive().orgDisambiguatedCountry}}</span>
                                            <span *ngIf="group.getActive().orgDisambiguatedUrl"><br>
                                            <a href="{{group.getActive().orgDisambiguatedUrl}}" target="orgDisambiguatedUrl"><span>{{group.getActive().orgDisambiguatedUrl}}</span></a>
                                            </span>
                                         
                                            <div *ngIf="group.getActive().orgDisambiguatedExternalIdentifiers">
                                                <strong><@orcid.msg 'workspace_affiliations.external_ids'/> {{group.getActive().disambiguationSource.value}}</strong><br>
                                                <ul class="reset">
                                                    <li *ngFor="let orgDisambiguatedExternalIdentifier of group.getActive().orgDisambiguatedExternalIdentifiers | orderBy:orgDisambiguatedExternalIdentifier.identifierType">
                                                        {{orgDisambiguatedExternalIdentifier.identifierType}}:  
                                                        <span *ngIf="orgDisambiguatedExternalIdentifier.preferred">{{orgDisambiguatedExternalIdentifier.preferred}} <@orcid.msg 'workspace_affiliations.external_ids_preferred'/>, </span> 
                                                        <span *ngIf="orgDisambiguatedExternalIdentifier.all">
                                                            <span *ngFor="let orgDisambiguatedExternalIdentifierAll of orgDisambiguatedExternalIdentifier.all">{{orgDisambiguatedExternalIdentifierAll}}{{$last ? '' : ', '}}</span>
                                                        </span>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6" *ngIf="group.getActive().url.value">
                                        <div class="bottomBuffer">
                                            <strong><@orcid.msg 'common.url'/></strong><br> 
                                            <a href="{{group.getActive().url.value}}" target="affiliation.url.value">{{group.getActive().url.value}}</a>
                                        </div>
                                    </div>  
                                    <div class="col-md-12">
                                        <div class="bottomBuffer">
                                            <strong><@orcid.msg 'groups.common.created'/></strong><br /> 
                                            <span>{{group.getActive().createdDate | ajaxFormDateToISO8601}}</span>
                                        </div>
                                    </div>  
                                </div>
                            </div>
                            </@orcid.checkFeatureStatus>
    
                            <div class="row source-line">
                                <div class="col-md-12 col-sm-12 col-xs-12">
                                    <div class="sources-container-header">          
                                        <div class="row">
                                            <div class="col-md-7 col-sm-7 col-xs-12">
                                                <@orcid.msg 'groups.common.source'/>: {{(group.getActive().sourceName == null || group.getActive().sourceName == '') ? group.getActive().source : group.getActive().sourceName}}    
                                            </div>
                                            
                                            <div class="col-md-3 col-sm-3 col-xs-6">
                                                <@orcid.msg 'groups.common.created'/>: <span>{{group.getActive().createdDate | ajaxFormDateToISO8601}}</span>
                                            </div>
                                                        
                                            <div class="col-md-2 col-sm-2 col-xs-6 pull-right">
                                                <ul class="sources-options">
                                                    <#if !(isPublicProfile??)>
                                                    <li *ngIf="group.getActive().source == '${effectiveUserOrcid}'">
                                                        <a (click)="openEditAffiliation(group.getActive())" ng-mouseenter="showTooltip(group.getActive().putCode.value+'-edit')" ng-mouseleave="hideTooltip(group.getActive().putCode.value+'-edit')">
                                                            <span class="glyphicon glyphicon-pencil"></span>
                                                        </a>
                                                        <div class="popover popover-tooltip top edit-source-popover" *ngIf="showElement[group.getActive().putCode.value+'-edit']"> 
                                                            <div class="arrow"></div>
                                                            <div class="popover-content">
                                                                <span ><@orcid.msg 'groups.common.edit_my'/></span>
                                                            </div>                
                                                        </div>  
                                                    </li>   
                                                    <li>
                                                        <a id="delete-affiliation_{{group.getActive().putCode.value}}" href="" (click)="deleteAffiliation(group.getActive())" ng-mouseenter="showTooltip(group.getActive().putCode.value+'-delete')" ng-mouseleave="hideTooltip(group.getActive().putCode.value+'-delete')" class="glyphicon glyphicon-trash"></a>
                                                        <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group.getActive().putCode.value+'-delete']"> 
                                                            <div class="arrow"></div>
                                                            <div class="popover-content">
                                                                 <@orcid.msg 'groups.common.delete_this_source' />
                                                            </div>                
                                                        </div>
                                                    </li>
                                                    </#if>  
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </li>
                    </ul>
                    <div *ngIf="affiliationsSrvc.loading" class="text-center">
                        <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
                    </div>
                    <div *ngIf="affiliationsSrvc.loading == false && affiliationsSrvc.educations.length == 0">
                        <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.Noeducationaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.havenotaddedanyeducation")} <a (click)="addAffiliationModal('education')">${springMacroRequestContext.getMessage("workspace_affiliations_body_list.addsomenow")}</a></#if></strong>
                    </div>      
                </div>
            </div>
            
            <div id="workspace-employment" class="workspace-accordion-item workspace-accordion-active">
                <div class="workspace-accordion-header clearfix">
                    <div class="row">
                        <div class="col-md-3 col-sm-3 col-xs-12">
                            <a name='workspace-employments'></a>
                            <a href="" (click)="workspaceSrvc.toggleEmployment($event)" class="toggle-text">
                                <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayEmployment==false}"></i>
                                <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.employment'/> (<span>{{affiliationsSrvc.employments.length}}</span>)
                            </a>
                            <#if !(isPublicProfile??)> 
                            <div class="popover-help-container">
                                <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                                <div id="employment-help" class="popover bottom">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <p><@orcid.msg 'manage_affiliations_settings.helpPopoverEmployment'/> <a href="${knowledgeBaseUri}/articles/1807525" target="manage_affiliations_settings.helpPopoverEmployment"><@orcid.msg 'common.learn_more'/></a></p>
                                    </div>
                                </div>
                            </div>
                            </#if>                     
                        </div>
                        <div class="col-md-9 col-sm-9 col-xs-12 action-button-bar" *ngIf="workspaceSrvc.displayEmployment">
                            <#escape x as x?html>
                            
                            <div class="menu-container">                                     
                                <ul class="toggle-menu">
                                    <li>
                                        <span class="glyphicon glyphicon-sort"></span>                          
                                        <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                        <ul class="menu-options sort">
                                            <li [ngClass]="{'checked':sortState.predicateKey=='date'}" ng-hide="sortHideOption">                                         
                                                <a (click)="sort('date');" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_date'/>
                                                    <span *ngIf="sortState.reverseKey['date']" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='date'}"></span>
                                                    <span *ngIf="sortState.reverseKey['date'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='date'}"></span>
                                                </a>                                                                                    
                                            </li>
                                            <li [ngClass]="{'checked':sortState.predicateKey=='groupName'}" ng-hide="sortHideOption == null">
                                                <a (click)="sort('groupName');" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                    <span *ngIf="sortState.reverseKey['groupName']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='groupName'}" ></span>
                                                    <span *ngIf="sortState.reverseKey['groupName'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='groupName'}" ></span>
                                                </a>                                            
                                            </li>
                                            <li [ngClass]="{'checked':sortState.predicateKey=='title'}" ng-hide="sortHideOption">                                            
                                                <a (click)="sort('title');" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                    <span *ngIf="sortState.reverseKey['title']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='title'}" ></span>
                                                    <span *ngIf="sortState.reverseKey['title'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='title'}" ></span>
                                                </a>                                            
                                            </li>
                                            <li *ngIf="sortState.type != 'affiliation'" [ngClass]="{'checked':sortState.predicateKey=='type'}" ng-hide="sortHideOption">                                           
                                                <a (click)="sort('type');" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_type'/>
                                                    <span *ngIf="sortState.reverseKey['type']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='type'}"></span>
                                                    <span *ngIf="sortState.reverseKey['type'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='type'}"></span>
                                                </a>                                                                                        
                                            </li>
                                        </ul>                                           
                                    </li>
                                </ul>                                   
                            </div>
                            </#escape>
                                            
                            <#if !(isPublicProfile??)>
                            <ul class="workspace-bar-menu">                         
                                
                                <li class="hidden-xs">                  
                                    <div class="menu-container" id="add-employment-container">
                                        <ul class="toggle-menu">
                                            <li [ngClass]="{'green-bg' : showBibtexImportWizard == true}">       
                                                <span class="glyphicon glyphicon-plus"></span>
                                                <@orcid.msgCapFirst 'manual_affiliation_form_contents.add_employment' />    
                                                <ul class="menu-options employment">                            
                                                    
                                                    <li>                            
                                                        <a id="add-employment" href="" class="action-option manage-button two-options" (click)="addAffiliationModal('employment')">
                                                            <span class="glyphicon glyphicon-plus"></span>
                                                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                                        </a>            
                                                    </li>
                                                </ul>
                                            </li>
                                        </ul>
                                    </div>         
                                </li>
                                
                                <li class="hidden-md hidden-sm visible-xs-inline">                     
                                    <a href="" class="action-option manage-button two-options" (click)="addAffiliationModal('employment')">
                                        <span class="glyphicon glyphicon-plus"></span>
                                        <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                    </a>                
                                </li>
                            </ul>
                            </#if>
                                        
                        </div>
                    </div>
                </div>
                <div *ngIf="workspaceSrvc.displayEmployment" class="workspace-accordion-content">
                    <ul id="employments-list" ng-hide="!affiliationsSrvc.employments.length" class="workspace-affiliations workspace-body-list bottom-margin-medium">
                        <li class="bottom-margin-small workspace-border-box affiliation-box card" *ngFor="let group of affiliationsSrvc.employments ">***| orderBy:sortState.predicate:sortState.reverse" employment-put-code="{{group.getActive().putCode.value}}
                            <div class="row"> 
                         
                    
                                <div class="col-md-9 col-sm-9 col-xs-7">
                                    <h3 class="workspace-title">            
                                        <span>{{group.getActive().affiliationName.value}}</span>:
                                        <span>{{group.getActive().city.value}}</span><span *ngIf="group.getActive().region.value">, </span><span>{{group.getActive().region.value}}</span>, <span>{{group.getActive().countryForDisplay}}</span>                                               
                                    </h3>
                                    <div class="info-detail">
                                        <div class="info-date">                     
                                            <span class="affiliation-date" *ngIf="group.getActive().startDate">
                                                <span *ngIf="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span *ngIf="group.getActive().startDate.month">-{{group.getActive().startDate.month}}</span><span *ngIf="group.getActive().startDate.day">-{{group.getActive().startDate.day}}</span>
                                                <span><@orcid.msg 'workspace_affiliations.dateSeparator'/></span>
                                                <span ng-hide="group.getActive().endDate.year"><@orcid.msg 'workspace_affiliations.present'/></span>
                                                <span *ngIf="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span *ngIf="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span><span *ngIf="group.getActive().endDate.day">-{{group.getActive().endDate.day}}</span>
                                            </span>
                                            <span class="affiliation-date" *ngIf="!group.getActive().startDate && group.getActive().endDate">
                                                 <span *ngIf="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span *ngIf="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span><span *ngIf="group.getActive().endDate.day">-{{group.getActive().endDate.day}}</span>
                                            </span>
                                            <span *ngIf="(group.getActive().startDate || group.getActive().endDate) && (group.getActive().roleTitle.value || group.getActive().departmentName.value)"> | </span> <span *ngIf="group.getActive().roleTitle.value">{{group.getActive().roleTitle.value}}</span>        
                                            <span *ngIf="group.getActive().departmentName.value">
                                            <span *ngIf="group.getActive().roleTitle.value && !printView">&nbsp;</span>(<span>{{group.getActive().departmentName.value}}</span>)
                                            </span>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-3 col-sm-3 col-xs-5 padding-left-fix">          
                                    <div class="workspace-toolbar">         
                                        <ul class="workspace-private-toolbar"> 
                                            <@orcid.checkFeatureStatus 'AFFILIATION_ORG_ID'> 
                                            <li class="works-details">
                                                <a (click)="showDetailsMouseClick(group,$event);showMozillaBadges(group.activePutCode)" ng-mouseenter="showTooltip(group.groupId+'-showHideDetails')" ng-mouseleave="hideTooltip(group.groupId+'-showHideDetails')">
                                                    <span [ngClass]="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                                    </span>
                                                </a>
                                                <div class="popover popover-tooltip top show-hide-details-popover" *ngIf="showElement[group.groupId+'-showHideDetails']">
                                                     <div class="arrow"></div>
                                                    <div class="popover-content">   
                                                        <span *ngIf="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details'/></span>   
                                                        <span *ngIf="moreInfo[group.groupId]"><@orcid.msg 'common.details.hide_details'/></span>
                                                    </div>
                                                </div>
                                            </li>
                                            </@orcid.checkFeatureStatus>
                                            <#if !(isPublicProfile??)> 
                                            <li>
                                                <@orcid.privacyToggle2  angularModel="group.getActive().visibility.visibility"
                                                questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
                                                clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}" 
                                                publicClick="setPrivacy(group.getActive(), 'PUBLIC', $event)" 
                                                    limitedClick="setPrivacy(group.getActive(), 'LIMITED', $event)" 
                                                    privateClick="setPrivacy(group.getActive(), 'PRIVATE', $event)" />
                                            </li>
                                            </#if>
                                        </ul>
                                    </div>
                                </div>  
                            </div>
                            <div class="row" *ngIf="group.activePutCode == group.getActive().putCode.value">
                                <div class="col-md-12 col-sm-12 bottomBuffer">
                                    <ul class="id-details">
                                        <li class="url-work">
                                            <ul class="id-details">
                                                <li *ngFor='let extID of group.getActive().affiliationExternalIdentifiers | orderBy:["-relationship.value", "type.value"] track by $index' class="url-popover">
                                                    <span *ngIf="group.getActive().affiliationExternalIdentifiers[0].value.value.length > 0" bind-html-compile='extID | affiliationExternalIdentifierHtml:group.getActive().putCode.value:$index'></span>
                                                </li>
                                            </ul>                                   
                                        </li>
                                    </ul>
                                </div>
                            </div>  
                            <@orcid.checkFeatureStatus 'AFFILIATION_ORG_ID'>
                            <div class="more-info content" *ngIf="moreInfo[group.groupId]">
                                <div class="row bottomBuffer">
                                    <div class="col-md-12"></div>
                                </div>
                                <span class="dotted-bar"></span>    
                                <div class="row">
                                    <div class="org-ids" *ngIf="group.getActive().orgDisambiguatedId.value">
                                        <div class="col-md-12">   
                                            <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                                            <span bind-html-compile='group.getActive().disambiguatedAffiliationSourceId.value | orgIdentifierHtml:group.getActive().disambiguationSource.value:group.getActive().putCode.value:group.getActive().disambiguationSource' class="url-popover"> 
                                            </span>
                                        </div>
                                        <div class="col-md-11 bottomBuffer info-detail leftBuffer clearfix">
                                            <span *ngIf="group.getActive().orgDisambiguatedName">{{group.getActive().orgDisambiguatedName}}</span><span *ngIf="group.getActive().orgDisambiguatedCity || group.getActive().orgDisambiguatedRegion || group.getActive().orgDisambiguatedCountry">: </span><span *ngIf="group.getActive().orgDisambiguatedCity">{{group.getActive().orgDisambiguatedCity}}</span><span *ngIf="group.getActive().orgDisambiguatedCity && group.getActive().orgDisambiguatedRegion">, </span><span *ngIf="group.getActive().orgDisambiguatedRegion">{{group.getActive().orgDisambiguatedRegion}}</span><span *ngIf="group.getActive().orgDisambiguatedCountry && (group.getActive().orgDisambiguatedCity || group.getActive().orgDisambiguatedRegion)">, </span><span *ngIf="group.getActive().orgDisambiguatedCountry">{{group.getActive().orgDisambiguatedCountry}}</span>
                                            <span *ngIf="group.getActive().orgDisambiguatedUrl"><br>
                                            <a href="{{group.getActive().orgDisambiguatedUrl}}" target="orgDisambiguatedUrl"><span>{{group.getActive().orgDisambiguatedUrl}}</span></a>
                                            </span>
                                            
                                            <div *ngIf="group.getActive().orgDisambiguatedExternalIdentifiers">
                                                <strong><@orcid.msg 'workspace_affiliations.external_ids'/> {{group.getActive().disambiguationSource.value}}</strong><br>
                                                <ul class="reset">
                                                    <li *ngFor="let orgDisambiguatedExternalIdentifier of group.getActive().orgDisambiguatedExternalIdentifiers | orderBy:orgDisambiguatedExternalIdentifier.identifierType">{{orgDisambiguatedExternalIdentifier.identifierType}}:  <span *ngIf="orgDisambiguatedExternalIdentifier.preferred">{{orgDisambiguatedExternalIdentifier.preferred}} <@orcid.msg 'workspace_affiliations.external_ids_preferred'/>, </span> <span *ngIf="orgDisambiguatedExternalIdentifier.all"><span *ngFor="let orgDisambiguatedExternalIdentifierAll of orgDisambiguatedExternalIdentifier.all">{{orgDisambiguatedExternalIdentifierAll}}{{$last ? '' : ', '}}</span></span></li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6" *ngIf="group.getActive().url.value">
                                        <div class="bottomBuffer">
                                            <strong><@orcid.msg 'common.url'/></strong><br> 
                                            <a href="{{group.getActive().url.value}}" target="affiliation.url.value">{{group.getActive().url.value}}</a>
                                        </div>
                                    </div>  
                                    <div class="col-md-12">
                                        <div class="bottomBuffer">
                                            <strong><@orcid.msg 'groups.common.created'/></strong><br> 
                                            <span>{{group.getActive().createdDate | ajaxFormDateToISO8601}}</span>
                                        </div>
                                    </div>  
                                </div>
                            </div>
                            </@orcid.checkFeatureStatus>
                        </li>
                    </ul>
                </div>
    
                <div class="row source-line">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <div class="sources-container-header">          
                            <div class="row">
                                <div class="col-md-7 col-sm-7 col-xs-12">
                                    <@orcid.msg 'groups.common.source'/>: {{(group.getActive().sourceName == null || group.getActive().sourceName == '') ? group.getActive().source : group.getActive().sourceName}}    
                                </div>
                            
                                <div class="col-md-3 col-sm-3 col-xs-6">
                                    <@orcid.msg 'groups.common.created'/>: <span>{{group.getActive().createdDate | ajaxFormDateToISO8601}}</span>
                                </div>
                                            
                                <div class="col-md-2 col-sm-2 col-xs-6 pull-right">
                                    <ul class="sources-options">
                                        <#if !(isPublicProfile??)>
                                        <li *ngIf="group.getActive().source == true">***ngIf="group.getActive().source == '${effectiveUserOrcid}
                                            <a (click)="openEditAffiliation(group.getActive())" ng-mouseenter="showTooltip(group.getActive().putCode.value+'-edit')" ng-mouseleave="hideTooltip(group.getActive().putCode.value+'-edit')">
                                                <span class="glyphicon glyphicon-pencil"></span>
                                            </a>
                                            <div class="popover popover-tooltip top edit-source-popover" *ngIf="showElement[group.getActive().putCode.value+'-edit']"> 
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <span ><@orcid.msg 'groups.common.edit_my'/></span>
                                                </div>                
                                            </div>  
                                        </li>   
                                        <li>
                                            <a id="delete-affiliation_{{group.getActive().putCode.value}}" href="" (click)="deleteAffiliation(group.getActive())" ng-mouseenter="showTooltip(group.getActive().putCode.value+'-delete')" ng-mouseleave="hideTooltip(group.getActive().putCode.value+'-delete')" class="glyphicon glyphicon-trash"></a>
                                            <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group.getActive().putCode.value+'-delete']"> 
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                     <@orcid.msg 'groups.common.delete_this_source' />
                                                </div>                
                                            </div>
                                        </li>
                                        </#if>  
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
 
                    <div *ngIf="affiliationsSrvc.loading" class="text-center">
                        <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
                    </div>
                    <div *ngIf="affiliationsSrvc.loading == false && affiliationsSrvc.employments.length == 0">
                        <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.Noemploymentaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_affiliations_body_list.havenotaddedanyemployment")} <a (click)="addAffiliationModal('employment')">${springMacroRequestContext.getMessage("workspace_affiliations_body_list.addsomenow")}</a></#if></strong>
                    </div>
                </div>
            </div>
        </div>
    </script>
    </#if>

    <#if springMacroRequestContext.requestUri?contains("/my-orcid") >
    <script type="text/ng-template" id="biography-ng2-template">
        <div class="biography-controller" id="bio-section">
            <div class="row">
                <div class="col-md-9 col-sm-8 col-xs-4">
                    <h3 (click)="toggleEdit()" class="workspace-title">${springMacroRequestContext.getMessage("manage_bio_settings.labelbiography")}</h3>  
                    <div class="popover-help-container">
                        <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                        <div id="bio-help" class="popover bottom">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <p><@orcid.msg 'manage_bio_settings.helpPopoverBio'/></p>
                            </div>
                        </div>
                    </div>   
                </div>
                <div class="col-md-3 col-sm-4 col-xs-8">
                    <ul class="inline-list bio-edit right">
                        <li>
                            <div (click)="toggleEdit()" *ngIf="!showEdit" class="edit-biography edit-option">
                                <span class="glyphicon glyphicon-pencil"></span>
                                <div class="popover popover-tooltip top">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span><@orcid.msg 'manage_bio_settings.editBio' /></span>
                                    </div>                
                                </div>
                            </div>
                        </li>
                        <li>
                            <privacy-toggle-ng2 
                                [dataPrivacyObj]="biographyForm"  
                                (privacyUpdate)="privacyChange($event)"
                                elementId="bio-privacy-toggle" 
                                privacyNodeName="visiblity" 
                            ></privacy-toggle-ng2>
                        </li>
                    </ul>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-12">   
                    <div style="white-space: pre-wrap" *ngIf="!showEdit" (click)="toggleEdit()">{{biographyForm?.biography?.value}}</div> 
                </div>
            </div>
            
            <div *ngIf="showEdit" class="biography-edit">
                <div class="row">
                    <div class="col-md-12 col-xs-12 col-sm-12">
                        <textarea id="biography" name="biography" class="input-xlarge" rows="20" (change)="checkLength()" [(ngModel)]="biographyForm.biography.value"></textarea>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <span class="orcid-error" *ngIf="lengthError==true">
                            <div>${springMacroRequestContext.getMessage("Length.changePersonalInfoForm.biography")}</div>
                        </span>
                        <span class="orcid-error" *ngIf="biographyForm?.biography?.errors?.length > 0">
                            <div *ngFor='let error of biographyForm?.biography?.errors'>{{error}}</div>
                        </span>
                    </div>
                </div>
                <div class="row">                                   
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <div class="pull-right full-width">
                            <a class="cancel" (click)="cancel()"><@spring.message "freemarker.btncancel"/></a>
                            <button class="btn btn-primary" (click)="setBiographyForm()"><@spring.message "freemarker.btnsavechanges"/></button>
                        </div>
                    </div>
                </div>                                                    
            </div>
        </div>   
    </script>
    </#if>

    <!-- Country -->
    <#if springMacroRequestContext.requestUri?contains("/my-orcid") >
    <script type="text/ng-template" id="country-form-ng2-template">        
        <div class="edit-record <#if RequestParameters['bulkEdit']??>edit-record-bulk-edit</#if> edit-country row">

            <div class="col-md-12 col-sm-12 col-xs-12">           
                <div class=""> 
                    <h1 class="lightbox-title pull-left">
                        <@orcid.msg 'manage_bio_settings.editCountry'/>
                    </h1>
                </div>          
            </div>
            <div class="bottomBuffer" style="margin: 0!important;">                          
                <!-- Move this to component - Begin of bulk component-->
                <div class="row bulk-edit-modal">
                    <div class="pull-right bio-edit-modal">             
                        <span class="right">Edit all privacy settings</span>
                        <div class="bulk-privacy-bar">
                            <div [ngClass]="{'relative' : modal == false}" id="privacy-bar">
                                <ul class="privacyToggle" ng-mouseenter="commonSrvc.showPrivacyHelp(bulkEdit +'-privacy', $event, 145)" ng-mouseleave="commonSrvc.hideTooltip(bulkEdit +'-privacy')">
                                    <li class="publicActive publicInActive" [ngClass]="{publicInActive: bioModel != 'PUBLIC'}"><a (click)="setBulkGroupPrivacy('PUBLIC', $event, bioModel)" name="privacy-toggle-3-public" id=""></a></li>
                                    <li class="limitedActive limitedInActive" [ngClass]="{limitedInActive: bioModel != 'LIMITED'}"><a (click)="setBulkGroupPrivacy('LIMITED', $event, bioModel)" name="privacy-toggle-3-limited" id=""></a></li>
                                    <li class="privateActive privateInActive" [ngClass]="{privateInActive: bioModel != 'PRIVATE'}"><a (click)="setBulkGroupPrivacy('PRIVATE', $event, bioModel)" name="privacy-toggle-3-private" id=""></a></li>
                                </ul>
                            </div>
                            <div class="popover-help-container" style="top: -75px; left: 512px;">
                                <div class="popover top privacy-myorcid3" [ngClass]="commonSrvc.shownElement[bulkEdit +'-privacy'] == true ? 'block' : ''">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <strong>Who can see this? </strong>
                                        <ul class="privacyHelp">
                                            <li class="public" style="color: #009900;">everyone</li>
                                            <li class="limited" style="color: #ffb027;">trusted parties</li>
                                            <li class="private" style="color: #990000;">only me</li>
                                        </ul>
                                        <a href="https://support.orcid.org/knowledgebase/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information">More information on privacy settings</a>
                                    </div>                
                                </div>                              
                            </div>

                        </div>
                        <div class="bulk-help popover-help-container">
                            <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                            <div id="bulk-help" class="popover bottom">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <p>Use Edit all privacy settings to change the visibility level of all items, or Edit individual privacy settings to select different visibility levels for each item.</p>
                                </div>
                           </div>
                        </div>
                    </div>          
                </div>
                <!-- End of bulk edit -->          
            </div>    
            <div class="col-md-12 col-sm-12 col-xs-12">
                <div class=" padding-right-reset">
                    <span class="right"><@orcid.msg 'groups.common.edit_individual_privacy' /></span>   
                </div>
            </div>      
            <div class="col-md-12 col-xs-12 col-sm-12">
                <div style="position: static">
                    <div class="fixed-area" scroll>             
                        <div class="scroll-area">       
                                        
                            <div class="row aka-row" *ngFor="let country of countryFormAddresses; let index = index; let first = first; let last = last">
                                <div class="col-md-6">                                  
                                    <div class="aka">
                                        <select 
                                            [(ngModel)]="country.iso2Country" 
                                            [disabled]="country.source != orcidId"
                                            [ngClass]="{ 'not-allowed': country?.source != orcidId }"
                                            focus-me="newInput"
                                            name="country" 
                                        >

                                            <option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
                                            <#list isoCountries?keys as key>
                                                <option value="${key}">${isoCountries[key]}</option>
                                            </#list>
                                        </select>                                      
                                    </div>         
                                                            
                                    <div class="source" *ngIf="country.sourceName || country?.sourceName == null">
                                        <@orcid.msg 'manage_bio_settings.source'/>: <span *ngIf="country.sourceName">{{country.sourceName}}</span><span *ngIf="country.sourceName == null">{{orcidId}}</span>
                                    </div>
                                    
                                </div> 
                                <div class="col-md-6" style="position: static">
                                    <ul class="record-settings pull-right">              
                                        <li>                                    
                                            <div 
                                                (click)="first || swapUp(index)" 
                                                (mouseenter)="commonSrvc.showTooltip('tooltip-country-move-up-'+index, $event, 37, -33, 44)" 
                                                (mouseleave)="commonSrvc.hideTooltip('tooltip-country-move-up-'+index)"
                                                class="glyphicon glyphicon-arrow-up circle" 
                                            ></div>
                                            <@orcid.tooltip elementId="'tooltip-country-move-up-'+index" message="common.modals.move_up"/>                                         
                                        </li>
                                        <li>
                                            <div 
                                                class="glyphicon glyphicon-arrow-down circle" 
                                                (click)="last || swapDown(index)" 
                                                (mouseenter)="commonSrvc.showTooltip('tooltip-country-move-down-'+index, $event, 37, -2, 53)" 
                                                (mouseleave)="commonSrvc.hideTooltip('tooltip-country-move-down-'+index)"
                                            ></div>
                                            <@orcid.tooltip elementId="'tooltip-country-move-down-'+index" message="common.modals.move_down" />
                                        </li>
                                        <li>
                                            <div 
                                                (click)="deleteCountry(country)" 
                                                (mouseenter)="commonSrvc.showTooltip('tooltip-country-delete-'+$index, $event, 37, 50, 39)" 
                                                (mouseleave)="commonSrvc.hideTooltip('tooltip-country-delete-'+$index)"
                                                class="glyphicon glyphicon-trash" 
                                            ></div>
                                            <@orcid.tooltip elementId="'tooltip-country-delete-'+$index" message="common.modals.delete" />                               
                                        </li>
                                        <li>
                                            <!--
                                            <privacy-toggle-ng2 elementId="bio-privacy-toggle" [dataPrivacyObj]="country.visibility.visibility" (privacyUpdate)="privacyChange($event)"></privacy-toggle-ng2>
                                            -->
                                            <!--
                                            -->
                                            <@orcid.privacyToggle3  angularModel="country.visibility.visibility"
                                                questionClick="toggleClickPrivacyHelp($index)"
                                                clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
                                                publicClick="setPrivacyModal('PUBLIC', $event, country)" 
                                                limitedClick="setPrivacyModal('LIMITED', $event, country)" 
                                                privateClick="setPrivacyModal('PRIVATE', $event, country)"
                                                elementId="$index"/>   
                                        </li>
                                    </ul>
                                    <span class="created-date pull-right hidden-xs" *ngIf="country.createdDate"><@orcid.msg 'manage_bio_settings.created'/>: {{country.createdDate.year + '-' + country.createdDate.month + '-' + country.createdDate.day}}</span>
                                    <span class="created-date pull-left visible-xs" *ngIf="country.createdDate"><@orcid.msg 'manage_bio_settings.created'/>: {{country.createdDate.year + '-' + country.createdDate.month + '-' + country.createdDate.day}}</span>
                                </div>                                  
                            </div>                                          
                        </div>         
                        <div *ngIf="countryForm?.errors?.length > 0">
                            <div *ngFor="let error of countryFormErrors">
                                <span class="red">{{error}}</span>
                            </div>
                        </div>
                    </div>                  
                    <div class="record-buttons">                        
                        <a (click)="addNewCountry()"><span class="glyphicon glyphicon-plus pull-left">
                            <div class="popover popover-tooltip-add top">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <span><@orcid.msg 'common.modals.add' /></span>
                                </div>
                            </div>
                        </span></a>                         
                        <button class="btn btn-primary pull-right" (click)="setCountryForm()"><@spring.message "freemarker.btnsavechanges"/></button>
                        <a class="cancel-option pull-right" (click)="closeEditModal()"><@spring.message "freemarker.btncancel"/></a> 
                    </div>
                </div>
            </div>
        </div>
    </script>
    </#if>

    <#if springMacroRequestContext.requestUri?contains("/my-orcid") >
    <script type="text/ng-template" id="country-ng2-template">
        <div class="workspace-section country">
            <div class="workspace-section-header">
                <div class="workspace-section-title">
                    <div id="country-open-edit-modal" class="edit-country edit-option" (click)="openEditModal()" title=""> 
                        <div class="glyphicon glyphicon-pencil"> 
                            <div class="popover popover-tooltip top"> 
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <span><@orcid.msg 'manage_bio_settings.editCountry' /></span>
                                </div>                
                            </div>
                        </div>                  
                    </div>
                    <div class="workspace-section-label"><@orcid.msg 'public_profile.labelCountry'/></div>
                </div>
            </div>
            <div class="workspace-section-content">
                <span *ngFor="let country of countryFormAddresses">
                <span *ngIf="country != null && country.countryName != null" >{{country.countryName}}</span>
                </span>
            </div>
        </div>
    </script>
    </#if>

    <#if springMacroRequestContext.requestUri?contains("/my-orcid") >
    <script type="text/ng-template" id="email-unverified-warning-ng2-template">
        <div class="row">
            <div class="col-md-12 col-xs-12 col-sm-12">
                <h4><@orcid.msg 'orcid.frontend.workspace.your_primary_email'/></h4>
                <p><@orcid.msg 'orcid.frontend.workspace.ensure_future_access'/></p>
                <p><@orcid.msg 'orcid.frontend.workspace.ensure_future_access2'/><br /><strong>{{emailPrimary}}</strong></p>
                <p><@orcid.msg 'orcid.frontend.workspace.ensure_future_access3'/> <a target="orcid.frontend.link.url.knowledgebase" href="<@orcid.msg 'orcid.frontend.link.url.knowledgebase'/>"><@orcid.msg 'orcid.frontend.workspace.ensure_future_access4'/></a> <@orcid.msg 'orcid.frontend.workspace.ensure_future_access5'/> <a target="orcid.frontend.link.email.support" href="mailto:<@orcid.msg 'orcid.frontend.link.email.support'/>"><@orcid.msg 'orcid.frontend.link.email.support'/></a>.</p>
                <div class="topBuffer">
                    <button class="btn btn-primary" id="modal-close" (click)="verifyEmail()"><@orcid.msg 'orcid.frontend.workspace.send_verification'/></button>
                    <a class="cancel-option inner-row" (click)="close()"><@orcid.msg 'orcid.frontend.freemarker.btncancel'/></a>
                </div>
            </div>
        </div>
    </script>
    </#if>

    <#if springMacroRequestContext.requestUri?contains("/my-orcid") >
    <script type="text/ng-template" id="email-verification-sent-messsage-ng2-template">
        <div style="padding: 20px;">
            <h4><@orcid.msg 'manage.email.verificationEmail'/> {{emailPrimary}}</h4>
            <p><@orcid.msg 'workspace.check_your_email'/></p>
            <br />
            <button class="btn" (click)="close()"><@orcid.msg 'freemarker.btnclose'/></button>
        </div>
    </script>
    </#if>

    <#if springMacroRequestContext.requestUri?contains("/my-orcid") >
    <script type="text/ng-template" id="modal-ng2-template">
        <div [hidden]="!showModal" >
            <div class="popover-ng2-bck" (click)="closeModal()"></div>
            <div
                class="popover-ng2-content"
                id="colorbox" 
                role="dialog" 
                style="transition: width 2s, height 2s;"
                tabindex="-1" 
                [ngStyle]="{
                'height': this.elementHeight + 'px',
                'left': 'calc(50% - ' + this.elementWidth/2 + 'px)',
                'top': 'calc(50% - ' + this.elementHeight/2 + 'px)',
                'width': this.elementWidth + 'px'
                }"
            >
                <div id="cboxWrapper" 
                    [ngStyle]="{
                    'height': this.elementHeight + 'px',
                    'width': this.elementWidth + 'px'
                    }"
                >
                    <div>
                        <div id="cboxTopLeft" style="float: left;"></div>
                        <div id="cboxTopCenter" style="float: left;"
                            [ngStyle]="{
                            'width': this.elementWidth + 'px'
                            }"
                        ></div>
                        <div id="cboxTopRight" style="float: left;"></div>
                    </div>
                    <div style="clear: left;">
                        <div id="cboxMiddleLeft" style="float: left;"
                            [ngStyle]="{
                            'height': this.elementHeight + 'px'
                            }"
                        ></div>
                        <div id="cboxContent" style="float: left;"
                            [ngStyle]="{
                                'height': this.elementHeight + 'px',
                                'width': this.elementWidth + 'px'
                            }"
                        >
                            <div id="cboxLoadedContent" style=" overflow: auto;"
                                [ngStyle]="{
                                'height': this.elementHeight + 'px',
                                'width': this.elementWidth + 'px'
                                }"
                            >
                                <div class="lightbox-container">

                                    <ng-content></ng-content>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div> 
    </script>
    </#if>

    <#if springMacroRequestContext.requestUri?contains("/my-orcid") || springMacroRequestContext.requestUri?contains("/inbox") || springMacroRequestContext.requestUri?contains("/account") || springMacroRequestContext.requestUri?contains("/developer-tools")>
    <script type="text/ng-template" id="name-ng2-template">
        <!-- Name -->    
        <div class="workspace-section" id="names-section"> 
            <div *ngIf="!showEdit" (click)="toggleEdit()">
                <div class="row">               
                    <div class="col-md-12">
                        <div class="workspace-section-title">
                            <div class="edit-name edit-option" *ngIf="!showEdit" id="open-edit-names">
                                <div class="glyphicon glyphicon-pencil">
                                    <div class="popover popover-tooltip top">
                                        <div class="arrow"></div>
                                        <div class="popover-content">
                                            <span><@orcid.msg 'manage_bio_settings.editName'/></span>
                                        </div>                
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <h2 class="full-name">
                            <span *ngIf="displayFullName()">{{nameForm?.creditName?.value}}
                            </span>
                            <span *ngIf="displayPublishedName()">
                                {{nameForm?.givenNames?.value}} <span *ngIf="nameForm?.familyName?.value != null" >{{nameForm?.familyName?.value}}</span>
                            </span>
                        </h2>
                    </div>
                </div>
            </div>
            <!-- Edit Mode -->
            <div class="names-edit" *ngIf="showEdit == true">

                <label for="firstName">${springMacroRequestContext.getMessage("manage_bio_settings.labelfirstname")}</label>
               
                <input type="text" [(ngModel)]="nameForm.givenNames.value" (keydown)="setNameFormEnter($event)" class="full-width-input" />
               
                <span class="orcid-error" *ngIf="nameForm.givenNames.errors.length > 0">
                    <div *ngFor='let error of nameForm.givenNames.errors'>{{error}}</div>
                </span>
                <label for="lastName">${springMacroRequestContext.getMessage("manage_bio_settings.labellastname")}</label>
               
                <input type="text" [(ngModel)]="nameForm.familyName.value" (keydown)="setNameFormEnter($event)" class="full-width-input" />
               
                <label for="creditName">${springMacroRequestContext.getMessage("manage_bio_settings.labelpublishedname")}</label>                               
                <input type="text" [(ngModel)]="nameForm.creditName.value" (keydown)="setNameFormEnter($event)" class="full-width-input" />
               
                <div>
                    <privacy-toggle-ng2 
                        [dataPrivacyObj]="nameForm" 
                        (privacyUpdate)="privacyChange($event)"
                        elementId="name-privacy-toggle" 
                        privacyNodeName="namesVisibility" 
                    ></privacy-toggle-ng2>

                    <a href="${knowledgeBaseUri}/articles/142948-names-in-the-orcid-registry" target="142948-names-in-the-orcid-registry"><i class="glyphicon glyphicon-question-sign help-glyphicon" style="width: 14px;"></i></a>
                </div>
                <ul class="workspace-section-toolbar clearfix">
                    <li class="pull-right">
                        <button class="btn btn-primary" (click)="setNameForm( true )"><@spring.message "freemarker.btnsavechanges"/></button>
                    </li>
                    <li class="pull-right">
                        <a class="cancel-option" (click)="cancel()"><@spring.message "freemarker.btncancel"/></a>
                    </li>
                </ul>
            </div>
        </div>
    </script>
    </#if>

    <#if springMacroRequestContext.requestUri?contains("/my-orcid") || springMacroRequestContext.requestUri?contains("/inbox") || springMacroRequestContext.requestUri?contains("/account") || springMacroRequestContext.requestUri?contains("/developer-tools")>
    <script type="text/ng-template" id="privacy-toggle-ng2-template">
        <div class="relative" class="privacy-bar-impr">
            <ul class="privacyToggle" (mouseenter)="showTooltip(name)" (mouseleave)="hideTooltip(name)" >
                <li class="publicActive" [ngClass]="{publicInActive: dataPrivacyObj[privacyNodeName]?.visibility != 'PUBLIC'}"><a (click)="setPrivacy('PUBLIC')"></a></li>
                <li class="limitedActive limitedInActive" [ngClass]="{limitedInActive: dataPrivacyObj[privacyNodeName]?.visibility != 'LIMITED'}"><a (click)="setPrivacy('LIMITED')"></a></li>
                <li class="privateActive privateInActive" [ngClass]="{privateInActive: dataPrivacyObj[privacyNodeName]?.visibility != 'PRIVATE'}"><a (click)="setPrivacy('PRIVATE')"></a></li>
            </ul>

            <div class="popover-help-container">
                <div class="popover top privacy-myorcid3" [ngClass]="showElement[name] == true ? 'block' : ''">
                    <div class="arrow"></div>
                    <div class="popover-content">
                        <strong><@orcid.msg 'privacyToggle.help.who_can_see' /></strong>
                        <ul class="privacyHelp">
                            <li class="public" style="color: #009900;"><@orcid.msg 'privacyToggle.help.everyone' /></li>
                            <li class="limited" style="color: #ffb027;"><@orcid.msg 'privacyToggle.help.trusted_parties' /></li>
                            <li class="private" style="color: #990000;"><@orcid.msg 'privacyToggle.help.only_me' /></li>
                        </ul>
                        <a href="https://support.orcid.org/knowledgebase/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information"><@orcid.msg 'privacyToggle.help.more_information' /></a>
                    </div>                
                </div>                              
            </div>
        </div>
    </script>
    </#if>

    <#if springMacroRequestContext.requestUri?contains("/my-orcid") || springMacroRequestContext.requestUri?contains("/inbox") || springMacroRequestContext.requestUri?contains("/account") || springMacroRequestContext.requestUri?contains("/developer-tools")>
    <script type="text/ng-template" id="widget-ng2-template">
        <div class="widget-container">
            <div class="widget-header">
                <a (click)="toggleCopyWidget();"><span class="glyphicon glyphicon-phone"></span> <@orcid.msg 'orcid_widget.header'/></a>
                <div class="popover-help-container">
                    <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                    <div id="widget-help" class="popover bottom">
                        <div class="arrow"></div>
                        <div class="popover-content">
                            <p><@orcid.msg 'orcid_widget.tooltip'/></p>
                        </div>
                    </div>
                </div>
            </div>
            <div *ngIf="showCode" class="widget-code-container">
                <p class="widget-instructions"><@orcid.msg 'orcid_widget.copy_message'/></p>
                <textarea id="widget-code-nd" name="widget-code" class="form-control widget-code" (click)="inputTextAreaSelectAll($event)" readonly="readonly">{{widgetURLND}}</textarea>
                <p class="bold"><@orcid.msg 'orcid_widget.widget_preview'/></p>
                <div class="orcid-summary-widget">
                    <a id="widget-sample" href="${baseUri}/${(effectiveUserOrcid)!}" target="effectiveUserOrcid" rel="noopener noreferrer" style="vertical-align:top;">
                    <img src="https://orcid.org/sites/default/files/images/orcid_16x16.png" style="width:1em;margin-right:.5em;" alt="ORCID iD icon">${baseDomainRmProtocall}/${(effectiveUserOrcid)!}</a>
                </div>
                <p><small class="italic"><@orcid.msg 'orcid_widget.widget_preview_text'/></small></p>
                <a (click)="hideWidgetCode()"><@orcid.msg 'orcid_widget.hide_code'/></a>
            </div>
        </div>
    </script>
    </#if>

    <#if springMacroRequestContext.requestUri?contains("/account") >
    <script type="text/ng-template" id="works-privacy-preferences-ng2-template">
        <div class="editTablePadCell35" id="privacy-settings">
            ${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault.who_can_see_this")}
            <br>
            <@orcid.privacyToggle3Ng2
            angularModel="default_visibility"
            publicClick="updateActivitiesVisibilityDefault('PUBLIC', $event)" 
            limitedClick="updateActivitiesVisibilityDefault('LIMITED', $event)" 
            privateClick="updateActivitiesVisibilityDefault('PRIVATE', $event)" 
            elementId="workPrivHelp" /> 
        </div>
    </script> 
    </#if>

    <!-- Ng2 Templates - END -->
    <!-- ***************************************************** -->
</head>
