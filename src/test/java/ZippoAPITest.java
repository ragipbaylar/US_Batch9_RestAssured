import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoAPITest {

    @Test
    void test1(){
        given()  // preparation(token, request body, parameters, cookies...)

                .when() //for url, request method(get, post, put, patch, delete)
                        //get, post, put, patch, delete don't belong to postman.
                        // they are known as http methods. All programming languages use these methods

                .then(); // response(response body, tests, extract data from the response...)
    }

    @Test
    void statusCodeTest(){
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210") // Set up request method and url

                .then()
                .log().body() // prints response body to the console
                .log().status() // prints status code
                .statusCode(200); // Testing the status code
    }

    @Test
    void contentTypeTest(){
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .contentType(ContentType.JSON); // tests if the response is in JSON format
    }

    @Test
    void countryInformationTest(){
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("country",equalTo("United States")); // Test if the country value is United States
                                                                    // We used Hamcrest methods to write tests
    }

    // Send a request to "http://api.zippopotam.us/us/90210"
    // and check if the state is "California"
    @Test
    void stateInformationTest(){

        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places[0].state", equalTo("California"));
    }

    // Send a request to "http://api.zippopotam.us/us/90210"
    // and check if the state abbreviation is "CA"
    @Test
    void stateAbbreviationTest(){
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places[0].'state abbreviation'",equalTo("CA"));
    }

    // Postman                                  Rest Assured
    // pm.response.json()                       body()
    // pm.response.json().country               body("country")
    // pm.response.json().places[0].state       body("places[0].state")

    // Send a request to "http://api.zippopotam.us/tr/01000"
    // and check if the body has "Büyükdikili Köyü"
    @Test
    void bodyHasItemTest(){

        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .body("places.'place name'", hasItem("Büyükdikili Köyü"));

        // When we don't use index it gets all place names from the response and creates an array with them.
        // hasItem checks if that array contains "Büyükdikili Köyü" value in it
    }





}
