package org.orcid.persistence.test.mocks;

import org.orcid.persistence.jpa.entities.EmailEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.orcid.persistence.test.mocks.MocksHelper.*;

public class EmailEntityMocks {

    private static final Map<String, EmailEntity> MOCKS = new HashMap<>();

    static {
        try {
            EmailEntity e_fa755fdf4b = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_fa755fdf4b);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), e_fa755fdf4b);
            e_fa755fdf4b.setEmail("spike@milligan.com");
            e_fa755fdf4b.setId("fa755fdf4ba30ea92bbbd382f4787526d162110cd83192a3bec180e6a09396b5");
            e_fa755fdf4b.setOrcid("4444-4444-4444-4441");
            e_fa755fdf4b.setPrimary(true);
            e_fa755fdf4b.setCurrent(true);
            e_fa755fdf4b.setVerified(true);
            e_fa755fdf4b.setVisibility("LIMITED");
            e_fa755fdf4b.setSourceId("4444-4444-4444-4441");
            MOCKS.put("fa755fdf4ba30ea92bbbd382f4787526d162110cd83192a3bec180e6a09396b5", e_fa755fdf4b);

            EmailEntity e_4266e33c87 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_4266e33c87);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), e_4266e33c87);
            e_4266e33c87.setEmail("public@email.com");
            e_4266e33c87.setId("4266e33c87bf6a2410abe5a37a7c0aae3630a6ac18fb765f7ec2d570c687d898");
            e_4266e33c87.setOrcid("4444-4444-4444-4441");
            e_4266e33c87.setPrimary(false);
            e_4266e33c87.setCurrent(true);
            e_4266e33c87.setVerified(true);
            e_4266e33c87.setVisibility("PUBLIC");
            e_4266e33c87.setSourceId("4444-4444-4444-4441");
            MOCKS.put("4266e33c87bf6a2410abe5a37a7c0aae3630a6ac18fb765f7ec2d570c687d898", e_4266e33c87);

            EmailEntity e_537969d9e9 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_537969d9e9);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), e_537969d9e9);
            e_537969d9e9.setEmail("limited@email.com");
            e_537969d9e9.setId("537969d9e9df42b9ef3945c3c68ea08aeee2b81dae7114786f5bffbd471e4432");
            e_537969d9e9.setOrcid("4444-4444-4444-4441");
            e_537969d9e9.setPrimary(false);
            e_537969d9e9.setCurrent(true);
            e_537969d9e9.setVerified(true);
            e_537969d9e9.setVisibility("LIMITED");
            e_537969d9e9.setSourceId("4444-4444-4444-4441");
            MOCKS.put("537969d9e9df42b9ef3945c3c68ea08aeee2b81dae7114786f5bffbd471e4432", e_537969d9e9);

            EmailEntity e_d0ef05afca = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_d0ef05afca);
            injectLastModified(parseDate("2016-01-01 12:22:00.00"), e_d0ef05afca);
            e_d0ef05afca.setEmail("1@deprecate.com");
            e_d0ef05afca.setId("d0ef05afcac77e924955030b14a5debfb69b173e21b453cfe21e52e385f82cf1");
            e_d0ef05afca.setOrcid("4444-4444-4444-4441");
            e_d0ef05afca.setPrimary(false);
            e_d0ef05afca.setCurrent(false);
            e_d0ef05afca.setVerified(true);
            e_d0ef05afca.setVisibility("PRIVATE");
            e_d0ef05afca.setSourceId("4444-4444-4444-4441");
            MOCKS.put("d0ef05afcac77e924955030b14a5debfb69b173e21b453cfe21e52e385f82cf1", e_d0ef05afca);

            EmailEntity e_030d5c18e5 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_030d5c18e5);
            injectLastModified(parseDate("2016-01-02 15:31:00.00"), e_030d5c18e5);
            e_030d5c18e5.setEmail("2@deprecate.com");
            e_030d5c18e5.setId("030d5c18e52798a1c7cc5635b931dd1c0ecd16d96c9f22528a0ad83f65589990");
            e_030d5c18e5.setOrcid("4444-4444-4444-4441");
            e_030d5c18e5.setPrimary(false);
            e_030d5c18e5.setCurrent(false);
            e_030d5c18e5.setVerified(false);
            e_030d5c18e5.setVisibility("PRIVATE");
            e_030d5c18e5.setSourceId("4444-4444-4444-4441");
            MOCKS.put("030d5c18e52798a1c7cc5635b931dd1c0ecd16d96c9f22528a0ad83f65589990", e_030d5c18e5);

            EmailEntity e_82f825b039 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_82f825b039);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_82f825b039);
            e_82f825b039.setEmail("4441-10@milligan.com");
            e_82f825b039.setId("82f825b0395bea46d7360ee05758051aab8371b6a02e5bab32d278646457e772");
            e_82f825b039.setOrcid("4444-4444-4444-4499");
            e_82f825b039.setPrimary(true);
            e_82f825b039.setCurrent(true);
            e_82f825b039.setVerified(true);
            e_82f825b039.setVisibility("PRIVATE");
            e_82f825b039.setSourceId("4444-4444-4444-4441");
            MOCKS.put("82f825b0395bea46d7360ee05758051aab8371b6a02e5bab32d278646457e772", e_82f825b039);

            EmailEntity e_3a5e03495b = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_3a5e03495b);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_3a5e03495b);
            e_3a5e03495b.setEmail("4444-4444-4444-4498@milligan.com");
            e_3a5e03495b.setId("3a5e03495b39d0d18738d215d5f73141799cd3e96b40aa5c24ff54d58ced9c9f");
            e_3a5e03495b.setOrcid("4444-4444-4444-4498");
            e_3a5e03495b.setPrimary(true);
            e_3a5e03495b.setCurrent(true);
            e_3a5e03495b.setVerified(true);
            e_3a5e03495b.setVisibility("PRIVATE");
            e_3a5e03495b.setSourceId("4444-4444-4444-4441");
            MOCKS.put("3a5e03495b39d0d18738d215d5f73141799cd3e96b40aa5c24ff54d58ced9c9f", e_3a5e03495b);

            EmailEntity e_c8017625d3 = new EmailEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), e_c8017625d3);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), e_c8017625d3);
            e_c8017625d3.setEmail("michael@bentine.com");
            e_c8017625d3.setId("c8017625d38c53394062d1b5ea1a30b76fca792523435f2e87d20fea4172e97d");
            e_c8017625d3.setOrcid("4444-4444-4444-4442");
            e_c8017625d3.setPrimary(true);
            e_c8017625d3.setCurrent(true);
            e_c8017625d3.setVerified(true);
            e_c8017625d3.setVisibility("LIMITED");
            e_c8017625d3.setSourceId("4444-4444-4444-4442");
            MOCKS.put("c8017625d38c53394062d1b5ea1a30b76fca792523435f2e87d20fea4172e97d", e_c8017625d3);

            EmailEntity e_1293bdbb7e = new EmailEntity();
            injectDateCreated(parseDate("2016-08-09 12:42:00.00"), e_1293bdbb7e);
            injectLastModified(parseDate("2016-08-10 13:12:00.00"), e_1293bdbb7e);
            e_1293bdbb7e.setEmail("MiXeD@cASe.com");
            e_1293bdbb7e.setId("1293bdbb7ee3963ffa4f8c3cd0182b114a2d702d85c2811797712f09d00d5027");
            e_1293bdbb7e.setOrcid("4444-4444-4444-4442");
            e_1293bdbb7e.setPrimary(false);
            e_1293bdbb7e.setCurrent(true);
            e_1293bdbb7e.setVerified(true);
            e_1293bdbb7e.setVisibility("PRIVATE");
            e_1293bdbb7e.setSourceId("4444-4444-4444-4442");
            MOCKS.put("1293bdbb7ee3963ffa4f8c3cd0182b114a2d702d85c2811797712f09d00d5027", e_1293bdbb7e);

            EmailEntity e_93e5ca91a3 = new EmailEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), e_93e5ca91a3);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), e_93e5ca91a3);
            e_93e5ca91a3.setEmail("peter@sellers.com");
            e_93e5ca91a3.setId("93e5ca91a3a030187b723f7934992cd1cab061f32b568c57a276af58f1c2f77f");
            e_93e5ca91a3.setOrcid("4444-4444-4444-4443");
            e_93e5ca91a3.setPrimary(true);
            e_93e5ca91a3.setCurrent(true);
            e_93e5ca91a3.setVerified(false);
            e_93e5ca91a3.setVisibility("PRIVATE");
            e_93e5ca91a3.setSourceId("4444-4444-4444-4443");
            MOCKS.put("93e5ca91a3a030187b723f7934992cd1cab061f32b568c57a276af58f1c2f77f", e_93e5ca91a3);

            EmailEntity e_2c5cb98057 = new EmailEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), e_2c5cb98057);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), e_2c5cb98057);
            e_2c5cb98057.setEmail("teddybass@semantico.com");
            e_2c5cb98057.setId("2c5cb98057d742ca06eff946aa12a2eb3f9a383159ec85097d8a525fc260cbe7");
            e_2c5cb98057.setOrcid("4444-4444-4444-4443");
            e_2c5cb98057.setPrimary(false);
            e_2c5cb98057.setCurrent(true);
            e_2c5cb98057.setVerified(false);
            e_2c5cb98057.setVisibility("PRIVATE");
            e_2c5cb98057.setSourceId("4444-4444-4444-4443");
            MOCKS.put("2c5cb98057d742ca06eff946aa12a2eb3f9a383159ec85097d8a525fc260cbe7", e_2c5cb98057);

            EmailEntity e_342c1db9d1 = new EmailEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), e_342c1db9d1);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), e_342c1db9d1);
            e_342c1db9d1.setEmail("teddybass2@semantico.com");
            e_342c1db9d1.setId("342c1db9d167945cb61aeb83ee2e4eb9c2774a558dde128649a2a26c30b060f2");
            e_342c1db9d1.setOrcid("4444-4444-4444-4443");
            e_342c1db9d1.setPrimary(false);
            e_342c1db9d1.setCurrent(true);
            e_342c1db9d1.setVerified(false);
            e_342c1db9d1.setVisibility("LIMITED");
            e_342c1db9d1.setSourceId("4444-4444-4444-4443");
            MOCKS.put("342c1db9d167945cb61aeb83ee2e4eb9c2774a558dde128649a2a26c30b060f2", e_342c1db9d1);

            EmailEntity e_f956395f84 = new EmailEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), e_f956395f84);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), e_f956395f84);
            e_f956395f84.setEmail("teddybass3public@semantico.com");
            e_f956395f84.setId("f956395f84a3ed21f8350dfb478daa0373b5609cb14b391fd1927168094f9e59");
            e_f956395f84.setOrcid("4444-4444-4444-4443");
            e_f956395f84.setPrimary(false);
            e_f956395f84.setCurrent(true);
            e_f956395f84.setVerified(false);
            e_f956395f84.setVisibility("PUBLIC");
            e_f956395f84.setSourceId("4444-4444-4444-4443");
            MOCKS.put("f956395f84a3ed21f8350dfb478daa0373b5609cb14b391fd1927168094f9e59", e_f956395f84);

            EmailEntity e_7ec2c81339 = new EmailEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), e_7ec2c81339);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), e_7ec2c81339);
            e_7ec2c81339.setEmail("teddybass3private@semantico.com");
            e_7ec2c81339.setId("7ec2c813391c22a9b190062fe07a78b2a409e76eb7877b6bc01097422ca24a79");
            e_7ec2c81339.setOrcid("4444-4444-4444-4443");
            e_7ec2c81339.setPrimary(false);
            e_7ec2c81339.setCurrent(true);
            e_7ec2c81339.setVerified(true);
            e_7ec2c81339.setVisibility("PRIVATE");
            e_7ec2c81339.setClientSourceId("APP-5555555555555555");
            MOCKS.put("7ec2c813391c22a9b190062fe07a78b2a409e76eb7877b6bc01097422ca24a79", e_7ec2c81339);

            EmailEntity e_bbc3d9d7f3 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_bbc3d9d7f3);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_bbc3d9d7f3);
            e_bbc3d9d7f3.setEmail("harry@secombe.com");
            e_bbc3d9d7f3.setId("bbc3d9d7f304fe0139f83ad7745cc54f5e8abb56dde4f5e1c2f966a47df63d17");
            e_bbc3d9d7f3.setOrcid("4444-4444-4444-4444");
            e_bbc3d9d7f3.setPrimary(true);
            e_bbc3d9d7f3.setCurrent(true);
            e_bbc3d9d7f3.setVerified(true);
            e_bbc3d9d7f3.setVisibility("PRIVATE");
            e_bbc3d9d7f3.setSourceId("4444-4444-4444-4444");
            MOCKS.put("bbc3d9d7f304fe0139f83ad7745cc54f5e8abb56dde4f5e1c2f966a47df63d17", e_bbc3d9d7f3);

            EmailEntity e_813bdeb15d = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_813bdeb15d);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_813bdeb15d);
            e_813bdeb15d.setEmail("aNdReW@tImOtHy.com");
            e_813bdeb15d.setId("813bdeb15d67a17a3eb119daf7098736edeb499746e45c703b06957e15c03dc0");
            e_813bdeb15d.setOrcid("4444-4444-4444-4445");
            e_813bdeb15d.setPrimary(true);
            e_813bdeb15d.setCurrent(true);
            e_813bdeb15d.setVerified(false);
            e_813bdeb15d.setVisibility("PRIVATE");
            e_813bdeb15d.setSourceId("4444-4444-4444-4441");
            MOCKS.put("813bdeb15d67a17a3eb119daf7098736edeb499746e45c703b06957e15c03dc0", e_813bdeb15d);

            EmailEntity e_955d69759f = new EmailEntity();
            injectDateCreated(parseDate("2011-06-29 15:31:00.00"), e_955d69759f);
            injectLastModified(parseDate("2011-07-02 15:31:00.00"), e_955d69759f);
            e_955d69759f.setEmail("angel2@montenegro.com");
            e_955d69759f.setId("955d69759f05bef175aff04ec0242a643442ae089f229d0c4124c525e5dd02a2");
            e_955d69759f.setOrcid("4444-4444-4444-4443");
            e_955d69759f.setPrimary(false);
            e_955d69759f.setCurrent(false);
            e_955d69759f.setVerified(false);
            e_955d69759f.setVisibility("PRIVATE");
            e_955d69759f.setSourceId("4444-4444-4444-4441");
            MOCKS.put("955d69759f05bef175aff04ec0242a643442ae089f229d0c4124c525e5dd02a2", e_955d69759f);

            EmailEntity e_2965a4115f = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_2965a4115f);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_2965a4115f);
            e_2965a4115f.setEmail("angel1@montenegro.com");
            e_2965a4115f.setId("2965a4115f2639b43e5feb78adddfff52e5324813b6c12b4e25ebdac052f72df");
            e_2965a4115f.setOrcid("4444-4444-4444-4444");
            e_2965a4115f.setPrimary(true);
            e_2965a4115f.setCurrent(false);
            e_2965a4115f.setVerified(true);
            e_2965a4115f.setVisibility("PRIVATE");
            e_2965a4115f.setSourceId("4444-4444-4444-4441");
            MOCKS.put("2965a4115f2639b43e5feb78adddfff52e5324813b6c12b4e25ebdac052f72df", e_2965a4115f);

            EmailEntity e_716e061d83 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_716e061d83);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_716e061d83);
            e_716e061d83.setEmail("andrew2@timothy.com");
            e_716e061d83.setId("716e061d83dcac0e6f7efd4574a6e4c0cd9de4520731819faa054375a0e8c9a2");
            e_716e061d83.setOrcid("4444-4444-4444-4445");
            e_716e061d83.setPrimary(false);
            e_716e061d83.setCurrent(false);
            e_716e061d83.setVerified(false);
            e_716e061d83.setVisibility("PRIVATE");
            e_716e061d83.setSourceId("4444-4444-4444-4441");
            MOCKS.put("716e061d83dcac0e6f7efd4574a6e4c0cd9de4520731819faa054375a0e8c9a2", e_716e061d83);

            EmailEntity e_245ff4ed28 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_245ff4ed28);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_245ff4ed28);
            e_245ff4ed28.setEmail("billie@holiday.com");
            e_245ff4ed28.setId("245ff4ed28f695be77c90999acdbcb372f50e82491768e45f6a01f2f18c56a4a");
            e_245ff4ed28.setOrcid("4444-4444-4444-4446");
            e_245ff4ed28.setPrimary(true);
            e_245ff4ed28.setCurrent(true);
            e_245ff4ed28.setVerified(true);
            e_245ff4ed28.setVisibility("PRIVATE");
            e_245ff4ed28.setSourceId("4444-4444-4444-4446");
            MOCKS.put("245ff4ed28f695be77c90999acdbcb372f50e82491768e45f6a01f2f18c56a4a", e_245ff4ed28);

            EmailEntity e_f660ab912e = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_f660ab912e);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_f660ab912e);
            e_f660ab912e.setEmail("test@test.com");
            e_f660ab912e.setId("f660ab912ec121d1b1e928a0bb4bc61b15f5ad44d5efdc4e1c92a25e99b8e44a");
            e_f660ab912e.setOrcid("4444-4444-4444-4446");
            e_f660ab912e.setPrimary(false);
            e_f660ab912e.setCurrent(true);
            e_f660ab912e.setVerified(true);
            e_f660ab912e.setVisibility("PRIVATE");
            e_f660ab912e.setSourceId("4444-4444-4444-4446");
            MOCKS.put("f660ab912ec121d1b1e928a0bb4bc61b15f5ad44d5efdc4e1c92a25e99b8e44a", e_f660ab912e);

            EmailEntity e_b238c595b8 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_b238c595b8);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_b238c595b8);
            e_b238c595b8.setEmail("user@user.com");
            e_b238c595b8.setId("b238c595b84321b35b8e57610c49523d4e3b9b5b5d090923e9e54f4b929bedba");
            e_b238c595b8.setOrcid("4444-4444-4444-4446");
            e_b238c595b8.setPrimary(false);
            e_b238c595b8.setCurrent(true);
            e_b238c595b8.setVerified(true);
            e_b238c595b8.setVisibility("PRIVATE");
            e_b238c595b8.setSourceId("4444-4444-4444-4446");
            MOCKS.put("b238c595b84321b35b8e57610c49523d4e3b9b5b5d090923e9e54f4b929bedba", e_b238c595b8);

            EmailEntity e_ed69d58e68 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_ed69d58e68);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_ed69d58e68);
            e_ed69d58e68.setEmail("otis@reading.com");
            e_ed69d58e68.setId("ed69d58e68ea6f821f13f0a5006742d3065bb412bab8bc86899ae39638ddc7b5");
            e_ed69d58e68.setOrcid("4444-4444-4444-4447");
            e_ed69d58e68.setPrimary(true);
            e_ed69d58e68.setCurrent(true);
            e_ed69d58e68.setVerified(true);
            e_ed69d58e68.setVisibility("PRIVATE");
            e_ed69d58e68.setSourceId("4444-4444-4444-4447");
            MOCKS.put("ed69d58e68ea6f821f13f0a5006742d3065bb412bab8bc86899ae39638ddc7b5", e_ed69d58e68);

            EmailEntity e_0925f997eb = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_0925f997eb);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_0925f997eb);
            e_0925f997eb.setEmail("user@email.com");
            e_0925f997eb.setId("0925f997eb0d742678f66d2da134d15d842d57722af5f7605c4785cb5358831b");
            e_0925f997eb.setOrcid("4444-4444-4444-444X");
            e_0925f997eb.setPrimary(true);
            e_0925f997eb.setCurrent(true);
            e_0925f997eb.setVerified(true);
            e_0925f997eb.setVisibility("PUBLIC");
            e_0925f997eb.setSourceId("4444-4444-4444-444X");
            MOCKS.put("0925f997eb0d742678f66d2da134d15d842d57722af5f7605c4785cb5358831b", e_0925f997eb);

            EmailEntity e_18a88eff6e = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_18a88eff6e);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_18a88eff6e);
            e_18a88eff6e.setEmail("admin@user.com");
            e_18a88eff6e.setId("18a88eff6eac8ebd45ac3451ec2c60695d614ed9cbe7b56175d76aa780841146");
            e_18a88eff6e.setOrcid("4444-4444-4444-4440");
            e_18a88eff6e.setPrimary(true);
            e_18a88eff6e.setCurrent(true);
            e_18a88eff6e.setVerified(true);
            e_18a88eff6e.setVisibility("PRIVATE");
            e_18a88eff6e.setSourceId("4444-4444-4444-4441");
            MOCKS.put("18a88eff6eac8ebd45ac3451ec2c60695d614ed9cbe7b56175d76aa780841146", e_18a88eff6e);

            EmailEntity e_8f649f6d12 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_8f649f6d12);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_8f649f6d12);
            e_8f649f6d12.setEmail("public_0000-0000-0000-0001@test.orcid.org");
            e_8f649f6d12.setId("8f649f6d12203f020ee26467547432add46bc5395ff8dd72242fa2d7aa4fc04a");
            e_8f649f6d12.setOrcid("0000-0000-0000-0001");
            e_8f649f6d12.setPrimary(true);
            e_8f649f6d12.setCurrent(true);
            e_8f649f6d12.setVerified(false);
            e_8f649f6d12.setVisibility("PUBLIC");
            e_8f649f6d12.setClientSourceId("APP-5555555555555555");
            MOCKS.put("8f649f6d12203f020ee26467547432add46bc5395ff8dd72242fa2d7aa4fc04a", e_8f649f6d12);

            EmailEntity e_afcsd87b85 = new EmailEntity();
            injectDateCreated(parseDate("2018-01-01 00:00:00.00"), e_afcsd87b85);
            injectLastModified(parseDate("2018-01-01 00:00:00.00"), e_afcsd87b85);
            e_afcsd87b85.setEmail("limited_verified_0000-0000-0000-0001@test.orcid.org");
            e_afcsd87b85.setId("afcsd87b8547a96sdfba9g7sfdbba87asdf542q7aqw4fxsd5d4g77g5s1a4fx1bbc");
            e_afcsd87b85.setOrcid("0000-0000-0000-0001");
            e_afcsd87b85.setPrimary(false);
            e_afcsd87b85.setCurrent(true);
            e_afcsd87b85.setVerified(true);
            e_afcsd87b85.setVisibility("LIMITED");
            e_afcsd87b85.setClientSourceId("APP-5555555555555555");
            MOCKS.put("afcsd87b8547a96sdfba9g7sfdbba87asdf542q7aqw4fxsd5d4g77g5s1a4fx1bbc", e_afcsd87b85);

            EmailEntity e_bb24ead143 = new EmailEntity();
            injectDateCreated(parseDate("2024-01-01 00:00:00.00"), e_bb24ead143);
            injectLastModified(parseDate("2024-01-01 00:00:00.00"), e_bb24ead143);
            e_bb24ead143.setEmail("verified_non_professional@nonprofessional.org");
            e_bb24ead143.setId("bb24ead143f1a58f97394436a0548e7bc3d30a539188accd5d82a28af4345778");
            e_bb24ead143.setOrcid("0000-0000-0000-0001");
            e_bb24ead143.setPrimary(false);
            e_bb24ead143.setCurrent(false);
            e_bb24ead143.setVerified(true);
            e_bb24ead143.setVisibility("PUBLIC");
            e_bb24ead143.setClientSourceId("APP-5555555555555555");
            MOCKS.put("bb24ead143f1a58f97394436a0548e7bc3d30a539188accd5d82a28af4345778", e_bb24ead143);

            EmailEntity e_ecdc2c6aef = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_ecdc2c6aef);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_ecdc2c6aef);
            e_ecdc2c6aef.setEmail("public_0000-0000-0000-0002@test.orcid.org");
            e_ecdc2c6aef.setId("ecdc2c6aef7aa5aa4012b9e5f262de2214c9b0e3f3b0201da0eeebc7531ae018");
            e_ecdc2c6aef.setOrcid("0000-0000-0000-0002");
            e_ecdc2c6aef.setPrimary(true);
            e_ecdc2c6aef.setCurrent(true);
            e_ecdc2c6aef.setVerified(false);
            e_ecdc2c6aef.setVisibility("PUBLIC");
            e_ecdc2c6aef.setClientSourceId("APP-5555555555555555");
            MOCKS.put("ecdc2c6aef7aa5aa4012b9e5f262de2214c9b0e3f3b0201da0eeebc7531ae018", e_ecdc2c6aef);

            EmailEntity e_c3ba0b26ac = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_c3ba0b26ac);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_c3ba0b26ac);
            e_c3ba0b26ac.setEmail("public_0000-0000-0000-0003@test.orcid.org");
            e_c3ba0b26ac.setId("c3ba0b26aceb622a04908c202927db3633bd1e748e049e4bd2b070d29b189aa4");
            e_c3ba0b26ac.setOrcid("0000-0000-0000-0003");
            e_c3ba0b26ac.setPrimary(true);
            e_c3ba0b26ac.setCurrent(true);
            e_c3ba0b26ac.setVerified(true);
            e_c3ba0b26ac.setVisibility("PUBLIC");
            e_c3ba0b26ac.setClientSourceId("APP-5555555555555555");
            MOCKS.put("c3ba0b26aceb622a04908c202927db3633bd1e748e049e4bd2b070d29b189aa4", e_c3ba0b26ac);

            EmailEntity e_f936d182c8 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_f936d182c8);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_f936d182c8);
            e_f936d182c8.setEmail("public_0000-0000-0000-0003@orcid.org");
            e_f936d182c8.setId("f936d182c82f831ccee9667ac7f8fc6b9107fa5647b0c4a8250759462d5707d0");
            e_f936d182c8.setOrcid("0000-0000-0000-0003");
            e_f936d182c8.setPrimary(false);
            e_f936d182c8.setCurrent(true);
            e_f936d182c8.setVerified(true);
            e_f936d182c8.setVisibility("PUBLIC");
            e_f936d182c8.setSourceId("0000-0000-0000-0003");
            MOCKS.put("f936d182c82f831ccee9667ac7f8fc6b9107fa5647b0c4a8250759462d5707d0", e_f936d182c8);

            EmailEntity e_71d1e18acf = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_71d1e18acf);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_71d1e18acf);
            e_71d1e18acf.setEmail("limited_0000-0000-0000-0003@test.orcid.org");
            e_71d1e18acf.setId("71d1e18acf189e7b14e486a53691cef30249a3aedfd5b4c988b1754eb179e6b9");
            e_71d1e18acf.setOrcid("0000-0000-0000-0003");
            e_71d1e18acf.setPrimary(false);
            e_71d1e18acf.setCurrent(true);
            e_71d1e18acf.setVerified(true);
            e_71d1e18acf.setVisibility("LIMITED");
            e_71d1e18acf.setClientSourceId("APP-5555555555555555");
            MOCKS.put("71d1e18acf189e7b14e486a53691cef30249a3aedfd5b4c988b1754eb179e6b9", e_71d1e18acf);

            EmailEntity e_4cccdb9a83 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_4cccdb9a83);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_4cccdb9a83);
            e_4cccdb9a83.setEmail("private_0000-0000-0000-0003@test.orcid.org");
            e_4cccdb9a83.setId("4cccdb9a8342f8e7e7b730b0870664f9428f6958082957ed36e22997525fe7ce");
            e_4cccdb9a83.setOrcid("0000-0000-0000-0003");
            e_4cccdb9a83.setPrimary(false);
            e_4cccdb9a83.setCurrent(true);
            e_4cccdb9a83.setVerified(true);
            e_4cccdb9a83.setVisibility("PRIVATE");
            e_4cccdb9a83.setClientSourceId("APP-5555555555555555");
            MOCKS.put("4cccdb9a8342f8e7e7b730b0870664f9428f6958082957ed36e22997525fe7ce", e_4cccdb9a83);

            EmailEntity e_c1a6afc0ca = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_c1a6afc0ca);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_c1a6afc0ca);
            e_c1a6afc0ca.setEmail("self_limited_0000-0000-0000-0003@test.orcid.org");
            e_c1a6afc0ca.setId("c1a6afc0cace346a7f9c6cc56a28f063a8d034d1f00730f3103825ce229eac95");
            e_c1a6afc0ca.setOrcid("0000-0000-0000-0003");
            e_c1a6afc0ca.setPrimary(false);
            e_c1a6afc0ca.setCurrent(true);
            e_c1a6afc0ca.setVerified(true);
            e_c1a6afc0ca.setVisibility("LIMITED");
            e_c1a6afc0ca.setSourceId("0000-0000-0000-0003");
            MOCKS.put("c1a6afc0cace346a7f9c6cc56a28f063a8d034d1f00730f3103825ce229eac95", e_c1a6afc0ca);

            EmailEntity e_43e9409b44 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_43e9409b44);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_43e9409b44);
            e_43e9409b44.setEmail("self_private_0000-0000-0000-0003@test.orcid.org");
            e_43e9409b44.setId("43e9409b44f1875bf9df470b32555c298b6e3a5bd3a28a17b1cdc2cbeccab8d7");
            e_43e9409b44.setOrcid("0000-0000-0000-0003");
            e_43e9409b44.setPrimary(false);
            e_43e9409b44.setCurrent(false);
            e_43e9409b44.setVerified(false);
            e_43e9409b44.setVisibility("PRIVATE");
            e_43e9409b44.setSourceId("0000-0000-0000-0003");
            MOCKS.put("43e9409b44f1875bf9df470b32555c298b6e3a5bd3a28a17b1cdc2cbeccab8d7", e_43e9409b44);

            EmailEntity e_49919fd689 = new EmailEntity();
            injectDateCreated(parseDate("None"), e_49919fd689);
            injectLastModified(parseDate("2016-04-01 15:31:00.00"), e_49919fd689);
            e_49919fd689.setEmail("public_0000-0000-0000-0006@test.orcid.org");
            e_49919fd689.setId("49919fd6890f32d00cad6be9dbe277c3f1f84476d2ca0ec4dd74dbf03114b8d7");
            e_49919fd689.setOrcid("0000-0000-0000-0006");
            e_49919fd689.setPrimary(false);
            e_49919fd689.setCurrent(true);
            e_49919fd689.setVerified(true);
            e_49919fd689.setVisibility("PUBLIC");
            e_49919fd689.setClientSourceId("APP-5555555555555556");
            MOCKS.put("49919fd6890f32d00cad6be9dbe277c3f1f84476d2ca0ec4dd74dbf03114b8d7", e_49919fd689);

            EmailEntity e_a5533ff2a3 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_a5533ff2a3);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_a5533ff2a3);
            e_a5533ff2a3.setEmail("public_0000-0000-0000-0004@test.orcid.org");
            e_a5533ff2a3.setId("a5533ff2a39eae9687aea9906824e5ed9a65a350cfd8ec650b18593a2f9728b2");
            e_a5533ff2a3.setOrcid("0000-0000-0000-0004");
            e_a5533ff2a3.setPrimary(false);
            e_a5533ff2a3.setCurrent(false);
            e_a5533ff2a3.setVerified(false);
            e_a5533ff2a3.setVisibility("PUBLIC");
            e_a5533ff2a3.setSourceId("0000-0000-0000-0004");
            MOCKS.put("a5533ff2a39eae9687aea9906824e5ed9a65a350cfd8ec650b18593a2f9728b2", e_a5533ff2a3);

            EmailEntity e_4ffaa675b0 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_4ffaa675b0);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_4ffaa675b0);
            e_4ffaa675b0.setEmail("limited_0000-0000-0000-0004@test.orcid.org");
            e_4ffaa675b0.setId("4ffaa675b0a65aae1fbb715f0208d14f98fe15d11c52b0b1714f594c9237b324");
            e_4ffaa675b0.setOrcid("0000-0000-0000-0004");
            e_4ffaa675b0.setPrimary(false);
            e_4ffaa675b0.setCurrent(false);
            e_4ffaa675b0.setVerified(false);
            e_4ffaa675b0.setVisibility("LIMITED");
            e_4ffaa675b0.setSourceId("0000-0000-0000-0004");
            MOCKS.put("4ffaa675b0a65aae1fbb715f0208d14f98fe15d11c52b0b1714f594c9237b324", e_4ffaa675b0);

            EmailEntity e_eddeca51af = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_eddeca51af);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_eddeca51af);
            e_eddeca51af.setEmail("public_4444-4444-4444-4497@test.orcid.org");
            e_eddeca51af.setId("eddeca51af76b619be805d740a715921a666391dcc779f4eac7f0f71623456fc");
            e_eddeca51af.setOrcid("4444-4444-4444-4497");
            e_eddeca51af.setPrimary(true);
            e_eddeca51af.setCurrent(false);
            e_eddeca51af.setVerified(true);
            e_eddeca51af.setVisibility("PUBLIC");
            e_eddeca51af.setSourceId("4444-4444-4444-4497");
            MOCKS.put("eddeca51af76b619be805d740a715921a666391dcc779f4eac7f0f71623456fc", e_eddeca51af);

            EmailEntity e_d40c3bf051 = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_d40c3bf051);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_d40c3bf051);
            e_d40c3bf051.setEmail("limited_4444-4444-4444-4497@test.orcid.org");
            e_d40c3bf051.setId("d40c3bf0519f4584cbcd3da368b7c0def44a07b912d1e43bef1b5d695f7529b9");
            e_d40c3bf051.setOrcid("4444-4444-4444-4497");
            e_d40c3bf051.setPrimary(true);
            e_d40c3bf051.setCurrent(false);
            e_d40c3bf051.setVerified(true);
            e_d40c3bf051.setVisibility("LIMITED");
            e_d40c3bf051.setSourceId("4444-4444-4444-4497");
            MOCKS.put("d40c3bf0519f4584cbcd3da368b7c0def44a07b912d1e43bef1b5d695f7529b9", e_d40c3bf051);

            EmailEntity e_b21fcb985d = new EmailEntity();
            injectDateCreated(parseDate("2016-01-01 00:00:00.00"), e_b21fcb985d);
            injectLastModified(parseDate("2016-01-01 00:00:00.00"), e_b21fcb985d);
            e_b21fcb985d.setEmail("private_4444-4444-4444-4497@test.orcid.org");
            e_b21fcb985d.setId("b21fcb985dd882e44847aafbdd0fd4c7da7b9868e38b518d443ed951b3433e92");
            e_b21fcb985d.setOrcid("4444-4444-4444-4497");
            e_b21fcb985d.setPrimary(true);
            e_b21fcb985d.setCurrent(false);
            e_b21fcb985d.setVerified(true);
            e_b21fcb985d.setVisibility("PRIVATE");
            e_b21fcb985d.setSourceId("4444-4444-4444-4497");
            MOCKS.put("b21fcb985dd882e44847aafbdd0fd4c7da7b9868e38b518d443ed951b3433e92", e_b21fcb985d);

            EmailEntity e_5b569083f3 = new EmailEntity();
            injectDateCreated(new Date(), e_5b569083f3);
            injectLastModified(new Date(), e_5b569083f3);
            e_5b569083f3.setEmail("5555-5555-5555-5558@user.com");
            e_5b569083f3.setId("5b569083f34079f001b6c81bff6ec764c0c70c448f032dc79d930971aa407201");
            e_5b569083f3.setOrcid("5555-5555-5555-5558");
            e_5b569083f3.setPrimary(true);
            e_5b569083f3.setCurrent(true);
            e_5b569083f3.setVerified(true);
            e_5b569083f3.setVisibility("PRIVATE");
            e_5b569083f3.setSourceId("5555-5555-5555-5558");
            MOCKS.put("5b569083f34079f001b6c81bff6ec764c0c70c448f032dc79d930971aa407201", e_5b569083f3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static EmailEntity getEmail(String id) {
        return MOCKS.get(id);
    }

    public static Map<String, EmailEntity> getAllMocks() {
        return new HashMap<>(MOCKS);
    }

    private static Date parseDate(String dateStr) {
        try {
            if (dateStr.length() == 10) {
                return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            }
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
