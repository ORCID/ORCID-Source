<script type="text/ng-template" id="works-merge-ng2-template"> 
    <div class="bulk-merge-modal"> 
      <div>
        <h3><@orcid.msg 'groups.merge.confirm.header'/></h3>
        <p *ngIf="!groupingSuggestion">
          {{mergeCount}} <@orcid.msg 'groups.merge.choose.preferred.detail'/><br>
          <a href="<@orcid.msg 'common.kb_uri_default'/>360006894774" target="privacyToggle.help.more_information"> <@orcid.msg 'groups.merge.helpPopoverMerge_2'/></a>
        </p>
        <p *ngIf="groupingSuggestion">
          <@orcid.msg 'groups.merge.suggestion.we_found'/> {{groupingSuggestion.suggestions.length}} <@orcid.msg 'groups.merge.suggestion.sets_of_works'/><br>
        


          <#--  <a href="<@orcid.msg 'common.kb_uri_default'/>360006894774" target="privacyToggle.help.more_information"> <@orcid.msg 'groups.merge.helpPopoverMerge_2'/></a>  -->
        </p>
        <p>
        <@orcid.msg 'groups.merge.suggestion.merged_works'/>
        </p>
      </div>
      <hr>
      <div *ngFor="let workToMerge of worksToMerge;let i=index">
        <div class="font-size-small line-height-normal">
         <input *ngIf="groupingSuggestion"  [(ngModel)]="checkboxFlag[workToMerge.putCode.value]" type="checkbox"/>
          <strong>{{workToMerge.title.value}}</strong><br/>
            <span class="rightBuffer">
              <@orcid.msg 'groups.common.source'/>: {{(workToMerge.sourceName == null || workToMerge.sourceName == '') ? workToMerge.source : workToMerge.sourceName }}
            </span>
            <span>
              <@orcid.msg 'groups.common.added'/>:
              {{workToMerge.createdDate | ajaxFormDateToISO8601}}
            </span>
        </div>
        <hr> 
      </div>              
      <div class="orcid-error"> 
          <span class="glyphicon glyphicon-exclamation-sign"></span>
          <@orcid.msg 'groups.merge.confirm.cannot_undo'/>
      </div>
      <div *ngIf="showWorksMergeError" class="orcid-error"> 
          <@orcid.msg 'groups.merge.error'/>
      </div>
      <div class="left topBuffer">     
        <button class="btn btn-primary" (click)="mergeConfirm()"><@orcid.msg 'freemarker.btnmerge'/></button>&nbsp;&nbsp;
        <button class="btn btn-white-no-border cancel-right" (click)="cancelEdit()">
          <@orcid.msg 'freemarker.btncancel'/>
        </button>
      </div>
    </div>        
</script>