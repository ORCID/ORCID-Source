<@public>
	<div id="home" class="content col-md-10">
		<div class="headline col-md-offset-1">
			<h1><@orcid.msg 'home.distinguish_yourself'/><br />
			<span class="highlight"><@orcid.msg 'home.three_easy'/></span>
			</h1>
			<p class="description"><@orcid.msg 'home.orcid_provides'/> <a href="${baseUri}/about/what-is-orcid"><@orcid.msg 'home.find_out_more'/></a></p>
			<!-- Step 1 -->
			<div class="row">
				<div class="col-md-12 step">
					<span class="step-headline"><@orcid.msg 'home.one'/></span> <span class="step-subheadline"><@orcid.msg 'home.register'/></span> <span class="step-detail"><@orcid.msg 'home.get_your'/> <a href="/register"><@orcid.msg 'home.register_now'/></a><br />
					<@orcid.msg 'home.registration_takes'/></span>
				</div>
			</div>
		</div><!--Closes headline col-md-offset-1-->
		<!-- Step 2 -->
		<div class="row">
			<div class="col-md-12 step">
				<span class="step-headline"><@orcid.msg 'home.two'/></span> <span class="step-subheadline"><@orcid.msg 'home.add_your'/><br />
				<@orcid.msg 'home.info'/></span> <span class="step-detail"><@orcid.msg 'home.enhance_your'/> </span>
			</div>
		</div>
		<!-- Step 3 -->
		<div class="row">
			<div class="col-md-10 col-md-offset-2 step">
				<span class="step-headline"><@orcid.msg 'home.three'/></span> <span class="step-subheadline"><@orcid.msg 'home.user_your'/><br />
				<@orcid.msg 'home.orcid_id'/></span> <span class="step-detail"><@orcid.msg 'home.include_your'/></span>
			</div>
		</div>
		<!-- Members Section -->
		<div class="headline col-md-offset-1">
			<h2><@orcid.msg 'home.members_make'/></h2>
			<p class="description"><@orcid.msg 'home.orcid_is'/></p>
			<p class="description">
			<strong><@orcid.msg 'home.curious_about'/> </strong><a href="/members"><@orcid.msg 'home.see_our'/></a>
			</p>
		</div>
	</div>
<script type="text/ng-template" id="home-ng2-template">
	<!--Latest News Column-->
	<div id="home-blog-list" class="aside col-md-2">
		<div class="inner-box">
			<h3><@orcid.msg 'home.latest_news'/></h3>
			<div class="item-list">
				<ul *ngIf="blogFeed['rss']" >
					<li *ngFor="let item of blogFeed['rss']['channel']['item'] | slice:0:5; let i=index">
					<strong>{{item['pubDate'] | slice:0:16}}</strong><br>
					<a href="{{item['link']}}">{{item['title']}}</a>
                	</li>
				</ul>
			</div>
			<p>
				<strong><a href="${baseUri}/about/news"><@orcid.msg 'home.more_news'/></a></strong>
			</p>
		</div>
	</div>
</script>
<home-ng2></home-ng2>
</@public>