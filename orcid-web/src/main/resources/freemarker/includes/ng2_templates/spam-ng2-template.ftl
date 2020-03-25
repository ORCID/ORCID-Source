<script type="text/ng-template" id="spam-ng2-template">
    <div class="col-md-12" *ngIf="!hideSpam">
        <input type="button" (click)="reportSpam()" class="spam-button btn btn-primary" value="<@orcid.msg 'public_profile.btnSpam'/>"/>
    </div>
</script>