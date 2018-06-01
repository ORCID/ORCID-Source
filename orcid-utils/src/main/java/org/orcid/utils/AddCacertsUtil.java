package org.orcid.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/*
 * mvn one liner run form project root (ORCID-Source):
 * `mvn exec:java -pl orcid-utils -Dexec.mainClass="org.orcid.utils.AddCacertsUtil" -Dexec.args="-p changeit"` 
 */
public class AddCacertsUtil {

    @Option(name = "--keystore_password", required = true, usage = "Keystore password. (Java default is often 'changeit' or 'changeme')")
    private String password;

    @Option(name = "--rmDigi2Cert", required = false, usage = "Remove the digicertg2, only used for testing.")
    private boolean rmDigi2Cert = false;

    String digicertg2Alias = "digicertg2";

    String digicertg2Url = "https://www.digicert.com/CACerts/DigiCertGlobalRootG2.crt";

    String caLoc = getKeystoreLoc();

    public static void main(String[] args) {
        AddCacertsUtil acu = new AddCacertsUtil();
        CmdLineParser parser = new CmdLineParser(acu);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(0);
        }
        acu.execute();
    }

    private void execute() {
        try {
            KeyStore ks = getKeyStore();
            if (rmDigi2Cert) {
                rmCertToKeystore(digicertg2Alias, ks);
            } else if (ks.getCertificate(digicertg2Alias) == null) {
                Certificate digiCert = getCert(new URL(digicertg2Url));
                if (digiCert != null) {
                    addCertToKeystore(digicertg2Alias, digiCert, ks);
                } else {
                    System.out.println("digiCert from url is null");
                }
            }
            System.out.println("Has digicertg2 cert: " + String.valueOf(getKeyStore().getCertificate(digicertg2Alias) != null));
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("File path or password is incorrect e.getMessage(): '" + e.getMessage() + "'");
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private KeyStore getKeyStore() throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream ksInputStm = new FileInputStream(new File(caLoc));
        ks.load(ksInputStm, password.toCharArray());
        ksInputStm.close();
        return ks;
    }

    private void addCertToKeystore(String certAlias, Certificate digiCert, KeyStore ks) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        ks.setCertificateEntry(certAlias, digiCert);
        writeKeyStore(ks);
    }

    private void rmCertToKeystore(String certAlias, KeyStore ks) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        ks.deleteEntry(certAlias);
        writeKeyStore(ks);
    }

    private void writeKeyStore(KeyStore ks) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        try {
            FileOutputStream out = new FileOutputStream(caLoc);
            ks.store(out, password.toCharArray());
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("java.io.FileNotFoundException, most likely you need to run with root permissions. For example 'sudo mvn exec:java.....'");
            e.printStackTrace();
        }
    }

    private String getKeystoreLoc() {
        String caLoc = System.getProperty("java.home") + "/lib/security/cacerts".replace('/', File.separatorChar);
        System.out.println("");
        System.out.println("location of cacerts: " + caLoc);
        System.out.println("");
        return caLoc;
    }

    private Certificate getCert(URL url) {
        CertificateFactory cf;
        Certificate digiCert = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            InputStream digiStream = url.openStream();
            digiCert = cf.generateCertificate(digiStream);
        } catch (CertificateException e1) {
            System.out.println("error with cert " + digicertg2Url);
            e1.printStackTrace();
        } catch (IOException e1) {
            System.out.println("error with cert " + digicertg2Url);
            e1.printStackTrace();
        }
        return digiCert;
    }

}