package org.orcid.frontend.web.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CommonPasswordsTest {
    
    @Test
    public void testPasswordIsCommon() {
        assertTrue(CommonPasswords.passwordIsCommon("baseball"));
        assertTrue(CommonPasswords.passwordIsCommon("dragon"));
        assertTrue(CommonPasswords.passwordIsCommon("football"));
        assertTrue(CommonPasswords.passwordIsCommon("monkey"));
        assertTrue(CommonPasswords.passwordIsCommon("shadow"));
        assertTrue(CommonPasswords.passwordIsCommon("password"));
        
        assertFalse(CommonPasswords.passwordIsCommon("132871384164578961349"));
        assertFalse(CommonPasswords.passwordIsCommon("advkuwAFdaAdf387922"));
        assertFalse(CommonPasswords.passwordIsCommon("%@@$£&£$%^!@SSDFgwjhsad"));
    }

}
