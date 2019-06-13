<script type="text/ng-template" id="user-menu-template">

      <div class="form-group " role="presentation">
                    <div class="search-container" role="search"> 
                         
                        <div class="top-menu-button" (click)="state = !state">  
                            <img src="{{assetsPath + '/img/svg/profile-icon.svg'}}">
                            <div class="name" *ngIf="!isMobile"> Jane Doe </div> 
                            <div class="more blue" *ngIf="!isMobile"> </div>
                            
                        </div>

                         <div class="top-menu" *ngIf="state">
                            <div class="top-menu-header">   
                                <img src="{{assetsPath + '/img/svg/profile-icon.svg'}}">
                            
                                <div>
                                    <div class="name"> Jane Doe </div> 
                                    <div class="more"> View my ORCID record </div> 
                                </div>
                            </div>
                            <div class="division"></div>
                            <div class="top-menu-items">
                                <div class="top-menu-item">
                                    <img src="{{assetsPath + '/img/svg/baseline-inbox-24px.svg'}}">
                                    
                                    Inbox
                                </div>
                                <div class="top-menu-item">
                                    <img src="{{assetsPath + '/img/svg/baseline-settings-20px.svg'}}">
                                    Account settings
                                </div>
                                <div class="top-menu-item">
                                    <img src="{{assetsPath + '/img/svg/baseline-code-24px.svg'}}"> 
                                    Developer Tools
                                </div>
                                <div class="top-menu-item">
                                    <img src="{{assetsPath + '/img/svg/baseline-build-24px.svg'}}"> 
                                    Member tools 
                                </div>
                                <div class="top-menu-item">
                                    <img src="{{assetsPath + '/img/svg/baseline-group-24px.svg'}}"> 
                                    Manage members
                                </div>
                                <div class="top-menu-item">
                                    <img src="{{assetsPath + '/img/svg/baseline-verified_user-24px.svg'}}"> 
                                    Admin page
                                </div>
                                <div class="top-menu-item">
                                    <img src="{{assetsPath + '/img/svg/baseline-exit_to_app-24px.svg'}}"> 
                                    Log out
                                </div>
                            </div>
                            
                   
</script>