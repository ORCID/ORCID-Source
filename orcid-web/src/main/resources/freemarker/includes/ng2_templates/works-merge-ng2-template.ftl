<script type="text/ng-template" id="works-merge-ng2-template">
  <!--Merge interface--> 
  <div class="bulk-merge-modal">
      <!--Warning if attempting to merge >= 20 works--> 
      <div *ngIf="showWorksMergeWarning" class="row">
          <div class="col-md-12 col-xs-12 col-sm-12">
              <h3><@orcid.msg 'groups.combine.confirm.are_you_sure'/></h3>
              <p><@orcid.msg 'groups.combine.confirm.you_are_attempting'/> {{mergeCount}} <@orcid.msg 'common.works.lower'/></p><p class="orcid-error"><b><@orcid.msg 'groups.combine.confirm.cannot_undo'/> <@orcid.msg 'groups.combine.confirm.do_you_really'/></b></p>
              <div *ngIf="showWorksMergeError" class="orcid-error"> 
                  <@orcid.msg 'groups.combine.error'/>
              </div>
              <div class="pull-right topBuffer">   
                <button class="btn btn-white-no-border" (click)="cancelEdit()"><@orcid.msg 'groups.combine.confirm.cancel_dont_merge' /></button>&nbsp;&nbsp;  
                <button class="btn btn-primary" (click)="dismissWarning()">
                  <@orcid.msg 'groups.combine.confirm.yes_continue' />
                </button>
              </div>
          </div>
      </div> 
      <div *ngIf="groupingSuggestion || (!groupingSuggestion && !showWorksMergeWarning)">
        <h3><@orcid.msg 'groups.combine.confirm.review_works'/></h3>
        <p *ngIf="!groupingSuggestion">
          {{mergeCount}} <@orcid.msg 'groups.combine.choose.preferred.detail'/><br>
          <a href="<@orcid.msg 'common.kb_uri_default'/>360006894774" target="privacyToggle.help.more_information"> <@orcid.msg 'groups.combine.helpPopover_2'/></a>
        </p>
        <p *ngIf="groupingSuggestion">
          <@orcid.msg 'groups.combine.suggestion.we_found'/> {{checkboxFlag.length}} <@orcid.msg 'groups.combine.suggestion.sets_of_works'/><br>
          <#--  <a href="<@orcid.msg 'common.kb_uri_default'/>360006894774" target="privacyToggle.help.more_information"> <@orcid.msg 'groups.combine.helpPopover_2'/></a>  -->
        </p>
        <p *ngIf="groupingSuggestion">
        <@orcid.msg 'groups.combine.suggestion.combined_works'/>
        </p>
      <input *ngIf="groupingSuggestion"  [(ngModel)]="selectAll" type="checkbox"  (change)="fieldChangeSelectAll($event)" />
      <hr>
      <ng-container *ngIf="!groupingSuggestion">
        <div *ngFor="let workToMerge of worksToMerge;let i=index">
          <div class="font-size-small line-height-normal">
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
      </ng-container>
      <ng-container *ngIf="groupingSuggestion">
        <ng-container *ngFor="let group of checkboxFlag">
          <div  class="merge-suggestions-list">
            <input  [(ngModel)]="group.state" type="checkbox"  (change)="fieldsChange($event)"/>
            <div class="font-size-small line-height-normal">
            <div *ngFor="let work of group.groupingSuggestion">
                      <strong>{{work.title.value}}</strong><br/>
                <span class="rightBuffer">
                  <@orcid.msg 'groups.common.source'/>: {{(work.sourceName == null || work.sourceName == '') ? work.source : work.sourceName }}
                </span>
                <span>
                  <@orcid.msg 'groups.common.added'/>:
                  {{work.createdDate | ajaxFormDateToISO8601}}
                </span>
            </div>
            </div>
          </div>
          <hr> 
        </ng-container> 
      </ng-container> 
      <!--Merge errors-->     
      <div class="orcid-error"> 
          <span class="glyphicon glyphicon-exclamation-sign"></span>
          <@orcid.msg 'groups.combine.confirm.cannot_undo'/>
      </div>
      <div *ngIf="showWorksMergeError" class="orcid-error"> 
          <@orcid.msg 'groups.combine.error'/>
      </div>
      <!--Merge buttons-->  
      <div class="pull-right topBuffer bottomBuffer">   
        <button class="btn btn-white-no-border" (click)="cancelEdit()">
          <@orcid.msg 'freemarker.btncancel'/>
        </button>&nbsp;&nbsp;  
        <button [disabled]="!atLeastOneWorksSelectForMerge() && groupingSuggestion" class="btn btn-primary" (click)="mergeConfirm()"><@orcid.msg 'freemarker.btncombine'/></button>
      </div>
    </div>
  </div>        
</script>