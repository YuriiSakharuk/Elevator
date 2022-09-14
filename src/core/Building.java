package core;

import models.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Building {

    private final Random random = new Random();

    private final List<Person>[] personsOnFloor;

    private final int floors;

    private final int NUMBER_OF_STEPS = 20;

    private final Elevator elevator;

    private final List<Person> justLeftPersons;

    private int changed;
    private boolean enteringIfNotFull;
    private int step;
    private boolean changedDirection;

    public Building(int floors) {
        this.floors = floors;
        this.personsOnFloor = new ArrayList[floors];
        this.elevator = new ElevatorImpl();
        this.justLeftPersons = new ArrayList<>();
    }

    /*This method runs the elevator due to specified NUMBER_OF_STEPS. It prints the output as well.*/
    public void start() {
        generatePersons();

        for (int i = 0; i < NUMBER_OF_STEPS; i++) {

            System.out.println("*** STEP " + i + " ***");
            enterPersons();

            if (elevator.getCurrentStopFloor() == elevator.getLastFloor() && i != 0 && changed > 0) {

                elevator.changeDirection();
                changed = -NUMBER_OF_STEPS;

            } else changed += NUMBER_OF_STEPS;

            if (elevator.isShouldDoMove()) {

                elevator.move();
                resetLeftPersons();
                System.out.println("AFTER MOVE: \n" + this);
            }

            exitPersons();

            System.out.println("AFTER EXIT: \n" + this + "\n");

            if (elevator.getCurrentStopFloor() == elevator.getLastFloor() && changed > 0) {

                elevator.changeDirection();
                changed = -NUMBER_OF_STEPS;
                setChangedDirection();

            } else changed += NUMBER_OF_STEPS;

            if (elevator.getCurrentStopFloor() == 0 && changed > 0) {

                elevator.changeDirection();
                changed = -NUMBER_OF_STEPS;
            }
        }
    }

    /*This method generates passengers for each floor*/
    private void generatePersons() {

        for (int i = 0; i < personsOnFloor.length; i++) {

            int personsPerFloor = random.nextInt(11);
            personsOnFloor[i] = new ArrayList<>();

            for (int j = 0; j < personsPerFloor; j++) {
                int randomFinalFloor;

                do {
                    randomFinalFloor = random.nextInt(floors);
                } while (randomFinalFloor == i);
                personsOnFloor[i].add(new Person(randomFinalFloor, i));
            }
        }
    }

    /*This method put suitable passengers in the elevator. If the elevator is full, it moves to the closest passenger's
    * stop, otherwise it moves to the closest floor with suitable passengers.*/
    private void enterPersons() {
        int presentFloor = elevator.getCurrentStopFloor();

        Set<Person> collect = personsOnFloor[presentFloor].stream()
                .filter(Person::isWaitingForElevator)
                .filter(person -> (person.getFinalFloor() > presentFloor && elevator.isMovingUp()) ||
                        (person.getFinalFloor() < presentFloor && !elevator.isMovingUp()))
                .limit(elevator.getCAPACITY() - elevator.getPersonsInElevator().size()).collect(Collectors.toSet());

        collect.forEach(person -> {
            elevator.getPersonsInElevator().add(person);
            elevator.sortPersons();
            personsOnFloor[presentFloor].remove(person);
        });

        if (step == 0) elevator.initFloors();
        step++;
        System.out.println("AFTER ENTERING: ");
        System.out.println(this);

        enteringIfNotFull = true;
        while (!elevator.isFull() && elevator.getCurrentStopFloor() != elevator.getFloorForExit() && enteringIfNotFull) {
            enterPersonsIfNotFull();
        }
    }

    /*This method is being invoked, if elevator is not full.
    It fills the elevator with suitable passengers until it is not full.*/
    private void enterPersonsIfNotFull() {
        int presentFloor = elevator.getCurrentStopFloor();

        if (elevator.isMovingUp() && !elevator.isFull()) {

            for (int i = presentFloor + 1; i < elevator.getFloorForExit(); i++) {

                if (!elevator.isFull()) {
                    int finalI = i;

                    if (personsOnFloor[i].stream().anyMatch(person ->
                            person.isWaitingForElevator() && person.getFinalFloor() > finalI) && !elevator.isFull()) {

                        elevator.moveIfNotFull(i);
                        resetLeftPersons();
                        elevator.setShouldDoMove(false);
                        enterPersons();
                        System.out.println("AFTER MOVE: ");
                        System.out.println(this);
                    }
                } else break;
            }
        }

        if (!elevator.isMovingUp() && !elevator.isFull()) {

            for (int i = presentFloor - 1; i > elevator.getFloorForExit(); i--) {

                if (!elevator.isFull()) {

                    int finalI = i;
                    if (personsOnFloor[i].stream().anyMatch(person ->
                            person.isWaitingForElevator() && person.getFinalFloor() < finalI) && !elevator.isFull()) {

                        elevator.moveIfNotFull(i);
                        resetLeftPersons();
                        elevator.setShouldDoMove(false);
                        enterPersons();
                        System.out.println("AFTER MOVE: ");
                        System.out.println(this);
                    }
                } else break;
            }
        }
        enteringIfNotFull = false;
    }

    /*This method make passengers to exit the elevator if current floor is their floor of destination. If after exit of
    * passengers it is empty, it invokes methods to refill the elevator and choose new destination.*/
    private void exitPersons() {
        resetLeftPersons();

        int presentFloor = elevator.getCurrentStopFloor();

        Set<Person> collect = elevator.getPersonsInElevator().stream()
                .filter(person -> person.getFinalFloor() == presentFloor).collect(Collectors.toSet());

        for (Person person : collect) {
            if (person.getFinalFloor() == presentFloor) {

                personsOnFloor[presentFloor].add(person);

                elevator.getPersonsInElevator().remove(person);
                elevator.sortPersons();

                person.setWaitingForElevator(false);
                person.setCurrentFloor(presentFloor);

                justLeftPersons.add(person);
            }
        }

        if (elevator.isEmpty() && (presentFloor != floors - 1) && presentFloor > 0) {
            decideDirection();
            enterPersons();
            changed = -NUMBER_OF_STEPS * 2;
        }
        elevator.setShouldDoMove(true);
    }

    /*This method is being invoked if elevator is empty after exit of passengers. It chooses new destination due to
    * majority passengers in the current floor. */
    private void decideDirection() {
        int presentFloor = elevator.getCurrentStopFloor();

        int personsGoingUp = (int) personsOnFloor[presentFloor].stream()
                .filter(person -> person.getFinalFloor() > presentFloor && person.isWaitingForElevator())
                .count();

        int personsGoingDown = (int) personsOnFloor[presentFloor].stream()
                .filter(person -> person.getFinalFloor() < presentFloor && person.isWaitingForElevator())
                .count();

        if (personsGoingUp > personsGoingDown) {
            elevator.setMovingUp();
        } else if (personsGoingUp < personsGoingDown) {
            elevator.setMovingDown();
        } else elevator.setMovingDown();

    }

    /*This method makes passengers that have already left the elevator to be included in the queue to enter the elevator
    * again. It sets for each passenger new destination floor.*/
    private void resetLeftPersons() {
        justLeftPersons.forEach(person -> {
            person.setWaitingForElevator(true);
            person.setFinalFloor(random.nextInt(floors));
        });

        justLeftPersons.clear();
    }


    private void setChangedDirection() {
        this.changedDirection = !this.changedDirection;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String direction = elevator.isMovingUp() ? "^^^" : "vvv";

        List<Person> collectionCopy = new ArrayList<>(List.copyOf(personsOnFloor[elevator.getCurrentStopFloor()]));
        collectionCopy.removeAll(justLeftPersons);

        stringBuilder.append("FLOOR " + elevator.getCurrentStopFloor() + ":\n");
        stringBuilder.append("PASSENGERS WAITING: " + collectionCopy + " | PASSENGERS IN ELEVATOR: "
                + direction + elevator.getPersonsInElevator().toString() + direction
                + " | PASSENGERS THAT EXITED: " + justLeftPersons.toString());

        return stringBuilder.toString();
    }
}

