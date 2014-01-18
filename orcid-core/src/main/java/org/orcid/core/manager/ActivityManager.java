package org.orcid.core.manager;

import java.util.HashMap;

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.pojo.ajaxForm.Work;

public interface ActivityManager {

    public String createKey(OrcidProfile profile);

    public HashMap<String, Work> pubMinWorksMap(OrcidProfile profile, String key);

}
