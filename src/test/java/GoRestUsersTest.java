import POJOClasses.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUsersTest {

    public String randomName() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public String randomEmail() {
        return RandomStringUtils.randomAlphabetic(6) + "@techno.com";
    }

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;

    @BeforeClass
    public void setUp() {
        baseURI = "https://gorest.co.in/public/v2/users";

        requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer 1352035115bdf297fee05d2110140e048fa57732bcdf430aa119426721d3f505")
                .setContentType(ContentType.JSON)
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .expectContentType(ContentType.JSON)
                .build();
    }

    @Test
    void getUsersList() {
        given()
                .spec(requestSpecification)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("", hasSize(10))
                .spec(responseSpecification);
    }

    @Test
    void createNewUser() {
        given()
                .spec(requestSpecification)
                .body("{\"name\":\"" + randomName() + "\",\"email\":\"" + randomEmail() + "\",\"gender\":\"male\",\"status\":\"active\"}")
                .when()
                .post()
                .then()
                .statusCode(201)
                .spec(responseSpecification);
    }

    @Test
    void createNewUserWithMaps() {
        Map<String, String> newUser = new HashMap<>();
        newUser.put("name", randomName());
        newUser.put("email", randomEmail());
        newUser.put("gender", "female");
        newUser.put("status", "active");

        given()
                .spec(requestSpecification)
                .body(newUser)
                .when()
                .post()
                .then()
                .statusCode(201)
                .spec(responseSpecification)
                .body("email", equalTo(newUser.get("email")));
    }

    User newUser;
    User userFromResponse;

    @Test
    void createNewUserWithObject() {
//        User newUser = new User(randomName(),randomEmail(),"male", "active");

        newUser = new User();
        newUser.setName(randomName());
        newUser.setEmail(randomEmail());
        newUser.setGender("male");
        newUser.setStatus("active");

        userFromResponse = given()
                .spec(requestSpecification)
                .body(newUser)
                .when()
                .post()
                .then()
                .statusCode(201)
                .spec(responseSpecification)
                .body("email", equalTo(newUser.getEmail()))
                .extract().as(User.class);
    }

    /**
     * Write create user negative test
     **/
    @Test(dependsOnMethods = "createNewUserWithObject")
    void createUserNegativeTest() {
        newUser.setName(randomName());
        newUser.setGender("female");

        given()
                .spec(requestSpecification)
                .body(newUser)
                .when()
                .post()
                .then()
                .statusCode(422)
                .spec(responseSpecification)
                .body("[0].message", equalTo("has already been taken"));
    }

    /**
     * get the user you created in createNewUserWithObject test
     **/

    @Test(dependsOnMethods = "createNewUserWithObject")
    void getUserById() {
        given()
                .spec(requestSpecification)
                .pathParam("userId", userFromResponse.getId())
                .when()
                .get("/{userId}")
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .body("email", equalTo(newUser.getEmail()));
    }

    /**
     * Update the user you created in createNewUserWithObject
     **/

    @Test(dependsOnMethods = "createNewUserWithObject")
    void updateUser() {
        userFromResponse.setName(randomName());
        userFromResponse.setEmail(randomEmail());

        given()
                .spec(requestSpecification)
                .body(userFromResponse)
                .pathParam("userId", userFromResponse.getId())
                .when()
                .put("/{userId}")
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .body("name",equalTo(userFromResponse.getName()))
                .body("email",equalTo(userFromResponse.getEmail()));
    }

    /**
     * Delete the user you created in createNewUserWithObject
     **/

    @Test(dependsOnMethods = "createNewUserWithObject")
    void deleteUser(){

        given()
                .spec(requestSpecification)
                .pathParam("userId",userFromResponse.getId())
                .when()
                .delete("/{userId}")
                .then()
                .statusCode(204);
    }

    /**
     * create delete user negative test
     **/

    @Test(dependsOnMethods = {"createNewUserWithObject", "deleteUser"})
    void deleteUserNegativeTest(){

        given()
                .spec(requestSpecification)
                .pathParam("userId",userFromResponse.getId())
                .when()
                .delete("/{userId}")
                .then()
                .statusCode(404);
    }

    @Test(dependsOnMethods = {"createNewUserWithObject", "deleteUser"})
    void getUserByIdNegativeAfterDelete() {
        given()
                .spec(requestSpecification)
                .pathParam("userId", userFromResponse.getId())
                .when()
                .get("/{userId}")
                .then()
                .statusCode(404);
    }
}
