<script type="text/ng-template" id="notifications-count-ng2-template">
    <li>
        <a ${(nav=="notifications")?then('class="active" ', '')}href="<@orcid.rootPath "/inbox" />">${springMacroRequestContext.getMessage("workspace.notifications")} <span  *ngIf="!(getUnreadCount() === 0)">({{getUnreadCount()}})</span></a>
    </li>
</script>