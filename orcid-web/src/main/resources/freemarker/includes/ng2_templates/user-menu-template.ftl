<script type="text/ng-template" id="user-menu-template">

      <div class="form-group " role="presentation">
                    <div class="search-container" role="search" (mouseleave)="state = false">
                         <a *ngIf="!userInfo"  class="top-menu-button" href="{{getBaseUri()}}/signin">  
                          {{'<@orcid.msg 'public-layout.sign_in'/> '| uppercase  }}/{{'<@orcid.msg 'header.register'/> '| uppercase }}
                         </a>
                        <div *ngIf="userInfo"  class="top-menu-button" (click)="state = !state">  
                            <img src="{{assetsPath + '/img/svg/profile-icon.svg'}}">
                            <div class="name" *ngIf="!isMobile"> 
                                        <ng-container *ngIf="displayFullName()"> {{nameForm?.creditName?.value}}  </ng-container>
                                        <ng-container *ngIf="displayPublishedName()"> {{nameForm?.givenNames?.value}} {{nameForm?.familyName?.value}}  </ng-container> 
                            </div> 
                            <div class="more blue"  [ngClass]="{'less' : state}"  *ngIf="!isMobile"> </div>
                            
                        </div>

                         <div class="top-menu" *ngIf="state">
                            <a  class="top-menu-header" href="{{getBaseUri()}}/my-orcid">   
                                <img src="{{assetsPath + '/img/svg/profile-icon.svg'}}">
                            
                                <div>
                                    <div class="name"> 
                                        <ng-container *ngIf="displayFullName()"> {{nameForm?.creditName?.value}}  </ng-container>
                                        <ng-container *ngIf="displayPublishedName()"> {{nameForm?.givenNames?.value}} {{nameForm?.familyName?.value}}   </ng-container>
                                    </div>
                                    <div class="my-profile"> View my ORCID record </div> 
                                </div>
                            </a>
                            <div class="division"></div>
                            <div class="top-menu-items">
                                <a  href="{{getBaseUri()}}/inbox" class="top-menu-item">
                                    <img src="{{assetsPath + '/img/svg/baseline-inbox-24px.svg'}}">
                                    
                                    {{'${springMacroRequestContext.getMessage("workspace.notifications")}' }} <span *ngIf="getUnreadCount > 0">({{getUnreadCount}})</span>
                                </a>
                                <a class="top-menu-item" href="{{getBaseUri()}}/account">
                                    <img src="{{assetsPath + '/img/svg/baseline-settings-20px.svg'}}">
                                    {{'<@orcid.msg 'public-layout.account_setting'/>  ' }}
                                </a>
                                <a class="top-menu-item" *ngIf="(userInfo['IN_DELEGATION_MODE'] == 'false' || userInfo['DELEGATED_BY_ADMIN'] == 'true') && userInfo['MEMBER_MENU']=='true'" href="{{getBaseUri()}}/group/developer-tools">
                                    <img src="{{assetsPath + '/img/svg/baseline-code-24px.svg'}}"> 
                                    {{'${springMacroRequestContext.getMessage("workspace.developer_tools")}' }}
                                </a>
                                <a class="top-menu-item" *ngIf="(userInfo['IN_DELEGATION_MODE'] == 'false' || userInfo['DELEGATED_BY_ADMIN'] == 'true') && userInfo['MEMBER_MENU']!='true'" href="{{getBaseUri()}}/developer-tools">
                                    <img src="{{assetsPath + '/img/svg/baseline-code-24px.svg'}}"> 
                                    {{'${springMacroRequestContext.getMessage("workspace.developer_tools")}' }}
                                </a>
                                <a  class="top-menu-item"   *ngIf="userInfo['SELF_SERVICE_MENU']"  href="{{getBaseUri()}}/manage-members">
                                    <img src="{{assetsPath + '/img/svg/baseline-build-24px.svg'}}"> 
                                    {{'<@orcid.msg 'workspace.self_service' />  ' }}
                                </a>
                                <a  class="top-menu-item"*ngIf="userInfo['ADMIN_MENU']" href="{{getBaseUri()}}/manage-members" >
                                    <img src="{{assetsPath + '/img/svg/baseline-group-24px.svg'}}"> 
                                    {{'<@orcid.msg 'admin.members.workspace_link' />  ' }}
                                </a>
                                <a  class="top-menu-item" *ngIf="userInfo['ADMIN_MENU']" href="{{getBaseUri()}}/admin-actions">
                                    <img src="{{assetsPath + '/img/svg/baseline-verified_user-24px.svg'}}"> 
                                    {{'<@orcid.msg 'admin.workspace_link' />  ' }}
                                </a>
                                <a  class="top-menu-item" href="{{getBaseUri()}}/signout">
                                    <img src="{{assetsPath + '/img/svg/baseline-exit_to_app-24px.svg'}}"> 
                                    {{'<@orcid.msg 'public-layout.sign_out'/>  ' }}
                                </a>

                                
                                 
                            </div>
                            
                   
</script>