/*
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */

/*
 * 1 - Utility functions 
 */
function openImportWizardUrl(url) {
    var win = window.open(url, "_target");
    setTimeout( function() {
        if(!win || win.outerHeight === 0) {
            //First Checking Condition Works For IE & Firefox
            //Second Checking Condition Works For Chrome
            window.location.href = url;
        }
    }, 250);
    $.colorbox.close();
}

function contains(arr, obj) {
    var index = arr.length;
    while (index--) {
       if (arr[index] === obj) {
           return true;
       }
    }
    return false;
}

function formatDate(oldDate) {
    var date = new Date(oldDate);
    var day = date.getDate();
    var month = date.getMonth() + 1;
    var year = date.getFullYear();
    if(month < 10) {
        month = '0' + month;
    }
    if(day < 10) {
        day = '0' + day;
    }
    return (year + '-' + month + '-' + day);
}

function getScripts(scripts, callback) {
    var progress = 0;
    var internalCallback = function () {        
        if (++progress == scripts.length - 1) {
            callback();
        }
    };    
    scripts.forEach(function(script) {        
        $.getScript(script, internalCallback);        
    });
}

function formColorBoxWidth() {
    return isMobile()? '100%': '800px';
}

function formColorBoxResize() {
    if (isMobile())
        $.colorbox.resize({width: formColorBoxWidth(), height: '100%'});
    else
        // IE8 and below doesn't take auto height
        // however the default div height
        // is auto anyway
        $.colorbox.resize({width:'800px'});
}

function fixZindexIE7(target, zindex){
    if(isIE() == 7){
        $(target).each(function(){
            $(this).css('z-index', zindex);
            --zindex;
        });
    }
}

function emptyTextField(field) {
    if (field != null
        && field.value != null
        && field.value.trim() != '') {
        return false;
    }
    return true;
}

function addComma(str) {
    if (str.length > 0) return str + ', ';
    return str;
}

//Needs refactor for dw object
function removeBadContributors(dw) {
    for (var idx in dw.contributors) {
        if (dw.contributors[idx].contributorSequence == null
            && dw.contributors[idx].email == null
            && dw.contributors[idx].orcid == null
            && dw.contributors[idx].creditName == null
            && dw.contributors[idx].contributorRole == null
            && dw.contributors[idx].creditNameVisibility == null) {
                dw.contributors.splice(idx,1);
            }
    }
}

//Needs refactor for dw object
function removeBadExternalIdentifiers(dw) {
    for(var idx in dw.workExternalIdentifiers) {
        if(dw.workExternalIdentifiers[idx].workExternalIdentifierType == null
            && dw.workExternalIdentifiers[idx].workExternalIdentifierId == null) {
            dw.workExternalIdentifiers.splice(idx,1);
        }
    }
}

function isEmail(email) {
    var re = /\S+@\S+\.\S+/;
    return re.test(email);
}

function getParameterByName(name) {
    var name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}


//IE7 hack
if (!(window.console && console.log)) {
    console = {
        log : function() {
        },
        debug : function() {
        },
        info : function() {
        },
        warn : function() {
        },
        error : function() {
        }
    };
};

// add number padding function
Number.prototype.pad = function(size) {
    var s = String(this);
    if (typeof (size) !== "number") {
        size = 2;
    }

    while (s.length < size) {
        s = "0" + s;
    }
    return s;
};

// add new method to string
if (typeof String.prototype.startsWith != 'function') {
    String.prototype.startsWith = function(str) {
        return this.slice(0, str.length) == str;
    };
}

// add new method to string
if (typeof String.prototype.contains != 'function') {
    String.prototype.contains = function(str) {
        return this.indexOf(str) != -1;
    };
}

//add new method to escape html
if (typeof String.prototype.escapeHtml != 'function') {
    String.prototype.escapeHtml = function() {
            return this
            .replace(/&/g, '&amp;')
            .replace(/>/g, '&gt;')
            .replace(/</g, '&lt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;')
            .replace(/\//g, '&#x2F;');
    };
}

// add new method to string
if (typeof String.prototype.endsWith != 'function') {
    String.prototype.endsWith = function(str) {
        return this.slice(-str.length) == str;
    };
}

if (!Object.keys) {
    Object.keys = function(obj) {
        var keys = [], k;
        for (k in obj) {
            if (Object.prototype.hasOwnProperty.call(obj, k)) {
                keys.push(k);
            }
        }
        return keys;
    };
}

if (typeof String.prototype.trim != 'function') {
    String.prototype.trim = function() {
        return this.replace(/^\s+|\s+$/g, '');
    };
}

// This is to prevent IE from caching ajax request via jquery
$.ajaxSetup({
    cache : false
});

// test for touch events support and if not supported, attach .no-touch class to
// the HTML tag.
if (!("ontouchstart" in document.documentElement)) {
    document.documentElement.className += " no-touch";
}

// function for javascript cookies
var OrcidCookie = new function() {
    this.getCookie = function(c_name) {
        var i, x, y, ARRcookies = document.cookie.split(";");
        for (i = 0; i < ARRcookies.length; i++) {
            x = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
            y = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);
            x = x.replace(/^\s+|\s+$/g, "");
            if (x == c_name) {
                return unescape(y);
            }
        }
    };

    this.setCookie = function(c_name, value, exdays) {
        var exdate = new Date();
        exdate.setDate(exdate.getDate() + exdays);
        var c_value = escape(value)
                + ((exdays == null) ? "" : "; expires=" + exdate.toUTCString());
        document.cookie = c_name + "=" + c_value + ";path=/";
    };
    
    this.checkIfCookiesEnabled = function() {
    	this.setCookie("cookieTest", "test", 1);
    	var result = this.getCookie("cookieTest");
    	this.setCookie("cookieTest", "test", -1);
        return result;
    };
};

var OrcidGA = function() {
    this.buildClientString = function(clientGroupName, clientName) {
        return clientGroupName + ' - ' + clientName
    };
    this.gaPush = function(trackArray) {
        /*
         * window.ga is blocked by Ghostery and disconnect.me 
         * window.gaGlobal is blocked by uBlock
        */
        if(window.ga && window.gaGlobal) {
            if(typeof trackArray === 'function') {
                ga(trackArray);
            } else {
                if(trackArray[5] == undefined) {
                    ga(trackArray[0], trackArray[1], trackArray[2], trackArray[3], trackArray[4]);
                } else {
                    ga(trackArray[0], trackArray[1], trackArray[2], trackArray[3], trackArray[4], trackArray[5]);
                }                
                console.log("_gap.push for " + trackArray);
            }
            
            setTimeout(function(){
                if(!ga.create) {
                    // if it's a function and _gap isn't available run (typically only
                    // on dev)
                    console.log("no _gap.push for " + trackArray);
                    if (typeof trackArray === 'function')
                        trackArray();
                }
            }, 200); 
        } else {
            console.log("no _gap.push for " + trackArray);
            if (typeof trackArray === 'function')
                trackArray();                      
        }
    };

    // Delays are async functions used to make sure event track que has cleared
    // See
    // https://developers.google.com/analytics/devguides/collection/gajs/methods/gaJSApi_gaq
    //
    // Additionally adding in delay:
    // http://support.google.com/analytics/answer/1136920?hl=en
    this.gaFormSumbitDelay = function($el) {
        if (!$el instanceof jQuery) {
            $el = $(el);
        }
        this.gaPush(function() {
            console.log("_gap.push executing $el.submit()");
            setTimeout(function() {
                $el.submit();
            }, 100);
        });
        return false;
    };

    this.windowLocationHrefDelay = function(url) {
        this.gaPush(function() {
            console.log("_gap.push has executing window.location.href " + url);
            setTimeout(function() {
                window.location.href = url;
            }, 100);
        });
        return false;
    };
};

var orcidGA = new OrcidGA();

var OrcidMessage = function() {
    // nothing to init now
};

OrcidMessage.prototype.get = function(name) {
    return orcidVar.jsMessages.messages[name];
};

var om = new OrcidMessage();

/*
 * every 15 seconds check and make sure the user is logged in. This should keep
 * their session going and if they get logged out (server restart ect...) it
 * will redir them to the signin page.
 */
function logAjaxError(e){
    console.log("status: " + e.status);
    console.log("statusText: " + e.statusText);
    console.log("readyState: " + e.readyState);
    console.log("responseText: " + e.responseText);
}

function getBaseUri() {
    return 'https:' == document.location.protocol ? orcidVar.baseUri
            : orcidVar.baseUriHttp;
}

function myTest() {
    return 'a success';
}

function checkOrcidLoggedIn() {	
    if (OrcidCookie.checkIfCookiesEnabled())
        if ($("meta[name='_csrf']").attr("content") != '')
            $.ajax({
                url : orcidVar.baseUri + '/userStatus.json?callback=?',
                type : 'POST',
                dataType : 'json',
                success : function(data) {
                    if (data.loggedIn == false
                            && (basePath.startsWith(baseUrl
                                    + 'my-orcid') || basePath
                                    .startsWith(baseUrl + 'account'))) {
                        console.log("loggedOutRedir " + data);
                        window.location.href = baseUrl + "signin";
                    }

                }
            }).fail(
            // detects server is down or CSRF mismatches
            // do to session expiration or server bounces
            function(e) {
                console.log("error with loggin check on :"
                        + window.location.href);
                logAjaxError(e);
                window.location = window.location.href;
            });
}

var OM = OrcidMessage;

/*******************************************************************************
 * used for triming org.orcid.pojo.ajaxForm.Text trim the value if it has spaces
 */
function trimAjaxFormText(pojoMember) {
    if (pojoMember != null
            && pojoMember.value != null
            && (pojoMember.value.charAt(0) == ' ' || pojoMember.value
                    .charAt(pojoMember.value.length - 1) == ' '))
        pojoMember.value = pojoMember.value.trim();
}

function logOffReload(reload_param) {
    $.ajax({
        url : baseUrl + 'userStatus.json?logUserOut=true',
        type : 'GET',
        dataType : 'json',
        success : function(data) {
            if (reload_param != null) {
                window.location = window.location.href + '#' + reload_param;
            }
            window.location.reload();
        }
    }).fail(function() {
        // something bad is happening!
        console.log("Error with log out");
        logAjaxError(e);
        window.location.reload();
    });
};

function addShibbolethGa(oauthGaString){
    $('#idpSelectSelectButton').click(
        function() {
            if(typeof oauthGaString !== 'undefined') orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit' , 'OAuth ' + oauthGaString]);
            var entityId = $(this).prev()[0].value;
            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit-Federated', entityId]);
            return true;
        }
    );
    $('#idpSelectPreferredIdPTile a').click(
        function() {
            if(typeof oauthGaString !== 'undefined') orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit' , 'OAuth ' + oauthGaString]);
            var encodedEntityId = this.href.substring(this.href.lastIndexOf('=') + 1);
            var entityId = decodeURIComponent(encodedEntityId);
            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit-Federated', entityId]);
        }
    );
}

// jquery ready
$(function() {
    
    // Common
    
    window.baseUrl = $('body').data('baseurl');
    window.basePath = window.location.pathname;

    // fire off  check, if this page wasn't loaded via iframe (or html5
    // foo)
    if (location == parent.location) {
        checkOrcidLoggedIn();
        setInterval(checkOrcidLoggedIn, 15000);
    }

    // if not iframed check if not orcid.org
    if (location == parent.location
            && window.location.hostname.toLowerCase() != "orcid.org") {

        var cookieName = "testWarningCookie";
        var warnMessCookie = OrcidCookie.getCookie(cookieName);
        if (!warnMessCookie) {
            var wHtml = '<div class="alert" id="test-warn-div">';
            wHtml = wHtml + '<strong>';
            wHtml = wHtml
                    + om.get('common.js.domain.warn.template').replace(
                            '{{curentDomian}}', window.location.hostname);
            wHtml = wHtml + '</strong> ';
            // don't let the warning be disabled for test-warn-dismiss
            if (window.location.hostname.toLowerCase() != "sandbox-1.orcid.org"
                    && window.location.hostname.toLowerCase() != "sandbox.orcid.org") {
                wHtml = wHtml
                        + ' <div style="float: right" class="small"><a href="#" id="test-warn-dismiss">'
                wHtml = wHtml + om.get('common.cookies.click_dismiss');
                wHtml = wHtml + '</a></div>';
            }
            wHtml = wHtml + '</div>';
            $(wHtml).insertBefore('body');
            $("#test-warn-dismiss").click(function() {
                $("#test-warn-div").remove();
                OrcidCookie.setCookie(cookieName, "dont show message", 365);
                return false;
            });
        }

    }

    // track when deactived people are pushed to signin page
    if (window.location.href.endsWith("signin#deactivated")) {
        orcidGA.gaPush([ 'send', 'event', 'Disengagement', 'Deactivate_Complete',
                'Website' ]);
        showLoginError(om.get('orcid.frontend.security.orcid_deactivated'));
    }

    // if on signin or register do cookie check
    if (basePath.startsWith(baseUrl + 'register')
            || basePath.startsWith(baseUrl + 'signin')
            || basePath.startsWith(baseUrl + 'oauth/signin')) {
        if (!OrcidCookie.checkIfCookiesEnabled()) {
            $('#cookie-check-msg').css("display", "inline");
        }
    }

    // jquery browser is deprecated, when you upgrade
    // to 1.9 or higher you will need to use the pluggin
    var oldBrowserFlag = false;

    if (!!navigator.userAgent.match(/Trident\/7\./)) {
        // IE 11
        oldBrowserFlag = false;
    } else if ($.browser.msie && parseInt($.browser.version, 10) < 8) {
        oldBrowserFlag = true;
    } else if (/chrom(e|ium)/.test(navigator.userAgent.toLowerCase())
            && parseInt($.browser.version, 10) < 22) {
        oldBrowserFlag = true;
    } else if ($.browser.mozilla && parseInt($.browser.version, 10) < 15) {
        oldBrowserFlag = true;
    } else if ($.browser.opera && parseInt($.browser.version, 10) < 12) {
        oldBrowserFlag = true;
    } else if ($.browser.safari && parseInt($.browser.version, 10) < 6) {
        oldBrowserFlag = true;
    }

    if (oldBrowserFlag && location == parent.location) {
        var wHtml = '<div class="alert" id="browser-warn-div">';
        wHtml = wHtml + '<strong>';
        wHtml = wHtml + om.get('common.old.browser');
        wHtml = wHtml + '</strong>';
        wHtml = wHtml + '</div>';
        $('body').prepend(wHtml);
    }

    $('form#loginForm')
            .submit(
                    function() {
                        var loginUrl = baseUrl + 'signin/auth.json';
                        if ($('form#loginForm').attr('disabled')) {
                            return false;
                        }
                        if (basePath.startsWith(baseUrl + 'oauth')) {
                            var clientName = $(
                                    'form#loginForm input[name="client_name"]')
                                    .val();
                            var clientGroupName = $(
                                    'form#loginForm input[name="client_group_name"]')
                                    .val();
                            orcidGA.gaPush([
                                    'send',
                                    'event',
                                    'RegGrowth',
                                    'Sign-In-Submit',
                                    'OAuth '
                                            + orcidGA
                                                    .buildClientString(
                                                            clientGroupName,
                                                            clientName) ]);
                        } else
                            orcidGA.gaPush([ 'send', 'event', 'RegGrowth',
                                    'Sign-In-Submit', 'Website' ]);
                        if (basePath.startsWith(baseUrl + 'shibboleth')) {
                            loginUrl = baseUrl + 'shibboleth/signin/auth.json';
                        }
                        else if (basePath.startsWith(baseUrl + 'social')) {
                            loginUrl = baseUrl + 'social/signin/auth.json';
                        }
                        $('form#loginForm').attr('disabled', 'disabled');
                        $('#login-error-mess, #login-deactivated-error').hide();
                        $('#ajax-loader').css('display', 'block');
                        $
                                .ajax(
                                        {
                                            url : loginUrl,
                                            type : 'POST',
                                            data : 'userId=' + encodeURIComponent(orcidLoginFitler($('input[name=userId]').val())) + '&password=' + encodeURIComponent($('input[name=password]').val()),
                                            dataType : 'json',
                                            success : function(data) {
                                                $('#ajax-loader').hide();
                                                $('form#loginForm').removeAttr(
                                                        'disabled');
                                                if (data.success) {
                                                    if (basePath
                                                            .startsWith(baseUrl
                                                                    + 'oauth/signin')) {
                                                        var clientName = $(
                                                                'form#loginForm input[name="client_name"]')
                                                                .val();
                                                        var clientGroupName = $(
                                                                'div#RegistrationCtr input[name="client_group_name"]')
                                                                .val();
                                                        orcidGA
                                                                .gaPush([
                                                                        'send',
                                                                        'event',
                                                                        'RegGrowth',
                                                                        'Sign-In',
                                                                        'OAuth '
                                                                                + orcidGA
                                                                                        .buildClientString(
                                                                                                clientGroupName,
                                                                                                clientName) ]);
                                                    } else
                                                        orcidGA.gaPush([
                                                                'send',
                                                                'event',
                                                                'RegGrowth',
                                                                'Sign-In',
                                                                'Website' ]);
                                                    orcidGA
                                                            .windowLocationHrefDelay(data.url
                                                                    + window.location.hash);
                                                } else {
                                                    var message;
                                                    if (data.deprecated) {
                                                        if (data.primary)
                                                            message = om
                                                                    .get(
                                                                            'orcid.frontend.security.deprecated_with_primary')
                                                                    .replace(
                                                                            "{{primary}}",
                                                                            data.primary);
                                                        else
                                                            message = om
                                                                    .get('orcid.frontend.security.deprecated');
                                                    } else if (data.disabled) {
                                                            showLoginDeactivatedError();
                                                            return;
                                                    } else if (data.unclaimed) {
                                                        var resendClaimUrl = window.location
                                                                + "/../resend-claim";
                                                        var userId = $(
                                                                '#userId')
                                                                .val();
                                                        if (userId
                                                                .indexOf('@') != -1) {
                                                            resendClaimUrl += '?email='
                                                                   + encodeURIComponent(userId);
                                                        }
                                                        message = om
                                                                .get(
                                                                        'orcid.frontend.security.unclaimed_exists')
                                                                .replace(
                                                                        "{{resendClaimUrl}}",
                                                                       resendClaimUrl);
                                                    } else {
                                                        message = om
                                                               .get('orcid.frontend.security.bad_credentials');
                                                    }
                                                    showLoginError(message);
                                                };
                                            }
                                        }).fail(function(e) {
                                    // something bad is happening!
                                    console.log("Error with log in");
                                    logAjaxError(e);
                                    window.location.reload();
                                });
                        return false;
                    });
    
    $('.delete-url').on('click', function(e) {
        e.preventDefault();
        $(this).closest('p').fadeOut(300, function() {
            $(this).closest('p').remove();
        });
    });

    function orcidLoginFitler(userId) {
    	var orcidPattern = /(\d{4}[- ]{0,}){3}\d{3}[\dX]/;
    	var extId = orcidPattern.exec(userId);
    	if(extId != null) {
    		userId = extId[0].toString().replace(/ /g, '');
    		userId = userId.toString().replace(/-/g, '');
    		var temp = userId.toString().replace(/(.{4})/g, "$1-");
    		var length = temp.length;
    		userId = temp.substring(0, length - 1);
    	}
    	return userId;
    }
    
    var hideThing = function(e, selector, className) {
        var p = $(selector + "." + className);
        if (p.length == 0) {
            return;
        }
        if (!$.contains(p.get(0), e.target)) {
            p.removeClass(className);
        }
    };
    
    function showLoginError(message) {
        if ($('form#loginForm #login-error-mess, form#loginForm #login-deactivated-error:visible').length == 0) {
             $(
                "<div class='orcid-error' id='login-error-mess'>"
                        + message
                        + "</div>")
                .hide()
                .appendTo(
                        'form#loginForm')
                .fadeIn('fast');
        } 
        else {
             $(
             'form#loginForm #login-error-mess, form#loginForm #login-deactivated-error:visible')
             .fadeOut(
                    'fast',
                     function() {
                        $('form#loginForm #login-error-mess').html(message);
                         $(
                                 $('form#loginForm #login-error-mess'))
                                 .fadeIn(
                                         'fast');
                     });
        }
    }
    
    function showLoginDeactivatedError() {
        angular.element($("#login-deactivated-error")).scope().showDeactivationError();
        if ($('form#loginForm #login-error-mess').length == 0) {
            $('form#loginForm #login-deactivated-error').fadeIn('fast');
        } else {
             $(
             'form#loginForm #login-error-mess')
             .fadeOut(
                    'fast',
                     function() {
                        $('form#loginForm #login-deactivated-error').fadeIn('fast');
                     });
        }
    }

    // Privacy toggle

    $('body').on('mousedown', function(e) {
        hideThing(e, '.privacy-group', 'open');
        hideThing(e, '.popover', 'show');
    });

    $('body').on('mousedown', function(e) {
        hideThing(e, '.privacy-group', 'open');
        hideThing(e, '.popover', 'show');
    });

    var btnClassR = /(btn-\w+)/;

    var getBtnClass = function(el) {
        var r = btnClassR.exec(el.className);
        return (r ? r[0] : "");
    };

    function privacyBind() {

        $('.privacy-group').each(
                function(i, el) {
                    var $el = $(el), current = "", toggle = $el
                            .find('.privacy-toggle');
                    $el.on('click', '.privacy-toggle', function(e) {
                        e.preventDefault();
                        if ($el.hasClass('open')) {
                            return $el.removeClass('open');
                        }
                        $('.privacy-group.open').removeClass('open');
                        current = getBtnClass(this);
                        $el.toggleClass('open');
                    });
                    $el.on('click', '.btn-privacy', function(e) {
                        e.preventDefault();
                        var f = toggle.closest('form');
                        var s;
                        var priAct = $(this).attr('href').replace("#", "");
                        if (f.length
                                && (f.attr('action') == 'save-current-works')) {
                            s = $('select', toggle.closest('label'));
                            s.val(priAct);
                            showChangeMessage();
                            $el.removeClass('open');
                            toggle.removeClass(current).addClass(
                                    getBtnClass(this));
                            toggle.html($(this).html());
                        } else {
                            var s = toggle.closest('.privacy-tool').prev(
                                    '.visibility-lbl').find('select');
                            if (s.length) {
                                s.val(priAct);
                            }
                            toggle.removeClass(current).addClass(
                                    getBtnClass(this));
                            toggle.html($(this).html());
                            $el.removeClass('open');
                        }
                    });
                });

    }

    privacyBind();

    var passwordStrengthContainer = $(".password-strength");
    if (typeof passwordStrength !== 'undefined') {
        var ps = passwordStrengthContainer.passStrength();
        ps.on('keyup', function(e) {
            if ((location != parent.location) && !this.changed) {
                var i = $('.popover.show iframe', parent.document);
                i.height(i.contents().height());
                this.changed = 1;
            }
        });
    }

    // Manage

    // Popovers
    if (parent !== window) {
        if (typeof parent.$ !== 'undefined') {
            var popover = parent.$('.popover-large:visible');
            if (popover.length) {
                popover.find('iframe').height($('body').outerHeight());
            }
        }
    }

    // Workspace

    // lightboxes

    $('.colorbox').colorbox();

    top.colorOnCloseBoxDest = top.location;

    $('.btn-update:not(.no-icon), .update, #update-personal-modal-link')
            .colorbox({
                iframe : true,
                scrolling : true,
                height : function() {
                    isMobile() ? heightsize = 1150 : heightsize = 600;
                    return heightsize;
                },
                width : 990,
                close : '',
                onClosed : function() {
                    top.location = top.colorOnCloseBoxDest;
                }
            });

    $('.colorbox-modal').colorbox({
        inline : true,
        close : 'x'
    });

    $('body').on('click', '.colorbox-close', function(e) {
        $.colorbox.close();
    });

    if ($('#loginForm').length && (window.location != window.parent.location)) {
        window.parent.location.reload();
    }

    $('.close-button').on('click', function(e) {
        parent.location.reload();
    });

    $('.colorbox-add').colorbox({
        height : 400,
        href : baseUrl + "account/search-for-delegates #add-an-individual"
    });

    // Search hack

    $('#form-search')
            .on(
                    'submit',
                    function(e) {
                        if ($('[name="huh_radio"]:checked', this).val() === "registry") {
                            e.preventDefault();
                            window.location = baseUrl
                                    + "orcid-search/quick-search/?searchQuery="
                                    + encodeURIComponent($('[type="search"]',
                                            this).val());
                        }
                    });

    // delgates
    $('#searchForDelegatesForm').on(
            'submit',
            function(e) {
                e.preventDefault();
                console.log($(this).serialize(), baseUrl
                        + 'manage/search-for-delegates')
                $.post(baseUrl + 'manage/search-for-delegates', $(this)
                        .serialize(), function(data) {
                    $('#searchResults').html(data);
                });
            });
    
    // set of functions to track colorbox complete for automated testing
    window.cbox_complete = false;
    // traditional tracker
    $(document).bind("cbox_complete", function(){
       window.cbox_complete = true;
    });
    // traditional tracker
    $(document).bind("cbox_open", function(){
       window.cbox_complete = false;
    });
    // traditional tracker
    $(document).bind("cbox_closed", function(){
       window.cbox_complete = false;
    });
    
});

