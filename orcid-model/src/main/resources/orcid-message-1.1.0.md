# Summary of API Changes from 1.0.22 to 1.1.0

* New work types
* Added work subtypes
* It is still possible to add works with the old types if you use an older message version
* Added provisional country code for Kosovo
* RIS as a citation type 
* Restructured all elements containing ORCID number so that they have a child element for each of the full URI, ORCID number, and host (&lt;uri>, &lt;path>, and &lt;host>).
* Replaced &lt;orcid-id> with &lt;orcid-identifier> (containing &lt;uri>, &lt;path>, and &lt;host>) 
* Removed &lt;orcid> from &lt;orcid-profile>.