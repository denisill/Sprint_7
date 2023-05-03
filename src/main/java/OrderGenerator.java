public class OrderGenerator {

    public static Order getDefault(String[] color) {
        return new Order("sergei",
                "pushkin",
                "moscow",
                3,
                "+7 800 355 35 35",
                3,
                "2023-05-23",
                "test",
                color);
    }
}
