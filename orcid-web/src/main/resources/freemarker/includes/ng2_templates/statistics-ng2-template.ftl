<script type="text/ng-template" id="statistics-ng2-template">
<div id="statistics">
    <div class="row">       
        <div class="col-md-9 col-md-offset-3">
            <h1><@orcid.msg 'statistics.header'/></h1>
        </div>
    </div>
    <div *ngIf="stats.liveIds" class="row">    
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">
                <span class="stat-name"><@orcid.msg 'statistics.live_ids'/></span>
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right stat-subheader">
            <span class="stat-data">{{stats.liveIds}}</span>
        </div>    
    </div>
    <div *ngIf="stats.idsWithExternalId" class="row">        
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.ids_with_external_id'/></span><br>
            <span class="stat-name-st"><@orcid.msg 'statistics.ids_with_external_id.subtext'/></span>
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.idsWithExternalId}}</span>
        </div>    
    </div>
    <div *ngIf="stats.idsWithEducation" class="row"> 
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">
            <h2>Education</h2> 
        </div>
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.ids_with_education'/></span>
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.idsWithEducation}}</span>
        </div>    
    </div>
    <div *ngIf="stats.idsWithEducation" class="row">        
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.number_of_education'/></span>       
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.numEducations}}</span>
        </div>
    </div>
    <div *ngIf="stats.idsWithEducation" class="row">        
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.number_of_education_unique_org'/></span>        
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.educationUniqueOrgs}}</span>
        </div>
    </div> 
    <div *ngIf="stats.idsWithEmployment" class="row">  
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">   
            <h2>Employment</h2> 
        </div>   
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.ids_with_employment'/></span>
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.idsWithEmployment}}</span>
        </div>    
    </div>
    <div *ngIf="stats.idsWithEmployment" class="row">        
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.number_of_employment'/></span>      
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.numEmployments}}</span>
        </div>
    </div>
    <div *ngIf="stats.idsWithEmployment" class="row">        
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.number_of_employment_unique_org'/></span>       
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.employmentUniqueOrgs}}</span>
        </div>
    </div>
    <div *ngIf="stats.idsWithFunding" class="row"> 
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">
            <h2>Funding</h2> 
        </div>       
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.ids_with_funding'/></span>
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.idsWithFunding}}</span>
        </div>    
    </div>
    <div *ngIf="stats.idsWithFunding" class="row">        
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.number_of_funding'/></span>     
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.numFundings}}</span>
        </div>
    </div>
    <div *ngIf="stats.idsWithFunding" class="row">        
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.number_of_funding_unique_org'/></span>      
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.fundingUniqueOrgs}}</span>
        </div>
    </div>
    <div *ngIf="stats.idsWithPeerReview" class="row"> 
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">
            <h2>Peer review</h2> 
        </div>        
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.ids_with_peer_review'/></span>
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.idsWithPeerReview}}</span>
        </div>    
    </div>
    <div *ngIf="stats.idsWithPeerReview" class="row">        
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.number_of_peer_review'/></span>     
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.numPeerReviews}}</span>
        </div>
    </div>
    <div *ngIf="stats.idsWithPersonId" class="row">    
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">
            <h2>Person identifiers</h2> 
        </div>
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.ids_with_person_id'/></span>
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.idsWithPersonId}}</span>
        </div>    
    </div>
    <div *ngIf="stats.idsWithPersonId" class="row">        
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.number_of_person_id'/></span>       
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.numPersonIds}}</span>
        </div>
    </div>
    <div *ngIf="stats.idsWithWork" class="row">   
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">
            <h2>Works</h2> 
        </div>     
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.ids_with_works'/></span>
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.idsWithWork}}</span>
        </div>    
    </div>
    <div *ngIf="stats.idsWithWork" class="row">        
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.number_of_works'/></span><br>
            <span class="stat-name-st"><@orcid.msg 'statistics.number_of_works.subtext' /></span>
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.numWorks}}</span>   
        </div>    
    </div>
    <div *ngIf="stats.idsWithWorks && stats.idsWithWork" class="row">        
        <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
            <span class="stat-name"><@orcid.msg 'statistics.number_of_unique_dois'/></span>     
        </div>
        <div class="col-md-3 col-sm-4 col-xs-5 right">
            <span class="stat-data">{{stats.uniqueDois}}</span>
        </div>
    </div>
    
    
    <div class="row">        
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
            <span class="stat-date"><@orcid.msg 'statistics.statistics_generation_date_label'/>&nbsp;<span *ngIf="stats.statisticsDate">{{stats.statisticsDate}}</span><span *ngIf="!stats.statisticsDate">NA</span></span>      
        </div>          
    </div>
</div>
</script>