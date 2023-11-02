<p style="font-family: arial, helvetica, sans-serif; font-size: 16px; color: #494A4C;">
    <b><@emailMacros.msg "email.welcome.verify.2" /></b><br />
    <@emailMacros.msg "email.welcome.verify.3" />
    <table cellpadding="0" cellspacing="0" style="font-family: Helvetica, Arial, sans-serif;  border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; margin: 0 auto; margin-top:20px">
        <tbody>
            <tr>
                <td style="border-spacing: 0px; border-collapse: collapse; line-height: 24px; font-size: 16px; border-radius: 4px; margin: 0;">
                    <a id="verificationButton"
                       href="${verificationUrl}?lang=${locale}"
                       style="font-size: 20px; font-family: Helvetica, Arial, sans-serif; text-decoration: none; border-radius: 4.8px; line-height: 25px; display: inline-block; font-weight: normal; white-space: nowrap; background-color: #31789B; color: #ffffff; padding: 8px 16px; border: 1px solid #31789B;"
                       ><@emailMacros.msg "email.button" /></a>
                </td>
            </tr>
        </tbody>
    </table>
</p>            
<p style="font-family: arial, helvetica, sans-serif; font-size: 16px; color: #494A4C;">
    <@emailMacros.msg "email.welcome.verify.4" />
    <table cellpadding="0" cellspacing="0" style="font-family: arial, helvetica, sans-serif; border-spacing: 0px; border-collapse: separate !important; border-radius: 4px; margin: 0 auto; ">
        <tbody>
            <tr>
                <td>
                    <p align="center" class="text-center" style="line-height: 24px; font-size: 16px; margin: 0; padding-bottom: 30px; padding-top: 20px; word-break: break-word;">
                        <a id="verificationUrl"
                           href="${verificationUrl}?lang=${locale}"
                           target="orcid.blank"
                           >${verificationUrl}?lang=${locale}</a>
                    </p>
                </td>
            </tr>
        </tbody>
    </table>
</p>