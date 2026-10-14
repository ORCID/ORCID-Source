# ORCID-INTERNAL-API (Internal API)

The ORCID-INTERNAL-API is intended to be used for internal ORCID apps, so, only specific clients can use it.

Every request needs a client credentials token (`ROLE_CLIENT`); a human admin session cannot reach
this module at all. On top of that, each endpoint checks its own scope, so granting a client one
internal scope does not give it the others.

### Set up

1. Add the scope the client needs. Scopes are derived from the client type when a client is created,
so an internal scope has to be inserted by hand:

``insert into client_scope values('<Client ID>','/orcid-internal/person/last_modified',now() , now() );``

``update client_details set last_modified=now() where client_details_id='<CLIENT_ID>'``

The `last_modified` bump is not optional: client details are cached, and without it the new scope is
not picked up.

Note this grant is not durable. Updating the member type or converting the client reconciles
`client_scope` back to the scopes of the client type and silently drops anything else, which makes a
service depending on an internal scope fail closed and silent. Pair it with a check that alerts on
the resulting 403.

### Scopes

| Scope | Grants |
|---|---|
| `/orcid-internal/person/last_modified` | `GET /{orcid}/person` |
| `/orcid-internal` | `GET /orcid/{base64(email)}/email` |
| `/orcid-internal/account-recovery` | `POST /account-recovery/match`, `POST /account-recovery/reset-link` |

These are independent of one another. Despite the nested looking paths, holding `/orcid-internal`
grants neither of the others: inheritance is declared in `ScopePathType`, not derived from the path.

### How to use it?

1. Get the token:

``curl -i -L -k -H 'Accept: application/json' -d 'client_id=<CLIENT_ID>' -d 'client_secret=<CLIENT_SECRET>' -d 'scope=/orcid-internal/person/last_modified' -d 'grant_type=client_credentials' 'http://localhost:8080/orcid-internal-api/oauth/token'``

2. Use the token to get user info

``curl -H 'Accept: application/json' -H 'Authorization: Bearer <TOKEN>' http://localhost:8080/orcid-internal-api/<ORCID>/person``

### Account recovery

Used by the lost email account recovery workflow (PD-5872), which needs to act on records the
verified only lookups will not resolve. Both endpoints need the
`/orcid-internal/account-recovery` scope.

**Confirm an iD and an email belong to the same record.** Answers for locked, deactivated,
unclaimed and deprecated records too. A non match says nothing but that: an unknown email, an
unknown iD, and an email belonging to a different record all produce the same `{"match": false}`,
so it cannot be used to discover whether an address is registered. Only a confirmed pair is told
anything about the record - its status, and the addresses on it.

``curl -X POST -H 'Content-Type: application/json' -H 'Authorization: Bearer <TOKEN>' -d '{"orcid":"<ORCID>","email":"<EMAIL>"}' http://localhost:8080/orcid-internal-api/account-recovery/match``

Returns `{"match": true, "recordStatus": "ACTIVE", "emails": ["<EMAIL>", "<ANOTHER EMAIL>"]}`, where
the status is one of `ACTIVE`, `LOCKED`, `DEACTIVATED`, `UNCLAIMED` or `DEPRECATED`. A non match
reads exactly as it did before: `"match": false`, no record status, and no `emails` key at all.

`emails` is every address on the record - verified or not, whatever its visibility, each once and in
no particular order - and it is only ever present on a match (PD-14421). The workflow sends a
delivery check to each of them, so that an account is not released while its owner still reads an
address the requester did not name. Two consequences worth keeping in view:

- **This scope now reveals a record's addresses** to a caller who already knows the iD and one of
  its addresses, private ones included. Grant `/orcid-internal/account-recovery` to the recovery
  workflow's client and nothing else.
- **The list is never read on a non match**, so the answer to a wrong pair costs the same and says
  the same as it did. A match whose record has no address rows answers `"emails": []` rather than
  leaving the key out; a missing key is how a client tells this registry from an older one.

**Mint a single use password reset link**, the same one an admin generates by hand today. Issuing a
link invalidates any link issued earlier for that record, and it can only be redeemed once. The
lifetime is shared with admin generated links (`org.orcid.utils.adminJwtExpirationInMinutes`,
24 hours by default).

``curl -X POST -H 'Content-Type: application/json' -H 'Authorization: Bearer <TOKEN>' -d '{"orcid":"<ORCID>"}' http://localhost:8080/orcid-internal-api/account-recovery/reset-link``

Returns `{"resetLink": "...", "issueDate": "...", "expiryDate": "..."}`.

#License
See [LICENSE.md](https://github.com/ORCID/ORCID-Work-in-Progress/blob/master/LICENSE.md)
