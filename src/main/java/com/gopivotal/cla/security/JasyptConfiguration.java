
package com.gopivotal.cla.security;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfiguration {

    @Bean
    PBEStringEncryptor stringEncryptor(@Value("${encryption.key}") String encryptionKey) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setKeyObtentionIterations(1000);
        encryptor.setPassword(encryptionKey);

        HibernatePBEEncryptorRegistry.getInstance().registerPBEStringEncryptor("basicStringEncryptor", encryptor);

        return encryptor;
    }
}
