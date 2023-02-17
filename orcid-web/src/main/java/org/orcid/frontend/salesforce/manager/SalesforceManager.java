package org.orcid.frontend.salesforce.manager;

import java.util.List;

import org.orcid.frontend.salesforce.model.Member;
import org.orcid.frontend.salesforce.model.MemberDetails;

public interface SalesforceManager {
	List<Member> retrieveMembers();

	MemberDetails retrieveMemberDetails(String memberId);
	
	List<Member> retrieveConsortiaList();
}
