# ORCID-Pub-Web (Public API)
This webapp provides the public API. Need to know:

* RESTful API.

* Supports XML or JSON.

* Only information marked as public can be queried. 


# URLS for different environments

* **Local**: http://localhost:8080/orcid-pub-web

* **Sandbox**: http://pub.sandbox-1.orcid.org

* **Public Production**: http://pub.orcid.org

# Resources you can request:
<table><tbody>

<tr>
<td>Profile</td>
<td>/orcid-profile</td>
<td>Returns all public data for the contributor.</td>
</tr>

<tr>
<td>Bio</td>
<td>/orcid-bio</td>
<td>Returns name and affiliation data for the contributor.</td>
</tr>

<tr>
<td>Works</td>
<td>/orcid-works</td>
<td>Returns the list of works for the contributor.</td>
</tr>

<tr>
<td>Search</td>
<td>/search/orcid-bio?</td>
<td>Given whatever metadata provided, return a ranked list of potential contributors identified by that metadata.</td>
</tr>
</tbody></table>


# Sample Curl Calls

* **XML**
    ```
    curl -H "Accept: application/orcid+xml" 'http://pub.sandbox-1.orcid.org/0000-0002-6657-1928/orcid-bio' -L -i
    ```
    
* **JSON**
    ```
    curl -H "Accept: application/orcid+json" 'http://pub.sandbox-1.orcid.org/0000-0002-6657-1928/orcid-bio' -L -i
    ```

* **JSONP**
    ```
    curl -H "Accept: application/orcid+json" 'http://pub.sandbox-1.orcid.org/0000-0002-6657-1928/orcid-bio?callback=test' -L -i
    ```


# Known Implementations

* [ORCID Python](https://github.com/scholrly/orcid-python)

* [R ORCID](https://github.com/ropensci/rorcid)

    
# License
See [LICENSE.md](https://github.com/ORCID/ORCID-Work-in-Progress/blob/master/LICENSE.md)

