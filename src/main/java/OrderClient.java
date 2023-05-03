import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient {

    private static final String ORDER_PATH = "api/v1/orders";

    public ValidatableResponse create(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    public ValidatableResponse allOrders() {
        return given()
                .spec(getBaseSpec())
                .get(ORDER_PATH)
                .then();
    }
}