import * as angular from 'angular';
import { Injectable } from '@angular/core';

@Injectable()
export class CommonSrvc {
    copyErrorsLeft(data1: any, data2: any): void {
        for (var key in data1) {
            if (key == 'errors') {
                data1.errors = data2.errors;
            } else {
                if (data1[key] != null && data1[key].errors !== undefined) {
                    data1[key].errors = data2[key].errors;
                }
            };
        };
    }
    
    shownElement: any;

    constructor(

    ) { 

        this.shownElement = [];
    }

    showPrivacyHelp(elem: any, event: any, offsetArrow: any): void {
        var top = event.target.parentNode.parentElement.offsetTop;
        var left = event.target.parentNode.parentElement.offsetLeft;
        var scrollTop =  $('.fixed-area').scrollTop();
        
        if (elem === '-privacy'){
             $('.edit-record, .bulk-privacy-bar, .popover-help-container').css({
                top: -75,
                left: 512
            });
        } else{
            if (elem.indexOf('@') > -1) {
                left = 530; //Emails modal fix
            }
             $('.edit-record.record-settings.popover-help-container').css({
                top: top - scrollTop - 160,
                left: left + 25
            });             
        }

        $('.edit-record.record-settings.popover-help-container.arrow').css({                    
            left: offsetArrow
        });

        this.shownElement[elem] = true;
    }

    showTooltip(elem: any, event: any, topOffset: any, leftOffset: any, arrowOffset: any): void{
        var top = event.target.parentNode.parentElement.offsetTop;
        var left = event.target.parentNode.parentElement.offsetLeft;   
        var scrollTop =  $('.fixed-area').scrollTop();
        
        $('.edit-record.popover-tooltip').css({
            top: top - scrollTop - topOffset,
            left: left + leftOffset
        });
        
        $('.edit-record.popover-tooltip.arrow').css({                
            left: arrowOffset
        });            
        
        this.shownElement[elem] = true;
    }
    
    hideTooltip(elem: any): void{

        this.shownElement[elem] = false;
    }
}