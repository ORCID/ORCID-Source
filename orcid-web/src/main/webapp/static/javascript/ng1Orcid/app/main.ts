//This is only to bootstrap

import 'reflect-metadata';
import 'zone.js';

import { Component, NgModule } 
	from '@angular/core';

import { BrowserModule } 
	from "@angular/platform-browser";

import { platformBrowserDynamic } 
	from '@angular/platform-browser-dynamic';

import { HomeAppModule } 
    from './modules/homeApp.ts';

import { SigninAppModule } 
    from './modules/signinApp.ts';

import { enableProdMode } 
    from '@angular/core';

console.log(NODE_ENV);

if (NODE_ENV === 'production') {
    console.log("prod mode");
    enableProdMode();
}

console.log(window.location.pathname);
if(window.location.pathname.indexOf("signin") > -1){
    console.log("bootstrapping signin");
    platformBrowserDynamic().bootstrapModule(SigninAppModule)
  .catch(err => console.log(err));    
} else {
    platformBrowserDynamic().bootstrapModule(HomeAppModule)
  .catch(err => console.log(err));    
}

