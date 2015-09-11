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
<script>
var qp = null;
if(window.location.hash) {
  qp = location.hash.substring(1);
}
else {
  qp = location.search.substring(1);
}
qp = qp ? JSON.parse('{"' + qp.replace(/&/g, '","').replace(/=/g,'":"') + '"}',
  function(key, value) {
    return key===""?value:decodeURIComponent(value) }
  ):{}

if (window.opener.swaggerUi.tokenUrl)
    window.opener.processOAuthCode(qp);
else
    window.opener.onOAuthComplete(qp);

window.close();
</script>