<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
<head>
    <title>${subject}</title>
</head>
<body>
<div style="
                max-width: 580px;
                padding: 32px;
                margin: auto;
                font-family: Arial, helvetica, sans-serif;
                color: #494A4C;
                font-size: 15px;
                line-height: 1.5;
            ">
    <div style="line-height: 1.5;">
        <p>
            <@emailMacros.msg "email.addWorks.header.yourOrcidId" /> ${orcidId}
            <br>
            <@emailMacros.msg "email.addWorks.header.yourOrcidRecordIs" /> <a
                style="text-decoration: underline;color: #085c77;" href="${baseUri}/${orcidId}"
                target="_blank">${baseUri}/${orcidId}</a>
        </p>
        <p><@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}</p>
    </div>
    <div style="display: block">
        <p><@emailMacros.msg "email.addWorks.weHaveNoticed" /></p>
        <p style="font-weight: bolder;font-size: 16px"><@emailMacros.msg "email.addWorks.getStarted" /></p>
        <p><@emailMacros.msg "email.addWorks.belowAreLinks" /></p>
        <p><@emailMacros.msg "email.addWorks.forAddingJournal" /><@emailMacros.space /><a
                    style="text-decoration: underline;color: #085c77;display: inline-block;"
                    href="https://orcid.org/oauth/authorize?client_id=0000-0002-3054-1567&response_type=code&scope=%2Factivities%2Fupdate%20%2Fread-limited&redirect_uri=https:%2F%2Fsearch.crossref.org%2Fauth%2Forcid%2Fsearch-and-link"
                    target="_blank">Crossref Metadata Search</a><@emailMacros.space /><@emailMacros.msg "email.addWorks.allowsYouToSearch" /></p>
        <p><@emailMacros.msg "email.addWorks.forAddingLife" /><@emailMacros.space /><a
                    style="text-decoration: underline;color: #085c77;display: inline-block;"
                    href="https://orcid.org/oauth/authorize?client_id=0000-0002-9157-3431&response_type=code&scope=%2Fread-limited%20%2Factivities%2Fupdate&redirect_uri=https:%2F%2Feuropepmc.org%2Forcid%2Fimport"
                    target="_blank">Europe PubMed Central</a><@emailMacros.space /><@emailMacros.msg "email.addWorks.allowsYouToImport" /></p>
        <p><@emailMacros.msg "email.addWorks.forAddingMultiple" /><@emailMacros.space /><a
                    style="text-decoration: underline;color: #085c77;display: inline-block;"
                    href="https://orcid.org/oauth/authorize?client_id=0000-0002-5982-8983&response_type=code&scope=%2Fperson%2Fupdate%20%2Fread-limited%20%2Factivities%2Fupdate&redirect_uri=https:%2F%2Forcid.scopusfeedback.com%2Finward%2Forcid"
                    target="_blank">Scopus</a><@emailMacros.space /><@emailMacros.msg "email.addWorks.allowsYouToAddYourScopus" />
        </p>
        <p><a style="text-decoration: underline;color: #085c77;display: inline-block;"
                href="https://orcid.org/oauth/authorize?client_id=APP-HCGH49OO61VH1II3&response_type=code&scope=%2Fread-limited%20%2Factivities%2Fupdate&redirect_uri=https:%2F%2Fwww.base-search.net%2FSearch%2FOrcidSearchLink"
                target="_blank">BASE</a> (Bielefeld Academic Search Engine)<@emailMacros.space /><@emailMacros.msg "email.addWorks.allowsYouClaim" /></p>
        <p><@emailMacros.msg "email.addWorks.weHaveSearchAndLink" /><@emailMacros.space /><a
                    style="text-decoration: underline;color: #085c77;display: inline-block;"
                    href="${baseUri}/my-orcid"
                    target="_blank"><@emailMacros.msg "email.addWorks.yourOrcidRecord" /></a>.</p>
        <p><@emailMacros.msg "email.addWorks.ifYouAreHavingTrouble" /><@emailMacros.space /><a
                    style="text-decoration: underline;color: #085c77;display: inline-block;"
                    href="https://support.orcid.org/hc/en-us/articles/360006973133-Add-works-to-your-ORCID-record"
                    target="_blank"><@emailMacros.msg "email.addWorks.orcidHelpCenter" /></a>.</p>
        <p><@emailMacros.msg "email.addWorks.footer.warmRegards" />
        <br>
        <@emailMacros.msg "email.addWorks.footer.orcidSupportTeam" />
        </p>
        <a style="text-decoration: underline;color: #085c77;display: inline-block;"
           href="https://support.orcid.org/hc/en-us" target="_blank">https://support.orcid.org</a>
    </div>
    <footer style="display: inline-block;font-size: 13px !important;">
        <p>
            <@emailMacros.msg "email.addWorks.footer.youHaveReceivedThisEmail" /><@emailMacros.space /><a
                style="text-decoration: underline;color: #085c77;" href="${baseUri}/account"
                target="_blank"><@emailMacros.msg "email.addWorks.footer.accountSettings" /></a>.
        </p>
        <p>
            <a style="text-decoration: underline;color: #085c77;display: inline-block;"
               href="${baseUri}/account" target="_blank"
            ><@emailMacros.msg "email.common.email.preferences" /></a
            > | <a style="text-decoration: underline;color: #085c77;display: inline-block;"
                   href="https://orcid.org/footer/privacy-policy" target="_blank"
            ><@emailMacros.msg "email.common.privacy_policy" /></a> |
            <@emailMacros.msg "email.common.address1" /> | <@emailMacros.msg "email.common.address2" /> |
            <a
                    style="text-decoration: underline;color: #085c77;"
                    href="${baseUri}"
                    target="_blank"
            >ORCID.org</a>
        </p>
    </footer>
</div>
</body>
</html>
</#escape>
