import core.Building;

import java.util.Random;

public class App {

    public static void main(String[] args) {

        Random random = new Random();

        Building building = new Building(random.nextInt(16) + 5);

        building.start();
    }
}
