/*
 *
 *  Copyright 2016,2017 DTCC, Fujitsu Australia Software Technology, IBM - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package it.eng.jledgerclient.fabric.utils;


import it.eng.jledgerclient.exception.JLedgerClientException;
import it.eng.jledgerclient.fabric.config.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.validation.ConstraintViolation;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

public class Utils {
    private final static Logger logger = LogManager.getLogger(Utils.class);


    public static void getMessageViolations(Set<ConstraintViolation<?>> violations) throws JLedgerClientException {
        if (violations.isEmpty())
            return;
        StringBuilder messageBuilder = new StringBuilder();
        for (ConstraintViolation violation : violations) {
            messageBuilder.append(violation.getPropertyPath() + ": " + violation.getMessage() + "; ");
        }
        if (StringUtils.isNotEmpty(messageBuilder.toString()))
            throw new JLedgerClientException(messageBuilder.toString());
    }

    public static void getMessageViolationsResult(Set<ConstraintViolation<?>> violations) throws JLedgerClientException {
        if (violations.isEmpty())
            return;
        StringBuilder messageBuilder = new StringBuilder();
        for (ConstraintViolation violation : violations) {
            messageBuilder.append(violation.getPropertyPath() + ": " + violation.getMessage() + "; ");
        }
        if (StringUtils.isNotEmpty(messageBuilder.toString()))
            throw new JLedgerClientException(messageBuilder.toString());
    }


    public static File findFileSk(String domainName, User user, String cryptoDir) {
        File directory = getSkConfigPath(domainName, user, cryptoDir);

        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));

        if (null == matches) {
            throw new RuntimeException(format("Matches returned null does %s directory exist?", directory
                    .getAbsoluteFile().getName()));
        }

        if (matches.length != 1) {
            throw new RuntimeException(format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile
                    ().getName(), matches.length));
        }
        return matches[0];
    }

    private static File getSkConfigPath(String domainName, User user, String cryptoDir) {
        if (user.isAdmin())
            return Paths.get(cryptoDir,
                    "/peerOrganizations/",
                    domainName, format("/users/" + user.getName() + "@%s/msp/keystore", domainName))
                    .toFile();
        return Paths.get(cryptoDir,
                domainName, user.getName(), "keystore").toFile();

    }

    public static File getCertConfigPath(String domainName, User user, String cryptoDir) {
        if (user.isAdmin())
            return Paths.get(cryptoDir, "/peerOrganizations/",
                    domainName,
                    format("/users/" + user.getName() + "@%s/msp/signcerts/" + user.getName() + "@%s-cert.pem", domainName,
                            domainName)).toFile();
        return Paths.get(cryptoDir,
                domainName, user.getName(), "ca-cert.pem").toFile();
    }

    public static PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException, NoSuchProviderException,
            NoSuchAlgorithmException, InvalidKeySpecException {
        final Reader pemReader = new StringReader(new String(data));

        final PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }
        Security.addProvider(new BouncyCastleProvider());

        PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getPrivateKey(pemPair);

        return privateKey;
    }

    public static void main(String[] args) {
        User user = new User();
        user.setName("test2");
        Set roles = new HashSet();
        roles.add("user");
        user.setRoles(roles);
        System.out.println(getCertConfigPath("org1.example.com", user, "/Users/ascatox/Desktop/crypto-users"));
        System.out.println(findFileSk("org1.example.com", user, "/Users/ascatox/Desktop/crypto-users").getAbsoluteFile());
    }

}
