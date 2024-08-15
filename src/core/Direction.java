package core;

import java.awt.*;
import java.util.Random;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public static Point translate(Point point, Direction direction) {
        return new Point(point.x + getDx(direction), point.y + getDy(direction));
    }

    public static int getDx(Direction direction) {
        return switch (direction) {
            case LEFT -> -1;
            case RIGHT -> 1;
            default -> 0;
        };
    }

    public static int getDy(Direction direction) {
        return switch (direction) {
            case UP -> 1;
            case DOWN -> -1;
            default -> 0;
        };
    }

    public static Direction[] getShuffledDirections(Random random) {
        Direction[] directions = {UP, DOWN, LEFT, RIGHT};
        for (int i = 0; i < directions.length; i++) {
            int randomIndex = random.nextInt(directions.length);
            Direction temp = directions[i];
            directions[i] = directions[randomIndex];
            directions[randomIndex] = temp;
        }
        return directions;
    }

    public static Point translate(Point point, Direction direction, int distance) {
        return new Point(point.x + getDx(direction) * distance, point.y + getDy(direction) * distance);
    }

    public static Direction fromString(String direction) {
        return switch (direction) {
            case "UP" -> UP;
            case "DOWN" -> DOWN;
            case "LEFT" -> LEFT;
            case "RIGHT" -> RIGHT;
            default -> null;
        };
    }


}
