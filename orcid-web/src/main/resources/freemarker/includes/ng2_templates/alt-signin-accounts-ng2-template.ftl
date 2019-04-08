<script type="text/ng-template" id="alt-signin-accounts-ng2-template">
    <div class="section-heading">
        <h1>
            <@orcid.msg 'manage_signin_title' />
        </h1>
        <div class="popover-help-container">
            <i class="glyphicon glyphicon-question-sign"></i>
            <div id="alternative-signin-accounts-help" class="popover bottom">
                <div class="arrow"></div>
                <div class="popover-content">
                    <p><@orcid.msg 'manage.help_popover.alternateSigninAccounts'/>  <a href="<@orcid.msg 'common.kb_uri_default'/>360006972693" target="manage.help_popover.alternateSigninAccounts"><@orcid.msg 'common.learn_more'/></a></p>
                </div>
            </div>
        </div>
    </div>
    <div class="clearfix" id="SocialCtrl">
          <div *ngIf="!socialAccounts?.length > 0">
              <p><@orcid.msg 'manage.none_added.alternateSigninAccounts'/></p>
          </div>
          <div *ngIf="socialAccounts?.length > 0">
              <table class="table table-bordered settings-table normal-width" *ngIf="socialAccounts">
                  <thead>
                      <tr>
                          <th width="40%" (click)="changeSorting('accountIdForDisplay')"><@orcid.msg 'manage_signin_table_header1' /></th>
                          <th width="30%" (click)="changeSorting('idpName')"><@orcid.msg 'manage_signin_table_header2' /></th>
                          <th width="20%" (click)="changeSorting('dateCreated')"><@orcid.msg 'manage_delegators.delegates_table.access_granted' /></th>
                          <td width="10%"></td>
                      </tr>
                  </thead>
                  <tbody>
                      <tr *ngFor="let socialAccount of socialAccounts | orderBy:sort.column:sort.descending">
                          <td width="40%" style="word-break:break-all">{{socialAccount.accountIdForDisplay}}</td>
                          <td width="30%" style="word-break:break-all">{{socialAccount.idpName}}</td>
                          <td width="20%" style="word-break:break-all">{{socialAccount.dateCreated|date:'yyyy-MM-dd'}}</td>
                          <td width="10%">
                              <a
                              (click)="confirmRevoke(socialAccount)"
                              *ngIf="!isPasswordConfirmationRequired"
                              class="glyphicon glyphicon-trash grey"
                              title="${springMacroRequestContext.getMessage("manage_signin_unlink")}"></a>
                          </td>
                      </tr>
                  </tbody>
              </table>
              <#if isPasswordConfirmationRequired>
                  <@orcid.msg 'manage_signin_not_allowed' />
              </#if>
          </div>
      </div>
</script>