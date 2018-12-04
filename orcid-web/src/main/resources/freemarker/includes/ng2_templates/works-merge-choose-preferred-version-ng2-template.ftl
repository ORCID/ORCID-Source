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
              </div>
          </span>
        </div>
      </div>
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
          <hr>
          <span *ngFor='let workToMerge of worksToMerge'>
            <div class="row">
                <div class="col-md-11 col-sm-10 col-xs-10">
                    {{workToMerge.work.title.value}}<br/>
                    <@orcid.msg 'groups.common.source'/>: {{(workToMerge.work.sourceName == null || workToMerge.work.sourceName == '') ? workToMerge.work.source : workToMerge.work.sourceName }}
                </div>
            </div> 
            <hr>                   
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