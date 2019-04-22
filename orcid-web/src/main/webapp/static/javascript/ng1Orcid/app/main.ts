//This is only to bootstrap

import 'zone.js';

import { platformBrowserDynamic } 
    from '@angular/platform-browser-dynamic';

import { orcidApp } 
    from './modules/ng1_app';

import { Ng2AppModule } 
    from './modules/ng2_app';

import { enableProdMode } 
    from '@angular/core';

console.log(NODE_ENV);

if (NODE_ENV === 'production') {
    console.log("prod mode");
    enableProdMode();
}
platformBrowserDynamic().bootstrapModule(Ng2AppModule).then(
    platformRef => {
        const upgrade = (<any>platformRef.instance).upgrade; 

        // bootstrap angular1
        upgrade.bootstrap(document.body, [orcidApp.name]);
    }
);