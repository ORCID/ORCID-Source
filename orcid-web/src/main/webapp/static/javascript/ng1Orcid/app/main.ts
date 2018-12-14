//This is only to bootstrap

import 'reflect-metadata';
import 'zone.js';

import { Component, NgModule } 
	from '@angular/core';

import { BrowserModule } 
	from "@angular/platform-browser";

import { platformBrowserDynamic } 
	from '@angular/platform-browser-dynamic';

import { Ng2AppModule } 
    from './modules/ng2_app.ts';

import { enableProdMode } 
    from '@angular/core';

console.log(NODE_ENV);

if (NODE_ENV === 'production') {
    console.log("prod mode");
    enableProdMode();
}

platformBrowserDynamic().bootstrapModule(Ng2AppModule)
  .catch(err => console.log(err));
