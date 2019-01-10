<script type="text/ng-template" id="qrcode-ng2-template">
      <div class="qrcode-container">
          <a href="<@orcid.rootPath "/qr-code" />" target="<@orcid.msg 'workspace.qrcode.link.text'/>"><span class="glyphicons qrcode orcid-qr"></span><@orcid.msg 'workspace.qrcode.link.text'/>
          </a>
          <div class="popover-help-container">
              <i class="glyphicon glyphicon-question-sign"></i>
              <div id="qrcode-help" class="popover bottom">
                  <div class="arrow"></div>
                  <div class="popover-content">
                      <p><@orcid.msg 'workspace.qrcode.help'/> 
                          <a href="<@orcid.msg 'common.kb_uri_default'/>360006897654" target="qrcode.help"><@orcid.msg 'common.learn_more'/></a>
                      </p>
                  </div>
              </div>
          </div>
      </div>
</script>