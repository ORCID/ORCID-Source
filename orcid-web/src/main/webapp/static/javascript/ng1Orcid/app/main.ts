import 'reflect-metadata';
import 'zone.js';

import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import {Router} from '@angular/router';
import {UpgradeModule} from '@angular/upgrade/static';

import {orcidApp} from './modules/ng1_app.ts';
import {Ng2AppModule} from './modules/ng2_app.ts';


platformBrowserDynamic().bootstrapModule(Ng2AppModule).then(platformRef => {
  // bootstrap angular1
  //(<any>ref.instance).upgrade.bootstrap(document.body, [orcidApp.name]);

  // setTimeout is necessary because upgrade.bootstrap is async.
  // This should be fixed.
  //setTimeout(() => {
  //  ref.injector.get(Router).initialNavigation();
  //}, 0);
  const upgrade = platformRef.injector.get(UpgradeModule) as UpgradeModule;
  upgrade.bootstrap(document.body, ['orcidApp']);
});

/*
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { UpgradeModule } from '@angular/upgrade/static';
import { AppModule } from './app.module';

platformBrowserDynamic().bootstrapModule(AppModule).then(platformRef => {
    const upgrade = platformRef.injector.get(UpgradeModule) as UpgradeModule;
    upgrade.bootstrap(document.body, ['MyA1App']);
});
*/
/*
angular.element(function() {
    angular.bootstrap(
        document, 
        ['orcidApp']
    );
});
*/