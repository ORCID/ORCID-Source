import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common';

import { WorksFormComponent } 
    from './worksForm.component';

import { WorksExternalIdFormComponent}
    from './worksExternalIdForm.component'

//User generated filters
import { FilterImportWizardsPipe }
    from '../../pipes/filterImportWizardsNg2'; 

// This is the Angular 1 part of the module
export const WorksFormModule = angular.module(
    'WorksFormModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            WorksFormComponent,
            WorksExternalIdFormComponent
        ],
        entryComponents: [ 
            WorksFormComponent,
            WorksExternalIdFormComponent
        ],
        imports: [
            CommonNg2Module,
        ],
        
    }
)
export class WorksFormNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

WorksFormModule.directive(
    'worksFormNg2', 
    <any>downgradeComponent(
        {
            component: WorksFormComponent
        }
    )
).directive(
    'worksExternalIdFormNg2', 
    <any>downgradeComponent(
        {
            component: WorksExternalIdFormComponent
        }
    )
);
