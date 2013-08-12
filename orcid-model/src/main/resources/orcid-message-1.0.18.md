# Summary of API Changes from 1.0.17 to 1.0.18

* Moved &lt;affiliations> to &lt;orcid-activities>.
* Made month and day optional in affiliation &lt;start-date> and &lt;end-date>, by using same structured format as for &lt;publication-date>.
* Added &lt;affiliation-address>, and updated &lt;affiliation> to use that instead of &lt;address>.
* Added &lt;disambiguated-affiliation>. This element will be included in <affiliation> if the affiliation has been disambiguated, otherwise it will be absent.
* Restricted contents of &lt;country> to ISO 3611 values. This has always been enforced by the API, but was not explicitly enforced by the XML schema until now.
* Made <affiliation-name> required.
* Added &lt;source> to &lt;affiliation>.