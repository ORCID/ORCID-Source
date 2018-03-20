<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>ORCID Swagger UI</title>
  <link rel="icon" type="image/png" href="${swaggerBaseUrl}images/favicon-32x32.png" sizes="32x32" />
  <link rel="icon" type="image/png" href="${swaggerBaseUrl}images/favicon-16x16.png" sizes="16x16" />
  <link href='${swaggerBaseUrl}css/typography.css' media='screen' rel='stylesheet' type='text/css'/>
  <link href='${swaggerBaseUrl}css/reset.css' media='screen' rel='stylesheet' type='text/css'/>
  <link href='${swaggerBaseUrl}css/screen.css' media='screen' rel='stylesheet' type='text/css'/>
  <link href='${swaggerBaseUrl}css/reset.css' media='print' rel='stylesheet' type='text/css'/>
  <link href='${swaggerBaseUrl}css/print.css' media='print' rel='stylesheet' type='text/css'/>
  <script src='${swaggerBaseUrl}lib/jquery-1.8.0.min.js' type='text/javascript'></script>
  <script src='${swaggerBaseUrl}lib/jquery.slideto.min.js' type='text/javascript'></script>
  <script src='${swaggerBaseUrl}lib/jquery.wiggle.min.js' type='text/javascript'></script>
  <script src='${swaggerBaseUrl}lib/jquery.ba-bbq.min.js' type='text/javascript'></script>
  <script src='${swaggerBaseUrl}lib/handlebars-2.0.0.js' type='text/javascript'></script>
  <script src='${swaggerBaseUrl}lib/underscore-min.js' type='text/javascript'></script>
  <script src='${swaggerBaseUrl}lib/backbone-min.js' type='text/javascript'></script>
  <script src='${swaggerBaseUrl}swagger-ui.js' type='text/javascript'></script>
  <script src='${swaggerBaseUrl}lib/highlight.7.3.pack.js' type='text/javascript'></script>
  <script src='${swaggerBaseUrl}lib/jsoneditor.min.js' type='text/javascript'></script>
  <script src='${swaggerBaseUrl}lib/marked.js' type='text/javascript'></script>
  <script src='${swaggerBaseUrl}lib/swagger-oauth.js' type='text/javascript'></script>

  <!-- Some basic translations -->
  <!-- <script src='${swaggerBaseUrl}lang/translator.js' type='text/javascript'></script> -->
  <!-- <script src='${swaggerBaseUrl}lang/ru.js' type='text/javascript'></script> -->
  <!-- <script src='${swaggerBaseUrl}lang/en.js' type='text/javascript'></script> -->


  <script type="text/javascript">
    $(function () {
       function log() {
           if ('console' in window) {
             console.log.apply(console, arguments);
           }
       }
            
      url = "${swaggerJsonUrl}";
      
      // Pre load translate...
      if(window.SwaggerTranslator) {
        window.SwaggerTranslator.translate();
      }
      window.swaggerUi = new SwaggerUi({
        url: url,
        dom_id: "swagger-ui-container",
        supportedSubmitMethods: ['get', 'post', 'put', 'delete', 'patch'],
        onComplete: function(swaggerApi, swaggerUi){
          if(typeof initOAuth == "function") {
            initOAuth({
              clientId: "unknown",
              clientSecret: "unknown",
              realm: "your-realms",
              appName: "your-app-name",
              scopeSeparator: ",",
              additionalQueryStringParams: {}
            });
          }

          if(window.SwaggerTranslator) {
            window.SwaggerTranslator.translate();
          }

          $('pre code').each(function(i, e) {
            hljs.highlightBlock(e)
          });
        },
        onFailure: function(data) {
          log("Unable to Load SwaggerUI");
        },
        docExpansion: "none",
        jsonEditor: false,
        apisSorter: "alpha",
        defaultModelRendering: 'schema',
        showRequestHeaders: false,
        operationsSorter: function (a, b) { 
		    var order = { 'get': '0', 'post': '1', 'put': '2', 'delete': '3' }; 
		    return (order[a.method]+a.path).localeCompare(order[b.method]+b.path);    
		  }
      });

      function updateOauth(){
          initOAuth({
              clientId: $('#input_clientId')[0].value,
              clientSecret: $('#input_clientSecret')[0].value,
              realm: "blank",
              appName: "blank"
          });
      }

      $('#input_clientId').change(updateOauth);
      $('#input_clientSecret').change(updateOauth);
      
      window.swaggerUi.load();
      updateOauth();      
      
  });
  </script>
  
</head>

<body class="swagger-section">
<div id='header'>
  <div class="swagger-ui-wrap">
    <a id="logo" href="http://orcid.org">ORCID</a>
    <form id='api_selector'>
      <#if showOAuth == true>
      <div class='input'><input placeholder="client id" id="input_clientId" name="clientId" type="text" autocomplete="off"/></div>
      <div class='input'><input placeholder="client secret" id="input_clientSecret" name="clientSecret" type="text" autocomplete="off"/></div>      
      </#if>
    </form>
  </div>
</div>

<div id="message-bar" class="swagger-ui-wrap" data-sw-translate>&nbsp;</div>
<div id="swagger-ui-container" class="swagger-ui-wrap"></div>

</body>
</html>