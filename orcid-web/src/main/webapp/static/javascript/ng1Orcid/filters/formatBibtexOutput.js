orcidNgModule.filter('formatBibtexOutput', function () {
    return function (text) {
        var str = text.replace(/[\-?_?]/, ' ');
        return str.toUpperCase();
    };
});