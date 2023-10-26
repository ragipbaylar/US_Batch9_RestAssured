import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoAPITest {

    @Test
    void test1() {
        given()  // preparation(token, request body, parameters, cookies...)

                .when() //for url, request method(get, post, put, patch, delete)
                //get, post, put, patch, delete don't belong to postman.
                // they are known as http methods. All programming languages use these methods

                .then(); // response(response body, tests, extract data from the response...)
    }

    @Test
    void statusCodeTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210") // Set up request method and url

                .then()
                .log().body() // prints response body to the console
                .log().status() // prints status code
                .statusCode(200); // Testing the status code
    }

    @Test
    void contentTypeTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .contentType(ContentType.JSON); // tests if the response is in JSON format
    }

    @Test
    void countryInformationTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("country", equalTo("United States")); // Test if the country value is United States
        // We used Hamcrest methods to write tests
    }

    // Send a request to "http://api.zippopotam.us/us/90210"
    // and check if the state is "California"
    @Test
    void stateInformationTest() {

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
    void stateAbbreviationTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places[0].'state abbreviation'", equalTo("CA"));
    }

    // Postman                                  Rest Assured
    // pm.response.json()                       body()
    // pm.response.json().country               body("country")
    // pm.response.json().places[0].state       body("places[0].state")

    // Send a request to "http://api.zippopotam.us/tr/01000"
    // and check if the body has "Büyükdikili Köyü"
    @Test
    void bodyArrayHasItemTest() {

        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .body("places.'place name'", hasItem("Büyükdikili Köyü"));

        // When we don't use index it gets all place names from the response and creates an array with them.
        // hasItem checks if that array contains "Büyükdikili Köyü" value in it
    }

    @Test
    void arrayHasSizeTest1() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .body("places.'place name'", hasSize(71)); // tests the size of the place name array
    }

    @Test
    void arrayHasSizeTest2() {

        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places.'place name'", hasSize(1));
    }

    @Test
    void multipleTest() {

        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .statusCode(200)
                .body("places", hasSize(71))
                .body("places.'place name'", hasItem("Büyükdikili Köyü"))
                .body("country", equalTo("Turkey"))
                .body("places.state", hasItem("Adana"));
        // If one test fails the entire @Test fails
    }

    // Parameters
    // There are 2 types of parameters.
    //      1) Path Parameters ->  http://api.zippopotam.us/tr/01000
    //      2) Query Parameters -> https://gorest.co.in/public/v1/users?page=3

    @Test
    void pathParametersTest1() {
        String countryCode = "us";
        String zipCode = "90210";

        given()
                .pathParam("countryCode", countryCode)
                .pathParam("zipCode", zipCode)
                .log().uri() // prints the request url
                .when()
                .get("http://api.zippopotam.us/{countryCode}/{zipCode}")
                .then()
                .log().body()
                .statusCode(200);
    }

    // send a get request for zipcodes between 90210 and 90213 and verify that in all responses the size
    // of the place array is 1

    @Test
    void pathParametersTest2() {

        for (int i = 90210; i <= 90213; i++) {
            given()
                    .pathParam("countryCode", "us")
                    .pathParam("zipCode", i)
                    .log().uri() // prints the request url
                    .when()
                    .get("http://api.zippopotam.us/{countryCode}/{zipCode}")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("places", hasSize(1))
                    .body("'post code'", equalTo(String.valueOf(i)));
        }
    }

    @Test
    void queryParametersTest1() {
        given()
                .param("page", 3)
                .pathParam("APIName", "users")
                .log().uri()
                .when()
                .get("https://gorest.co.in/public/v1/{APIName}")
                .then()
                .log().body()
                .statusCode(200);
    }

    // send the same request for the pages between 1-10 and check if
    // the page number we send from request and page number we get from response are the same
    @Test
    void queryParameterTest2() {
        for (int i = 1; i <= 10; i++) {
            given()
                    .param("page", i)
                    .pathParam("APIName", "users")
                    .log().uri()
                    .when()
                    .get("https://gorest.co.in/public/v1/{APIName}")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("meta.pagination.page", equalTo(i));
        }
    }

    // Write the same test with Data Provider
    @Test(dataProvider = "parameters")
    void queryParameterTestWithDataProvider(int pageNumber, String apiName) {

        given()
                .param("page", pageNumber)
                .pathParam("APIName", apiName)
                .log().uri()
                .when()
                .get("https://gorest.co.in/public/v1/{APIName}")
                .then()
                .log().body()
                .statusCode(200)
                .body("meta.pagination.page", equalTo(pageNumber));
    }

    @DataProvider
    public Object[][] parameters() {

        Object[][] parameters = {
                {1, "users"},
                {2, "users"},
                {3, "users"},
                {4, "users"},
                {5, "users"},
                {6, "users"},
                {7, "users"},
                {8, "users"},
                {9, "users"},
                {10, "users"},
        };
        return parameters;
    }

    @BeforeClass
    public void setUp() {
        baseURI = "https://gorest.co.in/public/v1";
        // if the request url in the request method doesn't have http part
        // rest assured puts baseURI to the beginning of the url in the request method

        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .log(LogDetail.BODY)
                .addPathParam("APIName", "users")
                .addParam("page",3)
                .build();

        ResponseSpecification responseSpecification = new ResponseSpecBuilder()
                .log(LogDetail.BODY) // Continue Tomorrow

    }

    @Test
    void baeURITest(){
        given()
                .param("page", 3)
                .pathParam("APIName", "users")
                .log().uri()
                .when()
                .get("/{APIName}")
                .then()
                .log().body()
                .statusCode(200)
                .body("meta.pagination.page", equalTo(3));
    }

    @Test
}
