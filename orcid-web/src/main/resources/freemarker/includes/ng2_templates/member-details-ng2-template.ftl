<script type="text/ng-template" id="member-details-ng2-template">
    <div class="row member-list">
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
            <p><a href="<@orcid.rootPath '/members'/>"><i class="glyphicon x075 glyphicon-chevron-left"></i> <@orcid.msg 'member_details.all_members'/></a></p>
            <div class="text-center">
                <i *ngIf="showMemberDetailsLoader" class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
                <!--[if lt IE 8]>
                    <img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
                <![endif]-->
                <p *ngIf="showGetMemberDetailsError"><@orcid.msg 'member_details.could_not_get_details'/></p>
            </div>
            <div class="row" *ngIf="currentMemberDetails">
                <div class="col-md-12 col-sm-12 col-xs-12">
	                <h1>{{currentMemberDetails.member.publicDisplayName}}</h1>
                    <p ><span *ngIf="communityTypes[currentMemberDetails.member.researchCommunity]">{{communityTypes[currentMemberDetails.member.researchCommunity]}}</span><span *ngIf="communityTypes[currentMemberDetails.member.researchCommunity]&&currentMemberDetails.member.country"> | </span>{{currentMemberDetails.member.country}}</p>
                    <p *ngIf="currentMemberDetails.member.websiteUrl" class="clearfix"><a [href]="currentMemberDetails.member.websiteUrl" target="{{currentMemberDetails.member.publicDisplayName}}">{{currentMemberDetails.member.websiteUrl}}</a>
                    </p>
	            </div>
	            <div class="col-md-10 col-sm-10 col-xs-12">          
	                <p>
	                    <img class="member-logo" [src]="currentMemberDetails.member.logoUrl" *ngIf="currentMemberDetails.member.logoUrl">
	                    <span class="member-decsription" [innerHtml]="currentMemberDetails.member.description" *ngIf="currentMemberDetails.member.description"></span>
	                </p>
	            </div>
	            <hr />
	            <div class="col-md-12 col-sm-12 col-xs-12" *ngIf="currentMemberDetails.parentOrgName">              
	                <h3><@orcid.msg 'member_details.consortium_parent'/></h3>
	                <p> 
	                    <span *ngIf="currentMemberDetails.parentOrgName"><a [href]="getMemberPageUrl(currentMemberDetails.parentOrgSlug)">{{currentMemberDetails.parentOrgName}}</a></span>
	                    <span *ngIf="!currentMemberDetails.parentOrgName"><@orcid.msg 'member_details.none'/></span>
	                </p>
	                <hr />
	            </div>
	            <div class="col-md-12 col-sm-12 col-xs-12">   
	                <h3><@orcid.msg 'member_details.contact_information'/></h3>
	                <p *ngIf="currentMemberDetails.member.publicDisplayEmail">
	                    <a [href]="'mailto:' + currentMemberDetails.member.publicDisplayEmail">{{currentMemberDetails.member.publicDisplayEmail}}</a>
	                </p>
	                <p *ngIf="!currentMemberDetails.member.publicDisplayEmail"><@orcid.msg 'member_details.this_member_has_not_provided'/> 
	                </p>                  
	            </div> 
	            <hr />
	            <div class="col-md-12 col-sm-12 col-xs-12">   
	                <h3><@orcid.msg 'member_details.integrations'/></h3>
	                <div  *ngIf="currentMemberDetails.integrations">
		                <div *ngFor="let integration of currentMemberDetails.integrations">
		                    <p><b>{{integration.name}}</b>&nbsp;<em>{{integration.stage}}</em></p>
		                    <div *ngIf="integration.badgeAwarded">
		                        <div class="cc-badge authenticate popover-help-container" *ngIf="integration.level=='Collect'||integration.level=='Display'||integration.level=='Connect'||integration.level=='Sync'">
		                            <img src="${staticCdn}/img/cc_authenticate.png" height="34" width="34" alt="ORCID Authenticate badge" />
		                            <div id="cc-authenticate-help" class="popover bottom">
		                              <div class="arrow"></div>
		                              <div class="popover-content">
		                                <p><@orcid.msg 'member_list.details.authenticate_help_text'/></p>
		                              </div>
		                            </div>
		                        </div>
		                        <div class="cc-badge collect popover-help-container" *ngIf="integration.level=='Collect'||integration.level=='Display'||integration.level=='Connect'||integration.level=='Sync'">
		                            <img src="${staticCdn}/img/cc_collect.png" height="34" width="34" alt="ORCID Collect badge" />
		                            <div id="cc-collect-help" class="popover bottom">
		                              <div class="arrow"></div>
		                              <div class="popover-content">
		                                <p><@orcid.msg 'member_list.details.collect_help_text'/></p>
		                              </div>
		                            </div>
		                        </div>
		                        <div class="cc-badge display popover-help-container" *ngIf="integration.level=='Display'||integration.level=='Connect'||integration.level=='Sync'">
		                            <img src="${staticCdn}/img/cc_display.png" height="34" width="34" alt="ORCID Display badge" />
		                            <div id="cc-display-help" class="popover bottom">
		                              <div class="arrow"></div>
		                              <div class="popover-content">
		                                <p><@orcid.msg 'member_list.details.display_help_text'/></p>
		                              </div>
		                            </div>
		                        </div>
		                        <div class="cc-badge connect popover-help-container" *ngIf="integration.level=='Connect'||integration.level=='Sync'">
		                            <img src="${staticCdn}/img/cc_connect.png" height="34" width="34" alt="ORCID Connect badge" />
		                            <div id="cc-connect-help" class="popover bottom">
		                              <div class="arrow"></div>
		                              <div class="popover-content">
		                                <p><@orcid.msg 'member_list.details.connect_help_text'/></p>
		                              </div>
		                            </div>
		                        </div>
		                        <div class="cc-badge sync popover-help-container" *ngIf="integration.level=='Sync'">
		                            <img src="${staticCdn}/img/cc_sync.png" height="34" width="34" alt="ORCID Sync badge" />
		                            <div id="cc-sync-help" class="popover bottom">
		                                <div class="arrow"></div>
		                                <div class="popover-content">
		                                <p><@orcid.msg 'member_list.details.sync_help_text'/></p>
		                            </div>
		                        </div>
		                    </div>
		                </div>
		                <ul class="clearfix">
		                    <li [innerHtml]="integration.description" *ngIf="integration.description">
		                    </li>
		                    <li *ngIf="integration.resourceUrl" >
		                        <a [href]="integration.resourceUrl" target="Learn more about this integration"><@orcid.msg 'member_details.learn_more_about'/></a>
		                    </li>
		                </ul>
		            </div>
		        </div>
	            <div *ngIf="!currentMemberDetails.integrations.length"> 
	                <p><@orcid.msg 'member_details.this_member_has_not_completed'/></p>
	            </div>
	            <hr />
	        </div>
	        <div class="col-md-12 col-sm-12 col-xs-12" *ngIf="currentMemberDetails.subMembers.length">
	            <h3><@orcid.msg 'member_details.consortium_members'/></h3>
	            <table *ngIf="currentMemberDetails.subMembers">
	                <tr>
	                    <th><@orcid.msg 'member_details.member_name'/></th>
	                </tr>
	                <tr *ngFor="let subMember of currentMemberDetails.subMembers | orderBy : 'opportunity.accountName'">
	                    <td><a [href]="getMemberPageUrl(subMember.slug)">{{subMember.opportunity.accountName}}</a></td>
	                </tr>
	            </table>
	            <div *ngIf="!currentMemberDetails.subMembers.length"> 
	                <p><@orcid.msg 'member_details.this_consortium_does_not'/></p>
	                <hr />
	            </div>
	        </div>
	    </div>
    </div>
</script>