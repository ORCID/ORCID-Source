<!-- HTML for static distribution bundle build -->
<!DOCTYPE html>

<html lang="en">
  <head>
    <meta charset="UTF-8">
    <title>Swagger UI</title>

    <link rel="icon" type="image/png" href="${swaggerBaseUrl}favicon-32x32.png" sizes="32x32" />
    <link rel="icon" type="image/png" href="${swaggerBaseUrl}favicon-16x16.png" sizes="16x16" />
    <style>
      html
      {
        box-sizing: border-box;
        overflow: -moz-scrollbars-vertical;
        overflow-y: scroll;
      }

      *,
      *:before,
      *:after
      {
        box-sizing: inherit;
      }

      body
      {
        margin:0;
        background: #fafafa;
      }
    </style>
  </head>

  <body>
	  <div style="width: 100%; text-align: center;>
		<div style="padding:20px;padding-top:10px;margin:auto; display: inline-block;">
			<img src="https://orcid.org/assets/vectors/orcid.logo.svg" alt="ORCID.org">
		    <hr>
		    <p style="font-family:arial, helvetica, sans-serif;font-size:15px;color:#494A4C;">
		    Unfortunately Swagger is not available until further notice due to a maintenance upgrade.</br>
		    We plan to <a href="https://trello.com/c/1vXyNduO/768-fully-document-api-functions-in-openapi-3x-swagger-and-publish/" target="_blank"> support swagger</a> again in the future. If you have any questions then please contact <a href="mailto:support@orcid.org">support@orcid.org</a>
	  </br> The ORCiD API can be tested using <a href="https://postman.orcid.org/" target="_blank">postman.orcid.org</a>    
	  </div>
  </body>
</html>



<!-- html lang="en">
  <head>
    <meta charset="UTF-8">
    <title>Swagger UI</title>
    <link rel="stylesheet" type="text/css" href="${swaggerBaseUrl}swagger-ui.css" />
    <link rel="icon" type="image/png" href="${swaggerBaseUrl}favicon-32x32.png" sizes="32x32" />
    <link rel="icon" type="image/png" href="${swaggerBaseUrl}favicon-16x16.png" sizes="16x16" />
    <style>
      html
      {
        box-sizing: border-box;
        overflow: -moz-scrollbars-vertical;
        overflow-y: scroll;
      }

      *,
      *:before,
      *:after
      {
        box-sizing: inherit;
      }

      body
      {
        margin:0;
        background: #fafafa;
      }
    </style>
  </head>

  <body>
    <div id="swagger-ui"></div>

    <script src="${swaggerBaseUrl}swagger-ui-bundle.js" charset="UTF-8"> </script>
    <script src="${swaggerBaseUrl}swagger-ui-standalone-preset.js" charset="UTF-8"> </script>
    <script>
    window.onload = function() {
      // Begin Swagger UI call region
      const ui = SwaggerUIBundle({
        url: "https://localhost:8443/orcid-pub-web/openapi.json",
        dom_id: '#swagger-ui',
        deepLinking: true,
        presets: [
          SwaggerUIBundle.presets.apis,
          SwaggerUIStandalonePreset
        ],
        plugins: [
          SwaggerUIBundle.plugins.DownloadUrl
        ],
        layout: "StandaloneLayout"
      });
      // End Swagger UI call region

      window.ui = ui;
    };
  </script>
  </body>
</html -->
