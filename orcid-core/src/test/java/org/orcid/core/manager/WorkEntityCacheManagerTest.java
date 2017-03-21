/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.BaseTest;
import org.orcid.persistence.dao.impl.WorkDaoImpl;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.orcid.test.TargetProxyHelper;

public class WorkEntityCacheManagerTest extends BaseTest {
    
    @Resource
    private WorkEntityCacheManager workEntityCacheManager;
    
    @Mock
    private WorkDaoImpl workDao;
    
    private static String ORCID = "0000-0000-0000-0000";
    
    private static List<WorkLastModifiedEntity> elements;
    
    {
        elements = new ArrayList<WorkLastModifiedEntity>();
        for(int i = 0; i < 500; i++) {
            WorkLastModifiedEntity e = new WorkLastModifiedEntity();
            e.setId(Long.valueOf(i));
            e.setLastModified(new Date());
            elements.add(e);
        }
        
    }
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(workEntityCacheManager, "workDao", workDao);    
        when(workDao.getWorkLastModifiedList(ORCID)).thenReturn(elements);
    }
}
