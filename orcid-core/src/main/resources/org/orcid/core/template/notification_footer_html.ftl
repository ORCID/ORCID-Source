<footer>
    <div style="
            max-width: 736px;
            height: 152px;
            background-color: #fafafa;
            padding: 1px 16px 0;
            display: inline-block;
            font-size: 13px !important;
        ">
        <p>
            <b><@emailMacros.msg "notification.footer.why" /></b>
        </p>
        <p>
            <@emailMacros.msg "notification.footer.reasonNotifications" />
            <a style="text-decoration: underline;color: #085c77;" href="${baseUri}/account" target="_blank">
                <@emailMacros.msg "notification.footer.settings" />
            </a>
        </p>
        <a style="text-decoration: underline;color: #085c77;" href="https://support.orcid.org/hc/en-us/articles/360006972953" target="_blank">
            <@emailMacros.msg "notification.footer.inbox" />
        </a>
    </div>
    <br>
    <br>
    <p>
        <a style="text-decoration: underline;color: #085c77;display: inline-block;" href="${baseUri}/account" target="_blank">
            <@emailMacros.msg "notification.footer.preferences" />
        </a>
        <a style="text-decoration: underline;color: #085c77;padding-left: 16px;display: inline-block;" href="https://orcid.org/footer/privacy-policy" target="_blank">
            <@emailMacros.msg "notification.footer.privacy" />
        </a>
        <a style="text-decoration: underline;color: #085c77;padding-left: 16px;" href="https://orcid.org" target="_blank">
            <@emailMacros.msg "notification.footer.orcid" />
        </a>
    </p>
    <p>
        <b>
            <@emailMacros.msg "notification.footer.orcidInc" />
        </b> <@emailMacros.msg "notification.footer.address" />
    </p>
</footer>