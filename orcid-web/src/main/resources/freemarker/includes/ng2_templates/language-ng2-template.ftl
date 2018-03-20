<script type="text/ng-template" id="language-ng2-template">                              
    <!--<p>${springMacroRequestContext.getMessage("manage.language_copy")}</p>-->
    <div class="row">
        <form id="language-form" action="#" ng-controller="languageCtrl">

            <select
                name="language-codes" id="language-codes"
                [(ngModel)]="language" 
                (ngModelChange)="selectedLanguage()"
            >
                <option 
                    *ngFor="let language of languages"
                    [value]="language.val"
                >
                    {{language.label}}   
                </option>             
                
            </select>

        </form>
    </div>                      
</script>