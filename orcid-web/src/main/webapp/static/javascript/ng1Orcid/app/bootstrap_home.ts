//This is only to bootstrap

import 'reflect-metadata';

import { Component, NgModule } 
	from '@angular/core';

import { BrowserModule } 
	from "@angular/platform-browser";

import { platformBrowserDynamic } 
	from '@angular/platform-browser-dynamic';

import { HomeAppModule } 
    from './modules/homeApp.ts';

import { enableProdMode } 
    from '@angular/core';

console.log(NODE_ENV);

if (NODE_ENV === 'production') {
    console.log("prod mode");
    enableProdMode();
}

platformBrowserDynamic().bootstrapModule(HomeAppModule)
  .catch(err => console.log(err));    

