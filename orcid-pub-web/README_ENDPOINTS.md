

## Curl Examples with Bearer Token Authentication

These examples show the same endpoints with an `Authorization: Bearer` header for authenticated requests. Replace `$ACCESS_TOKEN` with your actual bearer token.

System and docs:

```bash
curl -sS -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/"
curl -sS -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/status"
curl -sS -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/pubStatus"
```

Record and activities:

```bash
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/record"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/activities"
```

Search:

```bash
curl -sS -H "Authorization: Bearer $ACCESS_TOKEN" -G "$BASE_URL/$API_VERSION/search" --data-urlencode "q=$QUERY"
curl -sS -H "Authorization: Bearer $ACCESS_TOKEN" -G "$BASE_URL/v3.0/csv-search" --data-urlencode "q=$QUERY"
curl -sS -H "Authorization: Bearer $ACCESS_TOKEN" -G "$BASE_URL/v3.0/expanded-search" --data-urlencode "q=$QUERY"
```

Client:

```bash
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/client/$CLIENT_ID"
```

Work:

```bash
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/works"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/works/$PUT_CODES"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/work/$PUT_CODE"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/work/summary/$PUT_CODE"
curl -sS -H "Accept: application/vnd.citationstyles.csl+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/work/$PUT_CODE"
```

Funding:

```bash
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/fundings"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/funding/$PUT_CODE"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/funding/summary/$PUT_CODE"
```

Education:

```bash
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/educations"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/education/$PUT_CODE"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/education/summary/$PUT_CODE"
```

Employment:

```bash
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/employments"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/employment/$PUT_CODE"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/employment/summary/$PUT_CODE"
```

Peer review:

```bash
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/peer-reviews"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/peer-review/$PUT_CODE"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/peer-review/summary/$PUT_CODE"
```

Personal profile endpoints:

```bash
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/person"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/personal-details"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/biography"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/email"

curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/address"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/address/$PUT_CODE"

curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/keywords"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/keywords/$PUT_CODE"

curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/other-names"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/other-names/$PUT_CODE"

curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/external-identifiers"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/external-identifiers/$PUT_CODE"

curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/researcher-urls"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/$API_VERSION/$ORCID_ID/researcher-urls/$PUT_CODE"
```

v3.0 additional affiliation endpoints:

```bash
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/distinctions"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/distinction/$PUT_CODE"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/distinction/summary/$PUT_CODE"

curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/invited-positions"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/invited-position/$PUT_CODE"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/invited-position/summary/$PUT_CODE"

curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/memberships"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/membership/$PUT_CODE"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/membership/summary/$PUT_CODE"

curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/qualifications"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/qualification/$PUT_CODE"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/qualification/summary/$PUT_CODE"

curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/services"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/service/$PUT_CODE"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/service/summary/$PUT_CODE"

curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/research-resources"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/research-resource/$PUT_CODE"
curl -sS -H "Accept: application/vnd.orcid+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/v3.0/$ORCID_ID/research-resource/summary/$PUT_CODE"
```

Identifier API:

```bash
curl -sS -H "Authorization: Bearer $ACCESS_TOKEN" -G "$BASE_URL/v3.0/identifiers" --data-urlencode "locale=$LOCALE"
curl -sS -H "Authorization: Bearer $ACCESS_TOKEN" -G "$BASE_URL/v2.1/identifiers" --data-urlencode "locale=$LOCALE"
curl -sS -H "Authorization: Bearer $ACCESS_TOKEN" -G "$BASE_URL/v2.0/identifiers" --data-urlencode "locale=$LOCALE"
```

Experimental RDF API:

```bash
curl -sS -H "Accept: application/rdf+xml" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/experimental_rdf_v1/$ORCID_ID"
curl -sS -H "Accept: text/turtle" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/experimental_rdf_v1/$ORCID_ID"
curl -sS -H "Accept: application/ld+json" -H "Authorization: Bearer $ACCESS_TOKEN" "$BASE_URL/experimental_rdf_v1/$ORCID_ID"
```
