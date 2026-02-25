<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.addWorks.header.yourOrcidId" /> ${orcidId}
<@emailMacros.msg "email.addWorks.header.yourOrcidRecordIs" /> ${baseUri}/${orcidId}

<@emailMacros.msg "email.common.dear" /> ${emailName}

<@emailMacros.msg "email.addWorks.weHaveNoticed" />

<@emailMacros.msg "email.addWorks.belowAreLinks" /><@emailMacros.space /><@emailMacros.msg "email.addWorks.connectingToTheseServices" />
  * Web of Science: <@emailMacros.msg "email.addWorks.webOfScience" /> (https://orcid.org/oauth/authorize?client_id=APP-OKON44OLRIOZU944&response_type=code&scope=/read-limited%20/activities/update&redirect_uri=https://www.webofscience.com/wos/author/orcid-wizard)
  * The Lens: <@emailMacros.msg "email.addWorks.theLens" /> (https://orcid.org/oauth/authorize?client_id=APP-EN52WHSMFO6TZT6B&response_type=code&scope=/activities/update%20/read-limited%20/person/update&redirect_uri=https://www.lens.org/lens/orcid/wizard)	

<@emailMacros.msg "email.addWorks.otherPopularServices" />
  * Crossref Metadata Search: <@emailMacros.msg "email.addWorks.crossrefMetaSearch" /> (https://orcid.org/oauth/authorize?client_id=0000-0002-3054-1567&response_type=code&scope=%2Factivities%2Fupdate%20%2Fread-limited&redirect_uri=https:%2F%2Fsearch.crossref.org%2Fauth%2Forcid%2Fsearch-and-link)
  * Europe PubMed Central: <@emailMacros.msg "email.addWorks.europePubMedCentral" /> (https://orcid.org/oauth/authorize?client_id=0000-0002-9157-3431&response_type=code&scope=%2Fread-limited%20%2Factivities%2Fupdate&redirect_uri=https:%2F%2Feuropepmc.org%2Forcid%2Fimport)
  * Scopus: <@emailMacros.msg "email.addWorks.scopus" /> (https://orcid.org/oauth/authorize?client_id=0000-0002-5982-8983&response_type=code&scope=%2Fperson%2Fupdate%20%2Fread-limited%20%2Factivities%2Fupdate&redirect_uri=https:%2F%2Forcid.scopusfeedback.com%2Finward%2Forcid)

<@emailMacros.msg "email.addWorks.ifYouAreHavingTrouble" /><@emailMacros.space /><@emailMacros.msg "email.addWorks.orcidHelpCenter" />.

<@emailMacros.msg "email.addWorks.footer.warmRegards" />

<@emailMacros.msg "email.addWorks.footer.orcidSupportTeam" />

https://support.orcid.org

<@emailMacros.msg "email.addWorks.footer.youHaveReceivedThisEmail" /><@emailMacros.space /><@emailMacros.msg "email.addWorks.footer.accountSettings" />.
<@emailMacros.msg "email.common.email.preferences" /> | <@emailMacros.msg "email.common.privacy_policy" /> | <@emailMacros.msg "email.common.address1" /> | <@emailMacros.msg "email.common.address2" /> | ORCID.org
