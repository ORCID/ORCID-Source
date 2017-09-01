import { Injectable } from '@angular/core';

@Injectable()
export class CommonService {
    private shownElement: any;

    constructor() { 
        this.shownElement = [];
    }

    copyErrorsLeft( data1, data2 ): void {
        for (let key in data1) {
            if (key == 'errors') {
                data1.errors = data2.errors;
            } else {
                if (data1[key] != null && data1[key].errors !== undefined) {
                    data1[key].errors = data2[key].errors;
                }
            };
        };
    };

    hideTooltip(elem): void{
        this.shownElement[elem] = false;
    };

    showPrivacyHelp(elem, event, offsetArrow): void{
        let top = $(event.target.parentNode).parent().prop('offsetTop');
        let left = $(event.target.parentNode).parent().prop('offsetLeft');
        let scrollTop = $('.fixed-area').scrollTop();
        
        if (elem === '-privacy'){
            $('.edit-record .bulk-privacy-bar .popover-help-container').css({
                top: -75,
                left: 512
            });
        }else{
            if (elem.indexOf('@') > -1) {
                left = 530; //Emails modal fix
            }
            $('.edit-record .record-settings .popover-help-container').css({
                top: top - scrollTop - 160,
                left: left + 25
            });             
        }
        $('.edit-record .record-settings .popover-help-container .arrow').css({                    
            left: offsetArrow
        }); 
        this.shownElement[elem] = true;
    };

    showTooltip(elem, event, topOffset, leftOffset, arrowOffset): void {
        let top = $(event.target.parentNode).parent().prop('offsetTop');
        let left = $(event.target.parentNode).parent().prop('offsetLeft');   
        let scrollTop = $('.fixed-area').scrollTop();
        
        $('.edit-record .popover-tooltip').css({
            top: top - scrollTop - topOffset,
            left: left + leftOffset
        });
        
        $('.edit-record .popover-tooltip .arrow').css({                
            left: arrowOffset
        });            
        
        this.shownElement[elem] = true;
    };
}
