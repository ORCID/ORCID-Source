<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->

<script type="text/ng-template" id="social-networks-ng2-template">
    <div class="editTablePadCell35">
        <p><@orcid.msg 'manage.social_networks_label_1'/></p>
        <div class="grey-box">
            <form role="form" id="social-network-options">
              <div class="checkbox-inline">
                <label>
                  <input type="checkbox" name="twitter" (ngModelChange)="updateTwitter()" [(ngModel)]="twitter"><img alt="Twitter" src="${staticCdn}/img/social/twitter.png" />
                </label>
              </div>
              <div class="checkbox-inline">
                <label>
                  <input type="checkbox" name="facebook" disabled><img src="${staticCdn}/img/social/facebook.png" alt="Facebook" />
                </label>
              </div>
              <div class="checkbox-inline">
                <label>
                  <input type="checkbox" name="google-plus"  disabled><img src="${staticCdn}/img/social/google-plus.png" alt="Google+" />
                </label>
              </div>
            </form>
        </div>
        
    </div>
</script>