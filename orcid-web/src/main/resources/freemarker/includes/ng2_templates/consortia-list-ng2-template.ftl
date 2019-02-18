<script type="text/ng-template" id="consortia-list-ng2-template">
    <div>
        <div *ngIf="!membersList" class="text-center">
            <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
            <!--[if lt IE 8]>
                <img src="{{assetsPath}}/img/spin-big.gif" width="85" height ="85"/>
            <![endif]-->
        </div>
        <div *ngIf="membersList">
            <p><@orcid.msg 'member_list.there_are_currently'/> {{membersList.length}} <@orcid.msg 'member_list.orcid_consortia_members'/></p>
            <p>
        		<a href="<@orcid.rootPath '/members'/>"><@orcid.msg 'member_details.all_members'/></a> | <a class="selected" href="<@orcid.rootPath '/consortia'/>"><@orcid.msg 'member_list.consortia_members'/></a>
        	<p>
				<select [(ngModel)]="byCountry" (change)="filterSelected()">
    				<option value="" selected><@orcid.msg 'macros.orcid.Country'/></option>
	    			<option *ngFor="let member of unfilteredMembersList | unique:'country' | orderBy : 'country'" [value]="member.country">{{member.country}}</option>
				</select>
				<select [(ngModel)]="byResearchCommunity" (change)="filterSelected()">
    				<option value="" selected><@orcid.msg 'member_list.research_community'/></option>
	    			<option *ngFor="let member of unfilteredMembersList |  unique:'researchCommunity' | orderBy : 'researchCommunity'" [value]="member.researchCommunity">{{communityTypes[member.researchCommunity]}}</option>
				</select>
				<button class="btn btn-primary" (click)="clearFilters()">Reset</button>
			</p>
				
			<hr class="no-margin-top no-margin-bottom" />
			<ul class="filter">
				<li (click)="activateLetter('')" [ngClass]="{'active':activeLetter==''}"><a>ALL</a></li>
				<li *ngFor="let letter of alphabet" (click)="activateLetter(letter)" [ngClass]="{'active':letter==activeLetter}"><a>{{letter}}</a></li>
			</ul>
            <div class="member" *ngFor="let member of membersList">
                <hr class="no-margin-top" />
                	<div class="col-md-12 col-sm-12 col-xs-12">
                    	<h2 ><a [href]="getMemberPageUrl(member.slug)" target="member.publicDisplayName">{{member.publicDisplayName}}</a></h2>
                    	<p><span *ngIf="communityTypes[member.researchCommunity]">{{communityTypes[member.researchCommunity]}}</span><span *ngIf="communityTypes[member.researchCommunity]&&member.country"> | </span>{{member.country}}</p>
                    </div>
                    <div class="col-md-10 col-sm-10 col-xs-12">
                    	<p>
                        	<img class="member-logo" [src]="member.logoUrl"  *ngIf="member.logoUrl">
                    	    <span class="member-description" [innerHtml]="member.description" *ngIf="member.description"></span>
                    	</p>
                    </div>
            </div>
        </div>
    </div>
</script>