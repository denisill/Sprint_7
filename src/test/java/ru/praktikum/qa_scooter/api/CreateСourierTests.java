package ru.praktikum.qa_scooter.api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.qa_scooter.api.client.CourierClient;
import ru.praktikum.qa_scooter.api.model.CourierCredentials;
import ru.praktikum.qa_scooter.api.util.CourierGenerator;

import static org.junit.Assert.*;

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
        int statusCode = responseCreate.extract().statusCode();
        //проверяем статус код и текст сообщения
        assertEquals("Некорректный статус код", 400, statusCode);
        String message = responseCreate.extract().path("message");
        assertEquals("Недостаточно данных для создания учетной записи", message);
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Запрос возвращает статус код - 201")
    public void createNewCourierAndCheckResponse() {
        //создаем нового курьера
        ValidatableResponse responseCreate = courierClient.createCourier(CourierGenerator.getDefault());
        int statusCode = responseCreate.extract().statusCode();
        //проверяем статус код создания курьера
        assertEquals("Некорректный статус код", 201, statusCode);
        boolean isCreated = responseCreate.extract().path("ok");
        //провереяем ответ о успешности создания
        assertTrue("Курьер не создан", isCreated);
        //логинимся под новым курьером
        ValidatableResponse responseLogin = courierClient.loginCourier(CourierCredentials.from(CourierGenerator.getDefault()));
        int loginStatusCode = responseLogin.extract().statusCode();
        //проверяем что курьер залогинился
        assertEquals("Некорректный статус код", 200, loginStatusCode);
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
        int statusCode = responseCreate.extract().statusCode();
        //проверяем статус код создания курьера
        assertEquals("Некорректный статус код", 201, statusCode);
        boolean isCreated = responseCreate.extract().path("ok");
        //провереяем ответ о успешности создания
        assertTrue("Курьер не создан", isCreated);
        //логинимся под новым курьером
        ValidatableResponse responseLogin = courierClient.loginCourier(CourierCredentials.from(CourierGenerator.getDefault()));
        int loginStatusCode = responseLogin.extract().statusCode();
        //проверяем что курьер залогинился
        assertEquals("Некорректный статус код", 200, loginStatusCode);
        //записываем id созданного курьера
        courierId = responseLogin.extract().path("id");
        //проверяем, что id что не равен нулю
        assertNotNull("Id is null", courierId);
        //пытаемся создать курьера с тем же самым логином
        ValidatableResponse responseNotCreate = courierClient.createCourier(CourierGenerator.getDefault());
        int doubleStatusCode = responseNotCreate.extract().statusCode();
        //проверяем статус код и текст сообщения
        assertEquals("Некорректный статус код", 409, doubleStatusCode);
        String message = responseNotCreate.extract().path("message");
        assertEquals("Этот логин уже используется. Попробуйте другой.", message);
    }
}