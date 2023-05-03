import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class CreateOrderTests {

    private Order order;
    private OrderClient orderClient;
    private final String[] color;
    private int orderTrack;

    public CreateOrderTests(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getColors() {
        return new Object[][] {
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{""}}
        };
    }

    @Before
    public void setUp() {
        order = OrderGenerator.getDefault(color);
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Успешное создание заказа")
    @Description("Запрос возвращает статус код - 201")
    public void createOrder() {
        //создаем заказ
        ValidatableResponse response = orderClient.create(order);
        response.assertThat().statusCode(201);
        orderTrack = response.extract().path("track");
        //проверяем, что track заказа не равен нулю
        assertNotNull("Track is null", orderTrack);
    }
}