<script type="text/ng-template" id="works-merge-choose-preferred-version-ng2-template">
    <div class="bulk-delete-modal">     
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
          <h3><@orcid.msg 'groups.merge.confirm.header'/></h3>
          <span *ngIf='externalIdsPresent'>
              <div class="orcid-error">
                <p>
                  <@orcid.msg 'groups.merge.choose.preferred.detail'/>
                </p>
                <p>
                  <@orcid.msg 'groups.merge.choose.preferred'/>
                </p>
              </div>
          </span>
        </div>
      </div>
      <div class="orcid-error" *ngIf='preferredNotSelected'>
         <@orcid.msg 'groups.merge.preferred_not_selected'/>
      </div>
      
      <span>{{externalIdsPresent}}</span>
      <div class="orcid-error" *ngIf='externalIdsPresent == false'>
         <@orcid.msg 'groups.merge.no_external_ids_1'/><a href="https://support.orcid.org/hc/articles/360006894774"><@orcid.msg 'groups.merge.no_external_ids_2'/></a>
         <div class="row"> 
            <div class="right">     
                <a (click)="cancelEdit()">
                  <@orcid.msg 'freemarker.btncancel'/>
                </a>  
            </div>        
         </div>  
      </div>
      <span *ngIf='externalIdsPresent == true'>
          <span *ngFor='let workToMerge of worksToMerge'>
            <div class="row">
                <div class="col-md-1 col-sm-2 col-xs-2">
                    <input type="radio" name="preferred" [disabled]="!workToMerge.work.userSource && workToMerge.work.workExternalIdentifiers.length == 0" [checked]="workToMerge.preferred" [id]="workToMerge.work.putCode.value" (change)="selectPreferred(workToMerge)"  />
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
            <div class="col-md-6 col-sm-12 col-xs-12"> 
                <span class="glyphicon glyphicon-exclamation-sign"></span>
                <@orcid.msg 'groups.merge.confirm.cannot_undo'/>
            </div>
            <div class="col-md-6 col-sm-12 col-xs-12"> 
              <div class="right">     
                <button class="btn btn-primary" (click)="merge()"><@orcid.msg 'freemarker.btnmerge'/></button>&nbsp;&nbsp;
                <a (click)="cancelEdit()">
                  <@orcid.msg 'freemarker.btncancel'/>
                </a>  
              </div>        
            </div>  
          </div>
      </span>
    </div>
</script>