package models;

public class Person  {

    private int finalFloor;

    int currentFloor;

    private boolean waitingForElevator;

    public Person(int finalFloor, int currentFloor) {
        this.finalFloor = finalFloor;
        this.currentFloor = currentFloor;
        waitingForElevator = true;
    }

    public int getFinalFloor() {
        return finalFloor;
    }

    public void setFinalFloor(int finalFloor) {
         this.finalFloor = finalFloor;
    }

    public boolean isWaitingForElevator() {
        return waitingForElevator;
    }

    public void setWaitingForElevator(boolean waitingForElevator) {
        this.waitingForElevator = waitingForElevator;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    @Override
    public String toString() {
        return String.valueOf(finalFloor);
    }
}
