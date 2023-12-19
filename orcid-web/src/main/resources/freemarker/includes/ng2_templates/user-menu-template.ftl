<script type="text/ng-template" id="user-menu-template">

      <div class="form-group " role="presentation">
                    <div class="search-container" role="navigation" aria-label="user menu" (mouseleave)="state = false">
                         <a *ngIf="!userInfo"  class="top-menu-button hide-on-mobile" href="{{getBaseUri()}}/signin">  
                          {{'${springMacroRequestContext.getMessage("public-layout.sign_in")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase  }}/{{'${springMacroRequestContext.getMessage("header.register")?replace("<br />", " ")?replace("'", "\\'")}'| uppercase }}
                         </a>
                        <div *ngIf="userInfo && nameForm"  class="top-menu-button" (click)="state = !state" (keyup.enter)="state = !state" tabindex="0" >  
                            <img src="{{assetsPath + '/img/svg/profile-icon.svg'}}" role="presentation">
                            <div class="name" *ngIf="!isMobile"> 
                                        <ng-container *ngIf="displayFullName()"> {{nameForm?.creditName?.value}}  </ng-container>
                                        <ng-container *ngIf="displayPublishedName()"> {{nameForm?.givenNames?.value}} {{nameForm?.familyName?.value}}  </ng-container> 
                            </div> 
                            <div class="more blue"  [ngClass]="{'less' : state}"  *ngIf="!isMobile"> </div>
                            
                        </div>

                         <div class="top-menu" *ngIf="state && userInfo && nameForm">
                            <a  class="top-menu-header" href="{{getBaseUri()}}/my-orcid">   
                                <img src="{{assetsPath + '/img/svg/profile-icon.svg'}}" role="presentation">
                            
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
                                <#--  INBOX  -->
                                <a  href="{{getBaseUri()}}/inbox" class="top-menu-item">
                                    <img src="{{assetsPath + '/img/svg/baseline-inbox-24px.svg'}}">
                                    
                                    {{'${springMacroRequestContext.getMessage("workspace.notifications")}' }} <span *ngIf="getUnreadCount > 0">({{getUnreadCount}})</span>
                                </a>
                                <#--  ACCOUNT SETTINGS  -->
                                 <a class="top-menu-item" *ngIf="(userInfo['IN_DELEGATION_MODE'] == 'false' || userInfo['DELEGATED_BY_ADMIN'] == 'true') "  href="{{getBaseUri()}}/account">
                                    <img src="{{assetsPath + '/img/svg/baseline-settings-20px.svg'}}">
                                    {{'${springMacroRequestContext.getMessage("public-layout.account_setting")?replace("<br />", " ")?replace("'", "\\'")}' }}
                                </a>
                                <#--  TRUSTED PARTIES -->
                                <a class="top-menu-item" href="{{getBaseUri()}}/trusted-parties">
                                    <img src="{{assetsPath + '/img/svg/vpn_key_FILL1_wght400_GRAD0_opsz20.svg'}}">
                                    {{'${springMacroRequestContext.getMessage("public-layout.trusted_individuals")?replace("<br />", " ")?replace("'", "\\'")}' }}
                                </a>
                                <#--  (GROUP) DEVELOPER TOOLS  -->
                                <a class="top-menu-item" *ngIf="(userInfo['IN_DELEGATION_MODE'] == 'false' || userInfo['DELEGATED_BY_ADMIN'] == 'true') && userInfo['MEMBER_MENU']=='true'" href="{{getBaseUri()}}/group/developer-tools">
                                    <img src="{{assetsPath + '/img/svg/baseline-code-24px.svg'}}"> 
                                    {{'${springMacroRequestContext.getMessage("workspace.developer_tools")}' }}
                                </a>
                                 <#--  DEVELOPER TOOLS  -->
                                <a class="top-menu-item" *ngIf="(userInfo['IN_DELEGATION_MODE'] == 'false' || userInfo['DELEGATED_BY_ADMIN'] == 'true') && userInfo['MEMBER_MENU']!='true'" href="{{getBaseUri()}}/developer-tools">
                                    <img src="{{assetsPath + '/img/svg/baseline-code-24px.svg'}}"> 
                                    {{'${springMacroRequestContext.getMessage("workspace.developer_tools")}' }}
                                </a>
                                <#--  MEMBER TOOLS  (SELF SERVICE)-->
                                <a  class="top-menu-item"   *ngIf="userInfo['SELF_SERVICE_MENU']"  href="{{getBaseUri()}}/self-service">
                                    <img src="{{assetsPath + '/img/svg/baseline-build-24px.svg'}}"> 
                                    {{'${springMacroRequestContext.getMessage("workspace.self_service")?replace("<br />", " ")?replace("'", "\\'")}'  }}
                                </a>
                                <#--  MANAGE MEMBERS  -->
                                <a  class="top-menu-item" *ngIf="userInfo['ADMIN_MENU']" href="{{getBaseUri()}}/manage-members" >
                                    <img src="{{assetsPath + '/img/svg/baseline-group-24px.svg'}}"> 
                                    {{'${springMacroRequestContext.getMessage("admin.members.workspace_link")?replace("<br />", " ")?replace("'", "\\'")}' }}
                                </a>
                                <#--  ADMIN ACTIONS  -->
                                <a  class="top-menu-item" *ngIf="userInfo['ADMIN_MENU']" href="{{getBaseUri()}}/admin-actions">
                                    <img src="{{assetsPath + '/img/svg/baseline-verified_user-24px.svg'}}"> 
                                    {{'${springMacroRequestContext.getMessage("admin.workspace_link")?replace("<br />", " ")?replace("'", "\\'")}'}}
                                </a>
                                <#--  SIGN OUT  -->
                                <a  class="top-menu-item" href="{{getBaseUri()}}/signout">
                                    <img src="{{assetsPath + '/img/svg/baseline-exit_to_app-24px.svg'}}"> 
                                    {{'${springMacroRequestContext.getMessage("public-layout.sign_out")?replace("<br />", " ")?replace("'", "\\'")}' }}
                                </a>

                                
                                 
                            </div>
                            
                   
</script>