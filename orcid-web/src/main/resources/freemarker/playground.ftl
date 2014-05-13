<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <title>${springMacroRequestContext.getMessage("playground.titleORCIDPlayground")}</title>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
    <script type="text/javascript">
        if (typeof jQuery == 'undefined') {
            document.write(unescape("%3Cscript src='${staticCdn}/javascript/jquery/1.8.1/jquery.min.js' type='text/javascript'%3E%3C/script%3E"));
        }
    </script>
    <script>
        $(document).ready(
                function(){
                    $('#code').html(getParameterByName('code'));
                    $('#client_id').val(localStorage['client_id']);
                    $('#client_secret').val(localStorage['client_secret']);
                    $('#scope').val(localStorage['scope']);
                    $('#method').val(localStorage['method']);
                    $('#uri_suffix').val(localStorage['uri_suffix']);
                    $('#body').val(localStorage['body']);
                    setAuthorizeLink();
                    $('#config').submit(
                        function(){
                            localStorage['client_id'] = $('#client_id').val();
                            localStorage['client_secret'] = $('#client_secret').val();
                            localStorage['scope'] = $('#scope').val();
                            localStorage['method'] = $('#method').val();
                            localStorage['uri_suffix'] = $('#uri_suffix').val();
                            localStorage['body'] = $('#body').val();
                            setAuthorizeLink();
                            return false;
                        }
                    );
                    $('#getToken').click(
                        function(){
                            $.post('/orcid-api-web/oauth/token',
                                {
                                    grant_type: 'authorization_code',
                                    client_id: localStorage['client_id'],
                                    client_secret: localStorage['client_secret'],
                                    redirect_uri: 'http://localhost:8080/orcid-web/oauth/playground',
                                    scope: localStorage['scope'],
                                    code: $('#code').html()
                                },
                                function(data) {
                                    $('#token').html(data.access_token);
                                    $('#refresh_token').html(data.refresh_token);
                                    $('#orcid').html(data.orcid);
                                }
                             );
                        }
                    );
                    $('#refreshToken').click(
                        function(){
                            $.post('/orcid-api-web/oauth/token',
                                {
                                    grant_type: 'refresh_token',
                                    client_id: localStorage['client_id'],
                                    client_secret: localStorage['client_secret'],
                                    refresh_token: $('#refresh_token').html()
                                },
                                function(data) {
                                    $('#token').html(data.access_token);
                                    $('#refresh_token').html(data.refresh_token);
                                    $('#orcid').html(data.orcid);
                                }
                             );
                        }
                    );
                    $('#callApi').click(
                        function(){
                            $.ajax(
                                { url: '/orcid-api-web/v1.2_rc4/' + $('#orcid').html() + $('#uri_suffix').val(),
                                  type : $('#method').val(),
                                  contentType : 'application/xml',
                                  data : $('#body').val(),
                                  success: function(data){
                                               if(data.replace == undefined){
                                                   result = 'undefined';                                                   
                                               } else{
                                                   result = data.replace(/</g, '&lt;');
                                               }
                                               $('#result').html(result);
                                           },
                                 headers: { Authorization: 'Bearer ' + $('#token').html() }
                                }
                            );
                        }
                    );
                }
        );
        function getParameterByName(name) {
            var match = RegExp('[?&]' + name + '=([^&]*)')
                            .exec(window.location.search);
            return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
        }
        function setAuthorizeLink(){
            $('#authorize').prop('href', '/orcid-web/oauth/authorize?client_id=' + localStorage['client_id'] + '&response_type=code&scope=' + localStorage['scope'] +'&redirect_uri=http://localhost:8080/orcid-web/oauth/playground');
        }
    </script>
</head>
<body>
<form id="config">
    <div>
        <label>${springMacroRequestContext.getMessage("playground.labelClientID")}</label>
        <input id="client_id" type="text"></input>
        <label>${springMacroRequestContext.getMessage("playground.labelClientsecret")}</label>
        <input id="client_secret" type="text"></input>
        <label>${springMacroRequestContext.getMessage("playground.labelScope")}</label>
        <input id="scope" type="text"></input>
    </div>
    <div>
        <label>${springMacroRequestContext.getMessage("playground.labelMethod")}</label>
        <select id="method">
            <option>GET</option>
            <option>PUT</option>
            <option>POST</option>
        </select>
        <label>${springMacroRequestContext.getMessage("playground.labelURIsuffix")}</label>
        <input id="uri_suffix" type="text"></input>
    </div>
    <div>
        <label>${springMacroRequestContext.getMessage("playground.labelBody")}</label>
        <textarea id="body" rows="20" cols="100"></textarea>
    </div>
    <div>
        <input type="submit" value="Save config"></input>
    </div>
</form>
<a id="authorize" href="#">${springMacroRequestContext.getMessage("playground.labelAuthorize")}</a>
<div id="code"></div>
<a id="getToken" href="#">${springMacroRequestContext.getMessage("playground.labelGettoken")}</a> (<a id="refreshToken" href="#">${springMacroRequestContext.getMessage("playground.labelRefresh")}</a>)
<div id="token"></div>
<div id="refresh_token"></div>
<div id="orcid"></div>
<a id="callApi" href="#">${springMacroRequestContext.getMessage("playground.labelCallAPI")}</a>
<pre id="result"></pre>
</body>
</html>