/* START: Bibjson to work AjaxForm */
/*
 * { "errors":[], "publicationDate": {
 * "errors":[],"month":"","day":"","year":"","required":true,"getRequiredMessage":null},
 * "visibility":"LIMITED","putCode":null, "shortDescription":{
 * "errors":[],"value":null,"required":true,"getRequiredMessage":null},
 * "url":{"errors":[],"value":null,"required":true,"getRequiredMessage":null},
 * "journalTitle":{"errors":[],"value":null,"required":false,"getRequiredMessage":null},
 * "languageCode":{"errors":[],"value":null,"required":false,"getRequiredMessage":null},
 * "languageName":{"errors":[],"value":null,"required":false,"getRequiredMessage":null},
 * "citation":{"errors":[],"citation":{"errors":[],"value":null,"required":true,"getRequiredMessage":null},
 * "citationType":{"errors":[],"value":"formatted-unspecified","required":true,"getRequiredMessage":null},"required":true,"getRequiredMessage":null},
 * "countryCode":{"errors":[],"value":null,"required":true,"getRequiredMessage":null},
 * "countryName":{"errors":[],"value":null,"required":true,"getRequiredMessage":null},
 * "contributors":[{"errors":[],"contributorSequence":{"errors":[],"value":"","required":true,"getRequiredMessage":null},"email":null,"orcid":null,"uri":null,"creditName":null,"contributorRole":{"errors":[],"value":"","required":true,"getRequiredMessage":null}}],
 * "workExternalIdentifiers":[ { "errors":[],
 * "workExternalIdentifierId":{"errors":[],"value":null,"required":true,"getRequiredMessage":null},
 * "workExternalIdentifierType":{"errors":[],"value":"","required":true,"getRequiredMessage":null}
 * }], "source":null, "sourceName":null,
 * "title":{"errors":[],"value":null,"required":true,"getRequiredMessage":null},
 * "subtitle":{"errors":[],"value":null,"required":true,"getRequiredMessage":null},
 * "translatedTitle":{"errors":[],"content":"","languageCode":"","languageName":"","required":false,"getRequiredMessage":null},
 * "workCategory":{"errors":[],"value":"","required":true,"getRequiredMessage":null},
 * "workType":{"errors":[],"value":"","required":true,"getRequiredMessage":null},"dateSortString":null}
 */

var bibMonths = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"];

var bibToWorkTypeMap = {};
bibToWorkTypeMap['article'] = [ 'publication', 'journal-article' ];
bibToWorkTypeMap['book'] = [ 'publication', 'book' ];
bibToWorkTypeMap['booklet'] = [ 'other_output', 'other' ];
bibToWorkTypeMap['conference'] = [ 'conference', 'conference-paper' ];
bibToWorkTypeMap['inbook'] = [ 'publication', 'book-chapter' ];
bibToWorkTypeMap['incollection'] = [ 'publication', 'book-chapter' ];
bibToWorkTypeMap['inproceedings'] = [ 'conference', 'conference-paper' ];
bibToWorkTypeMap['manual'] = [ 'publication', 'manual' ];
bibToWorkTypeMap['mastersthesis'] = [ 'publication',
        'supervised-student-publication' ];
bibToWorkTypeMap['misc'] = [ 'other_output', 'other' ];
bibToWorkTypeMap['phdthesis'] = [ 'publication', 'dissertation' ];
bibToWorkTypeMap['proceedings'] = [ 'conference', 'conference-paper' ];
bibToWorkTypeMap['techreport'] = [ 'publication', 'report' ];
bibToWorkTypeMap['unpublished'] = [ 'other_output', 'other' ];

function workExternalIdentifierId(work, id, value) {
	
	//Define relationship type based on work type
	var relationship = 'self';
	if(id == 'issn') {
		if(work.workType.value != 'book') {
			relationship = 'part-of';
		}
	} else if(id == 'isbn') {
		if(work.workType.value == 'book-chapter' || work.workType.value == 'conference-paper') {
			relationship = 'part-of';
		}
	} 
	
    var ident = {
        workExternalIdentifierId : {
            'value' : value
        },
        workExternalIdentifierType : {
            'value' : id
        }, 
        relationship : {
        	'value' : relationship
        }
    };
    if (work.workExternalIdentifiers[0].workExternalIdentifierId.value == null)
        work.workExternalIdentifiers[0] = ident;
    else
        work.workExternalIdentifiers.push(ident);
};

function populateWorkAjaxForm(bibJson, work) {

    // get the bibtex back put it in the citation field
    var bibtex = bibtexParse.toBibtex([ bibJson ]);
    work.citation = {'citation': {'value': bibtex},'citationType': {'value': 'bibtex'}}
    
    // set the work type based off the entry type    
    if (bibJson.entryType) {

        var type = bibJson.entryType.toLowerCase();

        if (bibToWorkTypeMap.hasOwnProperty(type)) {
            work.workCategory.value = latexParseJs.decodeLatex(bibToWorkTypeMap[type][0]);
            work.workType.value = bibToWorkTypeMap[type][1];
        }
    }

    // tags we mapped
    if (bibJson.entryTags) {
        // create a lower case create a reference map
        var tags = bibJson.entryTags;
        var lowerKeyTags = {};
        for (key in tags)
            lowerKeyTags[key.toLowerCase()] = tags[key];

        if (lowerKeyTags.hasOwnProperty('booktitle'))
            if (!lowerKeyTags.hasOwnProperty('title'))
                work.title.value = latexParseJs.decodeLatex(lowerKeyTags['booktitle']);
            else if (!lowerKeyTags.hasOwnProperty('journal'))
                work.journalTitle.value = latexParseJs.decodeLatex(lowerKeyTags['booktitle']);

        if (lowerKeyTags.hasOwnProperty('doi'))
            workExternalIdentifierId(work, 'doi', lowerKeyTags['doi']);
        
        if (lowerKeyTags.hasOwnProperty('pmid'))
            workExternalIdentifierId(work, 'pmid', lowerKeyTags['pmid']);

        if (lowerKeyTags.hasOwnProperty('eprint')
                && lowerKeyTags.hasOwnProperty('eprint'))
            workExternalIdentifierId(work, 'arxiv', tags['eprint']);

        if (lowerKeyTags.hasOwnProperty('isbn'))
            workExternalIdentifierId(work, 'isbn', lowerKeyTags['isbn']);

        if (lowerKeyTags.hasOwnProperty('journal'))
            work.journalTitle.value = latexParseJs.decodeLatex(lowerKeyTags['journal']);

        if (lowerKeyTags.hasOwnProperty('title'))
            work.title.value = latexParseJs.decodeLatex(lowerKeyTags['title']);

        if (lowerKeyTags.hasOwnProperty('year'))
            if (!isNaN(lowerKeyTags['year']))
                work.publicationDate.year = lowerKeyTags['year'];

        if (lowerKeyTags.hasOwnProperty('month')) {
            var month = lowerKeyTags['month'];
            if (bibMonths.indexOf(month.trim().substring(0,3)) >= 0) 
                month = bibMonths.indexOf(month.trim().substring(0,3)) + 1;
            if (!isNaN(month) && month > 0 && month <= 12)
                work.publicationDate.month = Number(month).pad(2);
        }

        if (lowerKeyTags.hasOwnProperty('url'))
            work.url.value = lowerKeyTags['url'];

    }
    return work;
};

/* start bibtexParse 0.0.22 */

//Original work by Henrik Muehe (c) 2010
//
//CommonJS port by Mikola Lysenko 2013
//
//Port to Browser lib by ORCID / RCPETERS
//
//Issues:
//no comment handling within strings
//no string concatenation
//no variable values yet
//Grammar implemented here:
//bibtex -> (string | preamble | comment | entry)*;
//string -> '@STRING' '{' key_equals_value '}';
//preamble -> '@PREAMBLE' '{' value '}';
//comment -> '@COMMENT' '{' value '}';
//entry -> '@' key '{' key ',' key_value_list '}';
//key_value_list -> key_equals_value (',' key_equals_value)*;
//key_equals_value -> key '=' value;
//value -> value_quotes | value_braces | key;
//value_quotes -> '"' .*? '"'; // not quite
//value_braces -> '{' .*? '"'; // not quite
(function(exports) {

  function BibtexParser() {
      
      this.months = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"];
      this.notKey = [',','{','}',' ','='];
      this.pos = 0;
      this.input = "";
      this.entries = new Array();

      this.currentEntry = "";

      this.setInput = function(t) {
          this.input = t;
      };

      this.getEntries = function() {
          return this.entries;
      };

      this.isWhitespace = function(s) {
          return (s == ' ' || s == '\r' || s == '\t' || s == '\n');
      };

      this.match = function(s, canCommentOut) {
          if (canCommentOut == undefined || canCommentOut == null)
              canCommentOut = true;
          this.skipWhitespace(canCommentOut);
          if (this.input.substring(this.pos, this.pos + s.length) == s) {
              this.pos += s.length;
          } else {
              throw "Token mismatch, expected " + s + ", found "
                      + this.input.substring(this.pos);
          };
          this.skipWhitespace(canCommentOut);
      };

      this.tryMatch = function(s, canCommentOut) {
          if (canCommentOut == undefined || canCommentOut == null)
              canComment = true;
          this.skipWhitespace(canCommentOut);
          if (this.input.substring(this.pos, this.pos + s.length) == s) {
              return true;
          } else {
              return false;
          };
          this.skipWhitespace(canCommentOut);
      };

      /* when search for a match all text can be ignored, not just white space */
      this.matchAt = function() {
          while (this.input.length > this.pos && this.input[this.pos] != '@') {
              this.pos++;
          };

          if (this.input[this.pos] == '@') {
              return true;
          };
          return false;
      };

      this.skipWhitespace = function(canCommentOut) {
          while (this.isWhitespace(this.input[this.pos])) {
              this.pos++;
          };
          if (this.input[this.pos] == "%" && canCommentOut == true) {
              while (this.input[this.pos] != "\n") {
                  this.pos++;
              };
              this.skipWhitespace(canCommentOut);
          };
      };

      this.value_braces = function() {
          var bracecount = 0;
          this.match("{", false);
          var start = this.pos;
          var escaped = false;
          while (true) {
              if (!escaped) {
                  if (this.input[this.pos] == '}') {
                      if (bracecount > 0) {
                          bracecount--;
                      } else {
                          var end = this.pos;
                          this.match("}", false);
                          return this.input.substring(start, end);
                      };
                  } else if (this.input[this.pos] == '{') {
                      bracecount++;
                  } else if (this.pos >= this.input.length - 1) {
                      throw "Unterminated value";
                  };
              };
              if (this.input[this.pos] == '\\' && escaped == false)
                  escaped == true;
              else
                  escaped == false;
              this.pos++;
          };
      };

      this.value_comment = function() {
          var str = '';
          var brcktCnt = 0;
          while (!(this.tryMatch("}", false) && brcktCnt == 0)) {
              str = str + this.input[this.pos];
              if (this.input[this.pos] == '{')
                  brcktCnt++;
              if (this.input[this.pos] == '}')
                  brcktCnt--;
              if (this.pos >= this.input.length - 1) {
                  throw "Unterminated value:" + this.input.substring(start);
              };
              this.pos++;
          };
          return str;
      };

      this.value_quotes = function() {
          this.match('"', false);
          var start = this.pos;
          var escaped = false;
          while (true) {
              if (!escaped) {
                  if (this.input[this.pos] == '"') {
                      var end = this.pos;
                      this.match('"', false);
                      return this.input.substring(start, end);
                  } else if (this.pos >= this.input.length - 1) {
                      throw "Unterminated value:" + this.input.substring(start);
                  };
              }
              if (this.input[this.pos] == '\\' && escaped == false)
                  escaped == true;
              else
                  escaped == false;
              this.pos++;
          };
      };

      this.single_value = function() {
          var start = this.pos;
          if (this.tryMatch("{")) {
              return this.value_braces();
          } else if (this.tryMatch('"')) {
              return this.value_quotes();
          } else {
              var k = this.key();
              if (k.match("^[0-9]+$"))
                  return k;
              else if (this.months.indexOf(k.toLowerCase()) >= 0)
                  return k.toLowerCase();
              else
                  throw "Value expected:" + this.input.substring(start) + ' for key: ' + k;
          
          };
      };

      this.value = function() {
          var values = [];
          values.push(this.single_value());
          while (this.tryMatch("#")) {
              this.match("#");
              values.push(this.single_value());
          };
          return values.join("");
      };

      this.key = function() {
          var start = this.pos;
          while (true) {
              if (this.pos >= this.input.length) {
                  throw "Runaway key";
              };
                              // а-яА-Я is Cyrillic
              //console.log(this.input[this.pos]);
              if (this.notKey.indexOf(this.input[this.pos]) >= 0) {
                  return this.input.substring(start, this.pos);
              } else {
                  this.pos++;
                  
              };
          };
      };

      this.key_equals_value = function() {
          var key = this.key();
          if (this.tryMatch("=")) {
              this.match("=");
              var val = this.value();
              return [ key, val ];
          } else {
              throw "... = value expected, equals sign missing:"
                      + this.input.substring(this.pos);
          };
      };

      this.key_value_list = function() {
          var kv = this.key_equals_value();
          this.currentEntry['entryTags'] = {};
          this.currentEntry['entryTags'][kv[0]] = kv[1];
          while (this.tryMatch(",")) {
              this.match(",");
              // fixes problems with commas at the end of a list
              if (this.tryMatch("}")) {
                  break;
              }
              ;
              kv = this.key_equals_value();
              this.currentEntry['entryTags'][kv[0]] = kv[1];
          };
      };

      this.entry_body = function(d) {
          this.currentEntry = {};
          this.currentEntry['citationKey'] = this.key();
          this.currentEntry['entryType'] = d.substring(1);
          this.match(",");
          this.key_value_list();
          this.entries.push(this.currentEntry);
      };

      this.directive = function() {
          this.match("@");
          return "@" + this.key();
      };

      this.preamble = function() {
          this.currentEntry = {};
          this.currentEntry['entryType'] = 'PREAMBLE';
          this.currentEntry['entry'] = this.value_comment();
          this.entries.push(this.currentEntry);
      };

      this.comment = function() {
          this.currentEntry = {};
          this.currentEntry['entryType'] = 'COMMENT';
          this.currentEntry['entry'] = this.value_comment();
          this.entries.push(this.currentEntry);
      };

      this.entry = function(d) {
          this.entry_body(d);
      };

      this.bibtex = function() {
          while (this.matchAt()) {
              var d = this.directive();
              this.match("{");
              if (d == "@STRING") {
                  this.string();
              } else if (d == "@PREAMBLE") {
                  this.preamble();
              } else if (d == "@COMMENT") {
                  this.comment();
              } else {
                  this.entry(d);
              }
              this.match("}");
          };
      };
  };
  
  exports.toJSON = function(bibtex) {
      var b = new BibtexParser();
      b.setInput(bibtex);
      b.bibtex();
      return b.entries;
  };

  /* added during hackathon don't hate on me */
  exports.toBibtex = function(json) {
      out = '';
      for ( var i in json) {
          out += "@" + json[i].entryType;
          out += '{';
          if (json[i].citationKey)
              out += json[i].citationKey + ', ';
          if (json[i].entry)
              out += json[i].entry ;
          if (json[i].entryTags) {
              var tags = '';
              for (jdx in json[i].entryTags) {
                  if (tags.length != 0)
                      tags += ', ';
                  tags += jdx + '= {' + json[i].entryTags[jdx] + '}';
              }
              out += tags;
          }
          out += '}\n\n';
      }
      return out;
      
  };

})(typeof exports === 'undefined' ? this['bibtexParse'] = {} : exports);

