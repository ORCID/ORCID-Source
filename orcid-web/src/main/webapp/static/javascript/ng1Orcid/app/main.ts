//This is only to bootstrap

import 'reflect-metadata';
import 'zone.js';

import { NgModule, Component } from '@angular/core';
import { BrowserModule } from "@angular/platform-browser";
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
//import {setUpLocationSync} from '@angular/router/upgrade';

import { orcidApp } from './modules/ng1_app.ts';
import { Ng2AppModule } from './modules/ng2_app.ts';

import { UpgradeAdapter } from '@angular/upgrade';
import { UpgradeModule } from '@angular/upgrade/static';

//var upgrade = new UpgradeAdapter();

platformBrowserDynamic().bootstrapModule(Ng2AppModule).then(platformRef => {
    const upgrade = (<any>platformRef.instance).upgrade;
    console.log('upgrade4');  

    //upgrade.upgradeNg1Provider('commonSrvc');
    // bootstrap angular1
    upgrade.bootstrap(document.body, [orcidApp.name]);
    //setUpLocationSync(upgrade);
});
