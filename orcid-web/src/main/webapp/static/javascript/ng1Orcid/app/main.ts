//This is only to bootstrap
import 'reflect-metadata';
import 'zone.js';

import { NgModule, Component } from '@angular/core';
import { UpgradeModule } from '@angular/upgrade/static';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { orcidApp } from './modules/ng1_app.ts';
import { AppModule } from './modules/ng2_app.ts';

platformBrowserDynamic().bootstrapModule(AppModule).then(platformRef => {
	const upgrade = platformRef.injector.get(UpgradeModule) as UpgradeModule;
	upgrade.bootstrap(document.body, ['myApp'], { strictDi: true });
});


/*
import 'reflect-metadata';
import 'zone.js';

import { NgModule, Component } from '@angular/core';
import { BrowserModule } from "@angular/platform-browser";
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { orcidApp } from './modules/ng1_app.ts';
import { Ng2AppModule } from './modules/ng2_app.ts';



platformBrowserDynamic().bootstrapModule(Ng2AppModule).then(platformRef => {
    //console.log('upgrade bootstraped v0.11');

    const upgrade = (<any>platformRef.instance).upgrade;
    // bootstrap angular1
    upgrade.bootstrap(document.body, [orcidApp.name]);
});
*/