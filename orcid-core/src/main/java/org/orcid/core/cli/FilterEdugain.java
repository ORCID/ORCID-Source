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
package org.orcid.core.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class FilterEdugain {

    public static void main(String[] args) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, TransformerException {
        List<String> idps = IOUtils.readLines(new FileInputStream(args[0]));
        File edugainFile = new File(args[1]);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(edugainFile);
        NodeList nodeList = doc.getElementsByTagName("md:EntityDescriptor");
        int matched = 0;
        int originalLength = nodeList.getLength();
        List<Node> nodesToRemove = new ArrayList<>();
        for (int i = 0; i < originalLength; i++) {
            Node item = nodeList.item(i);
            String entityId = item.getAttributes().getNamedItem("entityID").getTextContent();
            if (idps.contains(entityId)) {
                matched++;
                System.out.println("Found entity " + entityId);
            } else {
                nodesToRemove.add(item);
                Node previousSibling = item.getPreviousSibling();
                if (previousSibling != null && previousSibling instanceof Text) {
                    if (StringUtils.isBlank(previousSibling.getTextContent())) {
                        nodesToRemove.add(previousSibling);
                    }
                }
            }
        }
        nodesToRemove.stream().forEach(n -> n.getParentNode().removeChild(n));
        System.out.println("Number of entities matched = " + matched + "/" + originalLength);

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new FileOutputStream("/tmp/federation-metatdata-filtered.xml"));
        transformer.transform(source, result);
    }
}
