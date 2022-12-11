package pt.ulisboa.tecnico.surething.prover.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import pt.ulisboa.tecnico.surething.prover.R;

public class SSL {

    public static void init(Context context) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInputVerifier = new BufferedInputStream(context.getResources().openRawResource(R.raw.verifier));
        InputStream caInputLedger = new BufferedInputStream(context.getResources().openRawResource(R.raw.ledger));


        Certificate caVerifier;
        Certificate caLedger;
        try {
            caVerifier = cf.generateCertificate(caInputVerifier);
            Log.d("SSL", "caVerifier=" + ((X509Certificate) caVerifier).getSubjectDN());
            caLedger = cf.generateCertificate(caInputLedger);
            Log.d("SSL", "caVerifier=" + ((X509Certificate) caLedger).getSubjectDN());
        } finally {
            caInputLedger.close();
            caInputVerifier.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("caLedger", caLedger);
        keyStore.setCertificateEntry("caVerifier", caVerifier);
        Log.d("SIZE", String.valueOf(keyStore.size()));

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(final String hostname, final SSLSession session) {
                return true;
            }
        });
    }
}
