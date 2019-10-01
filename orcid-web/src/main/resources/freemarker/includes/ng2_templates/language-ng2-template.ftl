<script type="text/ng-template" id="language-ng2-template"> 
    <form role="presentation" action="#"  >
        <select
            *ngIf="languages"
            name="language-codes" id="language-codes"
            [(ngModel)]="language" 
            (ngModelChange)="selectedLanguage()"
            aria-label="language menu"
            role="navigation" 
        >
            <option 
                *ngFor="let languageOpt of languages"
                [value]="languageOpt.value"
                [selected]="languageOpt.value == language.value"
            >
                {{languageOpt.label}}   
            </option>             
            
        </select>
    </form>                     
</script>