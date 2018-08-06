<script type="text/ng-template" id="works-merge-choose-preferred-version-ng2-template">
    <div class="bulk-delete-modal">     
      <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
          <h3><@orcid.msg 'groups.merge.confirm.header'/></h3>
          <div class="orcid-error">
            <p>
              <@orcid.msg 'groups.merge.choose.preferred.detail'/>
            </p>
            <p>
              <@orcid.msg 'groups.merge.choose.preferred'/>
            </p>
          </div>
        </div>
      </div>
      <span *ngFor='let workToMerge of worksToMerge'>
        <div class="row">
            <div class="col-md-1 col-sm-2 col-xs-2">
                <input type="radio" name="preferred" [checked]="workToMerge.preferred" [id]="workToMerge.work.putCode.value" (change)="selectPreferred(workToMerge)"  />
            </div>
            <div class="col-md-11 col-sm-10 col-xs-10">
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
            <button class="btn btn-primary" (click)="merge()"><@orcid.msg 'freemarker.btnmerge'/></button>&nbsp;&nbsp;
            <a (click)="cancelEdit()">
              <@orcid.msg 'freemarker.btncancel'/>
            </a>  
          </div>        
        </div>  
      </div>
    </div>
</script>