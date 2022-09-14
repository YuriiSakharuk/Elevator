package core;

import models.Person;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class ElevatorImpl implements Elevator {

    private final int CAPACITY = 5;

    private ArrayDeque<Person> personsInElevator;

    private boolean movingUp;

    private boolean shouldDoMove;

    private int currentStopFloor;

    private int nextFloor;

    private int lastFloor;

    private int floorForExit;

    public ElevatorImpl() {
        personsInElevator = new ArrayDeque<>();
        movingUp = true;
        shouldDoMove = true;
    }

    /*This method init destination floors for the elevator on the beginning of the program.*/
    @Override
    public void initFloors() {
        setLastFloor();
        setNextFloor();
        setFloorForExit();
    }

    /*This method makes elevator move to the next passenger's destination floor.*/
    @Override
    public void move() {
        initFloors();
        setCurrentStopFloor(nextFloor);
    }

    /*This method makes elevator move to the closest floor with suitable passengers if it is not full.*/
    @Override
    public int moveIfNotFull(int nextFloor) {
        setLastFloor();
        this.nextFloor = nextFloor;
        setFloorForExit();
        setCurrentStopFloor(nextFloor);
        return currentStopFloor;
    }

    @Override
    public boolean isFull() {
        return personsInElevator.size() >= CAPACITY;
    }

    @Override
    public boolean isEmpty() {
        return personsInElevator.isEmpty();
    }

    @Override
    public boolean isMovingUp() {
        return movingUp;
    }

    @Override
    public void setMovingDown() {
        movingUp = false;
    }

    @Override
    public void setMovingUp() {
        movingUp = true;
    }

    @Override
    public int getCAPACITY() {
        return CAPACITY;
    }

    @Override
    public ArrayDeque<Person> getPersonsInElevator() {
        return personsInElevator;
    }

    @Override
    public int getCurrentStopFloor() {
        return currentStopFloor;
    }

    @Override
    public int getFloorForExit() {
        return floorForExit;
    }

    @Override
    public int getLastFloor() {
        return lastFloor;
    }

    @Override
    public void sortPersons() {
        Person[] personArr = personsInElevator.toArray(new Person[0]);

        Arrays.sort(personArr, Comparator.comparing(Person::getFinalFloor));

        personsInElevator.clear();

        Collections.addAll(personsInElevator, personArr);
    }

    @Override
    public void changeDirection() {
        if (movingUp)
            setMovingDown();

        else setMovingUp();
    }

    @Override
    public boolean isShouldDoMove() {
        return shouldDoMove;
    }

    @Override
    public void setShouldDoMove(boolean shouldDoMove) {
        this.shouldDoMove = shouldDoMove;
    }

    private void setCurrentStopFloor(int currentStopFloor) {
        this.currentStopFloor = currentStopFloor;
    }


    private void setNextFloor() {
        if (!personsInElevator.isEmpty()) {

            nextFloor = isMovingUp() ? personsInElevator.peekFirst().getFinalFloor()
                    : personsInElevator.peekLast().getFinalFloor();

        } else
            nextFloor = isMovingUp() ? currentStopFloor++ : currentStopFloor--;
    }

    private void setFloorForExit() {

        if (isMovingUp()) {

            floorForExit = personsInElevator.stream()
                    .mapToInt(Person::getFinalFloor)
                    .filter(finalFloor -> finalFloor > nextFloor)
                    .min().orElseGet(() -> currentStopFloor++);

        } else
            floorForExit = personsInElevator.stream()
                .filter(person -> person.getFinalFloor() < nextFloor)
                .mapToInt(Person::getFinalFloor)
                .max()
                .orElseGet(() -> currentStopFloor--);
    }

    private void setLastFloor() {
        if (!personsInElevator.isEmpty()) {
            lastFloor = isMovingUp() ? personsInElevator.peekLast().getFinalFloor()
                    : personsInElevator.peekFirst().getFinalFloor();
        } else
            lastFloor = isMovingUp() ? currentStopFloor++ : currentStopFloor--;
    }
}
