<script type="text/ng-template" id="works-merge-suggestions-ng2-template">
    <div class="bulk-delete-modal">     
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <h3><@orcid.msg 'groups.merge.suggestion.header'/></h3>
                <div class="orcid-error">
                    <p>
                        <@orcid.msg 'groups.merge.suggestion.detail'/>
                    </p>
                </div>
            </div>
        </div>
        <span *ngFor='let workToMerge of worksToMerge'>
            <div class="row">
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <label [for]="workToMerge.work.putCode.value">
                        {{workToMerge.work.title.value}}<br/>
                        <@orcid.msg 'groups.common.source'/>: {{(workToMerge.work.sourceName == null || workToMerge.work.sourceName == '') ? workToMerge.work.source : workToMerge.work.sourceName }}
                    </label>
                </div>
            </div>                    
        </span>
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12"> 
            <div class="right">     
                <button class="btn btn-primary" (click)="accept()"><@orcid.msg 'groups.merge.suggestion.accept'/></button>&nbsp;&nbsp;
                <a (click)="cancel()">
                    <@orcid.msg 'groups.merge.suggestion.reject'/>
                </a>  
            </div>        
        </div>  
    </div>
</script>