package org.orcid.core.version;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.core.version.impl.V2VersionConverterChainImpl;

/**
 * 
 * @author Will Simpson
 *
 */
public class V2VersionConverterChainTest extends BaseTest {

    @Test
    public void testSimpleChain() {
        V2VersionConverter converter1 = new Converter1();
        V2VersionConverter converter2 = new Converter2();
        List<V2VersionConverter> converters = new ArrayList<>();
        converters.add(converter1);
        converters.add(converter2);
        V2VersionConverterChainImpl chain = new V2VersionConverterChainImpl();
        chain.setConverters(converters);

        // Upgrade from 1 to 3
        Version1 version1 = new Version1();
        Object upgradeResult = chain.upgrade(new V2Convertible(version1, "1"), "3").getObjectToConvert();
        assertNotNull(upgradeResult);
        assertTrue(upgradeResult instanceof Version3);
        assertEquals("3", ((Version3) (upgradeResult)).getMyVersion());

        // Upgrade from 1 to 2
        upgradeResult = chain.upgrade(new V2Convertible(version1, "1"), "2").getObjectToConvert();
        assertNotNull(upgradeResult);
        assertTrue(upgradeResult instanceof Version2);
        assertEquals("2", ((Version2) (upgradeResult)).getMyVersion());

        // Downgrade from 3 to 1
        Version3 version3 = new Version3();
        Object downgradeResult = chain.downgrade(new V2Convertible(version3, "3"), "1").getObjectToConvert();
        assertNotNull(downgradeResult);
        assertTrue(downgradeResult instanceof Version1);
        assertEquals("1", ((Version1) (downgradeResult)).getMyVersion());

        // Downgrade from 3 to 2
        downgradeResult = chain.downgrade(new V2Convertible(version3, "3"), "2").getObjectToConvert();
        assertNotNull(downgradeResult);
        assertTrue(downgradeResult instanceof Version2);
        assertEquals("2", ((Version2) (downgradeResult)).getMyVersion());
    }

    public class Converter1 implements V2VersionConverter {

        @Override
        public String getLowerVersion() {
            return "1";
        }

        @Override
        public String getUpperVersion() {
            return "2";
        }

        @Override
        public V2Convertible downgrade(V2Convertible objectToDowngrade) {
            Version1 version1 = new Version1();
            version1.setRetainedValue(((Version2) objectToDowngrade.getObjectToConvert()).getRetainedValue());
            return new V2Convertible(version1, "1");
        }

        @Override
        public V2Convertible upgrade(V2Convertible objectToUpgrade) {
            Version2 version2 = new Version2();
            version2.setRetainedValue(((Version1) objectToUpgrade.getObjectToConvert()).getRetainedValue());
            return new V2Convertible(version2, "2");
        }

    }

    public class Converter2 implements V2VersionConverter {

        @Override
        public String getLowerVersion() {
            return "2";
        }

        @Override
        public String getUpperVersion() {
            return "3";
        }

        @Override
        public V2Convertible downgrade(V2Convertible objectToDowngrade) {
            Version2 version2 = new Version2();
            version2.setRetainedValue(((Version3) objectToDowngrade.getObjectToConvert()).getRetainedValue());
            return new V2Convertible(version2, "2");
        }

        @Override
        public V2Convertible upgrade(V2Convertible objectToUpgrade) {
            Version3 version3 = new Version3();
            version3.setRetainedValue(((Version2) objectToUpgrade.getObjectToConvert()).getRetainedValue());
            return new V2Convertible(version3, "3");
        }
    }

    public class Version1 {

        private String myVersion = "1";
        private String retainedValue;

        public String getMyVersion() {
            return myVersion;
        }

        public String getRetainedValue() {
            return retainedValue;
        }

        public void setRetainedValue(String retainedValue) {
            this.retainedValue = retainedValue;
        }
    }

    public class Version2 {

        private String myVersion = "2";
        private String retainedValue;

        public String getMyVersion() {
            return myVersion;
        }

        public String getRetainedValue() {
            return retainedValue;
        }

        public void setRetainedValue(String retainedValue) {
            this.retainedValue = retainedValue;
        }
    }

    public class Version3 {

        private String myVersion = "3";
        private String retainedValue;

        public String getMyVersion() {
            return myVersion;
        }

        public String getRetainedValue() {
            return retainedValue;
        }

        public void setRetainedValue(String retainedValue) {
            this.retainedValue = retainedValue;
        }
    }

}
