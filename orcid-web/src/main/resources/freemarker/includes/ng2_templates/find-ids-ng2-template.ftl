<script type="text/ng-template" id="find-ids-ng2">
    <div class="workspace-accordion-item" >
        <p>
            <a  *ngIf="showSection" (click)="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.find_ids' /></a>
            <a  *ngIf="!showSection" (click)="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.find_ids' /></a>
        </p>                
        <div class="collapsible bottom-margin-small admin-modal" id="find_ids_section" style="display:none;">
            <div class="form-group">
                <label for="emails"><@orcid.msg 'admin.find_ids.label' /></label>
                <input type="text" id="emails" (keyup.enter)="findIds()" [(ngModel)]="emails" placeholder="<@orcid.msg 'admin.find_ids.placeholder' />" class="input-xlarge" />
            </div>
            <div class="controls save-btns pull-left">
                <span id="find-ids" (click)="findIds()" class="btn btn-primary"><@orcid.msg 'admin.find_ids.button'/></span>                       
            </div>
        </div>  
    </div>
</script>