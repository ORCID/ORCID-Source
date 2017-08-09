import * as angular from 'angular';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';  
import { Component, NgModule } from '@angular/core';

@Component(
    {
        selector: 'modalngcomponent',
        template: `
            <div class="lightbox-container testmjc">
                <ng-content></ng-content>
            </div>
        `
    }
)
export class ModalNgComponent {
    constructor(){
        console.log('ModalNgComponent loaded');
        
    }
}