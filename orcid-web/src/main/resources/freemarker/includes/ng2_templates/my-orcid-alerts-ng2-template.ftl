<script type="text/ng-template" id="my-orcid-alerts-ng2-template">
    <!--If user just registered-->
    <#if justRegistered?? && justRegistered>
      <div class="alert alert-success">
          <strong>
            <div class="row">
                <div class="col-md-12 col-xs-12 col-sm-12">
                    <@spring.message "orcid.frontend.web.thanks_for_registering"/>
                    <div class="topBuffer">
                        <button class="btn btn-primary" id="modal-close" (click)="verifyEmail()"><@orcid.msg 'orcid.frontend.workspace.send_verification'/></button>
                    </div>
                </div>
            </div>
          </strong>
      </div>
    </#if>
    <!--If user verified an email-->
    <#if emailVerified?? && emailVerified>
      <div class="alert alert-success">
          <strong>
              ${emailVerifiedMessage}
              <#if primaryEmailUnverified?? && primaryEmailUnverified>
                  <div class="row">
                    <div class="col-md-12 col-xs-12 col-sm-12">
                        <@spring.message "orcid.frontend.web.primary_email_unverified"/>
                        <div class="topBuffer">
                            <button class="btn btn-primary" id="modal-close" (click)="verifyEmail()"><@orcid.msg 'orcid.frontend.workspace.send_verification'/></button>
                        </div>
                    </div>
                </div>
              </#if>
          </strong>
      </div>
    </#if>
    <!--If admin user tried to switch to an invalid ORCID ID-->
    <#if invalidOrcid?? && invalidOrcid>
      <div class="alert alert-success">
          <strong><@spring.message "orcid.frontend.web.invalid_switch_orcid"/></strong>
      </div>
    </#if>
    <#include "/includes/ng2_templates/claim-thanks-ng2-template.ftl">
</script>