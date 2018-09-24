<script type="text/ng-template" id="works-merge-ng2-template">
    <div class="bulk-delete-modal">     
      <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
          <h3><@orcid.msg 'groups.merge.confirm.header'/></h3>
          <div class="orcid-error">
            <p *ngIf="externalIdsPresent">
              <@orcid.msg 'groups.merge.confirm.line_1'/>
            </p>
            <p *ngIf="externalIdsPresent">
              <@orcid.msg 'groups.merge.confirm.line_2'/>
            </p>
            <p *ngIf="!externalIdsPresent">
              <@orcid.msg 'groups.merge.no_external_ids_1'/><a href="https://support.orcid.org/hc/articles/360006894774"><@orcid.msg 'groups.merge.no_external_ids_2'/></a>
            </p>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12"> 
          <div class="right">     
            <button *ngIf="externalIdsPresent" class="btn btn-primary" (click)="mergeContinue()"><@orcid.msg 'freemarker.btncontinue'/></button>&nbsp;&nbsp;
            <a (click)="cancelEdit()">
              <@orcid.msg 'freemarker.btncancel'/>
            </a>  
          </div>        
        </div>
      </div>
    </div>
</script>