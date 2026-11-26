<#import "delegate_invitation_macros.ftl" as m>
<#if key?starts_with("created.sender.")>
<@m.msg "created.sender.prefix"/> ${display}<#if !invitedByEmail> (${recordUrl})</#if> <@m.msg "created.sender.suffix"/>

<@m.msg "created.sender.deadline"/> <@m.msg "recipient.expired"/>
<#elseif actionable>
${display} (${recordUrl}) <@m.msg "recipient.intro"/>

<@m.msg "recipient.info"/> https://support.orcid.org/hc/articles/360006973613

<@m.msg "recipient.deadline"/> <@m.msg "recipient.expired"/>

<@m.msg "accept"/>: ${acceptUrl}
<@m.msg "decline"/>: ${declineUrl}
<#elseif key == "accepted.sender">
${display} (${recordUrl}) <@m.msg "accepted.sender.body"/>

<@m.msg "accepted.sender.info"/>
<#elseif key == "accepted.recipient">
<@m.msg "accepted.recipient.prefix"/> ${display} (${recordUrl}).

<@m.msg "accepted.recipient.info.prefix"/> ${display}<@m.msg "accepted.recipient.info.suffix"/>

<@m.msg "recipient.info"/> https://support.orcid.org/hc/articles/360006973613
<#elseif key?starts_with("declined.sender.")>
${display}<#if !invitedByEmail> (${recordUrl})</#if> <@m.msg "declined.sender.body"/>
<#else>
<@m.msg "declined.recipient.prefix"/> ${display} (${recordUrl}). <@m.msg "declined.recipient.suffix"/>
</#if>
