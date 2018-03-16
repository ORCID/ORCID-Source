<script type="text/ng-template" id="deactivate-account-ng2-template">
    <div class="editTablePadCell35 close-account-container">
        <p>${springMacroRequestContext.getMessage("deactivate_orcid.you_may")}</p>
        
        <h4>${springMacroRequestContext.getMessage("deactivate_orcid.whatHappens")}</h4>
        <p>
            ${springMacroRequestContext.getMessage("deactivate_orcid.once")} <a
                href="${knowledgeBaseUri}/articles/148970-closing-an-orcid-account"
                target="deactivate_orcid.close_an">${springMacroRequestContext.getMessage("deactivate_orcid.close_an")}</a>
        </p>
        
        <h4>${springMacroRequestContext.getMessage("deactivate_orcid.anotherAccount")}</h4>
        <p>
            ${springMacroRequestContext.getMessage("deactivate_orcid.duplicate_orcid.a")}&nbsp;<strong>${springMacroRequestContext.getMessage("deactivate_orcid.duplicate_orcid.b")}</strong>
            <a
                href="${knowledgeBaseUri}/articles/580410"
                target="deprecate_orcid.learn_more_link" class="no-wrap">${springMacroRequestContext.getMessage("deprecate_orcid.learn_more_link")}</a>
        </p>
        
                                        
        <h4>${springMacroRequestContext.getMessage("deactivate_orcid.listTitle")}</h4>
        <ol>
            <li>${springMacroRequestContext.getMessage("deactivate_orcid.b1")}</li>
            <li>${springMacroRequestContext.getMessage("deactivate_orcid.b2")}</li>
            <li>${springMacroRequestContext.getMessage("deactivate_orcid.b3")}</li>
        </ol>
        <button (click)="sendDeactivateEmail()" class="btn btn-primary">${springMacroRequestContext.getMessage("deactivate_orcid.deactivatemyOrcidaccount")}</button>
    </div>
</script>