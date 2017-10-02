<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
  <html>
  <body>
  <h2>ORCID identifier types</h2>
    <table border="1">
      <tr>
        <th>Name</th>
        <th>Description</th>
        <th>Resolution Prefix</th>
        <th>Case sensitive</th>
        <th>Primary use</th>
      </tr>
      <xsl:for-each select="identifierTypes/identifierType">
      <tr>
        <td><xsl:value-of select="name"/></td>
        <td><xsl:value-of select="description"/></td>
        <td><xsl:value-of select="resolutionPrefix"/></td>
        <td><xsl:value-of select="caseSensitive"/></td>
        <td><xsl:value-of select="primaryUse"/></td>
      </tr>
      </xsl:for-each>
    </table>
  </body>
  </html>
</xsl:template>
</xsl:stylesheet>