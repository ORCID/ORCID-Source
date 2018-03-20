<#--
	Make is easy to get properties.
-->
<#macro msg key, htmlEscape=false>${messages.getMessage(key ,messageArgs, locale)}</#macro>
<#macro space>${messages.getMessage("unicode.escape.space" ,messageArgs, locale)}</#macro>
<#macro knowledgeBaseUri>${messages.getMessage("orcid.knowledgbase.uri" ,messageArgs, locale)}</#macro>
