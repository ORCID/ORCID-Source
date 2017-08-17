declare var $: any;
declare var colorbox: any;
declare var contains: any;
declare var getBaseUri: any;
declare var logAjaxError: any;
declare var orcidVar: any;
declare var scriptTmpl: any;

import * as angular from 'angular';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';  
import { Component, EventEmitter, Input, NgModule, Output } from '@angular/core';

@Component(
    {
        selector: '[modalngcomponent]',
        template: `
            <div class="lightbox-container">
                <ng-content></ng-content>
            </div>
        `
    }
)
export class ModalNgComponent {
    //@Output() onOpen = new EventEmitter<void>();

    constructor(){
        console.log('ModalNgComponent loaded');
        
    }

    closeModal(): void{
        console.log('close modal');
        $.colorbox.close();
    };

    openModal(): void{
        console.log('open modal 3');

        $.colorbox({
            html: $('#edit-country').html(),
            scrolling: true,
            onLoad: function() {
                $('#cboxClose').remove();           
            },
 
            //width: utilsService.formColorBoxResize(),
            onComplete: function() {      
            },
            onClosed: function() {
                //this.getCountryForm();
            }            
        });
        $.colorbox.resize();
    };
}