// Avoid `console` errors in browsers that lack a console.
if (!(window.console && console.log)) {
    (function() {
        var noop = function() {};
        var methods = ['assert', 'clear', 'count', 'debug', 'dir', 'dirxml', 'error', 'exception', 'group', 'groupCollapsed', 'groupEnd', 'info', 'log', 'markTimeline', 'profile', 'profileEnd', 'markTimeline', 'table', 'time', 'timeEnd', 'timeStamp', 'trace', 'warn'];
        var length = methods.length;
        var console = window.console = {};
        while (length--) {
            console[methods[length]] = noop;
        }
    }());
}


/*!
Colorbox 1.6.3
license: MIT
http://www.jacklmoore.com/colorbox
*/
(function ($, document, window) {
var
// Default settings object.
// See http://jacklmoore.com/colorbox for details.
defaults = {
	// data sources
	html: false,
	photo: false,
	iframe: false,
	inline: false,

	// behavior and appearance
	transition: "elastic",
	speed: 300,
	fadeOut: 300,
	width: false,
	initialWidth: "600",
	innerWidth: false,
	maxWidth: false,
	height: false,
	initialHeight: "450",
	innerHeight: false,
	maxHeight: false,
	scalePhotos: true,
	scrolling: true,
	opacity: 0.9,
	preloading: true,
	className: false,
	overlayClose: true,
	escKey: true,
	arrowKey: true,
	top: false,
	bottom: false,
	left: false,
	right: false,
	fixed: false,
	data: undefined,
	closeButton: true,
	fastIframe: true,
	open: false,
	reposition: true,
	loop: true,
	slideshow: false,
	slideshowAuto: true,
	slideshowSpeed: 2500,
	slideshowStart: "start slideshow",
	slideshowStop: "stop slideshow",
	photoRegex: /\.(gif|png|jp(e|g|eg)|bmp|ico|webp|jxr|svg)((#|\?).*)?$/i,

	// alternate image paths for high-res displays
	retinaImage: false,
	retinaUrl: false,
	retinaSuffix: '@2x.$1',

	// internationalization
	current: "image {current} of {total}",
	previous: "previous",
	next: "next",
	close: "close",
	xhrError: "This content failed to load.",
	imgError: "This image failed to load.",

	// accessbility
	returnFocus: true,
	trapFocus: true,

	// callbacks
	onOpen: false,
	onLoad: false,
	onComplete: false,
	onCleanup: false,
	onClosed: false,

	rel: function() {
		return this.rel;
	},
	href: function() {
		// using this.href would give the absolute url, when the href may have been inteded as a selector (e.g. '#container')
		return $(this).attr('href');
	},
	title: function() {
		return this.title;
	},
	createImg: function() {
		var img = new Image();
		var attrs = $(this).data('cbox-img-attrs');

		if (typeof attrs === 'object') {
			$.each(attrs, function(key, val){
				img[key] = val;
			});
		}

		return img;
	},
	createIframe: function() {
		var iframe = document.createElement('iframe');
		var attrs = $(this).data('cbox-iframe-attrs');

		if (typeof attrs === 'object') {
			$.each(attrs, function(key, val){
				iframe[key] = val;
			});
		}

		if ('frameBorder' in iframe) {
			iframe.frameBorder = 0;
		}
		if ('allowTransparency' in iframe) {
			iframe.allowTransparency = "true";
		}
		iframe.name = (new Date()).getTime(); // give the iframe a unique name to prevent caching
		iframe.allowFullscreen = true;

		return iframe;
	}
},

// Abstracting the HTML and event identifiers for easy rebranding
colorbox = 'colorbox',
prefix = 'cbox',
boxElement = prefix + 'Element',

// Events
event_open = prefix + '_open',
event_load = prefix + '_load',
event_complete = prefix + '_complete',
event_cleanup = prefix + '_cleanup',
event_closed = prefix + '_closed',
event_purge = prefix + '_purge',

// Cached jQuery Object Variables
$overlay,
$box,
$wrap,
$content,
$topBorder,
$leftBorder,
$rightBorder,
$bottomBorder,
$related,
$window,
$loaded,
$loadingBay,
$loadingOverlay,
$title,
$current,
$slideshow,
$next,
$prev,
$close,
$groupControls,
$events = $('<a/>'), // $({}) would be prefered, but there is an issue with jQuery 1.4.2

// Variables for cached values or use across multiple functions
settings,
interfaceHeight,
interfaceWidth,
loadedHeight,
loadedWidth,
index,
photo,
open,
active,
closing,
loadingTimer,
publicMethod,
div = "div",
requests = 0,
previousCSS = {},
init;

// ****************
// HELPER FUNCTIONS
// ****************

// Convenience function for creating new jQuery objects
function $tag(tag, id, css) {
	var element = document.createElement(tag);

	if (id) {
		element.id = prefix + id;
	}

	if (css) {
		element.style.cssText = css;
	}

	return $(element);
}

// Get the window height using innerHeight when available to avoid an issue with iOS
// http://bugs.jquery.com/ticket/6724
function winheight() {
	return window.innerHeight ? window.innerHeight : $(window).height();
}

function Settings(element, options) {
	if (options !== Object(options)) {
		options = {};
	}

	this.cache = {};
	this.el = element;

	this.value = function(key) {
		var dataAttr;

		if (this.cache[key] === undefined) {
			dataAttr = $(this.el).attr('data-cbox-'+key);

			if (dataAttr !== undefined) {
				this.cache[key] = dataAttr;
			} else if (options[key] !== undefined) {
				this.cache[key] = options[key];
			} else if (defaults[key] !== undefined) {
				this.cache[key] = defaults[key];
			}
		}

		return this.cache[key];
	};

	this.get = function(key) {
		var value = this.value(key);
		return $.isFunction(value) ? value.call(this.el, this) : value;
	};
}

// Determine the next and previous members in a group.
function getIndex(increment) {
	var
	max = $related.length,
	newIndex = (index + increment) % max;

	return (newIndex < 0) ? max + newIndex : newIndex;
}

// Convert '%' and 'px' values to integers
function setSize(size, dimension) {
	return Math.round((/%/.test(size) ? ((dimension === 'x' ? $window.width() : winheight()) / 100) : 1) * parseInt(size, 10));
}

// Checks an href to see if it is a photo.
// There is a force photo option (photo: true) for hrefs that cannot be matched by the regex.
function isImage(settings, url) {
	return settings.get('photo') || settings.get('photoRegex').test(url);
}

function retinaUrl(settings, url) {
	return settings.get('retinaUrl') && window.devicePixelRatio > 1 ? url.replace(settings.get('photoRegex'), settings.get('retinaSuffix')) : url;
}

function trapFocus(e) {
	if ('contains' in $box[0] && !$box[0].contains(e.target) && e.target !== $overlay[0]) {
		e.stopPropagation();
		$box.focus();
	}
}

function setClass(str) {
	if (setClass.str !== str) {
		$box.add($overlay).removeClass(setClass.str).addClass(str);
		setClass.str = str;
	}
}

function getRelated(rel) {
	index = 0;

	if (rel && rel !== false && rel !== 'nofollow') {
		$related = $('.' + boxElement).filter(function () {
			var options = $.data(this, colorbox);
			var settings = new Settings(this, options);
			return (settings.get('rel') === rel);
		});
		index = $related.index(settings.el);

		// Check direct calls to Colorbox.
		if (index === -1) {
			$related = $related.add(settings.el);
			index = $related.length - 1;
		}
	} else {
		$related = $(settings.el);
	}
}

function trigger(event) {
	// for external use
	$(document).trigger(event);
	// for internal use
	$events.triggerHandler(event);
}

var slideshow = (function(){
	var active,
		className = prefix + "Slideshow_",
		click = "click." + prefix,
		timeOut;

	function clear () {
		clearTimeout(timeOut);
	}

	function set() {
		if (settings.get('loop') || $related[index + 1]) {
			clear();
			timeOut = setTimeout(publicMethod.next, settings.get('slideshowSpeed'));
		}
	}

	function start() {
		$slideshow
			.html(settings.get('slideshowStop'))
			.unbind(click)
			.one(click, stop);

		$events
			.bind(event_complete, set)
			.bind(event_load, clear);

		$box.removeClass(className + "off").addClass(className + "on");
	}

	function stop() {
		clear();

		$events
			.unbind(event_complete, set)
			.unbind(event_load, clear);

		$slideshow
			.html(settings.get('slideshowStart'))
			.unbind(click)
			.one(click, function () {
				publicMethod.next();
				start();
			});

		$box.removeClass(className + "on").addClass(className + "off");
	}

	function reset() {
		active = false;
		$slideshow.hide();
		clear();
		$events
			.unbind(event_complete, set)
			.unbind(event_load, clear);
		$box.removeClass(className + "off " + className + "on");
	}

	return function(){
		if (active) {
			if (!settings.get('slideshow')) {
				$events.unbind(event_cleanup, reset);
				reset();
			}
		} else {
			if (settings.get('slideshow') && $related[1]) {
				active = true;
				$events.one(event_cleanup, reset);
				if (settings.get('slideshowAuto')) {
					start();
				} else {
					stop();
				}
				$slideshow.show();
			}
		}
	};

}());


function launch(element) {
	var options;

	if (!closing) {

		options = $(element).data(colorbox);

		settings = new Settings(element, options);

		getRelated(settings.get('rel'));

		if (!open) {
			open = active = true; // Prevents the page-change action from queuing up if the visitor holds down the left or right keys.

			setClass(settings.get('className'));

			// Show colorbox so the sizes can be calculated in older versions of jQuery
			$box.css({visibility:'hidden', display:'block', opacity:''});

			$loaded = $tag(div, 'LoadedContent', 'width:0; height:0; overflow:hidden; visibility:hidden');
			$content.css({width:'', height:''}).append($loaded);

			// Cache values needed for size calculations
			interfaceHeight = $topBorder.height() + $bottomBorder.height() + $content.outerHeight(true) - $content.height();
			interfaceWidth = $leftBorder.width() + $rightBorder.width() + $content.outerWidth(true) - $content.width();
			loadedHeight = $loaded.outerHeight(true);
			loadedWidth = $loaded.outerWidth(true);

			// Opens inital empty Colorbox prior to content being loaded.
			var initialWidth = setSize(settings.get('initialWidth'), 'x');
			var initialHeight = setSize(settings.get('initialHeight'), 'y');
			var maxWidth = settings.get('maxWidth');
			var maxHeight = settings.get('maxHeight');

			settings.w = Math.max((maxWidth !== false ? Math.min(initialWidth, setSize(maxWidth, 'x')) : initialWidth) - loadedWidth - interfaceWidth, 0);
			settings.h = Math.max((maxHeight !== false ? Math.min(initialHeight, setSize(maxHeight, 'y')) : initialHeight) - loadedHeight - interfaceHeight, 0);

			$loaded.css({width:'', height:settings.h});
			publicMethod.position();

			trigger(event_open);
			settings.get('onOpen');

			$groupControls.add($title).hide();

			$box.focus();

			if (settings.get('trapFocus')) {
				// Confine focus to the modal
				// Uses event capturing that is not supported in IE8-
				if (document.addEventListener) {

					document.addEventListener('focus', trapFocus, true);

					$events.one(event_closed, function () {
						document.removeEventListener('focus', trapFocus, true);
					});
				}
			}

			// Return focus on closing
			if (settings.get('returnFocus')) {
				$events.one(event_closed, function () {
					$(settings.el).focus();
				});
			}
		}

		var opacity = parseFloat(settings.get('opacity'));
		$overlay.css({
			opacity: opacity === opacity ? opacity : '',
			cursor: settings.get('overlayClose') ? 'pointer' : '',
			visibility: 'visible'
		}).show();

		if (settings.get('closeButton')) {
			$close.html(settings.get('close')).appendTo($content);
		} else {
			$close.appendTo('<div/>'); // replace with .detach() when dropping jQuery < 1.4
		}

		load();
	}
}

// Colorbox's markup needs to be added to the DOM prior to being called
// so that the browser will go ahead and load the CSS background images.
function appendHTML() {
	if (!$box) {
		init = false;
		$window = $(window);
		$box = $tag(div).attr({
			id: colorbox,
			'class': $.support.opacity === false ? prefix + 'IE' : '', // class for optional IE8 & lower targeted CSS.
			role: 'dialog',
			tabindex: '-1'
		}).hide();
		$overlay = $tag(div, "Overlay").hide();
		$loadingOverlay = $([$tag(div, "LoadingOverlay")[0],$tag(div, "LoadingGraphic")[0]]);
		$wrap = $tag(div, "Wrapper");
		$content = $tag(div, "Content").append(
			$title = $tag(div, "Title"),
			$current = $tag(div, "Current"),
			$prev = $('<button type="button"/>').attr({id:prefix+'Previous'}),
			$next = $('<button type="button"/>').attr({id:prefix+'Next'}),
			$slideshow = $('<button type="button"/>').attr({id:prefix+'Slideshow'}),
			$loadingOverlay
		);

		$close = $('<button type="button"/>').attr({id:prefix+'Close'});

		$wrap.append( // The 3x3 Grid that makes up Colorbox
			$tag(div).append(
				$tag(div, "TopLeft"),
				$topBorder = $tag(div, "TopCenter"),
				$tag(div, "TopRight")
			),
			$tag(div, false, 'clear:left').append(
				$leftBorder = $tag(div, "MiddleLeft"),
				$content,
				$rightBorder = $tag(div, "MiddleRight")
			),
			$tag(div, false, 'clear:left').append(
				$tag(div, "BottomLeft"),
				$bottomBorder = $tag(div, "BottomCenter"),
				$tag(div, "BottomRight")
			)
		).find('div div').css({'float': 'left'});

		$loadingBay = $tag(div, false, 'position:absolute; width:9999px; visibility:hidden; display:none; max-width:none;');

		$groupControls = $next.add($prev).add($current).add($slideshow);
	}
	if (document.body && !$box.parent().length) {
		$(document.body).append($overlay, $box.append($wrap, $loadingBay));
	}
}

// Add Colorbox's event bindings
function addBindings() {
	function clickHandler(e) {
		// ignore non-left-mouse-clicks and clicks modified with ctrl / command, shift, or alt.
		// See: http://jacklmoore.com/notes/click-events/
		if (!(e.which > 1 || e.shiftKey || e.altKey || e.metaKey || e.ctrlKey)) {
			e.preventDefault();
			launch(this);
		}
	}

	if ($box) {
		if (!init) {
			init = true;

			// Anonymous functions here keep the public method from being cached, thereby allowing them to be redefined on the fly.
			$next.click(function () {
				publicMethod.next();
			});
			$prev.click(function () {
				publicMethod.prev();
			});
			$close.click(function () {
				publicMethod.close();
			});
			$overlay.click(function () {
				if (settings.get('overlayClose')) {
					publicMethod.close();
				}
			});

			// Key Bindings
			$(document).bind('keydown.' + prefix, function (e) {
				var key = e.keyCode;
				if (open && settings.get('escKey') && key === 27) {
					e.preventDefault();
					publicMethod.close();
				}
				if (open && settings.get('arrowKey') && $related[1] && !e.altKey) {
					if (key === 37) {
						e.preventDefault();
						$prev.click();
					} else if (key === 39) {
						e.preventDefault();
						$next.click();
					}
				}
			});

			if ($.isFunction($.fn.on)) {
				// For jQuery 1.7+
				$(document).on('click.'+prefix, '.'+boxElement, clickHandler);
			} else {
				// For jQuery 1.3.x -> 1.6.x
				// This code is never reached in jQuery 1.9, so do not contact me about 'live' being removed.
				// This is not here for jQuery 1.9, it's here for legacy users.
				$('.'+boxElement).live('click.'+prefix, clickHandler);
			}
		}
		return true;
	}
	return false;
}

// Don't do anything if Colorbox already exists.
if ($[colorbox]) {
	return;
}

// Append the HTML when the DOM loads
$(appendHTML);


// ****************
// PUBLIC FUNCTIONS
// Usage format: $.colorbox.close();
// Usage from within an iframe: parent.jQuery.colorbox.close();
// ****************

publicMethod = $.fn[colorbox] = $[colorbox] = function (options, callback) {
	var settings;
	var $obj = this;

	options = options || {};

	if ($.isFunction($obj)) { // assume a call to $.colorbox
		$obj = $('<a/>');
		options.open = true;
	}

	if (!$obj[0]) { // colorbox being applied to empty collection
		return $obj;
	}

	appendHTML();

	if (addBindings()) {

		if (callback) {
			options.onComplete = callback;
		}

		$obj.each(function () {
			var old = $.data(this, colorbox) || {};
			$.data(this, colorbox, $.extend(old, options));
		}).addClass(boxElement);

		settings = new Settings($obj[0], options);

		if (settings.get('open')) {
			launch($obj[0]);
		}
	}

	return $obj;
};

publicMethod.position = function (speed, loadedCallback) {
	var
	css,
	top = 0,
	left = 0,
	offset = $box.offset(),
	scrollTop,
	scrollLeft;

	$window.unbind('resize.' + prefix);

	// remove the modal so that it doesn't influence the document width/height
	$box.css({top: -9e4, left: -9e4});

	scrollTop = $window.scrollTop();
	scrollLeft = $window.scrollLeft();

	if (settings.get('fixed')) {
		offset.top -= scrollTop;
		offset.left -= scrollLeft;
		$box.css({position: 'fixed'});
	} else {
		top = scrollTop;
		left = scrollLeft;
		$box.css({position: 'absolute'});
	}

	// keeps the top and left positions within the browser's viewport.
	if (settings.get('right') !== false) {
		left += Math.max($window.width() - settings.w - loadedWidth - interfaceWidth - setSize(settings.get('right'), 'x'), 0);
	} else if (settings.get('left') !== false) {
		left += setSize(settings.get('left'), 'x');
	} else {
		left += Math.round(Math.max($window.width() - settings.w - loadedWidth - interfaceWidth, 0) / 2);
	}

	if (settings.get('bottom') !== false) {
		top += Math.max(winheight() - settings.h - loadedHeight - interfaceHeight - setSize(settings.get('bottom'), 'y'), 0);
	} else if (settings.get('top') !== false) {
		top += setSize(settings.get('top'), 'y');
	} else {
		top += Math.round(Math.max(winheight() - settings.h - loadedHeight - interfaceHeight, 0) / 2);
	}

	$box.css({top: offset.top, left: offset.left, visibility:'visible'});

	// this gives the wrapper plenty of breathing room so it's floated contents can move around smoothly,
	// but it has to be shrank down around the size of div#colorbox when it's done.  If not,
	// it can invoke an obscure IE bug when using iframes.
	$wrap[0].style.width = $wrap[0].style.height = "9999px";

	function modalDimensions() {
		$topBorder[0].style.width = $bottomBorder[0].style.width = $content[0].style.width = (parseInt($box[0].style.width,10) - interfaceWidth)+'px';
		$content[0].style.height = $leftBorder[0].style.height = $rightBorder[0].style.height = (parseInt($box[0].style.height,10) - interfaceHeight)+'px';
	}

	css = {width: settings.w + loadedWidth + interfaceWidth, height: settings.h + loadedHeight + interfaceHeight, top: top, left: left};

	// setting the speed to 0 if the content hasn't changed size or position
	if (speed) {
		var tempSpeed = 0;
		$.each(css, function(i){
			if (css[i] !== previousCSS[i]) {
				tempSpeed = speed;
				return;
			}
		});
		speed = tempSpeed;
	}

	previousCSS = css;

	if (!speed) {
		$box.css(css);
	}

	$box.dequeue().animate(css, {
		duration: speed || 0,
		complete: function () {
			modalDimensions();

			active = false;

			// shrink the wrapper down to exactly the size of colorbox to avoid a bug in IE's iframe implementation.
			$wrap[0].style.width = (settings.w + loadedWidth + interfaceWidth) + "px";
			$wrap[0].style.height = (settings.h + loadedHeight + interfaceHeight) + "px";

			if (settings.get('reposition')) {
				setTimeout(function () {  // small delay before binding onresize due to an IE8 bug.
					$window.bind('resize.' + prefix, publicMethod.position);
				}, 1);
			}

			if ($.isFunction(loadedCallback)) {
				loadedCallback();
			}
		},
		step: modalDimensions
	});
};

publicMethod.resize = function (options) {
	var scrolltop;

	if (open) {
		options = options || {};

		if (options.width) {
			settings.w = setSize(options.width, 'x') - loadedWidth - interfaceWidth;
		}

		if (options.innerWidth) {
			settings.w = setSize(options.innerWidth, 'x');
		}

		$loaded.css({width: settings.w});

		if (options.height) {
			settings.h = setSize(options.height, 'y') - loadedHeight - interfaceHeight;
		}

		if (options.innerHeight) {
			settings.h = setSize(options.innerHeight, 'y');
		}

		if (!options.innerHeight && !options.height) {
			scrolltop = $loaded.scrollTop();
			$loaded.css({height: "auto"});
			settings.h = $loaded.height();
		}

		$loaded.css({height: settings.h});

		if(scrolltop) {
			$loaded.scrollTop(scrolltop);
		}

		publicMethod.position(settings.get('transition') === "none" ? 0 : settings.get('speed'));
	}
};

publicMethod.prep = function (object) {
	if (!open) {
		return;
	}

	var callback, speed = settings.get('transition') === "none" ? 0 : settings.get('speed');

	$loaded.remove();

	$loaded = $tag(div, 'LoadedContent').append(object);

	function getWidth() {
		settings.w = settings.w || $loaded.width();
		settings.w = settings.mw && settings.mw < settings.w ? settings.mw : settings.w;
		return settings.w;
	}
	function getHeight() {
		settings.h = settings.h || $loaded.height();
		settings.h = settings.mh && settings.mh < settings.h ? settings.mh : settings.h;
		return settings.h;
	}

	$loaded.hide()
	.appendTo($loadingBay.show())// content has to be appended to the DOM for accurate size calculations.
	.css({width: getWidth(), overflow: settings.get('scrolling') ? 'auto' : 'hidden'})
	.css({height: getHeight()})// sets the height independently from the width in case the new width influences the value of height.
	.prependTo($content);

	$loadingBay.hide();

	// floating the IMG removes the bottom line-height and fixed a problem where IE miscalculates the width of the parent element as 100% of the document width.

	$(photo).css({'float': 'none'});

	setClass(settings.get('className'));

	callback = function () {
		var total = $related.length,
			iframe,
			complete;

		if (!open) {
			return;
		}

		function removeFilter() { // Needed for IE8 in versions of jQuery prior to 1.7.2
			if ($.support.opacity === false) {
				$box[0].style.removeAttribute('filter');
			}
		}

		complete = function () {
			clearTimeout(loadingTimer);
			$loadingOverlay.hide();
			trigger(event_complete);
			settings.get('onComplete');
		};


		$title.html(settings.get('title')).show();
		$loaded.show();

		if (total > 1) { // handle grouping
			if (typeof settings.get('current') === "string") {
				$current.html(settings.get('current').replace('{current}', index + 1).replace('{total}', total)).show();
			}

			$next[(settings.get('loop') || index < total - 1) ? "show" : "hide"]().html(settings.get('next'));
			$prev[(settings.get('loop') || index) ? "show" : "hide"]().html(settings.get('previous'));

			slideshow();

			// Preloads images within a rel group
			if (settings.get('preloading')) {
				$.each([getIndex(-1), getIndex(1)], function(){
					var img,
						i = $related[this],
						settings = new Settings(i, $.data(i, colorbox)),
						src = settings.get('href');

					if (src && isImage(settings, src)) {
						src = retinaUrl(settings, src);
						img = document.createElement('img');
						img.src = src;
					}
				});
			}
		} else {
			$groupControls.hide();
		}

		if (settings.get('iframe')) {

			iframe = settings.get('createIframe');

			if (!settings.get('scrolling')) {
				iframe.scrolling = "no";
			}

			$(iframe)
				.attr({
					src: settings.get('href'),
					'class': prefix + 'Iframe'
				})
				.one('load', complete)
				.appendTo($loaded);

			$events.one(event_purge, function () {
				iframe.src = "//about:blank";
			});

			if (settings.get('fastIframe')) {
				$(iframe).trigger('load');
			}
		} else {
			complete();
		}

		if (settings.get('transition') === 'fade') {
			$box.fadeTo(speed, 1, removeFilter);
		} else {
			removeFilter();
		}
	};

	if (settings.get('transition') === 'fade') {
		$box.fadeTo(speed, 0, function () {
			publicMethod.position(0, callback);
		});
	} else {
		publicMethod.position(speed, callback);
	}
};

function load () {
	var href, setResize, prep = publicMethod.prep, $inline, request = ++requests;

	active = true;

	photo = false;

	trigger(event_purge);
	trigger(event_load);
	settings.get('onLoad');

	settings.h = settings.get('height') ?
			setSize(settings.get('height'), 'y') - loadedHeight - interfaceHeight :
			settings.get('innerHeight') && setSize(settings.get('innerHeight'), 'y');

	settings.w = settings.get('width') ?
			setSize(settings.get('width'), 'x') - loadedWidth - interfaceWidth :
			settings.get('innerWidth') && setSize(settings.get('innerWidth'), 'x');

	// Sets the minimum dimensions for use in image scaling
	settings.mw = settings.w;
	settings.mh = settings.h;

	// Re-evaluate the minimum width and height based on maxWidth and maxHeight values.
	// If the width or height exceed the maxWidth or maxHeight, use the maximum values instead.
	if (settings.get('maxWidth')) {
		settings.mw = setSize(settings.get('maxWidth'), 'x') - loadedWidth - interfaceWidth;
		settings.mw = settings.w && settings.w < settings.mw ? settings.w : settings.mw;
	}
	if (settings.get('maxHeight')) {
		settings.mh = setSize(settings.get('maxHeight'), 'y') - loadedHeight - interfaceHeight;
		settings.mh = settings.h && settings.h < settings.mh ? settings.h : settings.mh;
	}

	href = settings.get('href');

	loadingTimer = setTimeout(function () {
		$loadingOverlay.show();
	}, 100);

	if (settings.get('inline')) {
		var $target = $(href);
		// Inserts an empty placeholder where inline content is being pulled from.
		// An event is bound to put inline content back when Colorbox closes or loads new content.
		$inline = $('<div>').hide().insertBefore($target);

		$events.one(event_purge, function () {
			$inline.replaceWith($target);
		});

		prep($target);
	} else if (settings.get('iframe')) {
		// IFrame element won't be added to the DOM until it is ready to be displayed,
		// to avoid problems with DOM-ready JS that might be trying to run in that iframe.
		prep(" ");
	} else if (settings.get('html')) {
		prep(settings.get('html'));
	} else if (isImage(settings, href)) {

		href = retinaUrl(settings, href);

		photo = settings.get('createImg');

		$(photo)
		.addClass(prefix + 'Photo')
		.bind('error.'+prefix,function () {
			prep($tag(div, 'Error').html(settings.get('imgError')));
		})
		.one('load', function () {
			if (request !== requests) {
				return;
			}

			// A small pause because some browsers will occassionaly report a
			// img.width and img.height of zero immediately after the img.onload fires
			setTimeout(function(){
				var percent;

				if (settings.get('retinaImage') && window.devicePixelRatio > 1) {
					photo.height = photo.height / window.devicePixelRatio;
					photo.width = photo.width / window.devicePixelRatio;
				}

				if (settings.get('scalePhotos')) {
					setResize = function () {
						photo.height -= photo.height * percent;
						photo.width -= photo.width * percent;
					};
					if (settings.mw && photo.width > settings.mw) {
						percent = (photo.width - settings.mw) / photo.width;
						setResize();
					}
					if (settings.mh && photo.height > settings.mh) {
						percent = (photo.height - settings.mh) / photo.height;
						setResize();
					}
				}

				if (settings.h) {
					photo.style.marginTop = Math.max(settings.mh - photo.height, 0) / 2 + 'px';
				}

				if ($related[1] && (settings.get('loop') || $related[index + 1])) {
					photo.style.cursor = 'pointer';

					$(photo).bind('click.'+prefix, function () {
						publicMethod.next();
					});
				}

				photo.style.width = photo.width + 'px';
				photo.style.height = photo.height + 'px';
				prep(photo);
			}, 1);
		});

		photo.src = href;

	} else if (href) {
		$loadingBay.load(href, settings.get('data'), function (data, status) {
			if (request === requests) {
				prep(status === 'error' ? $tag(div, 'Error').html(settings.get('xhrError')) : $(this).contents());
			}
		});
	}
}

// Navigates to the next page/image in a set.
publicMethod.next = function () {
	if (!active && $related[1] && (settings.get('loop') || $related[index + 1])) {
		index = getIndex(1);
		launch($related[index]);
	}
};

publicMethod.prev = function () {
	if (!active && $related[1] && (settings.get('loop') || index)) {
		index = getIndex(-1);
		launch($related[index]);
	}
};

// Note: to use this within an iframe use the following format: parent.jQuery.colorbox.close();
publicMethod.close = function () {
	if (open && !closing) {

		closing = true;
		open = false;
		trigger(event_cleanup);
		settings.get('onCleanup');
		$window.unbind('.' + prefix);
		$overlay.fadeTo(settings.get('fadeOut') || 0, 0);

		$box.stop().fadeTo(settings.get('fadeOut') || 0, 0, function () {
			$box.hide();
			$overlay.hide();
			trigger(event_purge);
			$loaded.remove();

			setTimeout(function () {
				closing = false;
				trigger(event_closed);
				settings.get('onClosed');
			}, 1);
		});
	}
};

// Removes changes Colorbox made to the document, but does not remove the plugin.
publicMethod.remove = function () {
	if (!$box) { return; }

	$box.stop();
	$[colorbox].close();
	$box.stop(false, true).remove();
	$overlay.remove();
	closing = false;
	$box = null;
	$('.' + boxElement)
		.removeData(colorbox)
		.removeClass(boxElement);

	$(document).unbind('click.'+prefix).unbind('keydown.'+prefix);
};

// A method for fetching the current element Colorbox is referencing.
// returns a jQuery object.
publicMethod.element = function () {
	return $(settings.el);
};

publicMethod.settings = defaults;

}(jQuery, document, window));







/*
 * =============================================================================
 *
 * The MIT License (MIT)
 * Copyright (c) 2012 ORCID, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * The name of ORCID, Inc., ORCID, its marks and logo, may not be used in 
 * advertising or publicity pertaining to distribution of the Software.
 *
 * =============================================================================
 */
 /* 
 * =============================================================================
 * Modified from the following source: 
 * =============================================================================
 * 
 * password_strength_plugin.js
 * Copyright (c) 20010 myPocket technologies (www.mypocket-technologies.com)
 

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * @author Darren Mason (djmason9@gmail.com)
 * @date 3/13/2009
 * @projectDescription Password Strength Meter is a jQuery plug-in provide you smart algorithm to detect a password strength. Based on Firas Kassem orginal plugin - http://phiras.wordpress.com/2007/04/08/password-strength-meter-a-jquery-plugin/
 * @version 1.0.1
 * 
 * @requires jquery.js (tested with 1.3.2)
 * @param shortPass:	"shortPass",	//optional
 * @param badPass:		"badPass",		//optional
 * @param goodPass:		"goodPass",		//optional
 * @param strongPass:	"strongPass",	//optional
 * @param baseStyle:	"testresult",	//optional
 * @param userid:		"",				//required override
 * @param messageloc:	1				//before == 0 or after == 1
 * =============================================================================
*/

(function($){ 
	$.fn.shortPass = 'Too short';
	$.fn.invalidFormat = 'Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol';
	$.fn.badPass = 'Weak';
	$.fn.goodPass = 'Good';
	$.fn.strongPass = 'Strong';
	$.fn.samePassword = 'Username and Password identical.';
	$.fn.resultStyle = "";
	
	 $.fn.passStrength = function(options) {  
	  
		 var defaults = {
				shortPass: 		"shortPass",	//optional
				invalidFormat:  "invalidFormatPass", //optional
				badPass:		"badPass",		//optional
				goodPass:		"goodPass",		//optional
				strongPass:		"strongPass",	//optional
				baseStyle:		"testresult",	//optional
				userid:			"",				//required override
				messageloc:		1				//before == 0 or after == 1
			}; 
		 	var opts = $.extend(defaults, options);  
		      
		 	return this.each(function() { 
		 		 var obj = $(this);
		 		
		 		$(obj).unbind().keyup(function()
		 		{
					
					var results = $.fn.teststrength($(this).val(),opts);
					
					if(opts.messageloc === 1)
					{
						$(this).next("." + opts.baseStyle).remove();
						$(this).after("<span class=\""+opts.baseStyle+"\"><span></span></span>");
						$(this).next("." + opts.baseStyle).addClass($(this).resultStyle).find("span").text(results);
					}
					else
					{
						$(this).prev("." + opts.baseStyle).remove();
						$(this).before("<span class=\""+opts.baseStyle+"\"><span></span></span>");
						$(this).prev("." + opts.baseStyle).addClass($(this).resultStyle).find("span").text(results);
					}
		 		 });
		 		 
		 		//FUNCTIONS
		 		$.fn.teststrength = function(password,option){
		 			 	var score = 0; 
		 			    
		 			    //password < 8		 			 	
		 			 	if (password.length < 8 ) { this.resultStyle =  option.shortPass;return $(this).shortPass; }
		 			 	
		 			 	//password not conforming to regex
		 			 	if (!password.match(/(?=.{8,})(?=.*\d)(?=.*[\w{L}])(?=.*\D).*/)) { this.resultStyle =  option.invalidFormat;return $(this).invalidFormat; }		 			    
		 			    
		 			    //password length
		 			    score += password.length * 4;
		 			    score += ( $.fn.checkRepetition(1,password).length - password.length ) * 1;
		 			    score += ( $.fn.checkRepetition(2,password).length - password.length ) * 1;
		 			    score += ( $.fn.checkRepetition(3,password).length - password.length ) * 1;
		 			    score += ( $.fn.checkRepetition(4,password).length - password.length ) * 1;
		 	
		 			    //password has 3 numbers
		 			    if (password.match(/(.*[0-9].*[0-9].*[0-9])/)){ score += 5;} 
		 			    
		 			    //password has 2 symbols
		 			    if (password.match(/(.*[!,@,#,$,%,^,&,*,?,_,~].*[!,@,#,$,%,^,&,*,?,_,~])/)){ score += 5 ;}
		 			    
		 			    //password has Upper and Lower chars
		 			    if (password.match(/([a-z].*[A-Z])|([A-Z].*[a-z])/)){  score += 10;} 
		 			    
		 			    //password has number and chars
		 			    if (password.match(/([a-zA-Z])/) && password.match(/([0-9])/)){  score += 15;} 
		 			    //
		 			    //password has number and symbol
		 			    if (password.match(/([!,@,#,$,%,^,&,*,?,_,~])/) && password.match(/([0-9])/)){  score += 15;} 
		 			    
		 			    //password has char and symbol
		 			    if (password.match(/([!,@,#,$,%,^,&,*,?,_,~])/) && password.match(/([a-zA-Z])/)){score += 15;}
		 			    
		 			    //password is just a numbers or chars
		 			    if (password.match(/^\w+$/) || password.match(/^\d+$/) ){ score -= 10;}
		 			    
		 			    //verifying 0 < score < 100
		 			    if ( score < 0 ){score = 0;} 
		 			    if ( score > 100 ){  score = 100;} 
		 			    
		 			    if (score < 34 ){ this.resultStyle = option.badPass; return $(this).badPass;} 
		 			    if (score < 68 ){ this.resultStyle = option.goodPass;return $(this).goodPass;}
		 			    
		 			   this.resultStyle= option.strongPass;
		 			    return $(this).strongPass;
		 			    
		 		};
		  
		  });  
	 };  
})(jQuery); 


$.fn.checkRepetition = function(pLen,str) {
 	var res = "";
     for (var i=0; i<str.length ; i++ ) 
     {
         var repeated=true;
         
         for (var j=0;j < pLen && (j+i+pLen) < str.length;j++){
             repeated=repeated && (str.charAt(j+i)==str.charAt(j+i+pLen));
             }
         if (j<pLen){repeated=false;}
         if (repeated) {
             i+=pLen-1;
             repeated=false;
         }
         else {
             res+=str.charAt(i);
         }
     }
     return res;
	};
	
	
	/*! jQuery Validation Plugin - v1.10.0 - 9/7/2012
	* https://github.com/jzaefferer/jquery-validation
	* Copyright (c) 2012 JÃ¶rn Zaefferer; Licensed MIT, GPL */

	(function($) {

	$.extend($.fn, {
		// http://docs.jquery.com/Plugins/Validation/validate
		validate: function( options ) {

			// if nothing is selected, return nothing; can't chain anyway
			if (!this.length) {
				if (options && options.debug && window.console) {
					console.warn( "nothing selected, can't validate, returning nothing" );
				}
				return;
			}

			// check if a validator for this form was already created
			var validator = $.data(this[0], 'validator');
			if ( validator ) {
				return validator;
			}

			// Add novalidate tag if HTML5.
			this.attr('novalidate', 'novalidate');

			validator = new $.validator( options, this[0] );
			$.data(this[0], 'validator', validator);

			if ( validator.settings.onsubmit ) {

				this.validateDelegate( ":submit", "click", function(ev) {
					if ( validator.settings.submitHandler ) {
						validator.submitButton = ev.target;
					}
					// allow suppressing validation by adding a cancel class to the submit button
					if ( $(ev.target).hasClass('cancel') ) {
						validator.cancelSubmit = true;
					}
				});

				// validate the form on submit
				this.submit( function( event ) {
					if ( validator.settings.debug ) {
						// prevent form submit to be able to see console output
						event.preventDefault();
					}
					function handle() {
						var hidden;
						if ( validator.settings.submitHandler ) {
							if (validator.submitButton) {
								// insert a hidden input as a replacement for the missing submit button
								hidden = $("<input type='hidden'/>").attr("name", validator.submitButton.name).val(validator.submitButton.value).appendTo(validator.currentForm);
							}
							validator.settings.submitHandler.call( validator, validator.currentForm, event );
							if (validator.submitButton) {
								// and clean up afterwards; thanks to no-block-scope, hidden can be referenced
								hidden.remove();
							}
							return false;
						}
						return true;
					}

					// prevent submit for invalid forms or custom submit handlers
					if ( validator.cancelSubmit ) {
						validator.cancelSubmit = false;
						return handle();
					}
					if ( validator.form() ) {
						if ( validator.pendingRequest ) {
							validator.formSubmitted = true;
							return false;
						}
						return handle();
					} else {
						validator.focusInvalid();
						return false;
					}
				});
			}

			return validator;
		},
		// http://docs.jquery.com/Plugins/Validation/valid
		valid: function() {
			if ( $(this[0]).is('form')) {
				return this.validate().form();
			} else {
				var valid = true;
				var validator = $(this[0].form).validate();
				this.each(function() {
					valid &= validator.element(this);
				});
				return valid;
			}
		},
		// attributes: space seperated list of attributes to retrieve and remove
		removeAttrs: function(attributes) {
			var result = {},
				$element = this;
			$.each(attributes.split(/\s/), function(index, value) {
				result[value] = $element.attr(value);
				$element.removeAttr(value);
			});
			return result;
		},
		// http://docs.jquery.com/Plugins/Validation/rules
		rules: function(command, argument) {
			var element = this[0];

			if (command) {
				var settings = $.data(element.form, 'validator').settings;
				var staticRules = settings.rules;
				var existingRules = $.validator.staticRules(element);
				switch(command) {
				case "add":
					$.extend(existingRules, $.validator.normalizeRule(argument));
					staticRules[element.name] = existingRules;
					if (argument.messages) {
						settings.messages[element.name] = $.extend( settings.messages[element.name], argument.messages );
					}
					break;
				case "remove":
					if (!argument) {
						delete staticRules[element.name];
						return existingRules;
					}
					var filtered = {};
					$.each(argument.split(/\s/), function(index, method) {
						filtered[method] = existingRules[method];
						delete existingRules[method];
					});
					return filtered;
				}
			}

			var data = $.validator.normalizeRules(
			$.extend(
				{},
				$.validator.metadataRules(element),
				$.validator.classRules(element),
				$.validator.attributeRules(element),
				$.validator.staticRules(element)
			), element);

			// make sure required is at front
			if (data.required) {
				var param = data.required;
				delete data.required;
				data = $.extend({required: param}, data);
			}

			return data;
		}
	});

	// Custom selectors
	$.extend($.expr[":"], {
		// http://docs.jquery.com/Plugins/Validation/blank
		blank: function(a) {return !$.trim("" + a.value);},
		// http://docs.jquery.com/Plugins/Validation/filled
		filled: function(a) {return !!$.trim("" + a.value);},
		// http://docs.jquery.com/Plugins/Validation/unchecked
		unchecked: function(a) {return !a.checked;}
	});

	// constructor for validator
	$.validator = function( options, form ) {
		this.settings = $.extend( true, {}, $.validator.defaults, options );
		this.currentForm = form;
		this.init();
	};

	$.validator.format = function(source, params) {
		if ( arguments.length === 1 ) {
			return function() {
				var args = $.makeArray(arguments);
				args.unshift(source);
				return $.validator.format.apply( this, args );
			};
		}
		if ( arguments.length > 2 && params.constructor !== Array  ) {
			params = $.makeArray(arguments).slice(1);
		}
		if ( params.constructor !== Array ) {
			params = [ params ];
		}
		$.each(params, function(i, n) {
			source = source.replace(new RegExp("\\{" + i + "\\}", "g"), n);
		});
		return source;
	};

	$.extend($.validator, {

		defaults: {
			messages: {},
			groups: {},
			rules: {},
			errorClass: "error",
			validClass: "valid",
			errorElement: "label",
			focusInvalid: true,
			errorContainer: $( [] ),
			errorLabelContainer: $( [] ),
			onsubmit: true,
			ignore: ":hidden",
			ignoreTitle: false,
			onfocusin: function(element, event) {
				this.lastActive = element;

				// hide error label and remove error class on focus if enabled
				if ( this.settings.focusCleanup && !this.blockFocusCleanup ) {
					if ( this.settings.unhighlight ) {
						this.settings.unhighlight.call( this, element, this.settings.errorClass, this.settings.validClass );
					}
					this.addWrapper(this.errorsFor(element)).hide();
				}
			},
			onfocusout: function(element, event) {
				if ( !this.checkable(element) && (element.name in this.submitted || !this.optional(element)) ) {
					this.element(element);
				}
			},
			onkeyup: function(element, event) {
				if ( event.which === 9 && this.elementValue(element) === '' ) {
					return;
				} else if ( element.name in this.submitted || element === this.lastActive ) {
					this.element(element);
				}
			},
			onclick: function(element, event) {
				// click on selects, radiobuttons and checkboxes
				if ( element.name in this.submitted ) {
					this.element(element);
				}
				// or option elements, check parent select in that case
				else if (element.parentNode.name in this.submitted) {
					this.element(element.parentNode);
				}
			},
			highlight: function(element, errorClass, validClass) {
				if (element.type === 'radio') {
					this.findByName(element.name).addClass(errorClass).removeClass(validClass);
				} else {
					$(element).addClass(errorClass).removeClass(validClass);
				}
			},
			unhighlight: function(element, errorClass, validClass) {
				if (element.type === 'radio') {
					this.findByName(element.name).removeClass(errorClass).addClass(validClass);
				} else {
					$(element).removeClass(errorClass).addClass(validClass);
				}
			}
		},

		// http://docs.jquery.com/Plugins/Validation/Validator/setDefaults
		setDefaults: function(settings) {
			$.extend( $.validator.defaults, settings );
		},

		messages: {
			required: "This field is required.",
			remote: "Please fix this field.",
			email: "Please enter a valid email address.",
			url: "Please enter a valid URL.",
			date: "Please enter a valid date.",
			dateISO: "Please enter a valid date (ISO).",
			number: "Please enter a valid number.",
			digits: "Please enter only digits.",
			creditcard: "Please enter a valid credit card number.",
			equalTo: "Please enter the same value again.",
			maxlength: $.validator.format("Please enter no more than {0} characters."),
			minlength: $.validator.format("Please enter at least {0} characters."),
			rangelength: $.validator.format("Please enter a value between {0} and {1} characters long."),
			range: $.validator.format("Please enter a value between {0} and {1}."),
			max: $.validator.format("Please enter a value less than or equal to {0}."),
			min: $.validator.format("Please enter a value greater than or equal to {0}.")
		},

		autoCreateRanges: false,

		prototype: {

			init: function() {
				this.labelContainer = $(this.settings.errorLabelContainer);
				this.errorContext = this.labelContainer.length && this.labelContainer || $(this.currentForm);
				this.containers = $(this.settings.errorContainer).add( this.settings.errorLabelContainer );
				this.submitted = {};
				this.valueCache = {};
				this.pendingRequest = 0;
				this.pending = {};
				this.invalid = {};
				this.reset();

				var groups = (this.groups = {});
				$.each(this.settings.groups, function(key, value) {
					$.each(value.split(/\s/), function(index, name) {
						groups[name] = key;
					});
				});
				var rules = this.settings.rules;
				$.each(rules, function(key, value) {
					rules[key] = $.validator.normalizeRule(value);
				});

				function delegate(event) {
					var validator = $.data(this[0].form, "validator"),
						eventType = "on" + event.type.replace(/^validate/, "");
					if (validator.settings[eventType]) {
						validator.settings[eventType].call(validator, this[0], event);
					}
				}
				$(this.currentForm)
					.validateDelegate(":text, [type='password'], [type='file'], select, textarea, " +
						"[type='number'], [type='search'] ,[type='tel'], [type='url'], " +
						"[type='email'], [type='datetime'], [type='date'], [type='month'], " +
						"[type='week'], [type='time'], [type='datetime-local'], " +
						"[type='range'], [type='color'] ",
						"focusin focusout keyup", delegate)
					.validateDelegate("[type='radio'], [type='checkbox'], select, option", "click", delegate);

				if (this.settings.invalidHandler) {
					$(this.currentForm).bind("invalid-form.validate", this.settings.invalidHandler);
				}
			},

			// http://docs.jquery.com/Plugins/Validation/Validator/form
			form: function() {
				this.checkForm();
				$.extend(this.submitted, this.errorMap);
				this.invalid = $.extend({}, this.errorMap);
				if (!this.valid()) {
					$(this.currentForm).triggerHandler("invalid-form", [this]);
				}
				this.showErrors();
				return this.valid();
			},

			checkForm: function() {
				this.prepareForm();
				for ( var i = 0, elements = (this.currentElements = this.elements()); elements[i]; i++ ) {
					this.check( elements[i] );
				}
				return this.valid();
			},

			// http://docs.jquery.com/Plugins/Validation/Validator/element
			element: function( element ) {
				element = this.validationTargetFor( this.clean( element ) );
				this.lastElement = element;
				this.prepareElement( element );
				this.currentElements = $(element);
				var result = this.check( element ) !== false;
				if (result) {
					delete this.invalid[element.name];
				} else {
					this.invalid[element.name] = true;
				}
				if ( !this.numberOfInvalids() ) {
					// Hide error containers on last error
					this.toHide = this.toHide.add( this.containers );
				}
				this.showErrors();
				return result;
			},

			// http://docs.jquery.com/Plugins/Validation/Validator/showErrors
			showErrors: function(errors) {
				if(errors) {
					// add items to error list and map
					$.extend( this.errorMap, errors );
					this.errorList = [];
					for ( var name in errors ) {
						this.errorList.push({
							message: errors[name],
							element: this.findByName(name)[0]
						});
					}
					// remove items from success list
					this.successList = $.grep( this.successList, function(element) {
						return !(element.name in errors);
					});
				}
				if (this.settings.showErrors) {
					this.settings.showErrors.call( this, this.errorMap, this.errorList );
				} else {
					this.defaultShowErrors();
				}
			},

			// http://docs.jquery.com/Plugins/Validation/Validator/resetForm
			resetForm: function() {
				if ( $.fn.resetForm ) {
					$( this.currentForm ).resetForm();
				}
				this.submitted = {};
				this.lastElement = null;
				this.prepareForm();
				this.hideErrors();
				this.elements().removeClass( this.settings.errorClass ).removeData( "previousValue" );
			},

			numberOfInvalids: function() {
				return this.objectLength(this.invalid);
			},

			objectLength: function( obj ) {
				var count = 0;
				for ( var i in obj ) {
					count++;
				}
				return count;
			},

			hideErrors: function() {
				this.addWrapper( this.toHide ).hide();
			},

			valid: function() {
				return this.size() === 0;
			},

			size: function() {
				return this.errorList.length;
			},

			focusInvalid: function() {
				if( this.settings.focusInvalid ) {
					try {
						$(this.findLastActive() || this.errorList.length && this.errorList[0].element || [])
						.filter(":visible")
						.focus()
						// manually trigger focusin event; without it, focusin handler isn't called, findLastActive won't have anything to find
						.trigger("focusin");
					} catch(e) {
						// ignore IE throwing errors when focusing hidden elements
					}
				}
			},

			findLastActive: function() {
				var lastActive = this.lastActive;
				return lastActive && $.grep(this.errorList, function(n) {
					return n.element.name === lastActive.name;
				}).length === 1 && lastActive;
			},

			elements: function() {
				var validator = this,
					rulesCache = {};

				// select all valid inputs inside the form (no submit or reset buttons)
				return $(this.currentForm)
				.find("input, select, textarea")
				.not(":submit, :reset, :image, [disabled]")
				.not( this.settings.ignore )
				.filter(function() {
					if ( !this.name && validator.settings.debug && window.console ) {
						console.error( "%o has no name assigned", this);
					}

					// select only the first element for each name, and only those with rules specified
					if ( this.name in rulesCache || !validator.objectLength($(this).rules()) ) {
						return false;
					}

					rulesCache[this.name] = true;
					return true;
				});
			},

			clean: function( selector ) {
				return $( selector )[0];
			},

			errors: function() {
				var errorClass = this.settings.errorClass.replace(' ', '.');
				return $( this.settings.errorElement + "." + errorClass, this.errorContext );
			},

			reset: function() {
				this.successList = [];
				this.errorList = [];
				this.errorMap = {};
				this.toShow = $([]);
				this.toHide = $([]);
				this.currentElements = $([]);
			},

			prepareForm: function() {
				this.reset();
				this.toHide = this.errors().add( this.containers );
			},

			prepareElement: function( element ) {
				this.reset();
				this.toHide = this.errorsFor(element);
			},

			elementValue: function( element ) {
				var type = $(element).attr('type'),
					val = $(element).val();

				if ( type === 'radio' || type === 'checkbox' ) {
					return $('input[name="' + $(element).attr('name') + '"]:checked').val();
				}

				if ( typeof val === 'string' ) {
					return val.replace(/\r/g, "");
				}
				return val;
			},

			check: function( element ) {
				element = this.validationTargetFor( this.clean( element ) );

				var rules = $(element).rules();
				var dependencyMismatch = false;
				var val = this.elementValue(element);
				var result;

				for (var method in rules ) {
					var rule = { method: method, parameters: rules[method] };
					try {

						result = $.validator.methods[method].call( this, val, element, rule.parameters );

						// if a method indicates that the field is optional and therefore valid,
						// don't mark it as valid when there are no other rules
						if ( result === "dependency-mismatch" ) {
							dependencyMismatch = true;
							continue;
						}
						dependencyMismatch = false;

						if ( result === "pending" ) {
							this.toHide = this.toHide.not( this.errorsFor(element) );
							return;
						}

						if( !result ) {
							this.formatAndAdd( element, rule );
							return false;
						}
					} catch(e) {
						if ( this.settings.debug && window.console ) {
							console.log("exception occured when checking element " + element.id + ", check the '" + rule.method + "' method", e);
						}
						throw e;
					}
				}
				if (dependencyMismatch) {
					return;
				}
				if ( this.objectLength(rules) ) {
					this.successList.push(element);
				}
				return true;
			},

			// return the custom message for the given element and validation method
			// specified in the element's "messages" metadata
			customMetaMessage: function(element, method) {
				if (!$.metadata) {
					return;
				}
				var meta = this.settings.meta ? $(element).metadata()[this.settings.meta] : $(element).metadata();
				return meta && meta.messages && meta.messages[method];
			},

			// return the custom message for the given element and validation method
			// specified in the element's HTML5 data attribute
			customDataMessage: function(element, method) {
				return $(element).data('msg-' + method.toLowerCase()) || (element.attributes && $(element).attr('data-msg-' + method.toLowerCase()));
			},

			// return the custom message for the given element name and validation method
			customMessage: function( name, method ) {
				var m = this.settings.messages[name];
				return m && (m.constructor === String ? m : m[method]);
			},

			// return the first defined argument, allowing empty strings
			findDefined: function() {
				for(var i = 0; i < arguments.length; i++) {
					if (arguments[i] !== undefined) {
						return arguments[i];
					}
				}
				return undefined;
			},

			defaultMessage: function( element, method) {
				return this.findDefined(
					this.customMessage( element.name, method ),
					this.customDataMessage( element, method ),
					this.customMetaMessage( element, method ),
					// title is never undefined, so handle empty string as undefined
					!this.settings.ignoreTitle && element.title || undefined,
					$.validator.messages[method],
					"<strong>Warning: No message defined for " + element.name + "</strong>"
				);
			},

			formatAndAdd: function( element, rule ) {
				var message = this.defaultMessage( element, rule.method ),
					theregex = /\$?\{(\d+)\}/g;
				if ( typeof message === "function" ) {
					message = message.call(this, rule.parameters, element);
				} else if (theregex.test(message)) {
					message = $.validator.format(message.replace(theregex, '{$1}'), rule.parameters);
				}
				this.errorList.push({
					message: message,
					element: element
				});

				this.errorMap[element.name] = message;
				this.submitted[element.name] = message;
			},

			addWrapper: function(toToggle) {
				if ( this.settings.wrapper ) {
					toToggle = toToggle.add( toToggle.parent( this.settings.wrapper ) );
				}
				return toToggle;
			},

			defaultShowErrors: function() {
				var i, elements;
				for ( i = 0; this.errorList[i]; i++ ) {
					var error = this.errorList[i];
					if ( this.settings.highlight ) {
						this.settings.highlight.call( this, error.element, this.settings.errorClass, this.settings.validClass );
					}
					this.showLabel( error.element, error.message );
				}
				if( this.errorList.length ) {
					this.toShow = this.toShow.add( this.containers );
				}
				if (this.settings.success) {
					for ( i = 0; this.successList[i]; i++ ) {
						this.showLabel( this.successList[i] );
					}
				}
				if (this.settings.unhighlight) {
					for ( i = 0, elements = this.validElements(); elements[i]; i++ ) {
						this.settings.unhighlight.call( this, elements[i], this.settings.errorClass, this.settings.validClass );
					}
				}
				this.toHide = this.toHide.not( this.toShow );
				this.hideErrors();
				this.addWrapper( this.toShow ).show();
			},

			validElements: function() {
				return this.currentElements.not(this.invalidElements());
			},

			invalidElements: function() {
				return $(this.errorList).map(function() {
					return this.element;
				});
			},

			showLabel: function(element, message) {
				var label = this.errorsFor( element );
				if ( label.length ) {
					// refresh error/success class
					label.removeClass( this.settings.validClass ).addClass( this.settings.errorClass );

					// check if we have a generated label, replace the message then
					if ( label.attr("generated") ) {
						label.html(message);
					}
				} else {
					// create label
					label = $("<" + this.settings.errorElement + "/>")
						.attr({"for":  this.idOrName(element), generated: true})
						.addClass(this.settings.errorClass)
						.html(message || "");
					if ( this.settings.wrapper ) {
						// make sure the element is visible, even in IE
						// actually showing the wrapped element is handled elsewhere
						label = label.hide().show().wrap("<" + this.settings.wrapper + "/>").parent();
					}
					if ( !this.labelContainer.append(label).length ) {
						if ( this.settings.errorPlacement ) {
							this.settings.errorPlacement(label, $(element) );
						} else {
						label.insertAfter(element);
						}
					}
				}
				if ( !message && this.settings.success ) {
					label.text("");
					if ( typeof this.settings.success === "string" ) {
						label.addClass( this.settings.success );
					} else {
						this.settings.success( label, element );
					}
				}
				this.toShow = this.toShow.add(label);
			},

			errorsFor: function(element) {
				var name = this.idOrName(element);
				return this.errors().filter(function() {
					return $(this).attr('for') === name;
				});
			},

			idOrName: function(element) {
				return this.groups[element.name] || (this.checkable(element) ? element.name : element.id || element.name);
			},

			validationTargetFor: function(element) {
				// if radio/checkbox, validate first element in group instead
				if (this.checkable(element)) {
					element = this.findByName( element.name ).not(this.settings.ignore)[0];
				}
				return element;
			},

			checkable: function( element ) {
				return (/radio|checkbox/i).test(element.type);
			},

			findByName: function( name ) {
				return $(this.currentForm).find('[name="' + name + '"]');
			},

			getLength: function(value, element) {
				switch( element.nodeName.toLowerCase() ) {
				case 'select':
					return $("option:selected", element).length;
				case 'input':
					if( this.checkable( element) ) {
						return this.findByName(element.name).filter(':checked').length;
					}
				}
				return value.length;
			},

			depend: function(param, element) {
				return this.dependTypes[typeof param] ? this.dependTypes[typeof param](param, element) : true;
			},

			dependTypes: {
				"boolean": function(param, element) {
					return param;
				},
				"string": function(param, element) {
					return !!$(param, element.form).length;
				},
				"function": function(param, element) {
					return param(element);
				}
			},

			optional: function(element) {
				var val = this.elementValue(element);
				return !$.validator.methods.required.call(this, val, element) && "dependency-mismatch";
			},

			startRequest: function(element) {
				if (!this.pending[element.name]) {
					this.pendingRequest++;
					this.pending[element.name] = true;
				}
			},

			stopRequest: function(element, valid) {
				this.pendingRequest--;
				// sometimes synchronization fails, make sure pendingRequest is never < 0
				if (this.pendingRequest < 0) {
					this.pendingRequest = 0;
				}
				delete this.pending[element.name];
				if ( valid && this.pendingRequest === 0 && this.formSubmitted && this.form() ) {
					$(this.currentForm).submit();
					this.formSubmitted = false;
				} else if (!valid && this.pendingRequest === 0 && this.formSubmitted) {
					$(this.currentForm).triggerHandler("invalid-form", [this]);
					this.formSubmitted = false;
				}
			},

			previousValue: function(element) {
				return $.data(element, "previousValue") || $.data(element, "previousValue", {
					old: null,
					valid: true,
					message: this.defaultMessage( element, "remote" )
				});
			}

		},

		classRuleSettings: {
			required: {required: true},
			email: {email: true},
			url: {url: true},
			date: {date: true},
			dateISO: {dateISO: true},
			number: {number: true},
			digits: {digits: true},
			creditcard: {creditcard: true}
		},

		addClassRules: function(className, rules) {
			if ( className.constructor === String ) {
				this.classRuleSettings[className] = rules;
			} else {
				$.extend(this.classRuleSettings, className);
			}
		},

		classRules: function(element) {
			var rules = {};
			var classes = $(element).attr('class');
			if ( classes ) {
				$.each(classes.split(' '), function() {
					if (this in $.validator.classRuleSettings) {
						$.extend(rules, $.validator.classRuleSettings[this]);
					}
				});
			}
			return rules;
		},

		attributeRules: function(element) {
			var rules = {};
			var $element = $(element);

			for (var method in $.validator.methods) {
				var value;

				// support for <input required> in both html5 and older browsers
				if (method === 'required') {
					value = $element.get(0).getAttribute(method);
					// Some browsers return an empty string for the required attribute
					// and non-HTML5 browsers might have required="" markup
					if (value === "") {
						value = true;
					}
					// force non-HTML5 browsers to return bool
					value = !!value;
				} else {
					value = $element.attr(method);
				}

				if (value) {
					rules[method] = value;
				} else if ($element[0].getAttribute("type") === method) {
					rules[method] = true;
				}
			}

			// maxlength may be returned as -1, 2147483647 (IE) and 524288 (safari) for text inputs
			if (rules.maxlength && /-1|2147483647|524288/.test(rules.maxlength)) {
				delete rules.maxlength;
			}

			return rules;
		},

		metadataRules: function(element) {
			if (!$.metadata) {
				return {};
			}

			var meta = $.data(element.form, 'validator').settings.meta;
			return meta ?
				$(element).metadata()[meta] :
				$(element).metadata();
		},

		staticRules: function(element) {
			var rules = {};
			var validator = $.data(element.form, 'validator');
			if (validator.settings.rules) {
				rules = $.validator.normalizeRule(validator.settings.rules[element.name]) || {};
			}
			return rules;
		},

		normalizeRules: function(rules, element) {
			// handle dependency check
			$.each(rules, function(prop, val) {
				// ignore rule when param is explicitly false, eg. required:false
				if (val === false) {
					delete rules[prop];
					return;
				}
				if (val.param || val.depends) {
					var keepRule = true;
					switch (typeof val.depends) {
						case "string":
							keepRule = !!$(val.depends, element.form).length;
							break;
						case "function":
							keepRule = val.depends.call(element, element);
							break;
					}
					if (keepRule) {
						rules[prop] = val.param !== undefined ? val.param : true;
					} else {
						delete rules[prop];
					}
				}
			});

			// evaluate parameters
			$.each(rules, function(rule, parameter) {
				rules[rule] = $.isFunction(parameter) ? parameter(element) : parameter;
			});

			// clean number parameters
			$.each(['minlength', 'maxlength', 'min', 'max'], function() {
				if (rules[this]) {
					rules[this] = Number(rules[this]);
				}
			});
			$.each(['rangelength', 'range'], function() {
				if (rules[this]) {
					rules[this] = [Number(rules[this][0]), Number(rules[this][1])];
				}
			});

			if ($.validator.autoCreateRanges) {
				// auto-create ranges
				if (rules.min && rules.max) {
					rules.range = [rules.min, rules.max];
					delete rules.min;
					delete rules.max;
				}
				if (rules.minlength && rules.maxlength) {
					rules.rangelength = [rules.minlength, rules.maxlength];
					delete rules.minlength;
					delete rules.maxlength;
				}
			}

			// To support custom messages in metadata ignore rule methods titled "messages"
			if (rules.messages) {
				delete rules.messages;
			}

			return rules;
		},

		// Converts a simple string to a {string: true} rule, e.g., "required" to {required:true}
		normalizeRule: function(data) {
			if( typeof data === "string" ) {
				var transformed = {};
				$.each(data.split(/\s/), function() {
					transformed[this] = true;
				});
				data = transformed;
			}
			return data;
		},

		// http://docs.jquery.com/Plugins/Validation/Validator/addMethod
		addMethod: function(name, method, message) {
			$.validator.methods[name] = method;
			$.validator.messages[name] = message !== undefined ? message : $.validator.messages[name];
			if (method.length < 3) {
				$.validator.addClassRules(name, $.validator.normalizeRule(name));
			}
		},

		methods: {

			// http://docs.jquery.com/Plugins/Validation/Methods/required
			required: function(value, element, param) {
				// check if dependency is met
				if ( !this.depend(param, element) ) {
					return "dependency-mismatch";
				}
				if ( element.nodeName.toLowerCase() === "select" ) {
					// could be an array for select-multiple or a string, both are fine this way
					var val = $(element).val();
					return val && val.length > 0;
				}
				if ( this.checkable(element) ) {
					return this.getLength(value, element) > 0;
				}
				return $.trim(value).length > 0;
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/remote
			remote: function(value, element, param) {
				if ( this.optional(element) ) {
					return "dependency-mismatch";
				}

				var previous = this.previousValue(element);
				if (!this.settings.messages[element.name] ) {
					this.settings.messages[element.name] = {};
				}
				previous.originalMessage = this.settings.messages[element.name].remote;
				this.settings.messages[element.name].remote = previous.message;

				param = typeof param === "string" && {url:param} || param;

				if ( this.pending[element.name] ) {
					return "pending";
				}
				if ( previous.old === value ) {
					return previous.valid;
				}

				previous.old = value;
				var validator = this;
				this.startRequest(element);
				var data = {};
				data[element.name] = value;
				$.ajax($.extend(true, {
					url: param,
					mode: "abort",
					port: "validate" + element.name,
					dataType: "json",
					data: data,
					success: function(response) {
						validator.settings.messages[element.name].remote = previous.originalMessage;
						var valid = response === true || response === "true";
						if ( valid ) {
							var submitted = validator.formSubmitted;
							validator.prepareElement(element);
							validator.formSubmitted = submitted;
							validator.successList.push(element);
							delete validator.invalid[element.name];
							validator.showErrors();
						} else {
							var errors = {};
							var message = response || validator.defaultMessage( element, "remote" );
							errors[element.name] = previous.message = $.isFunction(message) ? message(value) : message;
							validator.invalid[element.name] = true;
							validator.showErrors(errors);
						}
						previous.valid = valid;
						validator.stopRequest(element, valid);
					}
				}, param));
				return "pending";
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/minlength
			minlength: function(value, element, param) {
				var length = $.isArray( value ) ? value.length : this.getLength($.trim(value), element);
				return this.optional(element) || length >= param;
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/maxlength
			maxlength: function(value, element, param) {
				var length = $.isArray( value ) ? value.length : this.getLength($.trim(value), element);
				return this.optional(element) || length <= param;
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/rangelength
			rangelength: function(value, element, param) {
				var length = $.isArray( value ) ? value.length : this.getLength($.trim(value), element);
				return this.optional(element) || ( length >= param[0] && length <= param[1] );
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/min
			min: function( value, element, param ) {
				return this.optional(element) || value >= param;
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/max
			max: function( value, element, param ) {
				return this.optional(element) || value <= param;
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/range
			range: function( value, element, param ) {
				return this.optional(element) || ( value >= param[0] && value <= param[1] );
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/email
			email: function(value, element) {
				// contributed by Scott Gonzalez: http://projects.scottsplayground.com/email_address_validation/
				return this.optional(element) || /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i.test(value);
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/url
			url: function(value, element) {
				// contributed by Scott Gonzalez: http://projects.scottsplayground.com/iri/
				return this.optional(element) || /^(https?|ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(value);
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/date
			date: function(value, element) {
				return this.optional(element) || !/Invalid|NaN/.test(new Date(value));
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/dateISO
			dateISO: function(value, element) {
				return this.optional(element) || /^\d{4}[\/\-]\d{1,2}[\/\-]\d{1,2}$/.test(value);
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/number
			number: function(value, element) {
				return this.optional(element) || /^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/.test(value);
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/digits
			digits: function(value, element) {
				return this.optional(element) || /^\d+$/.test(value);
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/creditcard
			// based on http://en.wikipedia.org/wiki/Luhn
			creditcard: function(value, element) {
				if ( this.optional(element) ) {
					return "dependency-mismatch";
				}
				// accept only spaces, digits and dashes
				if (/[^0-9 \-]+/.test(value)) {
					return false;
				}
				var nCheck = 0,
					nDigit = 0,
					bEven = false;

				value = value.replace(/\D/g, "");

				for (var n = value.length - 1; n >= 0; n--) {
					var cDigit = value.charAt(n);
					nDigit = parseInt(cDigit, 10);
					if (bEven) {
						if ((nDigit *= 2) > 9) {
							nDigit -= 9;
						}
					}
					nCheck += nDigit;
					bEven = !bEven;
				}

				return (nCheck % 10) === 0;
			},

			// http://docs.jquery.com/Plugins/Validation/Methods/equalTo
			equalTo: function(value, element, param) {
				// bind to the blur event of the target in order to revalidate whenever the target field is updated
				// TODO find a way to bind the event just once, avoiding the unbind-rebind overhead
				var target = $(param);
				if (this.settings.onfocusout) {
					target.unbind(".validate-equalTo").bind("blur.validate-equalTo", function() {
						$(element).valid();
					});
				}
				return value === target.val();
			}

		}

	});

	// deprecated, use $.validator.format instead
	$.format = $.validator.format;

	}(jQuery));

	// ajax mode: abort
	// usage: $.ajax({ mode: "abort"[, port: "uniqueport"]});
	// if mode:"abort" is used, the previous request on that port (port can be undefined) is aborted via XMLHttpRequest.abort()
	(function($) {
		var pendingRequests = {};
		// Use a prefilter if available (1.5+)
		if ( $.ajaxPrefilter ) {
			$.ajaxPrefilter(function(settings, _, xhr) {
				var port = settings.port;
				if (settings.mode === "abort") {
					if ( pendingRequests[port] ) {
						pendingRequests[port].abort();
					}
					pendingRequests[port] = xhr;
				}
			});
		} else {
			// Proxy ajax
			var ajax = $.ajax;
			$.ajax = function(settings) {
				var mode = ( "mode" in settings ? settings : $.ajaxSettings ).mode,
					port = ( "port" in settings ? settings : $.ajaxSettings ).port;
				if (mode === "abort") {
					if ( pendingRequests[port] ) {
						pendingRequests[port].abort();
					}
					return (pendingRequests[port] = ajax.apply(this, arguments));
				}
				return ajax.apply(this, arguments);
			};
		}
	}(jQuery));

	// provides cross-browser focusin and focusout events
	// IE has native support, in other browsers, use event caputuring (neither bubbles)

	// provides delegate(type: String, delegate: Selector, handler: Callback) plugin for easier event delegation
	// handler is only called when $(event.target).is(delegate), in the scope of the jquery-object for event.target
	(function($) {
		// only implement if not provided by jQuery core (since 1.4)
		// TODO verify if jQuery 1.4's implementation is compatible with older jQuery special-event APIs
		if (!jQuery.event.special.focusin && !jQuery.event.special.focusout && document.addEventListener) {
			$.each({
				focus: 'focusin',
				blur: 'focusout'
			}, function( original, fix ){
				$.event.special[fix] = {
					setup:function() {
						this.addEventListener( original, handler, true );
					},
					teardown:function() {
						this.removeEventListener( original, handler, true );
					},
					handler: function(e) {
						var args = arguments;
						args[0] = $.event.fix(e);
						args[0].type = fix;
						return $.event.handle.apply(this, args);
					}
				};
				function handler(e) {
					e = $.event.fix(e);
					e.type = fix;
					return $.event.handle.call(this, e);
				}
			});
		}
		$.extend($.fn, {
			validateDelegate: function(delegate, type, handler) {
				return this.bind(type, function(event) {
					var target = $(event.target);
					if (target.is(delegate)) {
						return handler.apply(target, arguments);
					}
				});
			}
		});
	}(jQuery));
	
	/*
	 * Metadata - jQuery plugin for parsing metadata from elements
	 *
	 * Copyright (c) 2006 John Resig, Yehuda Katz, Jörn Zaefferer, Paul McLanahan
	 *
	 * Dual licensed under the MIT and GPL licenses:
	 *   http://www.opensource.org/licenses/mit-license.php
	 *   http://www.gnu.org/licenses/gpl.html
	 *
	 */

	/**
	 * Sets the type of metadata to use. Metadata is encoded in JSON, and each property
	 * in the JSON will become a property of the element itself.
	 *
	 * There are three supported types of metadata storage:
	 *
	 *   attr:  Inside an attribute. The name parameter indicates *which* attribute.
	 *          
	 *   class: Inside the class attribute, wrapped in curly braces: { }
	 *   
	 *   elem:  Inside a child element (e.g. a script tag). The
	 *          name parameter indicates *which* element.
	 *          
	 * The metadata for an element is loaded the first time the element is accessed via jQuery.
	 *
	 * As a result, you can define the metadata type, use $(expr) to load the metadata into the elements
	 * matched by expr, then redefine the metadata type and run another $(expr) for other elements.
	 * 
	 * @name $.metadata.setType
	 *
	 * @example <p id="one" class="some_class {item_id: 1, item_label: 'Label'}">This is a p</p>
	 * @before $.metadata.setType("class")
	 * @after $("#one").metadata().item_id == 1; $("#one").metadata().item_label == "Label"
	 * @desc Reads metadata from the class attribute
	 * 
	 * @example <p id="one" class="some_class" data="{item_id: 1, item_label: 'Label'}">This is a p</p>
	 * @before $.metadata.setType("attr", "data")
	 * @after $("#one").metadata().item_id == 1; $("#one").metadata().item_label == "Label"
	 * @desc Reads metadata from a "data" attribute
	 * 
	 * @example <p id="one" class="some_class"><script>{item_id: 1, item_label: 'Label'}</script>This is a p</p>
	 * @before $.metadata.setType("elem", "script")
	 * @after $("#one").metadata().item_id == 1; $("#one").metadata().item_label == "Label"
	 * @desc Reads metadata from a nested script element
	 * 
	 * @param String type The encoding type
	 * @param String name The name of the attribute to be used to get metadata (optional)
	 * @cat Plugins/Metadata
	 * @descr Sets the type of encoding to be used when loading metadata for the first time
	 * @type undefined
	 * @see metadata()
	 */

	(function($) {

	$.extend({
		metadata : {
			defaults : {
				type: 'class',
				name: 'metadata',
				cre: /({.*})/,
				single: 'metadata'
			},
			setType: function( type, name ){
				this.defaults.type = type;
				this.defaults.name = name;
			},
			get: function( elem, opts ){
				var settings = $.extend({},this.defaults,opts);
				// check for empty string in single property
				if ( !settings.single.length ) settings.single = 'metadata';
				
				var data = $.data(elem, settings.single);
				// returned cached data if it already exists
				if ( data ) return data;
				
				data = "{}";
				
				if ( settings.type == "class" ) {
					var m = settings.cre.exec( elem.className );
					if ( m )
						data = m[1];
				} else if ( settings.type == "elem" ) {
					if( !elem.getElementsByTagName )
						return undefined;
					var e = elem.getElementsByTagName(settings.name);
					if ( e.length )
						data = $.trim(e[0].innerHTML);
				} else if ( elem.getAttribute != undefined ) {
					var attr = elem.getAttribute( settings.name );
					if ( attr )
						data = attr;
				}
				
				if ( data.indexOf( '{' ) <0 )
				data = "{" + data + "}";
				
				data = eval("(" + data + ")");
				
				$.data( elem, settings.single, data );
				return data;
			}
		}
	});

	/**
	 * Returns the metadata object for the first member of the jQuery object.
	 *
	 * @name metadata
	 * @descr Returns element's metadata object
	 * @param Object opts An object contianing settings to override the defaults
	 * @type jQuery
	 * @cat Plugins/Metadata
	 */
	$.fn.metadata = function( opts ){
		return $.metadata.get( this[0], opts );
	};

	})(jQuery);
	
	
	
	/*! iFrame Resizer (iframeSizer.min.js ) - v2.8.1 - 2015-01-20
	 *  Desc: Force cross domain iframes to size to content.
	 *  Requires: iframeResizer.contentWindow.min.js to be loaded into the target frame.
	 *  Copyright: (c) 2015 David J. Bradshaw - dave@bradshaw.net
	 *  License: MIT
	 */

	!function(){"use strict";function a(a,b,c){"addEventListener"in window?a.addEventListener(b,c,!1):"attachEvent"in window&&a.attachEvent("on"+b,c)}function b(){var a,b=["moz","webkit","o","ms"];for(a=0;a<b.length&&!A;a+=1)A=window[b[a]+"RequestAnimationFrame"];A||e(" RequestAnimationFrame not supported")}function c(){var a="Host page";return window.top!==window.self&&(a=window.parentIFrame?window.parentIFrame.getId():"Nested host page"),a}function d(a){return w+"["+c()+"]"+a}function e(a){C.log&&"object"==typeof window.console&&console.log(d(a))}function f(a){"object"==typeof window.console&&console.warn(d(a))}function g(a){function b(){function a(){k(F),i(),C.resizedCallback(F)}g("Height"),g("Width"),l(a,F,"resetPage")}function c(a){var b=a.id;e(" Removing iFrame: "+b),a.parentNode.removeChild(a),C.closedCallback(b),e(" --")}function d(){var a=E.substr(x).split(":");return{iframe:document.getElementById(a[0]),id:a[0],height:a[1],width:a[2],type:a[3]}}function g(a){var b=Number(C["max"+a]),c=Number(C["min"+a]),d=a.toLowerCase(),f=Number(F[d]);if(c>b)throw new Error("Value for min"+a+" can not be greater than max"+a);e(" Checking "+d+" is in range "+c+"-"+b),c>f&&(f=c,e(" Set "+d+" to min value")),f>b&&(f=b,e(" Set "+d+" to max value")),F[d]=""+f}function m(){var b=a.origin,c=F.iframe.src.split("/").slice(0,3).join("/");if(C.checkOrigin&&(e(" Checking connection is from: "+c),""+b!="null"&&b!==c))throw new Error("Unexpected message received from: "+b+" for "+F.iframe.id+". Message was: "+a.data+". This error can be disabled by adding the checkOrigin: false option.");return!0}function n(){return w===(""+E).substr(0,x)}function o(){var a=F.type in{"true":1,"false":1};return a&&e(" Ignoring init message from meta parent page"),a}function p(a){return E.substr(E.indexOf(":")+v+a)}function q(a){e(" MessageCallback passed: {iframe: "+F.iframe.id+", message: "+a+"}"),C.messageCallback({iframe:F.iframe,message:JSON.parse(a)}),e(" --")}function r(){if(null===F.iframe)throw new Error("iFrame ("+F.id+") does not exist on "+y);return!0}function s(a){var b=a.getBoundingClientRect();return h(),{x:parseInt(b.left,10)+parseInt(z.x,10),y:parseInt(b.top,10)+parseInt(z.y,10)}}function u(a){function b(){z=g,A(),e(" --")}function c(){return{x:Number(F.width)+d.x,y:Number(F.height)+d.y}}var d=a?s(F.iframe):{x:0,y:0},g=c();e(" Reposition requested from iFrame (offset x:"+d.x+" y:"+d.y+")"),window.top!==window.self?window.parentIFrame?a?parentIFrame.scrollToOffset(g.x,g.y):parentIFrame.scrollTo(F.width,F.height):f(" Unable to scroll to requested position, window.parentIFrame not found"):b()}function A(){!1!==C.scrollCallback(z)&&i()}function B(a){function b(b){var c=s(b);e(" Moving to in page link ("+a+") at x: "+c.x+" y: "+c.y),z={x:c.x,y:c.y},A(),e(" --")}var c=document.querySelector(a)||document.querySelector('[name="'+a.substr(1,999)+'"]');window.top!==window.self?window.parentIFrame?parentIFrame.moveToAnchor(a):f(" In page link "+a+" not found and window.parentIFrame not found"):null!==c?b(c):f(" In page link "+a+" not found")}function D(){switch(F.type){case"close":c(F.iframe),C.resizedCallback(F);break;case"message":q(p(6));break;case"scrollTo":u(!1);break;case"scrollToOffset":u(!0);break;case"inPageLink":B(p(9));break;case"reset":j(F);break;case"init":b(),C.initCallback(F.iframe);break;default:b()}}var E=a.data,F={};n()&&(e(" Received: "+E),F=d(),!o()&&r()&&m()&&(D(),t=!1))}function h(){null===z&&(z={x:void 0!==window.pageXOffset?window.pageXOffset:document.documentElement.scrollLeft,y:void 0!==window.pageYOffset?window.pageYOffset:document.documentElement.scrollTop},e(" Get page position: "+z.x+","+z.y))}function i(){null!==z&&(window.scrollTo(z.x,z.y),e(" Set page position: "+z.x+","+z.y),z=null)}function j(a){function b(){k(a),m("reset","reset",a.iframe)}e(" Size reset requested by "+("init"===a.type?"host page":"iFrame")),h(),l(b,a,"init")}function k(a){function b(b){a.iframe.style[b]=a[b]+"px",e(" IFrame ("+a.iframe.id+") "+b+" set to "+a[b]+"px")}C.sizeHeight&&b("height"),C.sizeWidth&&b("width")}function l(a,b,c){c!==b.type&&A?(e(" Requesting animation frame"),A(a)):a()}function m(a,b,c){e("["+a+"] Sending msg to iframe ("+b+")"),c.contentWindow.postMessage(w+b,"*")}function n(){function b(){function a(a){1/0!==C[a]&&0!==C[a]&&(i.style[a]=C[a]+"px",e(" Set "+a+" = "+C[a]+"px"))}a("maxHeight"),a("minHeight"),a("maxWidth"),a("minWidth")}function c(a){return""===a&&(i.id=a="iFrameResizer"+s++,e(" Added missing iframe ID: "+a+" ("+i.src+")")),a}function d(){e(" IFrame scrolling "+(C.scrolling?"enabled":"disabled")+" for "+k),i.style.overflow=!1===C.scrolling?"hidden":"auto",i.scrolling=!1===C.scrolling?"no":"yes"}function f(){("number"==typeof C.bodyMargin||"0"===C.bodyMargin)&&(C.bodyMarginV1=C.bodyMargin,C.bodyMargin=""+C.bodyMargin+"px")}function g(){return k+":"+C.bodyMarginV1+":"+C.sizeWidth+":"+C.log+":"+C.interval+":"+C.enablePublicMethods+":"+C.autoResize+":"+C.bodyMargin+":"+C.heightCalculationMethod+":"+C.bodyBackground+":"+C.bodyPadding+":"+C.tolerance}function h(b){a(i,"load",function(){var a=t;m("iFrame.onload",b,i),!a&&C.heightCalculationMethod in B&&j({iframe:i,height:0,width:0,type:"init"})}),m("init",b,i)}var i=this,k=c(i.id);d(),b(),f(),h(g())}function o(a){if("object"!=typeof a)throw new TypeError("Options is not an object.")}function p(a){a=a||{},o(a);for(var b in D)D.hasOwnProperty(b)&&(C[b]=a.hasOwnProperty(b)?a[b]:D[b])}function q(){function a(a){if(a.tagName&&"IFRAME"!==a.tagName.toUpperCase())throw new TypeError("Expected <IFRAME> tag, found <"+a.tagName+">.");n.call(a)}return function(b,c){switch(p(b),typeof c){case"undefined":case"string":Array.prototype.forEach.call(document.querySelectorAll(c||"iframe"),a);break;case"object":e(" Attaching to passed in iFrame object"),a(c);break;default:throw new TypeError("Unexpected data type ("+typeof c+").")}}}function r(a){a.fn.iFrameResize=function(a){return p(a),this.filter("iframe").each(n).end()}}var s=0,t=!0,u="message",v=u.length,w="[iFrameSizer]",x=w.length,y="",z=null,A=window.requestAnimationFrame,B={max:1,scroll:1,bodyScroll:1,documentElementScroll:1},C={},D={autoResize:!0,bodyBackground:null,bodyMargin:null,bodyMarginV1:8,bodyPadding:null,checkOrigin:!0,enablePublicMethods:!1,heightCalculationMethod:"offset",interval:32,log:!1,maxHeight:1/0,maxWidth:1/0,minHeight:0,minWidth:0,scrolling:!1,sizeHeight:!0,sizeWidth:!1,tolerance:0,closedCallback:function(){},initCallback:function(){},messageCallback:function(){},resizedCallback:function(){},scrollCallback:function(){return!0}};b(),a(window,"message",g),window.jQuery&&r(jQuery),"function"==typeof define&&define.amd?define([],q):"object"==typeof exports?module.exports=q():window.iFrameResize=q()}();
	//# sourceMappingURL=iframeResizer.map
	
	