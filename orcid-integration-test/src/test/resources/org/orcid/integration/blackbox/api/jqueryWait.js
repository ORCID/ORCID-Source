console.log("jquery wait script starting");
window._selenium_jquery_done = false;
function _seleniumAngularDone() { 
    angular.element(
        function(){
            console.log("dom ready in jquery wait script");
            setTimeout(function(){
                if ($.active > 0){
                    console.log("number of connections to wait for: " + $.active);
                    _seleniumAngularDone();
                } else {
                    window._selenium_jquery_done = true;
                }
            }, 0);
        }
    );
};
try { _seleniumAngularDone(); } catch(err) { console.log(err); }