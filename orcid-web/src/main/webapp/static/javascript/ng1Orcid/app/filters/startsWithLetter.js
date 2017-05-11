// used in alphabetical filter on /members and /consortia
angular.module('orcidApp').filter('startsWithLetter', function() {
    return function(items, letter) {

        var filtered = [];
        var letterMatch = new RegExp(letter, 'i');
        var item = null;
        for (var i = 0; i < items.length; i++) {
          item = items[i];
          if (letterMatch.test(item.name.substring(0, 1))) {
            filtered.push(item);
          }
        }
        return filtered;
      };
    });

