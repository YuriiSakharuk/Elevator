package core;

import models.Person;
import java.util.ArrayDeque;
import java.util.Queue;

public interface Elevator {

    boolean isFull ();

    boolean isEmpty ();

    boolean isMovingUp();

    void setMovingDown();

    int getCAPACITY();

    Queue<Person> getPersonsInElevator();

    int getCurrentStopFloor();

    void sortPersons();

    void move();

    int getLastFloor();

    void changeDirection();

    void setMovingUp();

    int moveIfNotFull(int nextFloor);

    int getFloorForExit();

    boolean isShouldDoMove();

    void setShouldDoMove(boolean shouldDoMove);

    void initFloors();
}
