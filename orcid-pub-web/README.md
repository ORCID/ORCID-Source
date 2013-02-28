# ORCID-t1-web (Public API)
This webapp provides the public API. Need to know:

* RESTful API.

* Supports XML or JSON.

* Only information marked as public can be queried. 


# URLS for different environments

* **Local**: http://localhost:8080/orcid-t1-web

* **Sandbox**: http://devsandbox.orcid.org

* **Public Production**: http://pub.orcid.org

* **Member Production:**: http://api.orcid.org


# Sample Curl Calls

* **XML**
    ```
    curl -H "Accept: application/orcid+xml" 'http://devsandbox.orcid.org/0000-0002-7148-2524/orcid-bio' -L -i
    ```
    
* **JSON**
    ```
    curl -H "Accept: application/orcid+json" 'http://devsandbox.orcid.org/0000-0002-7148-2524/orcid-bio' -L -i
    ```


# Known Implementations

* [ORCID Python](https://github.com/scholrly/orcid-python)

* [R ORCID](https://github.com/ropensci/rorcid)
 
    
# Resources you can request:
<table><tbody>
<tr>
<td>Bio</td>
<td>/orcid-bio</td>
<td>Given a contributor, return name and affiliation data.</td>
</tr>

<tr>
<td>Works</td>
<td>/orcid-works</td>
<td>Given a contributor, return the list of works he has contributed to.</td>
</tr>

<tr>
<td>Full</td>
<td>/orcid-profile</td>
<td>Given a contributor, return list of works he has contributed to, name and public affiliation data.</td>
</tr>

<tr>
<td>Search</td>
<td>/search/orcid-bio?</td>
<td>Given whatever metadata provided, return a ranked list of potential contributors identified by that metadata.</td>
</tr>
</tbody></table>

    
# License
See [LICENSE.md](https://github.com/ORCID/ORCID-Work-in-Progress/blob/master/LICENSE.md)

