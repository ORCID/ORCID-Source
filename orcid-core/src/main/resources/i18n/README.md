# i18n Translation Process

This directory contains java properties files needed for i18n support. 

For each resource (api, email, messages, javascript, identifiers), a separate properties file for each language, named with the corresponding [locale code](http://www.oracle.com/technetwork/java/javase/locales-137662.html), contains translations. Additionally, we use codes xx, lr and rl for testing.

In most cases, only en, xx, lr, and rl files should be edited directly. We use the following tools to manage files for other lanuages:

- [**Transifex**](https://www.transifex.com) translation managment tool provides a visual interface and management tool for translators, reviewers, and translation project managers
- [**TXGH**](https://github.com/transifex/txgh) application synchronizes Github and Transifex using the APIs for those services. TXGH runs on an internal ORCID server and is deployed via Puppet. It can also be run on Vagrant for local development - see [ORCID/registry_vagrant](https://github.com/ORCID/registry_vagrant) 

## Roles
- **Developers** ORCID Development team
- **Transifex Project Maintainer** ORICD staff member responsible for managing translation workflow
- **Translators** Vendors/community members who provide translations
- **Reviewers** Trusted community members who review translations provided by others

## Workflows
- [Add new strings](#add-new-strings)
- [Update existing string(s) - English only OR English + other languages](#update-existing-strings-english-only)
- [Update existing string(s) - non-English only](#update-existing-strings-non-english-only)
- [Add new language](#add-new-language)
- [Add a resource](#add-resource)
- [Remove a resource](#remove-resource)

## <a id="add-new-strings"></a>Add new strings
### Developers
New properties are added to English, \_xx, \_lr, and \_rl files only - files for other languages are updated via Transifex. **Do not edit properties files for other languages!**

#### api\_\*, javascript\_\*, or messages\_\*.properties
Use the Jython script in add\_keys.jy to add a new property to \*\_en.properties, \*\_xx.properties, \*\_lr.properties, and \*\_rl.properties. For \_xx, \_lr, and \_rl files, the script sets the value for new properties to X, LR, or RL.

1. Add the “Translation” label to the related project card on Trello
2. Change to the directory containing the script

        cd ~/git/ORCID-Source/orcid-core/src/main/resources/i18n

3. Run the script with correct options

     -p  file prefix (api, messages or javascript)
     -k  new property key
     -v  new property value 

        jython add_keys.jy -k "new.property.key" -v "new property value" -p "messages"

4. Commit and push the changes to remote

        git commit -m "message text"
        git push origin [branch name]

#### email\_\*.properties
The script listed above does not work on the email\_*.properties file due to the extensive commenting throughout those files. Edit email\_\*.properties manually.

1. Add the “Translation” label to the related project card on Trello
2. Add new keys/values to email_en.properties
3. Add new keys to email\_xx.properties, email\_lr.properties an email\_rl.properties and set the value for each key to X, LR or RL.
4. Commit and push the changes to remote

        git commit -m "message text"
        git push origin [branch name]

When changes to any properties files are merged into master, new strings will be pulled into Transifex automatically. If Github deployment was not successful, translated property files can be pushed from Github to Transifex manually. See [Push/Pull to/from Transifex manually](#pushpull-tofrom-transifex-manually)

### Transifex Project Maintainer
#### Configure Transifex notifications (first time only)
Transifex can notify you when developers add/update strings. To configure nofications, see http://docs.transifex.com/faq/#6-what-kind-of-notifications-can-i-get-for-the-projects-i-translate

#### Verify strings to be translated
When you are notified (via Transifex) that strings have been added/updated:
1. Visit https://www.transifex.com/orcid-inc-1/registry
2. Select a language with untranslated strings
3. Select a resource with untranslated strings, then click Translate
4. Select Untranslated from the top menu
5. Check the English version of the untranslated strings (left side of the screen) to ensure they appear correctly
6. Repeat for each resource/language

#### Translate strings
When a critical mass of untranslated strings accummulates or translation is needed in order to release a feature:
1. Add any new translators to Transifex - see [Invite people to a team](http://docs.transifex.com/introduction/managers/#invite-people-to-a-team)
2. Contact translators and request that they log into Transifex and translate strings marked as 'Untranslated'. See [Using the Transifex Web Editor](http://docs.transifex.com/tutorials/txeditor/) 

#### Review translated strings
After translation is complete, strings _must_ be marked as reviewed in Transifex before deployment. 'Reviewed' status in Transifex triggers automatic deployment of translation code to Github.

- If translation was completed by a community translator, review must be completed by another trusted community member. 
- If translation was completed by a vendor, review can be completed by ORCID staff.

1. Add any new reviewers to Transifex - see [Invite people to a team](http://docs.transifex.com/introduction/managers/#invite-people-to-a-team)
2. When translations are complete, contact reviewers and request that they log into Transifex and review strings marked as 'Unreviewed'. See [Using the Transifex Web Editor](http://docs.transifex.com/tutorials/txeditor/) 

When review status for a resource reaches 100% in a given language, the resource properties file for that language is pushed to the master branch of [ORCID-Source/orcid-core/src/main/resources/i18n](https://github.com/ORCID/ORCID-Source/tree/master/orcid-core/src/main/resources/i18n) automatically via TXGH

#### Verify Github deployment
After review is complete, check to make sure that automatic deployment to Github was successful:

1. Navigate to https://github.com/ORCID/ORCID-Source/commits/master/orcid-core/src/main/resources/i18n and verify that commits were made by the user **orcid-machine** at approximately the same time that review was completed.
2. Optional: To check file contents, navigate to [ORCID-Source/orcid-core/src/main/resources/i18n](https://github.com/ORCID/ORCID-Source/tree/master/orcid-core/src/main/resources/i18n) and choose the appropriate file.
3. On the Trello card for the translation set a QA check list to review the translation with one item for each language.

If Github deployment was not successful, translated property files can be pushed from Transifex to Github manually. See [Push/Pull to/from Transifex manually](#pushpull-tofrom-transifex-manually)

#### Release translations
Translated properties files are pushed directly to [ORCID-Source master](https://github.com/ORCID/ORCID-Source) by TXGH, so translations become live on QA, Sandbox or Prod during the normal ORCID release process.

## <a id="update-existing-strings-english-only"></a>Update existing string(s) -  English only OR English + other languages

### Developers

1. Edit \*\_en.properties file(s) manually
2. Commit and push the changes to remote

        git commit -m "message text"
        git push origin [branch name]

3. When changes are merged into master, edited strings will be pulled into Transifex automatically and marked as 'Untranslated'.
3. Add the Transifex Project Maintainer to the Trello card for the fix (create a card if there is not one already) so they are aware of this update. Include the both the key and value for each changed string on the card.

### Transifex Project Maintainer

1. When the corrected English string is pushed to master (eg: card moves to Current Development > Submitted), navigate to https://www.transifex.com/orcid-inc-1/registry
3. Select the first lanugage in the list
4. Select the update properties file, it will display as less than 100% translated, then click "Translate"
5. Select "Untranslated" from the top menu to go to the updated strings
6. In the left column select the string that has been updated
7. The right column will display a list of suggested translations, find the translation before the typo was corrected in this list then click the icon of two files (the "Use This" button) next to that translation
8. The translation will now be listed in the middle column, click "Save" then click "Review"
9. Repeated steps 6-8 for any other typos that were corrected
10. Repeat steps 3-9 for each language

## <a id="update-existing-strings-non-english-only"></a>Update existing string(s) -  non-English only

### Developers/Other ORCID team members

1. Create a card in Trello on the Translation issue list on the Bugs board describing the problem
2. Add the Transifex Project Maintainer to that card

### Transifex Project Maintainer

1. Contact the community translators via Transifex or another method to get or verify the corrected translation
2. Navigate to https://www.transifex.com/orcid-inc-1/registry/languages
3. Select the language which needs to be fixed
4. Select the update properties file which needs to be fixed then click "Translate"
5. In the search box at the top of the left column search for the English version of the text to be corrected
6. Select the string to be corrected from the left column
7. In the left column select the comments tab, then click "Comment" to add a new comment
8. Enter the older translation then the text "corrected to" then the new translation then add any other relevant notes about this change. Click Add
9. In the middle column delete the incorrect translation and add the new correct version. Click "Save" and then "Review"
10. Move the Trello card to the QA Testing list

## <a id="add-new-language"></a>Add new language
### Transifex Project Maintainer
##### Transifex API
1. Add a language and assign Transifex users as translators using a POST request per http://docs.transifex.com/api/languages/#post

        curl -X POST -i -H "Content-Type: application/json" -L
        --data '{"language_code": "es", "translators": ["username1", "username2"]}'
        --user username:password http://www.transifex.com/api/2/orcid-inc-1/[project-slug]/languages

 * language code:  ISO code for the new lanaguage (see list of [Transifex supported languages](https://www.transifex.com/explore/languages/))
 * translators: list of Transifex users to assign as translators for the new language 
 * username: project owner or maintainer's Transifex username
 * password: project owner or maintaner's Transifex password

##### Transifex UI
1. Navigate to https://www.transifex.com/orcid-inc-1/registry
2. Click Edit Langauges
3. Enter a new language and click Apply
4. Navigate to the translation teams at https://www.transifex.com/orcid-inc-1/teams/
5. To add translators to the new language, visit https://www.transifex.com/orcid-inc-1/[project-slug]/language/[language-code] and click Add Collaborators

When translation status for each resource in the new language reaches 100%, the resource properties file is pushed to the master branch of [ORCID-Source/orcid-core/src/main/resources/i18n](https://github.com/ORCID/ORCID-Source/tree/master/orcid-core/src/main/resources/i18n) automatically via TXGH

#### Release new language
1. Add a Trello card to [Current Development](https://trello.com/b/iuJwm8A6/orcid-current-development) requesting that the new language be added to the language picker (this must be done on both the Registry and Drupal sites)
2. When the language picker changes have been release to QA, contact community translators for this language and ask them to review the translation
3. After translators have reviewed the language, move the card to Launchpad and notify the appropriate developer, who will unhide the new language in the language picker so that it becomes visible in Prod.

## <a id="add-resource"></a>Add a Resource
1. Add an entry for the resource to [.tx/config](https://github.com/ORCID/ORCID-Source/blob/master/.tx/config)
2. Add a properties files for the resource (language codes en, xx, lr and rl)  to [orcid-core/src/main/resources/i18n](https://github.com/ORCID/ORCID-Source/tree/master/orcid-core/src/main/resources/i18n)
        
        touch [resource name]_en.properties
        touch [resource name]_xx.properties
        touch [resource name]_lr.properties
        touch [resource name]_rl.properties
        
4. Add the file name prefix for the new resource to [update_test_languages.jy](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/i18n/update_test_languages.jy#L85) (this script should be run and the results commited after adding properties to the file)
3. Add the resource to Transifex (requires [installing Transifex CLI](#install-tx); if you don't want to install the CLI you can [upload via Transifex UI](https://docs.transifex.com/projects/uploading-content#if-a-project-already-has-a-resource))

        tx push -s -r registry.[resource name]

## <a id="remove-resource"></a>Remove a Resource
1. Remove the entry for the resource from [.tx/config](https://github.com/ORCID/ORCID-Source/blob/master/.tx/config)
2. Remove properties files for the resource from [orcid-core/src/main/resources/i18n](https://github.com/ORCID/ORCID-Source/tree/master/orcid-core/src/main/resources/i18n)
        
        rm [resource name]_[lang code].properties

+ **If the resource is still in use, but no longer being translated:** delete all properties files for the resource EXCEPT [resource name]_en.properties
+ **If the resource is no longer used at all:** delete all [resource name]_[lang code].properties files, including xx, lr, and rl

3. Delete the resource from Transifex

        tx delete -r registry.[resource name]
(or [delete via Transifex UI](https://docs.transifex.com/projects/deleting-content))

## Push/Pull to/from Transifex manually
If automatic push/pull between Transifex and Github via TXGH fails, files can be transferred manually, via the Transifex client or the Transifex API.

### <a id="install-tx"></a>Install/configure Transifex client
1. [Install the Transifex client](http://docs.transifex.com/client/)
2. [Configure the Transifex client](http://docs.transifex.com/client/config/) to use your Transifex credentials. Complete the [.transifexrc](http://docs.transifex.com/client/config/#transifexrc) step only - do not edit .tx/config or language mappings.

### Push to Transifex
1. Change to the directory containing the properties files

        cd ~/git/ORCID-Source/orcid-core/src/main/resources/i18n

2. Push the project files to Transifex

        tx push -s

```-s``` pushes only source (en) files; to push translation files as well, add ``-t``. For additional options/usage http://docs.transifex.com/client/push 

### Pull from Transifex
1. Change to the directory containing the properties files

        cd ~/git/ORCID-Source/orcid-core/src/main/resources/i18n

2. Pull only strings that have been marked as reviewed in Transifex

        tx pull --mode reviewed

3. Commit and push changes to remote

        git commit -m "message text"
        git push origin [branch name]

For additional options/usage, see http://docs.transifex.com/client/pull




