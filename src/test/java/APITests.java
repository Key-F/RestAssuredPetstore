import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class APITests {

    private final int unexistingPetId = 232323493;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2/";
    }

    @Test  // Not BDD
    public void petNotFoundTestWithAssert() {
        RestAssured.baseURI += "pet/" + unexistingPetId;

        // Создаем объект requestSpecification
        requestSpecification = RestAssured.given();

        // Вызываем get метод, ответ кладем в response
        Response response = requestSpecification.get();

        // Выводим response на консоль
        System.out.println("Response: " + response.asPrettyString());

        assertEquals(404, response.statusCode(), "Не тот status code");
        assertEquals("HTTP/1.1 404 Not Found", response.statusLine(), "Не корректная status line");
        assertEquals("Pet not found", response.jsonPath().get("message"), "Не то сообщение об ошибке");
    }

    @Test  // Not BDD
    public void petNotFoundTest() {
        RestAssured.baseURI += "pet/" + unexistingPetId;

        // Создаем объект requestSpecification
        requestSpecification = RestAssured.given();

        // Вызываем get метод, ответ кладем в response
        Response response = requestSpecification.get();

        // Выводим response на консоль
        System.out.println("Response: " + response.asPrettyString());

        // Объект типа ValidatableResponse нужен для валидации ответа
        ValidatableResponse validatableResponse = response.then();

        // Проверям status code
        validatableResponse.statusCode(404);

        // Проверяем status line
        validatableResponse.statusLine("HTTP/1.1 404 Not Found");

        validatableResponse.body("message", equalTo("Pet not found"));
    }

    @Test
    public void petNotFoundTest_BDD() {
        given().when()
                .get(baseURI + "pet/{id}", unexistingPetId)
                .then()
                .log().all()
                .statusCode(404)
                .statusLine("HTTP/1.1 404 Not Found")
                .body("message", equalTo("Pet not found"));
    }

    @Test
    public void newPetTest() {
        Integer id = 11;
        String name = "dogg";
        String status = "sold";

        Map<String, String> request = new HashMap<>();
        request.put("id", id.toString());
        request.put("name", name);
        request.put("status", status);

        given().contentType("application/json")
                .body(request)
                .when()
                .post(baseURI + "pet/")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(id));
    }

}