/* end bibtexParse */


/* start latexJs 0.0.0 */

//complete rubish, please avoid 

(function(exports) {

 
 function LatexToUTF8 () {
     this.orcidCharLatexMap = {
     };

     this.orcidLatexCharMap = {
     "\\`A": "À", // begin grave
     "\\`E": "È",
     "\\`I": "Ì",
     "\\`O": "Ò",
     "\\`U": "Ù",
     "\\`a": "à",
     "\\`e": "è",
     "\\`i": "ì",
     "\\`o": "ò",
     "\\`u": "ù",
     "\\\'A": "Á", // begin acute
     "\\\'E": "É",
     "\\\'I": "Í",
     "\\\'O": "Ó",
     "\\\'U": "Ú",
     "\\\'Y": "Ý",
     "\\\'a": "á",
     "\\\'e": "é",
     "\\\'i": "í",
     "\\\'o": "ó",
     "\\\'u": "ú",
     "\\\'y": "ý",
     "\\\"A": "Ä", // begin diaeresis
     "\\r A": "Å",
     "\\\"E": "Ë",
     "\\\"I": "Ï",
     "\\\"O": "Ö",
     "\\\"U": "Ü",
     "\\\"a": "ä",
     "\\r a": "å",
     "\\\"e": "ë",
     "\\\"i": "ï",
     "\\\"o": "ö",
     "\\\"u": "ü",
     "\\~A": "Ã", // begin tilde
     "\\~N": "Ñ",
     "\\~O": "Õ",
     "\\~a": "ã",
     "\\~n": "ñ",
     "\\~o": "õ",
     "\\rU": "Ů", // begin ring above
     "\\ru": "ů",
     "\\vC": "Č",  // begin caron
     "\\vD": "Ď",
     "\\vE": "Ě",
     "\\vN": "Ň",
     "\\vR": "Ř",
     "\\vS": "Š",
     "\\vT": "Ť",
     "\\vZ": "Ž",
     "\\vc": "č",
     "\\vd": "ď",
     "\\ve": "ě",
     "\\vn": "ň",
     "\\vr": "ř",
     "\\vs": "š",
     "\\vt": "ť",
     "\\vz": "ž",
     "\\#": "#",  // begin special symbols
     "\\$": "$",
     "\\%": "%",
     "\\&": "&",
     "\\\\": "\\",
     "\\^": "^",
     "\\_": "_",
     "\\{": "{",
     "\\}": "}",
     "\\~": "~",
     "\\\"": "\"",
     "\\\'": "’", // closing single quote
     "\\`": "‘", // opening single quote
     "\\AA": "Å", // begin non-ASCII letters
     "\\AE": "Æ",
     "\\c{C}": "Ç",
     "\\O": "Ø",
     "\\aa": "å",
     "\\c{c}": "ç",
     "\\ae": "æ",
     "\\o": "ø",
     "\\ss": "ß",
     "\\textcopyright": "©",
     "\\textellipsis": "…" ,
     "\\textemdash": "—",
     "\\textendash": "–",
     "\\textregistered": "®",
     "\\texttrademark": "™",
     "\\alpha": "α", // begin greek alphabet
     "\\beta": "β",
     "\\gamma": "γ",
     "\\delta": "δ",
     "\\epsilon": "ε",
     "\\zeta": "ζ",
     "\\eta": "η",
     "\\theta": "θ",
     "\\iota": "ι",
     "\\kappa": "κ",
     "\\lambda": "λ",
     "\\mu": "μ",
     "\\nu": "ν",
     "\\xi": "ξ",
     "\\omicron": "ο",
     "\\pi": "π",
     "\\rho": "ρ",
     "\\sigma": "ς",
     "\\tau": "σ",
     "\\upsilon": "τ",
     "\\phi": "υ",
     "\\chi": "φ",
     "\\psi": "χ",
     "\\omega": "ψ",
     "\\=A": "Ā",
     "\\=a": "ā",
     "\\u{A}": "Ă",
     "\\u{a}": "ă",
     "\\k A": "Ą",
     "\\k a": "ą",
     "\\'C": "Ć",
     "\\'c": "ć",
     "\\^C": "Ĉ",
     "\\^c": "ĉ",
     "\\.C": "Ċ",
     "\\.c": "ċ",
     "\\v{C}": "Č",
     "\\v{c}": "č",
     "\\v{D}": "Ď",
     "\\=E": "Ē",
     "\\=e": "ē",
     "\\u{E}": "Ĕ",
     "\\u{e}": "ĕ",
     "\\.E": "Ė",
     "\\.e": "ė",
     "\\k E": "Ę",
     "\\k e": "ę",
     "\\v{E}": "Ě",
     "\\v{e}": "ě",
     "\\^G": "Ĝ",
     "\\^g": "ĝ",
     "\\u{G}": "Ğ",
     "\\u{g}": "ğ",
     "\\.G": "Ġ",
     "\\.g": "ġ",
     "\\c{G}": "Ģ",
     "\\c{g}": "ģ",
     "\\^H": "Ĥ",
     "\\^h": "ĥ",
     "\\dH": "Ħ",
     "\\dh": "ħ",
     "\\~I": "Ĩ",
     "\\~i": "ĩ",
     "\\=I": "Ī",
     "\\=i": "ī",
     "\\u{I}": "Ĭ",
     "\\u{i}": "ĭ",
     "\\k I": "Į",
     "\\k i": "į",
     "\\.I": "İ",
     "\\^J": "Ĵ",
     "\\^j": "ĵ",
     "\\c{J}": "Ķ",
     "\\c{j}": "ķ",
     "\\'L": "Ĺ",
     "\\'l": "ĺ",
     "\\c{L}": "Ļ",
     "\\c{l}": "ļ",
     "\\v{L}": "Ľ",
     "\\v{l}": "ľ",
     "\\dL": "Ł",
     "\\dl": "ł",
     "\\'N": "Ń",
     "\\'n": "ń",
     "\\c{N}": "Ņ",
     "\\c{n}": "ņ",
     "\\v{N}": "Ň",
     "\\v{n}": "ň",
     "\\=O": "Ō",
     "\\=o": "ō",
     "\\u{O}": "Ŏ",
     "\\u{o}": "ŏ",
     "\\H{O}": "Ő",
     "\\H{o}": "ő",
     "\\OE": "Œ",
     "\\oe": "œ",
     "\\'R": "Ŕ",
     "\\'r": "ŕ",
     "\\c{R}": "Ŗ",
     "\\c{r}": "ŗ",
     "\\v{R}": "Ř",
     "\\v{r}": "ř",
     "\\'R": "Ś",
     "\\'r": "ś",
     "\\^S": "Ŝ",
     "\\^s": "ŝ",
     "\\c{S}": "Ş",
     "\\c{s}": "ş",
     "\\v{S}": "Š",
     "\\v{s}": "š",
     "\\c{T}": "Ţ",
     "\\c{t}": "ţ",
     "\\v{T}": "Ť",
     "\\v{t}": "ť",
     "\\dT": "Ŧ",
     "\\dt": "ŧ",
     "\\~U": "Ũ",
     "\\~u": "ũ",
     "\\=U": "Ū",
     "\\=u": "ū",
     "\\u{U}": "Ŭ",
     "\\u{u}": "ŭ",
     "\\r U": "Ů",
     "\\r u": "ů",
     "\\H{U}": "Ű",
     "\\H{u}": "ű",
     "\\k U": "Ų",
     "\\k u": "ų",
     "\\^W": "Ŵ",
     "\\^w": "ŵ",
     "\\^Y": "Ŷ",
     "\\^y": "ŷ",
     "\\\"Y": "Ÿ",
     "\\'Z": "Ź",
     "\\'z": "ź",
     "\\.Z": "Ż",
     "\\.z": "ż",
     "\\v{Z}": "Ž",
     "\\v{z}": "ž"
 };

     this.w3cCharLatexMap = {
     };

//Generated from http://www.w3.org/2003/entities/2007xml/unicode.xml
//Duplicate latex keys skipped (first choosen) 
//Duplicate latex keys not starting with backwardslash (\) skipped
//Decimal Charcode with dashes (-) skipped 
this.w3cLatexCharMap = {
"\\space": " ",
"\\#": "#",
"\\textdollar": "$",
"\\%": "%",
"\\&": "&",
"\\textquotesingle": "'",
"\\ast": "*",
"\\textbackslash": "\\",
"\\^{}": "^",
"\\_": "_",
"\\textasciigrave": "`",
"\\lbrace": "{",
"\\vert": "|",
"\\rbrace": "}",
"\\textasciitilde": "~",
"\\textexclamdown": "¡",
"\\textcent": "¢",
"\\textsterling": "£",
"\\textcurrency": "¤",
"\\textyen": "¥",
"\\textbrokenbar": "¦",
"\\textsection": "§",
"\\textasciidieresis": "¨",
"\\textcopyright": "©",
"\\textordfeminine": "ª",
"\\guillemotleft": "«",
"\\lnot": "¬",
"\\-": "­",
"\\textregistered": "®",
"\\textasciimacron": "¯",
"\\textdegree": "°",
"\\pm": "±",
"\\textasciiacute": "´",
"\\mathrm{\\mu}": "µ",
"\\textparagraph": "¶",
"\\cdot": "·",
"\\c{}": "¸",
"\\textordmasculine": "º",
"\\guillemotright": "»",
"\\textonequarter": "¼",
"\\textonehalf": "½",
"\\textthreequarters": "¾",
"\\textquestiondown": "¿",
"\\`{A}": "À",
"\\'{A}": "Á",
"\\^{A}": "Â",
"\\~{A}": "Ã",
"\\\"{A}": "Ä",
"\\AA": "Å",
"\\AE": "Æ",
"\\c{C}": "Ç",
"\\`{E}": "È",
"\\'{E}": "É",
"\\^{E}": "Ê",
"\\\"{E}": "Ë",
"\\`{I}": "Ì",
"\\'{I}": "Í",
"\\^{I}": "Î",
"\\\"{I}": "Ï",
"\\DH": "Ð",
"\\~{N}": "Ñ",
"\\`{O}": "Ò",
"\\'{O}": "Ó",
"\\^{O}": "Ô",
"\\~{O}": "Õ",
"\\\"{O}": "Ö",
"\\texttimes": "×",
"\\O": "Ø",
"\\`{U}": "Ù",
"\\'{U}": "Ú",
"\\^{U}": "Û",
"\\\"{U}": "Ü",
"\\'{Y}": "Ý",
"\\TH": "Þ",
"\\ss": "ß",
"\\`{a}": "à",
"\\'{a}": "á",
"\\^{a}": "â",
"\\~{a}": "ã",
"\\\"{a}": "ä",
"\\aa": "å",
"\\ae": "æ",
"\\c{c}": "ç",
"\\`{e}": "è",
"\\'{e}": "é",
"\\^{e}": "ê",
"\\\"{e}": "ë",
"\\`{\\i}": "ì",
"\\'{\\i}": "í",
"\\^{\\i}": "î",
"\\\"{\\i}": "ï",
"\\dh": "ð",
"\\~{n}": "ñ",
"\\`{o}": "ò",
"\\'{o}": "ó",
"\\^{o}": "ô",
"\\~{o}": "õ",
"\\\"{o}": "ö",
"\\div": "÷",
"\\o": "ø",
"\\`{u}": "ù",
"\\'{u}": "ú",
"\\^{u}": "û",
"\\\"{u}": "ü",
"\\'{y}": "ý",
"\\th": "þ",
"\\\"{y}": "ÿ",
"\\={A}": "Ā",
"\\={a}": "ā",
"\\u{A}": "Ă",
"\\u{a}": "ă",
"\\k{A}": "Ą",
"\\k{a}": "ą",
"\\'{C}": "Ć",
"\\'{c}": "ć",
"\\^{C}": "Ĉ",
"\\^{c}": "ĉ",
"\\.{C}": "Ċ",
"\\.{c}": "ċ",
"\\v{C}": "Č",
"\\v{c}": "č",
"\\v{D}": "Ď",
"\\v{d}": "ď",
"\\DJ": "Đ",
"\\dj": "đ",
"\\={E}": "Ē",
"\\={e}": "ē",
"\\u{E}": "Ĕ",
"\\u{e}": "ĕ",
"\\.{E}": "Ė",
"\\.{e}": "ė",
"\\k{E}": "Ę",
"\\k{e}": "ę",
"\\v{E}": "Ě",
"\\v{e}": "ě",
"\\^{G}": "Ĝ",
"\\^{g}": "ĝ",
"\\u{G}": "Ğ",
"\\u{g}": "ğ",
"\\.{G}": "Ġ",
"\\.{g}": "ġ",
"\\c{G}": "Ģ",
"\\c{g}": "ģ",
"\\^{H}": "Ĥ",
"\\^{h}": "ĥ",
"\\Elzxh": "ħ",
"\\~{I}": "Ĩ",
"\\~{\\i}": "ĩ",
"\\={I}": "Ī",
"\\={\\i}": "ī",
"\\u{I}": "Ĭ",
"\\u{\\i}": "ĭ",
"\\k{I}": "Į",
"\\k{i}": "į",
"\\.{I}": "İ",
"\\i": "ı",
"\\^{J}": "Ĵ",
"\\^{\\j}": "ĵ",
"\\c{K}": "Ķ",
"\\c{k}": "ķ",
"\\'{L}": "Ĺ",
"\\'{l}": "ĺ",
"\\c{L}": "Ļ",
"\\c{l}": "ļ",
"\\v{L}": "Ľ",
"\\v{l}": "ľ",
"\\L": "Ł",
"\\l": "ł",
"\\'{N}": "Ń",
"\\'{n}": "ń",
"\\c{N}": "Ņ",
"\\c{n}": "ņ",
"\\v{N}": "Ň",
"\\v{n}": "ň",
"\\NG": "Ŋ",
"\\ng": "ŋ",
"\\={O}": "Ō",
"\\={o}": "ō",
"\\u{O}": "Ŏ",
"\\u{o}": "ŏ",
"\\H{O}": "Ő",
"\\H{o}": "ő",
"\\OE": "Œ",
"\\oe": "œ",
"\\'{R}": "Ŕ",
"\\'{r}": "ŕ",
"\\c{R}": "Ŗ",
"\\c{r}": "ŗ",
"\\v{R}": "Ř",
"\\v{r}": "ř",
"\\'{S}": "Ś",
"\\'{s}": "ś",
"\\^{S}": "Ŝ",
"\\^{s}": "ŝ",
"\\c{S}": "Ş",
"\\c{s}": "ş",
"\\v{S}": "Š",
"\\v{s}": "š",
"\\c{T}": "Ţ",
"\\c{t}": "ţ",
"\\v{T}": "Ť",
"\\v{t}": "ť",
"\\~{U}": "Ũ",
"\\~{u}": "ũ",
"\\={U}": "Ū",
"\\={u}": "ū",
"\\u{U}": "Ŭ",
"\\u{u}": "ŭ",
"\\r{U}": "Ů",
"\\r{u}": "ů",
"\\H{U}": "Ű",
"\\H{u}": "ű",
"\\k{U}": "Ų",
"\\k{u}": "ų",
"\\^{W}": "Ŵ",
"\\^{w}": "ŵ",
"\\^{Y}": "Ŷ",
"\\^{y}": "ŷ",
"\\\"{Y}": "Ÿ",
"\\'{Z}": "Ź",
"\\'{z}": "ź",
"\\.{Z}": "Ż",
"\\.{z}": "ż",
"\\v{Z}": "Ž",
"\\v{z}": "ž",
"\\texthvlig": "ƕ",
"\\textnrleg": "ƞ",
"\\eth": "ƪ",
"\\textdoublepipe": "ǂ",
"\\'{g}": "ǵ",
"\\Elztrna": "ɐ",
"\\Elztrnsa": "ɒ",
"\\Elzopeno": "ɔ",
"\\Elzrtld": "ɖ",
"\\Elzschwa": "ə",
"\\varepsilon": "ɛ",
"\\Elzpgamma": "ɣ",
"\\Elzpbgam": "ɤ",
"\\Elztrnh": "ɥ",
"\\Elzbtdl": "ɬ",
"\\Elzrtll": "ɭ",
"\\Elztrnm": "ɯ",
"\\Elztrnmlr": "ɰ",
"\\Elzltlmr": "ɱ",
"\\Elzltln": "ɲ",
"\\Elzrtln": "ɳ",
"\\Elzclomeg": "ɷ",
"\\textphi": "ɸ",
"\\Elztrnr": "ɹ",
"\\Elztrnrl": "ɺ",
"\\Elzrttrnr": "ɻ",
"\\Elzrl": "ɼ",
"\\Elzrtlr": "ɽ",
"\\Elzfhr": "ɾ",
"\\Elzrtls": "ʂ",
"\\Elzesh": "ʃ",
"\\Elztrnt": "ʇ",
"\\Elzrtlt": "ʈ",
"\\Elzpupsil": "ʊ",
"\\Elzpscrv": "ʋ",
"\\Elzinvv": "ʌ",
"\\Elzinvw": "ʍ",
"\\Elztrny": "ʎ",
"\\Elzrtlz": "ʐ",
"\\Elzyogh": "ʒ",
"\\Elzglst": "ʔ",
"\\Elzreglst": "ʕ",
"\\Elzinglst": "ʖ",
"\\textturnk": "ʞ",
"\\Elzdyogh": "ʤ",
"\\Elztesh": "ʧ",
"\\textasciicaron": "ˇ",
"\\Elzverts": "ˈ",
"\\Elzverti": "ˌ",
"\\Elzlmrk": "ː",
"\\Elzhlmrk": "ˑ",
"\\Elzsbrhr": "˒",
"\\Elzsblhr": "˓",
"\\Elzrais": "˔",
"\\Elzlow": "˕",
"\\textasciibreve": "˘",
"\\textperiodcentered": "˙",
"\\r{}": "˚",
"\\k{}": "˛",
"\\texttildelow": "˜",
"\\H{}": "˝",
"\\tone{55}": "˥",
"\\tone{44}": "˦",
"\\tone{33}": "˧",
"\\tone{22}": "˨",
"\\tone{11}": "˩",
"\\`": "̀",
"\\'": "́",
"\\^": "̂",
"\\~": "̃",
"\\=": "̄",
"\\u": "̆",
"\\.": "̇",
"\\\"": "̈",
"\\r": "̊",
"\\H": "̋",
"\\v": "̌",
"\\cyrchar\\C": "̏",
"\\Elzpalh": "̡",
"\\Elzrh": "̢",
"\\c": "̧",
"\\k": "̨",
"\\Elzsbbrg": "̪",
"\\Elzxl": "̵",
"\\Elzbar": "̶",
"\\'{H}": "Ή",
"\\'{}{I}": "Ί",
"\\'{}O": "Ό",
"\\mathrm{'Y}": "Ύ",
"\\mathrm{'\\Omega}": "Ώ",
"\\acute{\\ddot{\\iota}}": "ΐ",
"\\Alpha": "Α",
"\\Beta": "Β",
"\\Gamma": "Γ",
"\\Delta": "Δ",
"\\Epsilon": "Ε",
"\\Zeta": "Ζ",
"\\Eta": "Η",
"\\Theta": "Θ",
"\\Iota": "Ι",
"\\Kappa": "Κ",
"\\Lambda": "Λ",
"\\Xi": "Ξ",
"\\Pi": "Π",
"\\Rho": "Ρ",
"\\Sigma": "Σ",
"\\Tau": "Τ",
"\\Upsilon": "Υ",
"\\Phi": "Φ",
"\\Chi": "Χ",
"\\Psi": "Ψ",
"\\Omega": "Ω",
"\\mathrm{\\ddot{I}}": "Ϊ",
"\\mathrm{\\ddot{Y}}": "Ϋ",
"\\'{$\\alpha$}": "ά",
"\\acute{\\epsilon}": "έ",
"\\acute{\\eta}": "ή",
"\\acute{\\iota}": "ί",
"\\acute{\\ddot{\\upsilon}}": "ΰ",
"\\alpha": "α",
"\\beta": "β",
"\\gamma": "γ",
"\\delta": "δ",
"\\epsilon": "ε",
"\\zeta": "ζ",
"\\eta": "η",
"\\texttheta": "θ",
"\\iota": "ι",
"\\kappa": "κ",
"\\lambda": "λ",
"\\mu": "μ",
"\\nu": "ν",
"\\xi": "ξ",
"\\pi": "π",
"\\rho": "ρ",
"\\varsigma": "ς",
"\\sigma": "σ",
"\\tau": "τ",
"\\upsilon": "υ",
"\\varphi": "φ",
"\\chi": "χ",
"\\psi": "ψ",
"\\omega": "ω",
"\\ddot{\\iota}": "ϊ",
"\\ddot{\\upsilon}": "ϋ",
"\\acute{\\upsilon}": "ύ",
"\\acute{\\omega}": "ώ",
"\\Pisymbol{ppi022}{87}": "ϐ",
"\\textvartheta": "ϑ",
"\\phi": "ϕ",
"\\varpi": "ϖ",
"\\Stigma": "Ϛ",
"\\Digamma": "Ϝ",
"\\digamma": "ϝ",
"\\Koppa": "Ϟ",
"\\Sampi": "Ϡ",
"\\varkappa": "ϰ",
"\\varrho": "ϱ",
"\\textTheta": "ϴ",
"\\backepsilon": "϶",
"\\cyrchar\\CYRYO": "Ё",
"\\cyrchar\\CYRDJE": "Ђ",
"\\cyrchar{\\'\\CYRG}": "Ѓ",
"\\cyrchar\\CYRIE": "Є",
"\\cyrchar\\CYRDZE": "Ѕ",
"\\cyrchar\\CYRII": "І",
"\\cyrchar\\CYRYI": "Ї",
"\\cyrchar\\CYRJE": "Ј",
"\\cyrchar\\CYRLJE": "Љ",
"\\cyrchar\\CYRNJE": "Њ",
"\\cyrchar\\CYRTSHE": "Ћ",
"\\cyrchar{\\'\\CYRK}": "Ќ",
"\\cyrchar\\CYRUSHRT": "Ў",
"\\cyrchar\\CYRDZHE": "Џ",
"\\cyrchar\\CYRA": "А",
"\\cyrchar\\CYRB": "Б",
"\\cyrchar\\CYRV": "В",
"\\cyrchar\\CYRG": "Г",
"\\cyrchar\\CYRD": "Д",
"\\cyrchar\\CYRE": "Е",
"\\cyrchar\\CYRZH": "Ж",
"\\cyrchar\\CYRZ": "З",
"\\cyrchar\\CYRI": "И",
"\\cyrchar\\CYRISHRT": "Й",
"\\cyrchar\\CYRK": "К",
"\\cyrchar\\CYRL": "Л",
"\\cyrchar\\CYRM": "М",
"\\cyrchar\\CYRN": "Н",
"\\cyrchar\\CYRO": "О",
"\\cyrchar\\CYRP": "П",
"\\cyrchar\\CYRR": "Р",
"\\cyrchar\\CYRS": "С",
"\\cyrchar\\CYRT": "Т",
"\\cyrchar\\CYRU": "У",
"\\cyrchar\\CYRF": "Ф",
"\\cyrchar\\CYRH": "Х",
"\\cyrchar\\CYRC": "Ц",
"\\cyrchar\\CYRCH": "Ч",
"\\cyrchar\\CYRSH": "Ш",
"\\cyrchar\\CYRSHCH": "Щ",
"\\cyrchar\\CYRHRDSN": "Ъ",
"\\cyrchar\\CYRERY": "Ы",
"\\cyrchar\\CYRSFTSN": "Ь",
"\\cyrchar\\CYREREV": "Э",
"\\cyrchar\\CYRYU": "Ю",
"\\cyrchar\\CYRYA": "Я",
"\\cyrchar\\cyra": "а",
"\\cyrchar\\cyrb": "б",
"\\cyrchar\\cyrv": "в",
"\\cyrchar\\cyrg": "г",
"\\cyrchar\\cyrd": "д",
"\\cyrchar\\cyre": "е",
"\\cyrchar\\cyrzh": "ж",
"\\cyrchar\\cyrz": "з",
"\\cyrchar\\cyri": "и",
"\\cyrchar\\cyrishrt": "й",
"\\cyrchar\\cyrk": "к",
"\\cyrchar\\cyrl": "л",
"\\cyrchar\\cyrm": "м",
"\\cyrchar\\cyrn": "н",
"\\cyrchar\\cyro": "о",
"\\cyrchar\\cyrp": "п",
"\\cyrchar\\cyrr": "р",
"\\cyrchar\\cyrs": "с",
"\\cyrchar\\cyrt": "т",
"\\cyrchar\\cyru": "у",
"\\cyrchar\\cyrf": "ф",
"\\cyrchar\\cyrh": "х",
"\\cyrchar\\cyrc": "ц",
"\\cyrchar\\cyrch": "ч",
"\\cyrchar\\cyrsh": "ш",
"\\cyrchar\\cyrshch": "щ",
"\\cyrchar\\cyrhrdsn": "ъ",
"\\cyrchar\\cyrery": "ы",
"\\cyrchar\\cyrsftsn": "ь",
"\\cyrchar\\cyrerev": "э",
"\\cyrchar\\cyryu": "ю",
"\\cyrchar\\cyrya": "я",
"\\cyrchar\\cyryo": "ё",
"\\cyrchar\\cyrdje": "ђ",
"\\cyrchar{\\'\\cyrg}": "ѓ",
"\\cyrchar\\cyrie": "є",
"\\cyrchar\\cyrdze": "ѕ",
"\\cyrchar\\cyrii": "і",
"\\cyrchar\\cyryi": "ї",
"\\cyrchar\\cyrje": "ј",
"\\cyrchar\\cyrlje": "љ",
"\\cyrchar\\cyrnje": "њ",
"\\cyrchar\\cyrtshe": "ћ",
"\\cyrchar{\\'\\cyrk}": "ќ",
"\\cyrchar\\cyrushrt": "ў",
"\\cyrchar\\cyrdzhe": "џ",
"\\cyrchar\\CYROMEGA": "Ѡ",
"\\cyrchar\\cyromega": "ѡ",
"\\cyrchar\\CYRYAT": "Ѣ",
"\\cyrchar\\CYRIOTE": "Ѥ",
"\\cyrchar\\cyriote": "ѥ",
"\\cyrchar\\CYRLYUS": "Ѧ",
"\\cyrchar\\cyrlyus": "ѧ",
"\\cyrchar\\CYRIOTLYUS": "Ѩ",
"\\cyrchar\\cyriotlyus": "ѩ",
"\\cyrchar\\CYRBYUS": "Ѫ",
"\\cyrchar\\CYRIOTBYUS": "Ѭ",
"\\cyrchar\\cyriotbyus": "ѭ",
"\\cyrchar\\CYRKSI": "Ѯ",
"\\cyrchar\\cyrksi": "ѯ",
"\\cyrchar\\CYRPSI": "Ѱ",
"\\cyrchar\\cyrpsi": "ѱ",
"\\cyrchar\\CYRFITA": "Ѳ",
"\\cyrchar\\CYRIZH": "Ѵ",
"\\cyrchar\\CYRUK": "Ѹ",
"\\cyrchar\\cyruk": "ѹ",
"\\cyrchar\\CYROMEGARND": "Ѻ",
"\\cyrchar\\cyromegarnd": "ѻ",
"\\cyrchar\\CYROMEGATITLO": "Ѽ",
"\\cyrchar\\cyromegatitlo": "ѽ",
"\\cyrchar\\CYROT": "Ѿ",
"\\cyrchar\\cyrot": "ѿ",
"\\cyrchar\\CYRKOPPA": "Ҁ",
"\\cyrchar\\cyrkoppa": "ҁ",
"\\cyrchar\\cyrthousands": "҂",
"\\cyrchar\\cyrhundredthousands": "҈",
"\\cyrchar\\cyrmillions": "҉",
"\\cyrchar\\CYRSEMISFTSN": "Ҍ",
"\\cyrchar\\cyrsemisftsn": "ҍ",
"\\cyrchar\\CYRRTICK": "Ҏ",
"\\cyrchar\\cyrrtick": "ҏ",
"\\cyrchar\\CYRGUP": "Ґ",
"\\cyrchar\\cyrgup": "ґ",
"\\cyrchar\\CYRGHCRS": "Ғ",
"\\cyrchar\\cyrghcrs": "ғ",
"\\cyrchar\\CYRGHK": "Ҕ",
"\\cyrchar\\cyrghk": "ҕ",
"\\cyrchar\\CYRZHDSC": "Җ",
"\\cyrchar\\cyrzhdsc": "җ",
"\\cyrchar\\CYRZDSC": "Ҙ",
"\\cyrchar\\cyrzdsc": "ҙ",
"\\cyrchar\\CYRKDSC": "Қ",
"\\cyrchar\\cyrkdsc": "қ",
"\\cyrchar\\CYRKVCRS": "Ҝ",
"\\cyrchar\\cyrkvcrs": "ҝ",
"\\cyrchar\\CYRKHCRS": "Ҟ",
"\\cyrchar\\cyrkhcrs": "ҟ",
"\\cyrchar\\CYRKBEAK": "Ҡ",
"\\cyrchar\\cyrkbeak": "ҡ",
"\\cyrchar\\CYRNDSC": "Ң",
"\\cyrchar\\cyrndsc": "ң",
"\\cyrchar\\CYRNG": "Ҥ",
"\\cyrchar\\cyrng": "ҥ",
"\\cyrchar\\CYRPHK": "Ҧ",
"\\cyrchar\\cyrphk": "ҧ",
"\\cyrchar\\CYRABHHA": "Ҩ",
"\\cyrchar\\cyrabhha": "ҩ",
"\\cyrchar\\CYRSDSC": "Ҫ",
"\\cyrchar\\cyrsdsc": "ҫ",
"\\cyrchar\\CYRTDSC": "Ҭ",
"\\cyrchar\\cyrtdsc": "ҭ",
"\\cyrchar\\CYRY": "Ү",
"\\cyrchar\\cyry": "ү",
"\\cyrchar\\CYRYHCRS": "Ұ",
"\\cyrchar\\cyryhcrs": "ұ",
"\\cyrchar\\CYRHDSC": "Ҳ",
"\\cyrchar\\cyrhdsc": "ҳ",
"\\cyrchar\\CYRTETSE": "Ҵ",
"\\cyrchar\\cyrtetse": "ҵ",
"\\cyrchar\\CYRCHRDSC": "Ҷ",
"\\cyrchar\\cyrchrdsc": "ҷ",
"\\cyrchar\\CYRCHVCRS": "Ҹ",
"\\cyrchar\\cyrchvcrs": "ҹ",
"\\cyrchar\\CYRSHHA": "Һ",
"\\cyrchar\\cyrshha": "һ",
"\\cyrchar\\CYRABHCH": "Ҽ",
"\\cyrchar\\cyrabhch": "ҽ",
"\\cyrchar\\CYRABHCHDSC": "Ҿ",
"\\cyrchar\\cyrabhchdsc": "ҿ",
"\\cyrchar\\CYRpalochka": "Ӏ",
"\\cyrchar\\CYRKHK": "Ӄ",
"\\cyrchar\\cyrkhk": "ӄ",
"\\cyrchar\\CYRNHK": "Ӈ",
"\\cyrchar\\cyrnhk": "ӈ",
"\\cyrchar\\CYRCHLDSC": "Ӌ",
"\\cyrchar\\cyrchldsc": "ӌ",
"\\cyrchar\\CYRAE": "Ӕ",
"\\cyrchar\\cyrae": "ӕ",
"\\cyrchar\\CYRSCHWA": "Ә",
"\\cyrchar\\cyrschwa": "ә",
"\\cyrchar\\CYRABHDZE": "Ӡ",
"\\cyrchar\\cyrabhdze": "ӡ",
"\\cyrchar\\CYROTLD": "Ө",
"\\cyrchar\\cyrotld": "ө",
"\\hspace{0.6em}": " ",
"\\hspace{1em}": " ",
"\\hspace{0.33em}": " ",
"\\hspace{0.25em}": " ",
"\\hspace{0.166em}": " ",
"\\hphantom{0}": " ",
"\\hphantom{,}": " ",
"\\hspace{0.167em}": " ",
"\\mkern1mu": " ",
"\\textendash": "–",
"\\textemdash": "—",
"\\rule{1em}{1pt}": "―",
"\\Vert": "‖",
"\\Elzreapos": "‛",
"\\textquotedblleft": "“",
"\\textquotedblright": "”",
"\\textdagger": "†",
"\\textdaggerdbl": "‡",
"\\textbullet": "•",
"\\ldots": "…",
"\\textperthousand": "‰",
"\\textpertenthousand": "‱",
"\\backprime": "‵",
"\\guilsinglleft": "‹",
"\\guilsinglright": "›",
"\\mkern4mu": " ",
"\\nolinebreak": "⁠",
"\\ensuremath{\\Elzpes}": "₧",
"\\mbox{\\texteuro}": "€",
"\\dddot": "⃛",
"\\ddddot": "⃜",
"\\mathbb{C}": "ℂ",
"\\mathscr{g}": "ℊ",
"\\mathscr{H}": "ℋ",
"\\mathfrak{H}": "ℌ",
"\\mathbb{H}": "ℍ",
"\\hslash": "ℏ",
"\\mathscr{I}": "ℐ",
"\\mathfrak{I}": "ℑ",
"\\mathscr{L}": "ℒ",
"\\mathscr{l}": "ℓ",
"\\mathbb{N}": "ℕ",
"\\cyrchar\\textnumero": "№",
"\\wp": "℘",
"\\mathbb{P}": "ℙ",
"\\mathbb{Q}": "ℚ",
"\\mathscr{R}": "ℛ",
"\\mathfrak{R}": "ℜ",
"\\mathbb{R}": "ℝ",
"\\Elzxrat": "℞",
"\\texttrademark": "™",
"\\mathbb{Z}": "ℤ",
"\\mho": "℧",
"\\mathfrak{Z}": "ℨ",
"\\ElsevierGlyph{2129}": "℩",
"\\mathscr{B}": "ℬ",
"\\mathfrak{C}": "ℭ",
"\\mathscr{e}": "ℯ",
"\\mathscr{E}": "ℰ",
"\\mathscr{F}": "ℱ",
"\\mathscr{M}": "ℳ",
"\\mathscr{o}": "ℴ",
"\\aleph": "ℵ",
"\\beth": "ℶ",
"\\gimel": "ℷ",
"\\daleth": "ℸ",
"\\textfrac{1}{3}": "⅓",
"\\textfrac{2}{3}": "⅔",
"\\textfrac{1}{5}": "⅕",
"\\textfrac{2}{5}": "⅖",
"\\textfrac{3}{5}": "⅗",
"\\textfrac{4}{5}": "⅘",
"\\textfrac{1}{6}": "⅙",
"\\textfrac{5}{6}": "⅚",
"\\textfrac{1}{8}": "⅛",
"\\textfrac{3}{8}": "⅜",
"\\textfrac{5}{8}": "⅝",
"\\textfrac{7}{8}": "⅞",
"\\leftarrow": "←",
"\\uparrow": "↑",
"\\rightarrow": "→",
"\\downarrow": "↓",
"\\leftrightarrow": "↔",
"\\updownarrow": "↕",
"\\nwarrow": "↖",
"\\nearrow": "↗",
"\\searrow": "↘",
"\\swarrow": "↙",
"\\nleftarrow": "↚",
"\\nrightarrow": "↛",
"\\arrowwaveleft": "↜",
"\\arrowwaveright": "↝",
"\\twoheadleftarrow": "↞",
"\\twoheadrightarrow": "↠",
"\\leftarrowtail": "↢",
"\\rightarrowtail": "↣",
"\\mapsto": "↦",
"\\hookleftarrow": "↩",
"\\hookrightarrow": "↪",
"\\looparrowleft": "↫",
"\\looparrowright": "↬",
"\\leftrightsquigarrow": "↭",
"\\nleftrightarrow": "↮",
"\\Lsh": "↰",
"\\Rsh": "↱",
"\\ElsevierGlyph{21B3}": "↳",
"\\curvearrowleft": "↶",
"\\curvearrowright": "↷",
"\\circlearrowleft": "↺",
"\\circlearrowright": "↻",
"\\leftharpoonup": "↼",
"\\leftharpoondown": "↽",
"\\upharpoonright": "↾",
"\\upharpoonleft": "↿",
"\\rightharpoonup": "⇀",
"\\rightharpoondown": "⇁",
"\\downharpoonright": "⇂",
"\\downharpoonleft": "⇃",
"\\rightleftarrows": "⇄",
"\\dblarrowupdown": "⇅",
"\\leftrightarrows": "⇆",
"\\leftleftarrows": "⇇",
"\\upuparrows": "⇈",
"\\rightrightarrows": "⇉",
"\\downdownarrows": "⇊",
"\\leftrightharpoons": "⇋",
"\\rightleftharpoons": "⇌",
"\\nLeftarrow": "⇍",
"\\nLeftrightarrow": "⇎",
"\\nRightarrow": "⇏",
"\\Leftarrow": "⇐",
"\\Uparrow": "⇑",
"\\Rightarrow": "⇒",
"\\Downarrow": "⇓",
"\\Leftrightarrow": "⇔",
"\\Updownarrow": "⇕",
"\\Lleftarrow": "⇚",
"\\Rrightarrow": "⇛",
"\\rightsquigarrow": "⇝",
"\\DownArrowUpArrow": "⇵",
"\\forall": "∀",
"\\complement": "∁",
"\\partial": "∂",
"\\exists": "∃",
"\\nexists": "∄",
"\\varnothing": "∅",
"\\nabla": "∇",
"\\in": "∈",
"\\not\\in": "∉",
"\\ni": "∋",
"\\not\\ni": "∌",
"\\prod": "∏",
"\\coprod": "∐",
"\\sum": "∑",
"\\mp": "∓",
"\\dotplus": "∔",
"\\setminus": "∖",
"\\circ": "∘",
"\\bullet": "∙",
"\\surd": "√",
"\\propto": "∝",
"\\infty": "∞",
"\\rightangle": "∟",
"\\angle": "∠",
"\\measuredangle": "∡",
"\\sphericalangle": "∢",
"\\mid": "∣",
"\\nmid": "∤",
"\\parallel": "∥",
"\\nparallel": "∦",
"\\wedge": "∧",
"\\vee": "∨",
"\\cap": "∩",
"\\cup": "∪",
"\\int": "∫",
"\\int\\!\\int": "∬",
"\\int\\!\\int\\!\\int": "∭",
"\\oint": "∮",
"\\surfintegral": "∯",
"\\volintegral": "∰",
"\\clwintegral": "∱",
"\\ElsevierGlyph{2232}": "∲",
"\\ElsevierGlyph{2233}": "∳",
"\\therefore": "∴",
"\\because": "∵",
"\\Colon": "∷",
"\\ElsevierGlyph{2238}": "∸",
"\\mathbin{{:}\\!\\!{-}\\!\\!{:}}": "∺",
"\\homothetic": "∻",
"\\sim": "∼",
"\\backsim": "∽",
"\\lazysinv": "∾",
"\\wr": "≀",
"\\not\\sim": "≁",
"\\ElsevierGlyph{2242}": "≂",
"\\simeq": "≃",
"\\not\\simeq": "≄",
"\\cong": "≅",
"\\approxnotequal": "≆",
"\\not\\cong": "≇",
"\\approx": "≈",
"\\not\\approx": "≉",
"\\approxeq": "≊",
"\\tildetrpl": "≋",
"\\allequal": "≌",
"\\asymp": "≍",
"\\Bumpeq": "≎",
"\\bumpeq": "≏",
"\\doteq": "≐",
"\\doteqdot": "≑",
"\\fallingdotseq": "≒",
"\\risingdotseq": "≓",
"\\eqcirc": "≖",
"\\circeq": "≗",
"\\estimates": "≙",
"\\ElsevierGlyph{225A}": "≚",
"\\starequal": "≛",
"\\triangleq": "≜",
"\\ElsevierGlyph{225F}": "≟",
"\\not =": "≠",
"\\equiv": "≡",
"\\not\\equiv": "≢",
"\\leq": "≤",
"\\geq": "≥",
"\\leqq": "≦",
"\\geqq": "≧",
"\\lneqq": "≨",
"\\gneqq": "≩",
"\\ll": "≪",
"\\gg": "≫",
"\\between": "≬",
"\\not\\kern-0.3em\\times": "≭",
"\\not<": "≮",
"\\not>": "≯",
"\\not\\leq": "≰",
"\\not\\geq": "≱",
"\\lessequivlnt": "≲",
"\\greaterequivlnt": "≳",
"\\ElsevierGlyph{2274}": "≴",
"\\ElsevierGlyph{2275}": "≵",
"\\lessgtr": "≶",
"\\gtrless": "≷",
"\\notlessgreater": "≸",
"\\notgreaterless": "≹",
"\\prec": "≺",
"\\succ": "≻",
"\\preccurlyeq": "≼",
"\\succcurlyeq": "≽",
"\\precapprox": "≾",
"\\succapprox": "≿",
"\\not\\prec": "⊀",
"\\not\\succ": "⊁",
"\\subset": "⊂",
"\\supset": "⊃",
"\\not\\subset": "⊄",
"\\not\\supset": "⊅",
"\\subseteq": "⊆",
"\\supseteq": "⊇",
"\\not\\subseteq": "⊈",
"\\not\\supseteq": "⊉",
"\\subsetneq": "⊊",
"\\supsetneq": "⊋",
"\\uplus": "⊎",
"\\sqsubset": "⊏",
"\\sqsupset": "⊐",
"\\sqsubseteq": "⊑",
"\\sqsupseteq": "⊒",
"\\sqcap": "⊓",
"\\sqcup": "⊔",
"\\oplus": "⊕",
"\\ominus": "⊖",
"\\otimes": "⊗",
"\\oslash": "⊘",
"\\odot": "⊙",
"\\circledcirc": "⊚",
"\\circledast": "⊛",
"\\circleddash": "⊝",
"\\boxplus": "⊞",
"\\boxminus": "⊟",
"\\boxtimes": "⊠",
"\\boxdot": "⊡",
"\\vdash": "⊢",
"\\dashv": "⊣",
"\\top": "⊤",
"\\perp": "⊥",
"\\truestate": "⊧",
"\\forcesextra": "⊨",
"\\Vdash": "⊩",
"\\Vvdash": "⊪",
"\\VDash": "⊫",
"\\nvdash": "⊬",
"\\nvDash": "⊭",
"\\nVdash": "⊮",
"\\nVDash": "⊯",
"\\vartriangleleft": "⊲",
"\\vartriangleright": "⊳",
"\\trianglelefteq": "⊴",
"\\trianglerighteq": "⊵",
"\\original": "⊶",
"\\image": "⊷",
"\\multimap": "⊸",
"\\hermitconjmatrix": "⊹",
"\\intercal": "⊺",
"\\veebar": "⊻",
"\\rightanglearc": "⊾",
"\\ElsevierGlyph{22C0}": "⋀",
"\\ElsevierGlyph{22C1}": "⋁",
"\\bigcap": "⋂",
"\\bigcup": "⋃",
"\\diamond": "⋄",
"\\star": "⋆",
"\\divideontimes": "⋇",
"\\bowtie": "⋈",
"\\ltimes": "⋉",
"\\rtimes": "⋊",
"\\leftthreetimes": "⋋",
"\\rightthreetimes": "⋌",
"\\backsimeq": "⋍",
"\\curlyvee": "⋎",
"\\curlywedge": "⋏",
"\\Subset": "⋐",
"\\Supset": "⋑",
"\\Cap": "⋒",
"\\Cup": "⋓",
"\\pitchfork": "⋔",
"\\lessdot": "⋖",
"\\gtrdot": "⋗",
"\\verymuchless": "⋘",
"\\verymuchgreater": "⋙",
"\\lesseqgtr": "⋚",
"\\gtreqless": "⋛",
"\\curlyeqprec": "⋞",
"\\curlyeqsucc": "⋟",
"\\not\\sqsubseteq": "⋢",
"\\not\\sqsupseteq": "⋣",
"\\Elzsqspne": "⋥",
"\\lnsim": "⋦",
"\\gnsim": "⋧",
"\\precedesnotsimilar": "⋨",
"\\succnsim": "⋩",
"\\ntriangleleft": "⋪",
"\\ntriangleright": "⋫",
"\\ntrianglelefteq": "⋬",
"\\ntrianglerighteq": "⋭",
"\\vdots": "⋮",
"\\cdots": "⋯",
"\\upslopeellipsis": "⋰",
"\\downslopeellipsis": "⋱",
"\\barwedge": "⌅",
"\\varperspcorrespond": "⌆",
"\\lceil": "⌈",
"\\rceil": "⌉",
"\\lfloor": "⌊",
"\\rfloor": "⌋",
"\\recorder": "⌕",
"\\mathchar\"2208": "⌖",
"\\ulcorner": "⌜",
"\\urcorner": "⌝",
"\\llcorner": "⌞",
"\\lrcorner": "⌟",
"\\frown": "⌢",
"\\smile": "⌣",
"\\ElsevierGlyph{E838}": "⌽",
"\\Elzdlcorn": "⎣",
"\\lmoustache": "⎰",
"\\rmoustache": "⎱",
"\\textvisiblespace": "␣",
"\\ding{172}": "①",
"\\ding{173}": "②",
"\\ding{174}": "③",
"\\ding{175}": "④",
"\\ding{176}": "⑤",
"\\ding{177}": "⑥",
"\\ding{178}": "⑦",
"\\ding{179}": "⑧",
"\\ding{180}": "⑨",
"\\ding{181}": "⑩",
"\\circledS": "Ⓢ",
"\\Elzdshfnc": "┆",
"\\Elzsqfnw": "┙",
"\\diagup": "╱",
"\\ding{110}": "■",
"\\square": "□",
"\\blacksquare": "▪",
"\\fbox{~~}": "▭",
"\\Elzvrecto": "▯",
"\\ElsevierGlyph{E381}": "▱",
"\\ding{115}": "▲",
"\\bigtriangleup": "△",
"\\blacktriangle": "▴",
"\\vartriangle": "▵",
"\\blacktriangleright": "▸",
"\\triangleright": "▹",
"\\ding{116}": "▼",
"\\bigtriangledown": "▽",
"\\blacktriangledown": "▾",
"\\triangledown": "▿",
"\\blacktriangleleft": "◂",
"\\triangleleft": "◃",
"\\ding{117}": "◆",
"\\lozenge": "◊",
"\\bigcirc": "○",
"\\ding{108}": "●",
"\\Elzcirfl": "◐",
"\\Elzcirfr": "◑",
"\\Elzcirfb": "◒",
"\\ding{119}": "◗",
"\\Elzrvbull": "◘",
"\\Elzsqfl": "◧",
"\\Elzsqfr": "◨",
"\\Elzsqfse": "◪",
"\\ding{72}": "★",
"\\ding{73}": "☆",
"\\ding{37}": "☎",
"\\ding{42}": "☛",
"\\ding{43}": "☞",
"\\rightmoon": "☾",
"\\mercury": "☿",
"\\venus": "♀",
"\\male": "♂",
"\\jupiter": "♃",
"\\saturn": "♄",
"\\uranus": "♅",
"\\neptune": "♆",
"\\pluto": "♇",
"\\aries": "♈",
"\\taurus": "♉",
"\\gemini": "♊",
"\\cancer": "♋",
"\\leo": "♌",
"\\virgo": "♍",
"\\libra": "♎",
"\\scorpio": "♏",
"\\sagittarius": "♐",
"\\capricornus": "♑",
"\\aquarius": "♒",
"\\pisces": "♓",
"\\ding{171}": "♠",
"\\ding{168}": "♣",
"\\ding{170}": "♥",
"\\ding{169}": "♦",
"\\quarternote": "♩",
"\\eighthnote": "♪",
"\\flat": "♭",
"\\natural": "♮",
"\\sharp": "♯",
"\\ding{33}": "✁",
"\\ding{34}": "✂",
"\\ding{35}": "✃",
"\\ding{36}": "✄",
"\\ding{38}": "✆",
"\\ding{39}": "✇",
"\\ding{40}": "✈",
"\\ding{41}": "✉",
"\\ding{44}": "✌",
"\\ding{45}": "✍",
"\\ding{46}": "✎",
"\\ding{47}": "✏",
"\\ding{48}": "✐",
"\\ding{49}": "✑",
"\\ding{50}": "✒",
"\\ding{51}": "✓",
"\\ding{52}": "✔",
"\\ding{53}": "✕",
"\\ding{54}": "✖",
"\\ding{55}": "✗",
"\\ding{56}": "✘",
"\\ding{57}": "✙",
"\\ding{58}": "✚",
"\\ding{59}": "✛",
"\\ding{60}": "✜",
"\\ding{61}": "✝",
"\\ding{62}": "✞",
"\\ding{63}": "✟",
"\\ding{64}": "✠",
"\\ding{65}": "✡",
"\\ding{66}": "✢",
"\\ding{67}": "✣",
"\\ding{68}": "✤",
"\\ding{69}": "✥",
"\\ding{70}": "✦",
"\\ding{71}": "✧",
"\\ding{74}": "✪",
"\\ding{75}": "✫",
"\\ding{76}": "✬",
"\\ding{77}": "✭",
"\\ding{78}": "✮",
"\\ding{79}": "✯",
"\\ding{80}": "✰",
"\\ding{81}": "✱",
"\\ding{82}": "✲",
"\\ding{83}": "✳",
"\\ding{84}": "✴",
"\\ding{85}": "✵",
"\\ding{86}": "✶",
"\\ding{87}": "✷",
"\\ding{88}": "✸",
"\\ding{89}": "✹",
"\\ding{90}": "✺",
"\\ding{91}": "✻",
"\\ding{92}": "✼",
"\\ding{93}": "✽",
"\\ding{94}": "✾",
"\\ding{95}": "✿",
"\\ding{96}": "❀",
"\\ding{97}": "❁",
"\\ding{98}": "❂",
"\\ding{99}": "❃",
"\\ding{100}": "❄",
"\\ding{101}": "❅",
"\\ding{102}": "❆",
"\\ding{103}": "❇",
"\\ding{104}": "❈",
"\\ding{105}": "❉",
"\\ding{106}": "❊",
"\\ding{107}": "❋",
"\\ding{109}": "❍",
"\\ding{111}": "❏",
"\\ding{112}": "❐",
"\\ding{113}": "❑",
"\\ding{114}": "❒",
"\\ding{118}": "❖",
"\\ding{120}": "❘",
"\\ding{121}": "❙",
"\\ding{122}": "❚",
"\\ding{123}": "❛",
"\\ding{124}": "❜",
"\\ding{125}": "❝",
"\\ding{126}": "❞",
"\\ding{161}": "❡",
"\\ding{162}": "❢",
"\\ding{163}": "❣",
"\\ding{164}": "❤",
"\\ding{165}": "❥",
"\\ding{166}": "❦",
"\\ding{167}": "❧",
"\\ding{182}": "❶",
"\\ding{183}": "❷",
"\\ding{184}": "❸",
"\\ding{185}": "❹",
"\\ding{186}": "❺",
"\\ding{187}": "❻",
"\\ding{188}": "❼",
"\\ding{189}": "❽",
"\\ding{190}": "❾",
"\\ding{191}": "❿",
"\\ding{192}": "➀",
"\\ding{193}": "➁",
"\\ding{194}": "➂",
"\\ding{195}": "➃",
"\\ding{196}": "➄",
"\\ding{197}": "➅",
"\\ding{198}": "➆",
"\\ding{199}": "➇",
"\\ding{200}": "➈",
"\\ding{201}": "➉",
"\\ding{202}": "➊",
"\\ding{203}": "➋",
"\\ding{204}": "➌",
"\\ding{205}": "➍",
"\\ding{206}": "➎",
"\\ding{207}": "➏",
"\\ding{208}": "➐",
"\\ding{209}": "➑",
"\\ding{210}": "➒",
"\\ding{211}": "➓",
"\\ding{212}": "➔",
"\\ding{216}": "➘",
"\\ding{217}": "➙",
"\\ding{218}": "➚",
"\\ding{219}": "➛",
"\\ding{220}": "➜",
"\\ding{221}": "➝",
"\\ding{222}": "➞",
"\\ding{223}": "➟",
"\\ding{224}": "➠",
"\\ding{225}": "➡",
"\\ding{226}": "➢",
"\\ding{227}": "➣",
"\\ding{228}": "➤",
"\\ding{229}": "➥",
"\\ding{230}": "➦",
"\\ding{231}": "➧",
"\\ding{232}": "➨",
"\\ding{233}": "➩",
"\\ding{234}": "➪",
"\\ding{235}": "➫",
"\\ding{236}": "➬",
"\\ding{237}": "➭",
"\\ding{238}": "➮",
"\\ding{239}": "➯",
"\\ding{241}": "➱",
"\\ding{242}": "➲",
"\\ding{243}": "➳",
"\\ding{244}": "➴",
"\\ding{245}": "➵",
"\\ding{246}": "➶",
"\\ding{247}": "➷",
"\\ding{248}": "➸",
"\\ding{249}": "➹",
"\\ding{250}": "➺",
"\\ding{251}": "➻",
"\\ding{252}": "➼",
"\\ding{253}": "➽",
"\\ding{254}": "➾",
"\\langle": "⟨",
"\\rangle": "⟩",
"\\longleftarrow": "⟵",
"\\longrightarrow": "⟶",
"\\longleftrightarrow": "⟷",
"\\Longleftarrow": "⟸",
"\\Longrightarrow": "⟹",
"\\Longleftrightarrow": "⟺",
"\\longmapsto": "⟼",
"\\sim\\joinrel\\leadsto": "⟿",
"\\ElsevierGlyph{E212}": "⤅",
"\\UpArrowBar": "⤒",
"\\DownArrowBar": "⤓",
"\\ElsevierGlyph{E20C}": "⤣",
"\\ElsevierGlyph{E20D}": "⤤",
"\\ElsevierGlyph{E20B}": "⤥",
"\\ElsevierGlyph{E20A}": "⤦",
"\\ElsevierGlyph{E211}": "⤧",
"\\ElsevierGlyph{E20E}": "⤨",
"\\ElsevierGlyph{E20F}": "⤩",
"\\ElsevierGlyph{E210}": "⤪",
"\\ElsevierGlyph{E21C}": "⤳",
"\\ElsevierGlyph{E21A}": "⤶",
"\\ElsevierGlyph{E219}": "⤷",
"\\Elolarr": "⥀",
"\\Elorarr": "⥁",
"\\ElzRlarr": "⥂",
"\\ElzrLarr": "⥄",
"\\Elzrarrx": "⥇",
"\\LeftRightVector": "⥎",
"\\RightUpDownVector": "⥏",
"\\DownLeftRightVector": "⥐",
"\\LeftUpDownVector": "⥑",
"\\LeftVectorBar": "⥒",
"\\RightVectorBar": "⥓",
"\\RightUpVectorBar": "⥔",
"\\RightDownVectorBar": "⥕",
"\\DownLeftVectorBar": "⥖",
"\\DownRightVectorBar": "⥗",
"\\LeftUpVectorBar": "⥘",
"\\LeftDownVectorBar": "⥙",
"\\LeftTeeVector": "⥚",
"\\RightTeeVector": "⥛",
"\\RightUpTeeVector": "⥜",
"\\RightDownTeeVector": "⥝",
"\\DownLeftTeeVector": "⥞",
"\\DownRightTeeVector": "⥟",
"\\LeftUpTeeVector": "⥠",
"\\LeftDownTeeVector": "⥡",
"\\UpEquilibrium": "⥮",
"\\ReverseUpEquilibrium": "⥯",
"\\RoundImplies": "⥰",
"\\ElsevierGlyph{E214}": "⥼",
"\\ElsevierGlyph{E215}": "⥽",
"\\Elztfnc": "⦀",
"\\ElsevierGlyph{3018}": "⦅",
"\\Elroang": "⦆",
"\\ElsevierGlyph{E291}": "⦔",
"\\Elzddfnc": "⦙",
"\\Angle": "⦜",
"\\Elzlpargt": "⦠",
"\\ElsevierGlyph{E260}": "⦵",
"\\ElsevierGlyph{E61B}": "⦶",
"\\ElzLap": "⧊",
"\\Elzdefas": "⧋",
"\\LeftTriangleBar": "⧏",
"\\RightTriangleBar": "⧐",
"\\ElsevierGlyph{E372}": "⧜",
"\\blacklozenge": "⧫",
"\\RuleDelayed": "⧴",
"\\Elxuplus": "⨄",
"\\ElzThr": "⨅",
"\\Elxsqcup": "⨆",
"\\ElzInf": "⨇",
"\\ElzSup": "⨈",
"\\ElzCint": "⨍",
"\\clockoint": "⨏",
"\\ElsevierGlyph{E395}": "⨐",
"\\sqrint": "⨖",
"\\ElsevierGlyph{E25A}": "⨥",
"\\ElsevierGlyph{E25B}": "⨪",
"\\ElsevierGlyph{E25C}": "⨭",
"\\ElsevierGlyph{E25D}": "⨮",
"\\ElzTimes": "⨯",
"\\ElsevierGlyph{E25E}": "⨴",
"\\ElsevierGlyph{E259}": "⨼",
"\\amalg": "⨿",
"\\ElzAnd": "⩓",
"\\ElzOr": "⩔",
"\\ElsevierGlyph{E36E}": "⩕",
"\\ElOr": "⩖",
"\\perspcorrespond": "⩞",
"\\Elzminhat": "⩟",
"\\stackrel{*}{=}": "⩮",
"\\Equal": "⩵",
"\\leqslant": "⩽",
"\\geqslant": "⩾",
"\\lessapprox": "⪅",
"\\gtrapprox": "⪆",
"\\lneq": "⪇",
"\\gneq": "⪈",
"\\lnapprox": "⪉",
"\\gnapprox": "⪊",
"\\lesseqqgtr": "⪋",
"\\gtreqqless": "⪌",
"\\eqslantless": "⪕",
"\\eqslantgtr": "⪖",
"\\Pisymbol{ppi020}{117}": "⪝",
"\\Pisymbol{ppi020}{105}": "⪞",
"\\NestedLessLess": "⪡",
"\\NestedGreaterGreater": "⪢",
"\\preceq": "⪯",
"\\succeq": "⪰",
"\\precneqq": "⪵",
"\\succneqq": "⪶",
"\\precnapprox": "⪹",
"\\succnapprox": "⪺",
"\\subseteqq": "⫅",
"\\supseteqq": "⫆",
"\\subsetneqq": "⫋",
"\\supsetneqq": "⫌",
"\\ElsevierGlyph{E30D}": "⫫",
"\\Elztdcol": "⫶",
"\\ElsevierGlyph{300A}": "《",
"\\ElsevierGlyph{300B}": "》",
"\\ElsevierGlyph{3019}": "〙",
"\\openbracketleft": "〚",
"\\openbracketright": "〛",
"\\mathbf{A}": "𝐀",
"\\mathbf{B}": "𝐁",
"\\mathbf{C}": "𝐂",
"\\mathbf{D}": "𝐃",
"\\mathbf{E}": "𝐄",
"\\mathbf{F}": "𝐅",
"\\mathbf{G}": "𝐆",
"\\mathbf{H}": "𝐇",
"\\mathbf{I}": "𝐈",
"\\mathbf{J}": "𝐉",
"\\mathbf{K}": "𝐊",
"\\mathbf{L}": "𝐋",
"\\mathbf{M}": "𝐌",
"\\mathbf{N}": "𝐍",
"\\mathbf{O}": "𝐎",
"\\mathbf{P}": "𝐏",
"\\mathbf{Q}": "𝐐",
"\\mathbf{R}": "𝐑",
"\\mathbf{S}": "𝐒",
"\\mathbf{T}": "𝐓",
"\\mathbf{U}": "𝐔",
"\\mathbf{V}": "𝐕",
"\\mathbf{W}": "𝐖",
"\\mathbf{X}": "𝐗",
"\\mathbf{Y}": "𝐘",
"\\mathbf{Z}": "𝐙",
"\\mathbf{a}": "𝐚",
"\\mathbf{b}": "𝐛",
"\\mathbf{c}": "𝐜",
"\\mathbf{d}": "𝐝",
"\\mathbf{e}": "𝐞",
"\\mathbf{f}": "𝐟",
"\\mathbf{g}": "𝐠",
"\\mathbf{h}": "𝐡",
"\\mathbf{i}": "𝐢",
"\\mathbf{j}": "𝐣",
"\\mathbf{k}": "𝐤",
"\\mathbf{l}": "𝐥",
"\\mathbf{m}": "𝐦",
"\\mathbf{n}": "𝐧",
"\\mathbf{o}": "𝐨",
"\\mathbf{p}": "𝐩",
"\\mathbf{q}": "𝐪",
"\\mathbf{r}": "𝐫",
"\\mathbf{s}": "𝐬",
"\\mathbf{t}": "𝐭",
"\\mathbf{u}": "𝐮",
"\\mathbf{v}": "𝐯",
"\\mathbf{w}": "𝐰",
"\\mathbf{x}": "𝐱",
"\\mathbf{y}": "𝐲",
"\\mathbf{z}": "𝐳",
"\\mathmit{A}": "𝐴",
"\\mathmit{B}": "𝐵",
"\\mathmit{C}": "𝐶",
"\\mathmit{D}": "𝐷",
"\\mathmit{E}": "𝐸",
"\\mathmit{F}": "𝐹",
"\\mathmit{G}": "𝐺",
"\\mathmit{H}": "𝐻",
"\\mathmit{I}": "𝐼",
"\\mathmit{J}": "𝐽",
"\\mathmit{K}": "𝐾",
"\\mathmit{L}": "𝐿",
"\\mathmit{M}": "𝑀",
"\\mathmit{N}": "𝑁",
"\\mathmit{O}": "𝑂",
"\\mathmit{P}": "𝑃",
"\\mathmit{Q}": "𝑄",
"\\mathmit{R}": "𝑅",
"\\mathmit{S}": "𝑆",
"\\mathmit{T}": "𝑇",
"\\mathmit{U}": "𝑈",
"\\mathmit{V}": "𝑉",
"\\mathmit{W}": "𝑊",
"\\mathmit{X}": "𝑋",
"\\mathmit{Y}": "𝑌",
"\\mathmit{Z}": "𝑍",
"\\mathmit{a}": "𝑎",
"\\mathmit{b}": "𝑏",
"\\mathmit{c}": "𝑐",
"\\mathmit{d}": "𝑑",
"\\mathmit{e}": "𝑒",
"\\mathmit{f}": "𝑓",
"\\mathmit{g}": "𝑔",
"\\mathmit{i}": "𝑖",
"\\mathmit{j}": "𝑗",
"\\mathmit{k}": "𝑘",
"\\mathmit{l}": "𝑙",
"\\mathmit{m}": "𝑚",
"\\mathmit{n}": "𝑛",
"\\mathmit{o}": "𝑜",
"\\mathmit{p}": "𝑝",
"\\mathmit{q}": "𝑞",
"\\mathmit{r}": "𝑟",
"\\mathmit{s}": "𝑠",
"\\mathmit{t}": "𝑡",
"\\mathmit{u}": "𝑢",
"\\mathmit{v}": "𝑣",
"\\mathmit{w}": "𝑤",
"\\mathmit{x}": "𝑥",
"\\mathmit{y}": "𝑦",
"\\mathmit{z}": "𝑧",
"\\mathbit{A}": "𝑨",
"\\mathbit{B}": "𝑩",
"\\mathbit{C}": "𝑪",
"\\mathbit{D}": "𝑫",
"\\mathbit{E}": "𝑬",
"\\mathbit{F}": "𝑭",
"\\mathbit{G}": "𝑮",
"\\mathbit{H}": "𝑯",
"\\mathbit{I}": "𝑰",
"\\mathbit{J}": "𝑱",
"\\mathbit{K}": "𝑲",
"\\mathbit{L}": "𝑳",
"\\mathbit{M}": "𝑴",
"\\mathbit{N}": "𝑵",
"\\mathbit{O}": "𝑶",
"\\mathbit{P}": "𝑷",
"\\mathbit{Q}": "𝑸",
"\\mathbit{R}": "𝑹",
"\\mathbit{S}": "𝑺",
"\\mathbit{T}": "𝑻",
"\\mathbit{U}": "𝑼",
"\\mathbit{V}": "𝑽",
"\\mathbit{W}": "𝑾",
"\\mathbit{X}": "𝑿",
"\\mathbit{Y}": "𝒀",
"\\mathbit{Z}": "𝒁",
"\\mathbit{a}": "𝒂",
"\\mathbit{b}": "𝒃",
"\\mathbit{c}": "𝒄",
"\\mathbit{d}": "𝒅",
"\\mathbit{e}": "𝒆",
"\\mathbit{f}": "𝒇",
"\\mathbit{g}": "𝒈",
"\\mathbit{h}": "𝒉",
"\\mathbit{i}": "𝒊",
"\\mathbit{j}": "𝒋",
"\\mathbit{k}": "𝒌",
"\\mathbit{l}": "𝒍",
"\\mathbit{m}": "𝒎",
"\\mathbit{n}": "𝒏",
"\\mathbit{o}": "𝒐",
"\\mathbit{p}": "𝒑",
"\\mathbit{q}": "𝒒",
"\\mathbit{r}": "𝒓",
"\\mathbit{s}": "𝒔",
"\\mathbit{t}": "𝒕",
"\\mathbit{u}": "𝒖",
"\\mathbit{v}": "𝒗",
"\\mathbit{w}": "𝒘",
"\\mathbit{x}": "𝒙",
"\\mathbit{y}": "𝒚",
"\\mathbit{z}": "𝒛",
"\\mathscr{A}": "𝒜",
"\\mathscr{C}": "𝒞",
"\\mathscr{D}": "𝒟",
"\\mathscr{G}": "𝒢",
"\\mathscr{J}": "𝒥",
"\\mathscr{K}": "𝒦",
"\\mathscr{N}": "𝒩",
"\\mathscr{O}": "𝒪",
"\\mathscr{P}": "𝒫",
"\\mathscr{Q}": "𝒬",
"\\mathscr{S}": "𝒮",
"\\mathscr{T}": "𝒯",
"\\mathscr{U}": "𝒰",
"\\mathscr{V}": "𝒱",
"\\mathscr{W}": "𝒲",
"\\mathscr{X}": "𝒳",
"\\mathscr{Y}": "𝒴",
"\\mathscr{Z}": "𝒵",
"\\mathscr{a}": "𝒶",
"\\mathscr{b}": "𝒷",
"\\mathscr{c}": "𝒸",
"\\mathscr{d}": "𝒹",
"\\mathscr{f}": "𝒻",
"\\mathscr{h}": "𝒽",
"\\mathscr{i}": "𝒾",
"\\mathscr{j}": "𝒿",
"\\mathscr{k}": "𝓀",
"\\mathscr{m}": "𝓂",
"\\mathscr{n}": "𝓃",
"\\mathscr{p}": "𝓅",
"\\mathscr{q}": "𝓆",
"\\mathscr{r}": "𝓇",
"\\mathscr{s}": "𝓈",
"\\mathscr{t}": "𝓉",
"\\mathscr{u}": "𝓊",
"\\mathscr{v}": "𝓋",
"\\mathscr{w}": "𝓌",
"\\mathscr{x}": "𝓍",
"\\mathscr{y}": "𝓎",
"\\mathscr{z}": "𝓏",
"\\mathbcal{A}": "𝓐",
"\\mathbcal{B}": "𝓑",
"\\mathbcal{C}": "𝓒",
"\\mathbcal{D}": "𝓓",
"\\mathbcal{E}": "𝓔",
"\\mathbcal{F}": "𝓕",
"\\mathbcal{G}": "𝓖",
"\\mathbcal{H}": "𝓗",
"\\mathbcal{I}": "𝓘",
"\\mathbcal{J}": "𝓙",
"\\mathbcal{K}": "𝓚",
"\\mathbcal{L}": "𝓛",
"\\mathbcal{M}": "𝓜",
"\\mathbcal{N}": "𝓝",
"\\mathbcal{O}": "𝓞",
"\\mathbcal{P}": "𝓟",
"\\mathbcal{Q}": "𝓠",
"\\mathbcal{R}": "𝓡",
"\\mathbcal{S}": "𝓢",
"\\mathbcal{T}": "𝓣",
"\\mathbcal{U}": "𝓤",
"\\mathbcal{V}": "𝓥",
"\\mathbcal{W}": "𝓦",
"\\mathbcal{X}": "𝓧",
"\\mathbcal{Y}": "𝓨",
"\\mathbcal{Z}": "𝓩",
"\\mathbcal{a}": "𝓪",
"\\mathbcal{b}": "𝓫",
"\\mathbcal{c}": "𝓬",
"\\mathbcal{d}": "𝓭",
"\\mathbcal{e}": "𝓮",
"\\mathbcal{f}": "𝓯",
"\\mathbcal{g}": "𝓰",
"\\mathbcal{h}": "𝓱",
"\\mathbcal{i}": "𝓲",
"\\mathbcal{j}": "𝓳",
"\\mathbcal{k}": "𝓴",
"\\mathbcal{l}": "𝓵",
"\\mathbcal{m}": "𝓶",
"\\mathbcal{n}": "𝓷",
"\\mathbcal{o}": "𝓸",
"\\mathbcal{p}": "𝓹",
"\\mathbcal{q}": "𝓺",
"\\mathbcal{r}": "𝓻",
"\\mathbcal{s}": "𝓼",
"\\mathbcal{t}": "𝓽",
"\\mathbcal{u}": "𝓾",
"\\mathbcal{v}": "𝓿",
"\\mathbcal{w}": "𝔀",
"\\mathbcal{x}": "𝔁",
"\\mathbcal{y}": "𝔂",
"\\mathbcal{z}": "𝔃",
"\\mathfrak{A}": "𝔄",
"\\mathfrak{B}": "𝔅",
"\\mathfrak{D}": "𝔇",
"\\mathfrak{E}": "𝔈",
"\\mathfrak{F}": "𝔉",
"\\mathfrak{G}": "𝔊",
"\\mathfrak{J}": "𝔍",
"\\mathfrak{K}": "𝔎",
"\\mathfrak{L}": "𝔏",
"\\mathfrak{M}": "𝔐",
"\\mathfrak{N}": "𝔑",
"\\mathfrak{O}": "𝔒",
"\\mathfrak{P}": "𝔓",
"\\mathfrak{Q}": "𝔔",
"\\mathfrak{S}": "𝔖",
"\\mathfrak{T}": "𝔗",
"\\mathfrak{U}": "𝔘",
"\\mathfrak{V}": "𝔙",
"\\mathfrak{W}": "𝔚",
"\\mathfrak{X}": "𝔛",
"\\mathfrak{Y}": "𝔜",
"\\mathfrak{a}": "𝔞",
"\\mathfrak{b}": "𝔟",
"\\mathfrak{c}": "𝔠",
"\\mathfrak{d}": "𝔡",
"\\mathfrak{e}": "𝔢",
"\\mathfrak{f}": "𝔣",
"\\mathfrak{g}": "𝔤",
"\\mathfrak{h}": "𝔥",
"\\mathfrak{i}": "𝔦",
"\\mathfrak{j}": "𝔧",
"\\mathfrak{k}": "𝔨",
"\\mathfrak{l}": "𝔩",
"\\mathfrak{m}": "𝔪",
"\\mathfrak{n}": "𝔫",
"\\mathfrak{o}": "𝔬",
"\\mathfrak{p}": "𝔭",
"\\mathfrak{q}": "𝔮",
"\\mathfrak{r}": "𝔯",
"\\mathfrak{s}": "𝔰",
"\\mathfrak{t}": "𝔱",
"\\mathfrak{u}": "𝔲",
"\\mathfrak{v}": "𝔳",
"\\mathfrak{w}": "𝔴",
"\\mathfrak{x}": "𝔵",
"\\mathfrak{y}": "𝔶",
"\\mathfrak{z}": "𝔷",
"\\mathbb{A}": "𝔸",
"\\mathbb{B}": "𝔹",
"\\mathbb{D}": "𝔻",
"\\mathbb{E}": "𝔼",
"\\mathbb{F}": "𝔽",
"\\mathbb{G}": "𝔾",
"\\mathbb{I}": "𝕀",
"\\mathbb{J}": "𝕁",
"\\mathbb{K}": "𝕂",
"\\mathbb{L}": "𝕃",
"\\mathbb{M}": "𝕄",
"\\mathbb{O}": "𝕆",
"\\mathbb{S}": "𝕊",
"\\mathbb{T}": "𝕋",
"\\mathbb{U}": "𝕌",
"\\mathbb{V}": "𝕍",
"\\mathbb{W}": "𝕎",
"\\mathbb{X}": "𝕏",
"\\mathbb{Y}": "𝕐",
"\\mathbb{a}": "𝕒",
"\\mathbb{b}": "𝕓",
"\\mathbb{c}": "𝕔",
"\\mathbb{d}": "𝕕",
"\\mathbb{e}": "𝕖",
"\\mathbb{f}": "𝕗",
"\\mathbb{g}": "𝕘",
"\\mathbb{h}": "𝕙",
"\\mathbb{i}": "𝕚",
"\\mathbb{j}": "𝕛",
"\\mathbb{k}": "𝕜",
"\\mathbb{l}": "𝕝",
"\\mathbb{m}": "𝕞",
"\\mathbb{n}": "𝕟",
"\\mathbb{o}": "𝕠",
"\\mathbb{p}": "𝕡",
"\\mathbb{q}": "𝕢",
"\\mathbb{r}": "𝕣",
"\\mathbb{s}": "𝕤",
"\\mathbb{t}": "𝕥",
"\\mathbb{u}": "𝕦",
"\\mathbb{v}": "𝕧",
"\\mathbb{w}": "𝕨",
"\\mathbb{x}": "𝕩",
"\\mathbb{y}": "𝕪",
"\\mathbb{z}": "𝕫",
"\\mathbfrak{A}": "𝕬",
"\\mathbfrak{B}": "𝕭",
"\\mathbfrak{C}": "𝕮",
"\\mathbfrak{D}": "𝕯",
"\\mathbfrak{E}": "𝕰",
"\\mathbfrak{F}": "𝕱",
"\\mathbfrak{G}": "𝕲",
"\\mathbfrak{H}": "𝕳",
"\\mathbfrak{I}": "𝕴",
"\\mathbfrak{J}": "𝕵",
"\\mathbfrak{K}": "𝕶",
"\\mathbfrak{L}": "𝕷",
"\\mathbfrak{M}": "𝕸",
"\\mathbfrak{N}": "𝕹",
"\\mathbfrak{O}": "𝕺",
"\\mathbfrak{P}": "𝕻",
"\\mathbfrak{Q}": "𝕼",
"\\mathbfrak{R}": "𝕽",
"\\mathbfrak{S}": "𝕾",
"\\mathbfrak{T}": "𝕿",
"\\mathbfrak{U}": "𝖀",
"\\mathbfrak{V}": "𝖁",
"\\mathbfrak{W}": "𝖂",
"\\mathbfrak{X}": "𝖃",
"\\mathbfrak{Y}": "𝖄",
"\\mathbfrak{Z}": "𝖅",
"\\mathbfrak{a}": "𝖆",
"\\mathbfrak{b}": "𝖇",
"\\mathbfrak{c}": "𝖈",
"\\mathbfrak{d}": "𝖉",
"\\mathbfrak{e}": "𝖊",
"\\mathbfrak{f}": "𝖋",
"\\mathbfrak{g}": "𝖌",
"\\mathbfrak{h}": "𝖍",
"\\mathbfrak{i}": "𝖎",
"\\mathbfrak{j}": "𝖏",
"\\mathbfrak{k}": "𝖐",
"\\mathbfrak{l}": "𝖑",
"\\mathbfrak{m}": "𝖒",
"\\mathbfrak{n}": "𝖓",
"\\mathbfrak{o}": "𝖔",
"\\mathbfrak{p}": "𝖕",
"\\mathbfrak{q}": "𝖖",
"\\mathbfrak{r}": "𝖗",
"\\mathbfrak{s}": "𝖘",
"\\mathbfrak{t}": "𝖙",
"\\mathbfrak{u}": "𝖚",
"\\mathbfrak{v}": "𝖛",
"\\mathbfrak{w}": "𝖜",
"\\mathbfrak{x}": "𝖝",
"\\mathbfrak{y}": "𝖞",
"\\mathbfrak{z}": "𝖟",
"\\mathsf{A}": "𝖠",
"\\mathsf{B}": "𝖡",
"\\mathsf{C}": "𝖢",
"\\mathsf{D}": "𝖣",
"\\mathsf{E}": "𝖤",
"\\mathsf{F}": "𝖥",
"\\mathsf{G}": "𝖦",
"\\mathsf{H}": "𝖧",
"\\mathsf{I}": "𝖨",
"\\mathsf{J}": "𝖩",
"\\mathsf{K}": "𝖪",
"\\mathsf{L}": "𝖫",
"\\mathsf{M}": "𝖬",
"\\mathsf{N}": "𝖭",
"\\mathsf{O}": "𝖮",
"\\mathsf{P}": "𝖯",
"\\mathsf{Q}": "𝖰",
"\\mathsf{R}": "𝖱",
"\\mathsf{S}": "𝖲",
"\\mathsf{T}": "𝖳",
"\\mathsf{U}": "𝖴",
"\\mathsf{V}": "𝖵",
"\\mathsf{W}": "𝖶",
"\\mathsf{X}": "𝖷",
"\\mathsf{Y}": "𝖸",
"\\mathsf{Z}": "𝖹",
"\\mathsf{a}": "𝖺",
"\\mathsf{b}": "𝖻",
"\\mathsf{c}": "𝖼",
"\\mathsf{d}": "𝖽",
"\\mathsf{e}": "𝖾",
"\\mathsf{f}": "𝖿",
"\\mathsf{g}": "𝗀",
"\\mathsf{h}": "𝗁",
"\\mathsf{i}": "𝗂",
"\\mathsf{j}": "𝗃",
"\\mathsf{k}": "𝗄",
"\\mathsf{l}": "𝗅",
"\\mathsf{m}": "𝗆",
"\\mathsf{n}": "𝗇",
"\\mathsf{o}": "𝗈",
"\\mathsf{p}": "𝗉",
"\\mathsf{q}": "𝗊",
"\\mathsf{r}": "𝗋",
"\\mathsf{s}": "𝗌",
"\\mathsf{t}": "𝗍",
"\\mathsf{u}": "𝗎",
"\\mathsf{v}": "𝗏",
"\\mathsf{w}": "𝗐",
"\\mathsf{x}": "𝗑",
"\\mathsf{y}": "𝗒",
"\\mathsf{z}": "𝗓",
"\\mathsfbf{A}": "𝗔",
"\\mathsfbf{B}": "𝗕",
"\\mathsfbf{C}": "𝗖",
"\\mathsfbf{D}": "𝗗",
"\\mathsfbf{E}": "𝗘",
"\\mathsfbf{F}": "𝗙",
"\\mathsfbf{G}": "𝗚",
"\\mathsfbf{H}": "𝗛",
"\\mathsfbf{I}": "𝗜",
"\\mathsfbf{J}": "𝗝",
"\\mathsfbf{K}": "𝗞",
"\\mathsfbf{L}": "𝗟",
"\\mathsfbf{M}": "𝗠",
"\\mathsfbf{N}": "𝗡",
"\\mathsfbf{O}": "𝗢",
"\\mathsfbf{P}": "𝗣",
"\\mathsfbf{Q}": "𝗤",
"\\mathsfbf{R}": "𝗥",
"\\mathsfbf{S}": "𝗦",
"\\mathsfbf{T}": "𝗧",
"\\mathsfbf{U}": "𝗨",
"\\mathsfbf{V}": "𝗩",
"\\mathsfbf{W}": "𝗪",
"\\mathsfbf{X}": "𝗫",
"\\mathsfbf{Y}": "𝗬",
"\\mathsfbf{Z}": "𝗭",
"\\mathsfbf{a}": "𝗮",
"\\mathsfbf{b}": "𝗯",
"\\mathsfbf{c}": "𝗰",
"\\mathsfbf{d}": "𝗱",
"\\mathsfbf{e}": "𝗲",
"\\mathsfbf{f}": "𝗳",
"\\mathsfbf{g}": "𝗴",
"\\mathsfbf{h}": "𝗵",
"\\mathsfbf{i}": "𝗶",
"\\mathsfbf{j}": "𝗷",
"\\mathsfbf{k}": "𝗸",
"\\mathsfbf{l}": "𝗹",
"\\mathsfbf{m}": "𝗺",
"\\mathsfbf{n}": "𝗻",
"\\mathsfbf{o}": "𝗼",
"\\mathsfbf{p}": "𝗽",
"\\mathsfbf{q}": "𝗾",
"\\mathsfbf{r}": "𝗿",
"\\mathsfbf{s}": "𝘀",
"\\mathsfbf{t}": "𝘁",
"\\mathsfbf{u}": "𝘂",
"\\mathsfbf{v}": "𝘃",
"\\mathsfbf{w}": "𝘄",
"\\mathsfbf{x}": "𝘅",
"\\mathsfbf{y}": "𝘆",
"\\mathsfbf{z}": "𝘇",
"\\mathsfsl{A}": "𝘈",
"\\mathsfsl{B}": "𝘉",
"\\mathsfsl{C}": "𝘊",
"\\mathsfsl{D}": "𝘋",
"\\mathsfsl{E}": "𝘌",
"\\mathsfsl{F}": "𝘍",
"\\mathsfsl{G}": "𝘎",
"\\mathsfsl{H}": "𝘏",
"\\mathsfsl{I}": "𝘐",
"\\mathsfsl{J}": "𝘑",
"\\mathsfsl{K}": "𝘒",
"\\mathsfsl{L}": "𝘓",
"\\mathsfsl{M}": "𝘔",
"\\mathsfsl{N}": "𝘕",
"\\mathsfsl{O}": "𝘖",
"\\mathsfsl{P}": "𝘗",
"\\mathsfsl{Q}": "𝘘",
"\\mathsfsl{R}": "𝘙",
"\\mathsfsl{S}": "𝘚",
"\\mathsfsl{T}": "𝘛",
"\\mathsfsl{U}": "𝘜",
"\\mathsfsl{V}": "𝘝",
"\\mathsfsl{W}": "𝘞",
"\\mathsfsl{X}": "𝘟",
"\\mathsfsl{Y}": "𝘠",
"\\mathsfsl{Z}": "𝘡",
"\\mathsfsl{a}": "𝘢",
"\\mathsfsl{b}": "𝘣",
"\\mathsfsl{c}": "𝘤",
"\\mathsfsl{d}": "𝘥",
"\\mathsfsl{e}": "𝘦",
"\\mathsfsl{f}": "𝘧",
"\\mathsfsl{g}": "𝘨",
"\\mathsfsl{h}": "𝘩",
"\\mathsfsl{i}": "𝘪",
"\\mathsfsl{j}": "𝘫",
"\\mathsfsl{k}": "𝘬",
"\\mathsfsl{l}": "𝘭",
"\\mathsfsl{m}": "𝘮",
"\\mathsfsl{n}": "𝘯",
"\\mathsfsl{o}": "𝘰",
"\\mathsfsl{p}": "𝘱",
"\\mathsfsl{q}": "𝘲",
"\\mathsfsl{r}": "𝘳",
"\\mathsfsl{s}": "𝘴",
"\\mathsfsl{t}": "𝘵",
"\\mathsfsl{u}": "𝘶",
"\\mathsfsl{v}": "𝘷",
"\\mathsfsl{w}": "𝘸",
"\\mathsfsl{x}": "𝘹",
"\\mathsfsl{y}": "𝘺",
"\\mathsfsl{z}": "𝘻",
"\\mathsfbfsl{A}": "𝘼",
"\\mathsfbfsl{B}": "𝘽",
"\\mathsfbfsl{C}": "𝘾",
"\\mathsfbfsl{D}": "𝘿",
"\\mathsfbfsl{E}": "𝙀",
"\\mathsfbfsl{F}": "𝙁",
"\\mathsfbfsl{G}": "𝙂",
"\\mathsfbfsl{H}": "𝙃",
"\\mathsfbfsl{I}": "𝙄",
"\\mathsfbfsl{J}": "𝙅",
"\\mathsfbfsl{K}": "𝙆",
"\\mathsfbfsl{L}": "𝙇",
"\\mathsfbfsl{M}": "𝙈",
"\\mathsfbfsl{N}": "𝙉",
"\\mathsfbfsl{O}": "𝙊",
"\\mathsfbfsl{P}": "𝙋",
"\\mathsfbfsl{Q}": "𝙌",
"\\mathsfbfsl{R}": "𝙍",
"\\mathsfbfsl{S}": "𝙎",
"\\mathsfbfsl{T}": "𝙏",
"\\mathsfbfsl{U}": "𝙐",
"\\mathsfbfsl{V}": "𝙑",
"\\mathsfbfsl{W}": "𝙒",
"\\mathsfbfsl{X}": "𝙓",
"\\mathsfbfsl{Y}": "𝙔",
"\\mathsfbfsl{Z}": "𝙕",
"\\mathsfbfsl{a}": "𝙖",
"\\mathsfbfsl{b}": "𝙗",
"\\mathsfbfsl{c}": "𝙘",
"\\mathsfbfsl{d}": "𝙙",
"\\mathsfbfsl{e}": "𝙚",
"\\mathsfbfsl{f}": "𝙛",
"\\mathsfbfsl{g}": "𝙜",
"\\mathsfbfsl{h}": "𝙝",
"\\mathsfbfsl{i}": "𝙞",
"\\mathsfbfsl{j}": "𝙟",
"\\mathsfbfsl{k}": "𝙠",
"\\mathsfbfsl{l}": "𝙡",
"\\mathsfbfsl{m}": "𝙢",
"\\mathsfbfsl{n}": "𝙣",
"\\mathsfbfsl{o}": "𝙤",
"\\mathsfbfsl{p}": "𝙥",
"\\mathsfbfsl{q}": "𝙦",
"\\mathsfbfsl{r}": "𝙧",
"\\mathsfbfsl{s}": "𝙨",
"\\mathsfbfsl{t}": "𝙩",
"\\mathsfbfsl{u}": "𝙪",
"\\mathsfbfsl{v}": "𝙫",
"\\mathsfbfsl{w}": "𝙬",
"\\mathsfbfsl{x}": "𝙭",
"\\mathsfbfsl{y}": "𝙮",
"\\mathsfbfsl{z}": "𝙯",
"\\mathtt{A}": "𝙰",
"\\mathtt{B}": "𝙱",
"\\mathtt{C}": "𝙲",
"\\mathtt{D}": "𝙳",
"\\mathtt{E}": "𝙴",
"\\mathtt{F}": "𝙵",
"\\mathtt{G}": "𝙶",
"\\mathtt{H}": "𝙷",
"\\mathtt{I}": "𝙸",
"\\mathtt{J}": "𝙹",
"\\mathtt{K}": "𝙺",
"\\mathtt{L}": "𝙻",
"\\mathtt{M}": "𝙼",
"\\mathtt{N}": "𝙽",
"\\mathtt{O}": "𝙾",
"\\mathtt{P}": "𝙿",
"\\mathtt{Q}": "𝚀",
"\\mathtt{R}": "𝚁",
"\\mathtt{S}": "𝚂",
"\\mathtt{T}": "𝚃",
"\\mathtt{U}": "𝚄",
"\\mathtt{V}": "𝚅",
"\\mathtt{W}": "𝚆",
"\\mathtt{X}": "𝚇",
"\\mathtt{Y}": "𝚈",
"\\mathtt{Z}": "𝚉",
"\\mathtt{a}": "𝚊",
"\\mathtt{b}": "𝚋",
"\\mathtt{c}": "𝚌",
"\\mathtt{d}": "𝚍",
"\\mathtt{e}": "𝚎",
"\\mathtt{f}": "𝚏",
"\\mathtt{g}": "𝚐",
"\\mathtt{h}": "𝚑",
"\\mathtt{i}": "𝚒",
"\\mathtt{j}": "𝚓",
"\\mathtt{k}": "𝚔",
"\\mathtt{l}": "𝚕",
"\\mathtt{m}": "𝚖",
"\\mathtt{n}": "𝚗",
"\\mathtt{o}": "𝚘",
"\\mathtt{p}": "𝚙",
"\\mathtt{q}": "𝚚",
"\\mathtt{r}": "𝚛",
"\\mathtt{s}": "𝚜",
"\\mathtt{t}": "𝚝",
"\\mathtt{u}": "𝚞",
"\\mathtt{v}": "𝚟",
"\\mathtt{w}": "𝚠",
"\\mathtt{x}": "𝚡",
"\\mathtt{y}": "𝚢",
"\\mathtt{z}": "𝚣",
"\\mathbf{\\Alpha}": "𝚨",
"\\mathbf{\\Beta}": "𝚩",
"\\mathbf{\\Gamma}": "𝚪",
"\\mathbf{\\Delta}": "𝚫",
"\\mathbf{\\Epsilon}": "𝚬",
"\\mathbf{\\Zeta}": "𝚭",
"\\mathbf{\\Eta}": "𝚮",
"\\mathbf{\\Theta}": "𝚯",
"\\mathbf{\\Iota}": "𝚰",
"\\mathbf{\\Kappa}": "𝚱",
"\\mathbf{\\Lambda}": "𝚲",
"\\mathbf{\\Xi}": "𝚵",
"\\mathbf{\\Pi}": "𝚷",
"\\mathbf{\\Rho}": "𝚸",
"\\mathbf{\\vartheta}": "𝚹",
"\\mathbf{\\Sigma}": "𝚺",
"\\mathbf{\\Tau}": "𝚻",
"\\mathbf{\\Upsilon}": "𝚼",
"\\mathbf{\\Phi}": "𝚽",
"\\mathbf{\\Chi}": "𝚾",
"\\mathbf{\\Psi}": "𝚿",
"\\mathbf{\\Omega}": "𝛀",
"\\mathbf{\\nabla}": "𝛁",
"\\mathbf{\\alpha}": "𝛂",
"\\mathbf{\\beta}": "𝛃",
"\\mathbf{\\gamma}": "𝛄",
"\\mathbf{\\delta}": "𝛅",
"\\mathbf{\\epsilon}": "𝛆",
"\\mathbf{\\zeta}": "𝛇",
"\\mathbf{\\eta}": "𝛈",
"\\mathbf{\\theta}": "𝛉",
"\\mathbf{\\iota}": "𝛊",
"\\mathbf{\\kappa}": "𝛋",
"\\mathbf{\\lambda}": "𝛌",
"\\mathbf{\\mu}": "𝛍",
"\\mathbf{\\nu}": "𝛎",
"\\mathbf{\\xi}": "𝛏",
"\\mathbf{\\pi}": "𝛑",
"\\mathbf{\\rho}": "𝛒",
"\\mathbf{\\varsigma}": "𝛓",
"\\mathbf{\\sigma}": "𝛔",
"\\mathbf{\\tau}": "𝛕",
"\\mathbf{\\upsilon}": "𝛖",
"\\mathbf{\\phi}": "𝛗",
"\\mathbf{\\chi}": "𝛘",
"\\mathbf{\\psi}": "𝛙",
"\\mathbf{\\omega}": "𝛚",
"\\mathbf{\\varepsilon}": "𝛜",
"\\mathbf{\\varkappa}": "𝛞",
"\\mathbf{\\varrho}": "𝛠",
"\\mathbf{\\varpi}": "𝛡",
"\\mathmit{\\Alpha}": "𝛢",
"\\mathmit{\\Beta}": "𝛣",
"\\mathmit{\\Gamma}": "𝛤",
"\\mathmit{\\Delta}": "𝛥",
"\\mathmit{\\Epsilon}": "𝛦",
"\\mathmit{\\Zeta}": "𝛧",
"\\mathmit{\\Eta}": "𝛨",
"\\mathmit{\\Theta}": "𝛩",
"\\mathmit{\\Iota}": "𝛪",
"\\mathmit{\\Kappa}": "𝛫",
"\\mathmit{\\Lambda}": "𝛬",
"\\mathmit{\\Xi}": "𝛯",
"\\mathmit{\\Pi}": "𝛱",
"\\mathmit{\\Rho}": "𝛲",
"\\mathmit{\\vartheta}": "𝛳",
"\\mathmit{\\Sigma}": "𝛴",
"\\mathmit{\\Tau}": "𝛵",
"\\mathmit{\\Upsilon}": "𝛶",
"\\mathmit{\\Phi}": "𝛷",
"\\mathmit{\\Chi}": "𝛸",
"\\mathmit{\\Psi}": "𝛹",
"\\mathmit{\\Omega}": "𝛺",
"\\mathmit{\\nabla}": "𝛻",
"\\mathmit{\\alpha}": "𝛼",
"\\mathmit{\\beta}": "𝛽",
"\\mathmit{\\gamma}": "𝛾",
"\\mathmit{\\delta}": "𝛿",
"\\mathmit{\\epsilon}": "𝜀",
"\\mathmit{\\zeta}": "𝜁",
"\\mathmit{\\eta}": "𝜂",
"\\mathmit{\\theta}": "𝜃",
"\\mathmit{\\iota}": "𝜄",
"\\mathmit{\\kappa}": "𝜅",
"\\mathmit{\\lambda}": "𝜆",
"\\mathmit{\\mu}": "𝜇",
"\\mathmit{\\nu}": "𝜈",
"\\mathmit{\\xi}": "𝜉",
"\\mathmit{\\pi}": "𝜋",
"\\mathmit{\\rho}": "𝜌",
"\\mathmit{\\varsigma}": "𝜍",
"\\mathmit{\\sigma}": "𝜎",
"\\mathmit{\\tau}": "𝜏",
"\\mathmit{\\upsilon}": "𝜐",
"\\mathmit{\\phi}": "𝜑",
"\\mathmit{\\chi}": "𝜒",
"\\mathmit{\\psi}": "𝜓",
"\\mathmit{\\omega}": "𝜔",
"\\mathmit{\\varkappa}": "𝜘",
"\\mathmit{\\varrho}": "𝜚",
"\\mathmit{\\varpi}": "𝜛",
"\\mathbit{\\Alpha}": "𝜜",
"\\mathbit{\\Beta}": "𝜝",
"\\mathbit{\\Gamma}": "𝜞",
"\\mathbit{\\Delta}": "𝜟",
"\\mathbit{\\Epsilon}": "𝜠",
"\\mathbit{\\Zeta}": "𝜡",
"\\mathbit{\\Eta}": "𝜢",
"\\mathbit{\\Theta}": "𝜣",
"\\mathbit{\\Iota}": "𝜤",
"\\mathbit{\\Kappa}": "𝜥",
"\\mathbit{\\Lambda}": "𝜦",
"\\mathbit{\\Xi}": "𝜩",
"\\mathbit{\\Pi}": "𝜫",
"\\mathbit{\\Rho}": "𝜬",
"\\mathbit{\\Sigma}": "𝜮",
"\\mathbit{\\Tau}": "𝜯",
"\\mathbit{\\Upsilon}": "𝜰",
"\\mathbit{\\Phi}": "𝜱",
"\\mathbit{\\Chi}": "𝜲",
"\\mathbit{\\Psi}": "𝜳",
"\\mathbit{\\Omega}": "𝜴",
"\\mathbit{\\nabla}": "𝜵",
"\\mathbit{\\alpha}": "𝜶",
"\\mathbit{\\beta}": "𝜷",
"\\mathbit{\\gamma}": "𝜸",
"\\mathbit{\\delta}": "𝜹",
"\\mathbit{\\epsilon}": "𝜺",
"\\mathbit{\\zeta}": "𝜻",
"\\mathbit{\\eta}": "𝜼",
"\\mathbit{\\theta}": "𝜽",
"\\mathbit{\\iota}": "𝜾",
"\\mathbit{\\kappa}": "𝜿",
"\\mathbit{\\lambda}": "𝝀",
"\\mathbit{\\mu}": "𝝁",
"\\mathbit{\\nu}": "𝝂",
"\\mathbit{\\xi}": "𝝃",
"\\mathbit{\\pi}": "𝝅",
"\\mathbit{\\rho}": "𝝆",
"\\mathbit{\\varsigma}": "𝝇",
"\\mathbit{\\sigma}": "𝝈",
"\\mathbit{\\tau}": "𝝉",
"\\mathbit{\\upsilon}": "𝝊",
"\\mathbit{\\phi}": "𝝋",
"\\mathbit{\\chi}": "𝝌",
"\\mathbit{\\psi}": "𝝍",
"\\mathbit{\\omega}": "𝝎",
"\\mathbit{\\vartheta}": "𝝑",
"\\mathbit{\\varkappa}": "𝝒",
"\\mathbit{\\varrho}": "𝝔",
"\\mathbit{\\varpi}": "𝝕",
"\\mathsfbf{\\Alpha}": "𝝖",
"\\mathsfbf{\\Beta}": "𝝗",
"\\mathsfbf{\\Gamma}": "𝝘",
"\\mathsfbf{\\Delta}": "𝝙",
"\\mathsfbf{\\Epsilon}": "𝝚",
"\\mathsfbf{\\Zeta}": "𝝛",
"\\mathsfbf{\\Eta}": "𝝜",
"\\mathsfbf{\\Theta}": "𝝝",
"\\mathsfbf{\\Iota}": "𝝞",
"\\mathsfbf{\\Kappa}": "𝝟",
"\\mathsfbf{\\Lambda}": "𝝠",
"\\mathsfbf{\\Xi}": "𝝣",
"\\mathsfbf{\\Pi}": "𝝥",
"\\mathsfbf{\\Rho}": "𝝦",
"\\mathsfbf{\\vartheta}": "𝝧",
"\\mathsfbf{\\Sigma}": "𝝨",
"\\mathsfbf{\\Tau}": "𝝩",
"\\mathsfbf{\\Upsilon}": "𝝪",
"\\mathsfbf{\\Phi}": "𝝫",
"\\mathsfbf{\\Chi}": "𝝬",
"\\mathsfbf{\\Psi}": "𝝭",
"\\mathsfbf{\\Omega}": "𝝮",
"\\mathsfbf{\\nabla}": "𝝯",
"\\mathsfbf{\\alpha}": "𝝰",
"\\mathsfbf{\\beta}": "𝝱",
"\\mathsfbf{\\gamma}": "𝝲",
"\\mathsfbf{\\delta}": "𝝳",
"\\mathsfbf{\\epsilon}": "𝝴",
"\\mathsfbf{\\zeta}": "𝝵",
"\\mathsfbf{\\eta}": "𝝶",
"\\mathsfbf{\\theta}": "𝝷",
"\\mathsfbf{\\iota}": "𝝸",
"\\mathsfbf{\\kappa}": "𝝹",
"\\mathsfbf{\\lambda}": "𝝺",
"\\mathsfbf{\\mu}": "𝝻",
"\\mathsfbf{\\nu}": "𝝼",
"\\mathsfbf{\\xi}": "𝝽",
"\\mathsfbf{\\pi}": "𝝿",
"\\mathsfbf{\\rho}": "𝞀",
"\\mathsfbf{\\varsigma}": "𝞁",
"\\mathsfbf{\\sigma}": "𝞂",
"\\mathsfbf{\\tau}": "𝞃",
"\\mathsfbf{\\upsilon}": "𝞄",
"\\mathsfbf{\\phi}": "𝞅",
"\\mathsfbf{\\chi}": "𝞆",
"\\mathsfbf{\\psi}": "𝞇",
"\\mathsfbf{\\omega}": "𝞈",
"\\mathsfbf{\\varepsilon}": "𝞊",
"\\mathsfbf{\\varkappa}": "𝞌",
"\\mathsfbf{\\varrho}": "𝞎",
"\\mathsfbf{\\varpi}": "𝞏",
"\\mathsfbfsl{\\Alpha}": "𝞐",
"\\mathsfbfsl{\\Beta}": "𝞑",
"\\mathsfbfsl{\\Gamma}": "𝞒",
"\\mathsfbfsl{\\Delta}": "𝞓",
"\\mathsfbfsl{\\Epsilon}": "𝞔",
"\\mathsfbfsl{\\Zeta}": "𝞕",
"\\mathsfbfsl{\\Eta}": "𝞖",
"\\mathsfbfsl{\\vartheta}": "𝞗",
"\\mathsfbfsl{\\Iota}": "𝞘",
"\\mathsfbfsl{\\Kappa}": "𝞙",
"\\mathsfbfsl{\\Lambda}": "𝞚",
"\\mathsfbfsl{\\Xi}": "𝞝",
"\\mathsfbfsl{\\Pi}": "𝞟",
"\\mathsfbfsl{\\Rho}": "𝞠",
"\\mathsfbfsl{\\Sigma}": "𝞢",
"\\mathsfbfsl{\\Tau}": "𝞣",
"\\mathsfbfsl{\\Upsilon}": "𝞤",
"\\mathsfbfsl{\\Phi}": "𝞥",
"\\mathsfbfsl{\\Chi}": "𝞦",
"\\mathsfbfsl{\\Psi}": "𝞧",
"\\mathsfbfsl{\\Omega}": "𝞨",
"\\mathsfbfsl{\\nabla}": "𝞩",
"\\mathsfbfsl{\\alpha}": "𝞪",
"\\mathsfbfsl{\\beta}": "𝞫",
"\\mathsfbfsl{\\gamma}": "𝞬",
"\\mathsfbfsl{\\delta}": "𝞭",
"\\mathsfbfsl{\\epsilon}": "𝞮",
"\\mathsfbfsl{\\zeta}": "𝞯",
"\\mathsfbfsl{\\eta}": "𝞰",
"\\mathsfbfsl{\\iota}": "𝞲",
"\\mathsfbfsl{\\kappa}": "𝞳",
"\\mathsfbfsl{\\lambda}": "𝞴",
"\\mathsfbfsl{\\mu}": "𝞵",
"\\mathsfbfsl{\\nu}": "𝞶",
"\\mathsfbfsl{\\xi}": "𝞷",
"\\mathsfbfsl{\\pi}": "𝞹",
"\\mathsfbfsl{\\rho}": "𝞺",
"\\mathsfbfsl{\\varsigma}": "𝞻",
"\\mathsfbfsl{\\sigma}": "𝞼",
"\\mathsfbfsl{\\tau}": "𝞽",
"\\mathsfbfsl{\\upsilon}": "𝞾",
"\\mathsfbfsl{\\phi}": "𝞿",
"\\mathsfbfsl{\\chi}": "𝟀",
"\\mathsfbfsl{\\psi}": "𝟁",
"\\mathsfbfsl{\\omega}": "𝟂",
"\\mathsfbfsl{\\varkappa}": "𝟆",
"\\mathsfbfsl{\\varrho}": "𝟈",
"\\mathsfbfsl{\\varpi}": "𝟉",
"\\mathbf{0}": "𝟎",
"\\mathbf{1}": "𝟏",
"\\mathbf{2}": "𝟐",
"\\mathbf{3}": "𝟑",
"\\mathbf{4}": "𝟒",
"\\mathbf{5}": "𝟓",
"\\mathbf{6}": "𝟔",
"\\mathbf{7}": "𝟕",
"\\mathbf{8}": "𝟖",
"\\mathbf{9}": "𝟗",
"\\mathbb{0}": "𝟘",
"\\mathbb{1}": "𝟙",
"\\mathbb{2}": "𝟚",
"\\mathbb{3}": "𝟛",
"\\mathbb{4}": "𝟜",
"\\mathbb{5}": "𝟝",
"\\mathbb{6}": "𝟞",
"\\mathbb{7}": "𝟟",
"\\mathbb{8}": "𝟠",
"\\mathbb{9}": "𝟡",
"\\mathsf{0}": "𝟢",
"\\mathsf{1}": "𝟣",
"\\mathsf{2}": "𝟤",
"\\mathsf{3}": "𝟥",
"\\mathsf{4}": "𝟦",
"\\mathsf{5}": "𝟧",
"\\mathsf{6}": "𝟨",
"\\mathsf{7}": "𝟩",
"\\mathsf{8}": "𝟪",
"\\mathsf{9}": "𝟫",
"\\mathsfbf{0}": "𝟬",
"\\mathsfbf{1}": "𝟭",
"\\mathsfbf{2}": "𝟮",
"\\mathsfbf{3}": "𝟯",
"\\mathsfbf{4}": "𝟰",
"\\mathsfbf{5}": "𝟱",
"\\mathsfbf{6}": "𝟲",
"\\mathsfbf{7}": "𝟳",
"\\mathsfbf{8}": "𝟴",
"\\mathsfbf{9}": "𝟵",
"\\mathtt{0}": "𝟶",
"\\mathtt{1}": "𝟷",
"\\mathtt{2}": "𝟸",
"\\mathtt{3}": "𝟹",
"\\mathtt{4}": "𝟺",
"\\mathtt{5}": "𝟻",
"\\mathtt{6}": "𝟼",
"\\mathtt{7}": "𝟽",
"\\mathtt{8}": "𝟾",
"\\mathtt{9}": "𝟿"
}        
     for (var idx in this.orcidLatexCharMap) {
         if (this.orcidLatexCharMap[idx].length > this.maxLatexLength)
           this.maxLatexLength = this.orcidLatexCharMap[idx].length;
         this.orcidCharLatexMap[this.orcidLatexCharMap[idx]] = idx;
     }

     for (var idx in this.w3cLatexCharMap) {
         if (this.w3cLatexCharMap[idx].length > this.maxLatexLength)
           this.maxLatexLength = this.w3cLatexCharMap[idx].length;
         this.w3cCharLatexMap[this.w3cLatexCharMap[idx]] = idx;
     }

     this.getUni = function(latex) {
         if (this.w3cLatexCharMap[latex]) 
             return this.w3cLatexCharMap[latex];
         return this.orcidLatexCharMap[latex];
     };

     this.hasLatexMatch = function (latex) {
         return latex in this.orcidLatexCharMap
             || latex in this.w3cLatexCharMap;
     };

     this.getLatex = function(uni) {
         if (this.w3cCharLatexMap[uni])
             return this.w3cCharLatexMap[uni]
         return this.orcidCharLatexMap[uni];
     };

     this.hasUniMatch = function (uni) {
         return uni in this.orcidCharLatexMap
             ||  uni in this.w3cCharLatexMap;
     };

     this.longestEscapeMatch = function(value, pos) {
         var subStringEnd =  pos + 1 + this.maxLatexLength <= value.length ?
                     pos + 1 + this.maxLatexLength : value.length;
         var subStr =  value.substring(pos,subStringEnd);                    
         while (subStr.length > 0) {
          if (this.hasLatexMatch(subStr)) {
             break;
          }
          subStr = subStr.substring(0,subStr.length -1);
         }
         return subStr;
     }
     
     
 };



 var latexToUTF8 = new LatexToUTF8();
 
 exports.decodeLatex = function(value) {
     var newVal = '';
     var pos = 0;
     while (pos < value.length) {
         if (value[pos] == '\\') {
             var match = latexToUTF8.longestEscapeMatch(value, pos);
             if (match.length > 0) {
                 newVal += latexToUTF8.getUni(match);
                 pos = pos + match.length;
             } else {
                 newVal += value[pos];
                 pos++;
             }
         } else if (value[pos] == '{' || value[pos] == '}') {
           pos++;
         } else {
             newVal += value[pos];
             pos++;
         }
     }
     return newVal;
 }

 exports.encodeLatex = function(value) {
     var trans = '';
     for (var idx = 0; idx < value.length; ++idx) {
         var c = value.charAt(idx);
         if (this.hasUniMatch(c))
             trans += latexToUTF8.getLatex(c);
         else
             trans += c;
     }
     return trans;
 }


})(typeof exports === 'undefined' ? this['latexParseJs'] = {} : exports);

