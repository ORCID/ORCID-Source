# i18n Support

This directory contains the java properties files needed for i18n support. To
 add additional languages simply add a property file with the proper extension
 containing translations. You can read more about java for Locale codes 
 [here](http://docs.oracle.com/javase/1.5.0/docs/api/java/util/Locale.html).
 
 Additionally for fun and testing we support Orc/Troll the language of the 
 middle earth.
 
 
# Changing from the default locale
To change from the default locale simply hit the following /lang.json?lang=[language code]. For example
production in Orc would be [https://orcid.org/lang.json?lang=orc](https://orcid.org/lang.json?lang=orc).
Hitting URL will write a cookie called locale and also provide a json representation of the 
messages property file.
