import * as angular from 'angular';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';  
import { Component, Input, NgModule } from '@angular/core';

/*
//Example of how to include the directive in the template, just add the attr to the tag
@Component({
  selector: 'my-app',
  template: `
    <div class="app"> 
        <div class="data" modal-ng2>
            <h2>Header</h2>
            Content to be placed here.
        </div> 
    </div>
  `
})
export class AppComponent {}
*/

@Component({
    selector: 'modal-ng-component',
    template: `
        <div class="lightbox-container testmjc">
            <ng-content></ng-content>
        </div>
    `
})
export class ModalNgComponent {
    constructor(){
        console.log('ModalNgComponent loaded');
        
    }
}