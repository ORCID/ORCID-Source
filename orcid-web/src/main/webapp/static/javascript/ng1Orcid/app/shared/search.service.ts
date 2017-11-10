import { Injectable } from '@angular/core';
import { CookieXSRFStrategy, HttpModule, XSRFStrategy } from '@angular/http';
import { JsonpModule } from '@angular/http';
import { Headers, Http, Response, RequestOptions, Jsonp } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/Rx';

//import { Preferences } from './preferences';

@Injectable()
export class SearchSrvc {

    constructor(
        private http: Http,
        private jsonp: Jsonp) {

     }

    private handleError (error: Response | any) {
        let errMsg: string;
        if (error instanceof Response) {
            const body = error.json() || '';
            const err = body.error || JSON.stringify(body);
            errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
        } else {
            errMsg = error.message ? error.message : error.toString();
        }
        console.error(errMsg);
        return Observable.throw(errMsg);
    }

    /*getPreferences(): Observable<Preferences[]> {
        return this.http.get( 'account/preferences.json' ).map(( res: Response ) => res.json()).catch(this.handleError);
    }*/

    getResults(url): Observable<any> {
        var options = new RequestOptions({
          headers: new Headers({
            'Accept': 'application/json'
          })
        });

        return this.http.get(url, options).map(( res: Response ) => res.json()).catch(this.handleError);
    }

    /*$scope.getResults = function(){
                $.ajax({
                    url: orcidSearchUrlJs.buildUrl($scope.input),
                    dataType: 'json',
                    headers: { Accept: 'application/json'},
                    success: function(data) {
                        var bottom = null;
                        var newSearchResults = null;
                        var newSearchResultsTop = null;
                        var showMoreButtonTop = null;
                        $('#ajax-loader-search').hide();
                        $('#ajax-loader-show-more').hide();
                        var orcidList = data['result'];
                        
                        $scope.numFound = data['num-found'];

                        $scope.results = $scope.results.concat(orcidList); 
                        
                        if(!$scope.numFound){
                            $('#no-results-alert').fadeIn(1200);
                        }
                        
                        $scope.areMoreResults = $scope.numFound > ($scope.input.start + $scope.input.rows);
                        
                        //if less than 10 results, show total number found
                        if($scope.numFound && $scope.numFound <= $scope.input.rows){
                            $scope.resultsShowing = $scope.numFound;
                        }

                        //if more than 10 results increment num found by 10
                        if($scope.numFound && $scope.numFound > $scope.input.rows){
                            if($scope.numFound > ($scope.input.start + $scope.input.rows)){
                                $scope.resultsShowing = $scope.input.start + $scope.input.rows;
                            } else {
                                $scope.resultsShowing = ($scope.input.start + $scope.input.rows) - ($scope.input.rows - ($scope.numFound % $scope.input.rows));
                            }
                        }

                        $scope.$apply();
                        
                        newSearchResults = $('.new-search-result');
                        
                        if(newSearchResults.length > 0){
                            newSearchResults.fadeIn(1200);
                            newSearchResults.removeClass('new-search-result');
                            newSearchResultsTop = newSearchResults.offset().top;
                            showMoreButtonTop = $('#show-more-button-container').offset().top;
                            bottom = $(window).height();
                            if(showMoreButtonTop > bottom){
                                $('html, body').animate(
                                    {
                                        scrollTop: newSearchResultsTop
                                    },
                                    1000,
                                    'easeOutQuint'
                                );
                            }
                        }
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error doing search");
                    $('#ajax-loader-search').hide();
                    $('#search-error-alert').fadeIn(1200);

                });
            };*/

    //NOT TESTED: update email frequency
    updateEmailFrequency ( data: any ): Observable<any> {
        const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8', 'Accept': 'text/plain'});
        const options = new RequestOptions({ headers: headers });
        return this.http.post(
            getBaseUri() + '/account/email_preferences.json',
            data.email_frequency,
            options
        ).map(( res: Response ) => res.text()).catch(this.handleError);
    }

    updateDefaultVisibility( data: any ): Observable<any> {
        const headers = new Headers({ 'Content-Type': 'application/json;charset=UTF-8', 'Accept': 'text/plain'});
        const options = new RequestOptions({ headers: headers });
        return this.http.post(
            getBaseUri() + '/account/default_visibility.json',
            data.default_visibility,
            options
        ).map(( res: Response ) => res.text()).catch(this.handleError);
    }

    //NOT TESTED: clear message
    clearMessage(): boolean{
        return false;
    }
}