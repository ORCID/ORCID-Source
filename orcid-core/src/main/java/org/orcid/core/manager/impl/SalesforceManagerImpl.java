package org.orcid.core.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.SalesforceManager;
import org.orcid.core.salesforce.adapter.SalesForceAdapter;
import org.orcid.core.salesforce.model.Member;
import org.springframework.stereotype.Component;

@Component
public class SalesforceManagerImpl implements SalesforceManager {

	@Resource
    private SalesForceAdapter salesForceAdapter;
	
	public List<Member> retrieveMembers() {
		
	}
	
}
