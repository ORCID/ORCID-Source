package org.orcid.core.manager;

import java.util.List;

import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;

public interface SalesforceManager {
	List<Member> retrieveMembers();

	MemberDetails retrieveMemberDetails(String memberSlug);
	
	List<Member> retrieveConsortiaList();
}
