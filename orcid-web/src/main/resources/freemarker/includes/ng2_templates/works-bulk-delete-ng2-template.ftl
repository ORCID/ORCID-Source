<script type="text/ng-template" id="works-bulk-delete-ng2-template">
    <div class="bulk-delete-modal">     
      <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
          <h3><@orcid.msg 'groups.bulk_delete.confirm.header'/></h3>
          <div class="orcid-error">
            <p>
              <@orcid.msg 'groups.bulk_delete.confirm.line_1'/>
            </p>
            <p>
              <@orcid.msg 'groups.bulk_delete.confirm.line_2'/>
            </p>
            <p [ngClass]="{'red-error':bulkDeleteSubmit == true}">
              <@orcid.msg 'groups.bulk_delete.confirm.line_3'/> <input [ngClass]="{'red-border-error':bulkDeleteSubmit == true}" type="text" size="3" [(ngModel)]="delCountVerify"/>
            </p>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12"> 
          <div class="right">     
            <button class="btn btn-danger" (click)="deleteBulk()"><@orcid.msg 'freemarker.btnDelete'/></button>&nbsp;&nbsp;
            <a (click)="cancelEdit()">
              <@orcid.msg 'freemarker.btncancel'/>
            </a>  
          </div>        
        </div>
      </div>
    </div>
</script>