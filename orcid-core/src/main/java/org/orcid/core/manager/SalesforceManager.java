package org.orcid.core.manager;

import java.util.List;

import org.orcid.core.salesforce.model.Member;

public interface SalesforceManager {
	List<Member> retrieveMembers();
}
