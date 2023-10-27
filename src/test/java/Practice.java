import POJOClasses.ToDo;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Practice {
    /**
     * Task 1
     * write a request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * Convert Into POJO
     */

    @Test
    void task1(){
       ToDo toDo = given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .statusCode(200)
                .extract().as(ToDo.class);

        System.out.println("toDo = " + toDo);



    }
}
