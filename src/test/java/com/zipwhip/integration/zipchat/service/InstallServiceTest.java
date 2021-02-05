package com.zipwhip.integration.zipchat.service;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.zipwhip.integration.zipchat.service.RestConfig.RestConfigProps;

@Tag("smoke")
@Tag("development")
@Slf4j
@ExtendWith(SpringExtension.class)
@ActiveProfiles("prod")
@TestPropertySource({"classpath:application-test.yml", "classpath:application-prod.yml"} )
@Import(RestConfig.class)
class InstallServiceTest {

    @Autowired
    RestConfigProps restConfigProps;

    @Test
    public void test() {
        RestAssured.baseURI = restConfigProps.getHostname();
        assertThat(RestAssured.baseURI).isEqualTo("justo");
        given().log().everything().body("").then().response();
    }

}