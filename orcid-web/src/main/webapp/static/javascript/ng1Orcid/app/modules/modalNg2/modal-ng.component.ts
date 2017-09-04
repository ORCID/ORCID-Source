declare var $: any;
 
import { Component, OnInit, OnDestroy/*Component, EventEmitter, Input, NgModule, Output*/ } 
    from '@angular/core';

import { Subscription } 
    from 'rxjs/Subscription';

import { ModalService } 
    from '../../shared/modalService.ts'; 

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
export class ModalNgComponent implements OnInit, OnDestroy {
    //@Output() onOpen = new EventEmitter<void>();

    private subscription: Subscription;
    constructor( private modalService: ModalService ){
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

    ngOnInit() {
        this.subscription = this.modalService.notifyObservable.subscribe(
            (res) => {
                if ( res.hasOwnProperty('option') 
                    /*&& res.option === 'onSubmit'*/ ) {
                    console.log('res.value',res.value);
                    // perform your other action from here

                }
            }
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}