import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;

public class CreateСourierTests {

    private CourierClient courierClient;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @After
    public void cleanUp() {
        courierClient.deleteCourier(courierId);
    }

    @Test
    @DisplayName("Проверка на заполнение обязательных полей для создания курьера")
    @Description("Запрос возвращает статус код - 400, недостаточно данных для создания")
    public void checkNotAllRequiredFieldsFilled() {
        //создаем нового курьера без пароля
        ValidatableResponse responseCreate = courierClient.createCourier(CourierGenerator.getDefaultWithoutPassword());
        //проверяем статус код и текст сообщения
        responseCreate.assertThat().statusCode(400).and().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Запрос возвращает статус код - 201")
    public void createNewCourierAndCheckResponse() {
        //создаем нового курьера
        ValidatableResponse responseCreate = courierClient.createCourier(CourierGenerator.getDefault());
        //проверяем успешность создания
        responseCreate.assertThat().statusCode(201).and().body("ok", equalTo(true));
        //логинимся под новым курьером
        ValidatableResponse responseLogin = courierClient.loginCourier(CourierCredentials.from(CourierGenerator.getDefault()));
        //проверяем что курьер залогинился
        responseLogin.assertThat().statusCode(200);
        //записываем id созданного курьера
        courierId = responseLogin.extract().path("id");
        //проверяем, что id что не равен нулю
        assertNotNull("Id is null", courierId);
    }

    @Test
    @DisplayName("Невозможность создания двух одинаковых курьеров")
    @Description("Запрос возвращает статус код - 409 и с сообщением что логин уже используется")
    public void checkNotCreateTwoIdenticalCouriers() {
        //создаем нового курьера
        ValidatableResponse responseCreate = courierClient.createCourier(CourierGenerator.getDefault());
        //проверяем успешность создания
        responseCreate.assertThat().statusCode(201).and().body("ok", equalTo(true));
        //логинимся под новым курьером
        ValidatableResponse responseLogin = courierClient.loginCourier(CourierCredentials.from(CourierGenerator.getDefault()));
        //проверяем что курьер залогинился
        responseLogin.assertThat().statusCode(200);
        //записываем id созданного курьера
        courierId = responseLogin.extract().path("id");
        //проверяем, что id что не равен нулю
        assertNotNull("Id is null", courierId);
        //пытаемся создать курьера с тем же самым логином
        ValidatableResponse responseNotCreate = courierClient.createCourier(CourierGenerator.getDefault());
        //проверяем, что возвращается ошибка
        responseNotCreate.assertThat().statusCode(409).and().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }
}