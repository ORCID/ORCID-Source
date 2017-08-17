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
            <div class="lightbox-container testmjc">
                <ng-content></ng-content>
            </div>
        `
    }
)
export class ModalNgComponent {
    @Output() onOpen = new EventEmitter<void>();

    constructor(){
        console.log('ModalNgComponent loaded');
        
    }

    closeModal(): void{     
        $.colorbox.close();
    };

    openEditModal(): void{
        console.log('open modal 3');

        $.colorbox({
            //html: $compile($('#edit-country').html())(this),
            html: scriptTmpl("edit-country"),
            //html: 'testcolorbox',
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