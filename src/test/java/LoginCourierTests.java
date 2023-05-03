import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;

public class LoginCourierTests {

    private CourierClient courierClient;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    @Description("Запрос возвращает id и статус код 200")
    public void courierCanLogin() {
        //логинимся под зарегестированным курьером
        ValidatableResponse loginResponse = courierClient
                .loginCourier(CourierCredentials.from(CourierGenerator.getRegisteredCourier()));
        //проверяем что курьер залогинился успешно
        loginResponse.assertThat().statusCode(200);
        //записываем id курьера
        courierId = loginResponse.extract().path("id");
        //проверяем, что id что не равен нулю
        assertNotNull("Id is null", courierId);
    }

    @Test
    @DisplayName("Заполнены не все обязательные поля при авторизации")
    @Description("Запрос возвращает ошибку 400")
    public void loginCourierEmptyPassword() {
        //логинимся без пароля
        ValidatableResponse loginResponse = courierClient
                .loginCourier(CourierCredentials.from(CourierGenerator.getDefaultWithoutPassword()));
        //проверка ответа на запрос
        loginResponse.assertThat().statusCode(400).and().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация под несуществующим пользователем")
    @Description("Запрос возвращает ошибку 404")
    public void wrongLoginCourier() {
        //логинимся под несуществующим пользователем
        ValidatableResponse loginResponse = courierClient
                .loginCourier(CourierCredentials.from(CourierGenerator.getWrong()));
        //проверка ответа на запрос
        loginResponse.assertThat().statusCode(404).and().body("message", equalTo("Учетная запись не найдена"));
    }
}