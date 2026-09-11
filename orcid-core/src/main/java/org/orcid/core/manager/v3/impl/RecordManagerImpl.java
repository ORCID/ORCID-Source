package org.orcid.core.manager.v3.impl;

import org.springframework.transaction.annotation.Transactional;

import org.orcid.core.manager.v3.RecordManager;
import org.orcid.core.manager.v3.read_only.impl.RecordManagerReadOnlyImpl;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@Transactional(value = "transactionManager")
public class RecordManagerImpl extends RecordManagerReadOnlyImpl implements RecordManager {
	
}
