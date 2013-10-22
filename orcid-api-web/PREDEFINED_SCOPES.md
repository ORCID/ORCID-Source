# PREDEFINED SCOPES
Predefined scopes are used to initiate OAuth 2 code flow from the ORCID Site. Typically 
they are created by inserting or editing new rows in the client_redirect_uri or via 
ManageClientGroup utility.
 
## Import Works  Wizard
Clients with predefined scopes and a redirect type of import-works-wizard will show up in
the "Import Research Activities" modal. The user then can choose to authorize client.

For import-works-wizard common associated scopes

| predefined_client_redirect_scope        |
| ----------------------------------------|
| /orcid-works/create                     |
| /orcid-bio/external-identifiers/create  |
| /orcid-works/read-limited               |
| /orcid-bio/read-limited                 |
| /orcid-profile/read-limited             |

### Grant Read Wizard
When the user claims a profile said user will get prompted for read access if the clients 
originated the record.

For grant-read-wizard common associated scopes

| predefined_client_redirect_scope |
|----------------------------------|
| /orcid-profile/read-limited      |