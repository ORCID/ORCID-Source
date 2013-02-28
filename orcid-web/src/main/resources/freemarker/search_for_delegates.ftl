<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<@base>
<div class="colorbox-content" id="add-an-individual">
    <h1 class="colorbox-title">Add an individual</h1>
	<p class="regFieldData">Delegation: Search for another ORCID user who you would like to enable to manage your record on your behalf</p>
	<div>
		<form id="searchForDelegatesForm" class="form-inline">
			<input type="text" name="searchTerms"></input>
			<input type="submit" class="btn" value="Search" />
		</form>
	</div>
	<div id="searchResults"></div>
</div>
</@base>