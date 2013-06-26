# i18n Support

This directory contains the java properties files needed for i18n support. To
 add additional languages simply add a property file with the proper extension
 containing translations. You can read more about java for Locale codes 
 [here](http://www.oracle.com/technetwork/java/javase/locales-137662.html).
 
 Additionally for fun and testing we support Orc/Troll the language of the 
 middle earth.

# Properties file format 
The file is organized into key value pairs. Keys (the part before the equal sign) should 
only appear in the file once. Each file should have the same set of keys. Wikipedia has 
general good [overview](http://en.wikipedia.org/wiki/.properties) of properties files.

Also at the top of each file under the section "Add keys that need translating here:" 
you can list keys the appear later in the file that need translation or re-translation. 
Please remember to add a "#" at the beginning of each line. This allow us to past english
into non-english files and have backfill translations. When a translation is backfilled
please remove the key from the "Add keys that need translating here:" section.

# Changing from the default locale in javascript
To change the locale for registry, use a this relative link: 
/lang.json?lang=[language code]. For example production in Orc would be 
[https://orcid.org/lang.json?lang=orc](https://orcid.org/lang.json?lang=orc). Hitting URL 
will write a cookie called locale and also provide a json representation of the messages 
property file. 

# Changing the default locale by URL
An alternative way of switching langages on the registry pages is by adding 
"?lang=[language code]" to the page url. For example https://orcid.org/register?lang=orc

# Languages

<table>
<tr>
	<th>Language</th>
	<th>Code</th>
	<th>Release state</th>
</tr>
<tr>
	<td>French</td>
	<td>fr</td>
	<td>pre-release alpha</td>
</tr>
<tr>
	<td>English</td>
	<td>en</td>
	<td>Supported in Production</td>
</tr>
<tr>
	<td>Orc/Troll</td>
	<td>orc</td>
	<td>used for testing/fun</td>
</tr>
<tr>
	<td>Spanish</td>
	<td>es</td>
	<td>pre-release alpha</td>
</tr>
<tr>
	<td>Simplified Chinese</td>
	<td>zh_CN</td>
	<td>pre-release alpha</td>
</tr>
<tr>
	<td>Traditional Chinese</td>
	<td>zh_TW</td>
	<td>pre-release alpha</td>
</tr>
</table>

