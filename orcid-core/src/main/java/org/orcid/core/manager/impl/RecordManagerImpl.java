package org.orcid.core.manager.impl;

import org.springframework.transaction.annotation.Transactional;

import org.orcid.core.manager.RecordManager;
import org.orcid.core.manager.read_only.impl.RecordManagerReadOnlyImpl;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@Transactional(value = "transactionManager")
public class RecordManagerImpl extends RecordManagerReadOnlyImpl implements RecordManager {
	
}
