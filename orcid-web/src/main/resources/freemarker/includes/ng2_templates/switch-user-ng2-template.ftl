<script type="text/ng-template" id="switch-user-ng2-template">
    <#if springMacroRequestContext.requestUri?contains("/my-orcid")>
        <div class="dropdown id-banner-container" *ngIf="(me || unfilteredLength > 0)">
            <a (click)="openMenu($event)" class="id-banner-switch"><@orcid.msg 'public-layout.manage_proxy_account'/><span class="glyphicon glyphicon-chevron-right"></span></a>
            <ul class="dropdown-menu id-banner-dropdown" *ngIf="isDroppedDown">
                <li>
                    <input id="delegators-search" type="text" [(ngModel)]="searchTerm" (change)="search()" placeholder="<@orcid.msg 'manage_delegators.search.placeholder'/>"/>
                </li>
                <li *ngIf="me && !searchTerm">
                    <a (click)="switchUser(me.giverOrcid.path)">
                        <ul>
                            <li><@orcid.msg 'id_banner.switchbacktome'/></li>                                       
                            <li>{{me.giverOrcid.uri}}</li>                                                          
                        </ul>
                    </a>
                </li>
                <li *ngFor="let delegationDetails of delegators | orderBy:'giverName.value' | slice:0:10">
                    <a (click)="switchUser(delegationDetails.giverOrcid.path)">
                        <ul>
                            <li>{{delegationDetails.giverName.value}}</li>
                            <li>{{delegationDetails.giverOrcid.uri}}</li>                                       
                        </ul>
                    </a>
                </li>
                <li *ngIf="delegators.length > 10"><a href="<@orcid.rootPath '/delegators?delegates'/>"><@orcid.msg 'id_banner.more'/></a></li>
            </ul>
        </div> 
    </#if>
    <#if springMacroRequestContext.requestUri?contains("/oauth/authorize")>
        <div>
            <div class="dropdown id-banner-container" *ngIf="(me || unfilteredLength > 0)">
                <a (click)="openMenu($event)" class="id-banner-switch">
                    <div class="orcid-id-container">
                            {{getBaseUri()}}/{{requestInfoForm?.userOrcid}}
                        <span class="glyphicon glyphicon-chevron-down"></span>
                    </div>
                </a>
                <div class="dropdown-menu id-banner-dropdown" *ngIf="isDroppedDown">
                    <div class="id-banner-header"><@orcid.msg'public-layout.manage_proxy_account'/></div>
                    <ul class="id-banner-dropdown">
                        <li>
                            <input id="delegators-search" type="text" [(ngModel)]="searchTerm" (change)="search()" placeholder="<@orcid.msg 'manage_delegators.search.placeholder'/>"/>
                        </li>
                        <li *ngIf="me && !searchTerm">
                            <a (click)="switchUser(me.giverOrcid.path)">
                                <ul>
                                    <li><@orcid.msg 'id_banner.switchbacktome'/></li>                                       
                                    <li>{{me.giverOrcid.uri}}</li>                                                          
                                </ul>
                            </a>
                        </li>
                        <li *ngFor="let delegationDetails of delegators | orderBy:'giverName.value' | slice:0:10">
                            <a (click)="switchUser(delegationDetails.giverOrcid.path)">
                                <ul>
                                    <li>{{delegationDetails.giverName.value}}</li>
                                    <li>{{delegationDetails.giverOrcid.uri}}</li>                                       
                                </ul>
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
            <div *ngIf="!(me || unfilteredLength > 0)">
                <div class="pull-right">
                        <a href="{{getBaseUri()}}/{{requestInfoForm?.userOrcid}}" target="userOrcid">{{getBaseUri()}}/{{requestInfoForm?.userOrcid}}</a>
                </div>
            </div>
        </div> 
    </#if>                   
</script>