/* end latex */



/* START: workIdLinkJs v0.0.8 */
/* https://github.com/ORCID/workIdLinkJs */

/* browser and NodeJs compatible */
(function(exports){

   // add new method to string
   if (typeof String.prototype.startsWith != 'function') {
      String.prototype.startsWith = function (str){
         return this.slice(0, str.length) == str;
      };
   }

   //add new method to string
   if (typeof String.prototype.endsWith != 'function') {
      String.prototype.endsWith = function (str){
         return this.slice(-str.length) == str;
      };
   }

   //add new method to string
   if (typeof String.prototype.trim != 'function') {  
      String.prototype.trim = function () {  
         return this.replace(/^\s+|\s+$/g,'');  
      };  
   }

   var typeMap = {};
   
   typeMap.hasOwnProperty = function(property) {
      return typeMap[property] !== undefined;
   };
   
   typeMap['arxiv'] = function (id) {
      if (id.toLowerCase().startsWith('arxiv.org')) return 'http://' + id;
      if (id.startsWith('arXiv:')) return 'http://arxiv.org/abs/' + id.substring(6);
      return 'http://arxiv.org/abs/' + id;
   };
   
   typeMap['asin'] = function (id) {
      if (id.toLowerCase().startsWith('amazon.') || id.startsWith('www.amazon.')) return 'http://' + id;
      return 'http://www.amazon.com/dp/' + id;
   };

   typeMap['bibcode'] = function (id) {
      if (id.toLowerCase().startsWith('adsabs.harvard.edu')) return 'http://' + id;
      return 'http://adsabs.harvard.edu/abs/' + id;
   };
   
   typeMap['doi'] = function (id) {
      if (id.toLowerCase().startsWith('dx.doi.org') || id.toLowerCase().startsWith('doi.org') ) return 'https://' + id;
      return 'https://doi.org/' + id;
   };

   typeMap['ethos'] = function (id) {
      if (id.toLowerCase().startsWith('ethos.bl.uk')) return 'http://' + id;
      return 'http://ethos.bl.uk/OrderDetails.do?uin=' + encodeURIComponent(id);
   };

   typeMap['isbn'] = function (id) {
      if (id.toLowerCase().startsWith('amazon.com/dp/') || id.toLowerCase().startsWith('www.worldcat.org')) return 'http://' + id;
      return 'http://www.worldcat.org/isbn/' + id.replace(/\-/g, '');
   };

   typeMap['jfm'] = function (id) {
      if (id.toLowerCase().startsWith('www.zentralblatt-math.org')) return 'http://' + id;
      if (id.toLowerCase().startsWith('zbmath.org/?q=an:')) return 'http://' + id;
      return 'http://zbmath.org/?q=' + encodeURIComponent('an:' + id ) + '&format=complete';
   };

   typeMap['jstor'] = function (id) {
      if (id.toLowerCase().startsWith('dx.doi.org') || id.startsWith('www.jstor.org')) return 'http://' + id;
      return 'http://www.jstor.org/stable/' + id;
   };

   typeMap['lccn'] = function (id) {
      if (id.toLowerCase().startsWith('lccn.loc.gov')) return 'http://' + id;
      return 'http://lccn.loc.gov/' + id;
   };

   typeMap['mr'] = function (id) {
      id = id.match(/[^\(]*/)[0];
      if (id.toLowerCase().startsWith('ams.org/mathscinet-getitem')) return 'http://' + id;
      return 'http://www.ams.org/mathscinet-getitem?mr=' + encodeURIComponent(id);
   };

   typeMap['oclc'] = function (id) {
      if (id.toLowerCase().startsWith('worldcat.org')) return 'http://' + id;
      return 'http://www.worldcat.org/oclc/' + id;
   };

   typeMap['ol'] = function (id) {
      if (id.toLowerCase().startsWith('openlibrary.org/b/')) return 'http://' + id;
      return 'http://openlibrary.org/b/' + id;
   };
 
   typeMap['osti'] = function (id) {
      if (id.toLowerCase().startsWith('www.osti.gov')) return 'http://' + id;
      return 'http://www.osti.gov/energycitations/product.biblio.jsp?osti_id=' + encodeURIComponent(id);
   };

   typeMap['pmc'] = function (id) { 
      if (id.toLowerCase().startsWith('pmc')) return 'http://europepmc.org/articles/' + id;
      if (id.toLowerCase().startsWith('www.ncbi.nlm.nih.gov')) return 'http://' + id;
      //return 'http://www.ncbi.nlm.nih.gov/pubmed/' + id;
      return 'http://europepmc.org/articles/' + id;
   };

   /* 
    * We need a method of determining www.ncbi.nlm.nih.gov identifiers
    * vs europepmc.org identifiers
    * http://www.ncbi.nlm.nih.gov/pubmed/
    * http://europepmc.org/abstract/med/
    */
   typeMap['pmid'] = function (id) {
      if (id.toLowerCase().startsWith('www.ncbi.nlm.nih.gov')) return 'http://' + id;
      if (id.toLowerCase().startsWith('europepmc.org')) return 'http://' + id;
      return 'http://europepmc.org/abstract/med/' + id;
   };

   typeMap['rfc'] = function (id) {
      id = id.replace(/\s/g,'');
      id = id.toLowerCase();
      if (id.toLowerCase().startsWith('www.rfc-editor.org/rfc/')) return 'http://' + id;
      return 'http://www.rfc-editor.org/rfc/' + id + '.txt';
   };

   typeMap['ssrn'] = function (id) {
      if (id.toLowerCase().startsWith('papers.ssrn.com')) return 'http://' + id;
      return 'http://papers.ssrn.com/abstract_id=' + encodeURIComponent(id);
   };

   typeMap['zbl'] = function (id) {
      if (id.toLowerCase().startsWith('zentralblatt-math.org')) return 'http://' + id;
      if (id.toLowerCase().startsWith('zbmath.org/?q=an')) return 'http://' + id;
      return 'http://zbmath.org/?q=' + encodeURIComponent('an:' + id ) + '&format=complete';
   };
   
   typeMap['kuid'] = function (id) {
       return 'https://koreamed.org/article/' + encodeURIComponent(id);
   };
   
   typeMap['lensid'] = function (id) {
       if (id.toLowerCase().startsWith('www.lens.org')) return 'https://' + id;
       return 'https://www.lens.org/' + encodeURIComponent(id);
   };
   
   typeMap['cienciaiul'] = function (id) {
       return 'https://ciencia.iscte-iul.pt/id/' + encodeURIComponent(id);
   };

   exports.getLink = function(id, type) {
      if (id == null) id = "";//return null;
      id = id.trim();
      if (id.startsWith('http:') || id.startsWith('https:')) return id;
      if (type == null) return null;
      type = type.toLowerCase();
      if (!typeMap.hasOwnProperty(type)) return null;
      return typeMap[type](id);
    };

   exports.getTypes = function() {
      var types = '';
      for(i in typeMap) {
          if (i != 'hasOwnProperty')
          types = types + ' ' + i;
      }
      return types;
   }

})(typeof exports === 'undefined'? this['workIdLinkJs']={}: exports);

/* END: workIdLinkJs */


/* START: orcidSearchUrlJs v0.0.1 */
/* https://github.com/ORCID/orcidSearchUrlJs */

/* browser and NodeJs compatible */
(function(exports) {

    var baseUrl = 'https://orcid.org/v1.2/search/orcid-bio/';
    var quickSearchEDisMax = '{!edismax qf="given-and-family-names^50.0 family-name^10.0 given-names^5.0 credit-name^10.0 other-names^5.0 text^1.0" pf="given-and-family-names^50.0" mm=1}';
    var orcidPathRegex = new RegExp("(\\d{4}-){3,}\\d{3}[\\dX]");
    var orcidFullRegex = new RegExp(
            "^\\s*((http://)?([^/]*orcid\\.org|localhost.*/orcid-web)/)?(\\d{4}-){3,}\\d{3}[\\dX]\\s*$");

    function offset(input) {
        var start = hasValue(input.start) ? input.start : 0;
        var rows = hasValue(input.rows) ? input.rows : 10;
        return '&start=' + start + '&rows=' + rows;
    }

    function hasValue(ref) {
        return typeof ref !== 'undefined' && ref !== null && ref !== '';
    }

    function buildAdvancedSearchUrl(input) {
        var query = '';
        var doneSomething = false;
        if (hasValue(input.givenNames)) {
            query += 'given-names:' + input.givenNames.toLowerCase();
            doneSomething = true;
        }
        if (hasValue(input.familyName)) {
            if (doneSomething) {
                query += ' AND ';
            }
            query += 'family-name:' + input.familyName.toLowerCase();
            doneSomething = true;
        }
        if (hasValue(input.searchOtherNames) && hasValue(input.givenNames)) {
            query += ' OR other-names:' + input.givenNames.toLowerCase();
        }
        if (hasValue(input.keyword)) {
            if (doneSomething) {
                query += ' AND ';
            }
            query += 'keyword:' + input.keyword.toLowerCase();
            doneSomething = true;
        }
        return doneSomething ? baseUrl + '?q=' + encodeURIComponent(query)
                + offset(input) : baseUrl + '?q=';
    }

    exports.setBaseUrl = function(url) {
        baseUrl = url;
    };

    exports.isValidInput = function(input) {
        var fieldsToCheck = [ input.text, input.givenNames, input.familyName,
                input.keyword ];
        for ( var i = 0; i < fieldsToCheck.length; i++) {
            if (hasValue(fieldsToCheck[i])) {
                return true;
            }
        }
        return false;
    };

    function extractOrcidId(string) {
        var regexResult = orcidPathRegex.exec(string);
        if (regexResult) {
            return regexResult[0];
        }
        return null;
    }

    exports.buildUrl = function(input) {
        if (hasValue(input.text)) {
            var orcidId = extractOrcidId(input.text);
            if (orcidId) {
                // Search for iD specifically
                return baseUrl + "?q=orcid:" + orcidId + offset(input);
            }
            // General quick search
            return baseUrl + '?q='
                    + encodeURIComponent(quickSearchEDisMax + input.text)
                    + offset(input);
        } else {
            // Advanced search
            return buildAdvancedSearchUrl(input);
        }
    };

    exports.isValidOrcidId = function(orcidId) {
        if (orcidFullRegex.exec(orcidId)) {
            return true;
        }
        return false;
    };

})(typeof exports === 'undefined' ? this.orcidSearchUrlJs = {} : exports);

/* END: orcidSearchUrlJs */
orcidSearchUrlJs.setBaseUrl(orcidVar.searchBaseUrl);


/* Mobile detection, useful for colorbox lightboxes resizing */
function isMobile() {
    (/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i
            .test((navigator.userAgent || navigator.vendor || window.opera)) || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i
            .test((navigator.userAgent || navigator.vendor || window.opera)
                    .substr(0, 4))) ? im = true : im = false;
    return im;
};

function isIE() {
    var myNav = navigator.userAgent.toLowerCase();
    return (myNav.indexOf('msie') != -1) ? parseInt(myNav.split('msie')[1])
            : false;
}

function getWindowWidth() {
    var windowWidth = 0;
    if (typeof (window.innerWidth) == 'number') {
        windowWidth = window.innerWidth;
    } else {
        if (document.documentElement && document.documentElement.clientWidth) {
            windowWidth = document.documentElement.clientWidth;
        } else {
            if (document.body && document.body.clientWidth) {
                windowWidth = document.body.clientWidth;
            }
        }
    }
    return windowWidth;
};

function tabletDesktopActionButtons($event) {
    var thisWidth = getWindowWidth();
    if (thisWidth >= 767) {
        $('.action-button-bar').addClass('tablet-desktop-display');
    } else { // Mobile
        $('.action-button-bar').removeClass('tablet-desktop-display');
    }
};

function iframeResize(putCode){						
	$('#'+putCode).iFrameResize({
		log: false,
		autoResize: true			
	});
}

function isIndexOf(needle) {
    if(typeof Array.prototype.indexOf === 'function') {
        indexOf = Array.prototype.indexOf;
    } else {
        indexOf = function(needle) {
            var i = -1, index = -1;

            for(i = 0; i < this.length; i++) {
                if(this[i] === needle) {
                    index = i;
                    break;
                }
            }

            return index;
        };
    }
    return indexOf.call(this, needle);
};