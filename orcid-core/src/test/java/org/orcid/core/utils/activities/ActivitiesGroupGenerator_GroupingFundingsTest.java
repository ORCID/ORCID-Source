package org.orcid.core.utils.activities;

import java.util.HashMap;
import java.util.Map;

import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.FundingExternalIdentifier;
import org.orcid.jaxb.model.record.FundingExternalIdentifierType;
import org.orcid.jaxb.model.record.FundingExternalIdentifiers;
import org.orcid.jaxb.model.record.FundingTitle;
import org.orcid.jaxb.model.record.Title;

public class ActivitiesGroupGenerator_GroupingFundingsTest {

    /**
     * funding-1 -> A, B, C 
     * funding-2 -> C, D, E
     * funding-3 -> X, Y, Z
     * funding-4 -> Y, B, 1
     * funding-5 -> M, N, O 
     * funding-6 -> A, B, C
     * funding-7 -> 1, 2, B  
     * funding-8 -> No external identifiers
     * funding-9 -> No external identifiers  
     * */
    private Map<String, Funding> generateFundings() {
        Map<String, Funding> result = new HashMap<String, Funding>();
        for(int i = 0; i < 10; i++) {
            String name = "funding-" + i;
            Funding funding = new Funding();
            FundingTitle title = new FundingTitle();
            title.setTitle(new Title(name));
            funding.setTitle(title);
            FundingExternalIdentifiers fei = new FundingExternalIdentifiers();
            switch(i) {
            case 1:
                FundingExternalIdentifier f1 = new FundingExternalIdentifier();
                f1.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f1.setValue("A");
                FundingExternalIdentifier f2 = new FundingExternalIdentifier();
                f2.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f2.setValue("B");
                FundingExternalIdentifier f3 = new FundingExternalIdentifier();
                f3.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f3.setValue("C");
                fei.getExternalIdentifier().add(f1);
                fei.getExternalIdentifier().add(f2);
                fei.getExternalIdentifier().add(f3);
                break;
            case 2:
                FundingExternalIdentifier f4 = new FundingExternalIdentifier();
                f4.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f4.setValue("C");
                FundingExternalIdentifier f5 = new FundingExternalIdentifier();
                f5.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f5.setValue("D");
                FundingExternalIdentifier f6 = new FundingExternalIdentifier();
                f6.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f6.setValue("E");
                fei.getExternalIdentifier().add(f4);
                fei.getExternalIdentifier().add(f5);
                fei.getExternalIdentifier().add(f6);
                break;
            case 3:
                FundingExternalIdentifier f7 = new FundingExternalIdentifier();
                f7.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f7.setValue("X");
                FundingExternalIdentifier f8 = new FundingExternalIdentifier();
                f8.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f8.setValue("Y");
                FundingExternalIdentifier f9 = new FundingExternalIdentifier();
                f9.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f9.setValue("Z");
                fei.getExternalIdentifier().add(f7);
                fei.getExternalIdentifier().add(f8);
                fei.getExternalIdentifier().add(f9);
                break;
            case 4:
                FundingExternalIdentifier f10 = new FundingExternalIdentifier();
                f10.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f10.setValue("Y");
                FundingExternalIdentifier f11 = new FundingExternalIdentifier();
                f11.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f11.setValue("B");
                FundingExternalIdentifier f12 = new FundingExternalIdentifier();
                f12.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f12.setValue("1");
                fei.getExternalIdentifier().add(f10);
                fei.getExternalIdentifier().add(f11);
                fei.getExternalIdentifier().add(f12);
                break;
            case 5:
                FundingExternalIdentifier f13 = new FundingExternalIdentifier();
                f13.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f13.setValue("M");
                FundingExternalIdentifier f14 = new FundingExternalIdentifier();
                f14.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f14.setValue("N");
                FundingExternalIdentifier f15 = new FundingExternalIdentifier();
                f15.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f15.setValue("O");
                fei.getExternalIdentifier().add(f13);
                fei.getExternalIdentifier().add(f14);
                fei.getExternalIdentifier().add(f15);
                break;
            case 6:
                FundingExternalIdentifier f16 = new FundingExternalIdentifier();
                f16.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f16.setValue("A");
                FundingExternalIdentifier f17 = new FundingExternalIdentifier();
                f17.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f17.setValue("B");
                FundingExternalIdentifier f18 = new FundingExternalIdentifier();
                f18.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f18.setValue("C");
                fei.getExternalIdentifier().add(f16);
                fei.getExternalIdentifier().add(f17);
                fei.getExternalIdentifier().add(f18);
                break;
            case 7:
                FundingExternalIdentifier f19 = new FundingExternalIdentifier();
                f19.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f19.setValue("1");
                FundingExternalIdentifier f20 = new FundingExternalIdentifier();
                f20.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f20.setValue("2");
                FundingExternalIdentifier f21 = new FundingExternalIdentifier();
                f21.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                f21.setValue("B");
                fei.getExternalIdentifier().add(f19);
                fei.getExternalIdentifier().add(f20);
                fei.getExternalIdentifier().add(f21);
                break;
            }
            funding.setExternalIdentifiers(fei);
        }
        return result;
    }
}
