package com.coachera.backend.cli;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class WebPushShellCommands {

    static {
        // Add BouncyCastle once globally
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @ShellMethod(key = "generate-vapid-keys", value = "Generate VAPID public/private keys for web push")
    public void generateVapidKeys() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            generator.initialize(new ECGenParameterSpec("secp256r1"));

            KeyPair keyPair = generator.generateKeyPair();

            String publicKey = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(((ECPublicKey) keyPair.getPublic()).getEncoded());

            String privateKey = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(((ECPrivateKey) keyPair.getPrivate()).getEncoded());

            System.out.println("\n==== VAPID KEYS ====");
            System.out.println("Public Key:\n" + publicKey);
            System.out.println("\nPrivate Key:\n" + privateKey);
            System.out.println("\nAdd these to your application.properties or environment variables.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
