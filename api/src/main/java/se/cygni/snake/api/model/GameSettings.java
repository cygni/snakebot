package se.cygni.snake.api.model;

public class GameSettings {
    // World width
    private int width = 50;

    // World height
    private int height = 50;

    // Maximum noof players in this game
    private int maxNoofPlayers = 5;

    // The starting length of a snake
    private int startSnakeLength = 1;

    // The time clients have to respond with a new move
    private int timeInMsPerTick = 250;

    // Randomly place obstacles
    private boolean obstaclesEnabled = false;

    // Randomly place food
    private boolean foodEnabled = true;

    // Traveling to the edge does not kill but moves to
    // corresponding edge on other side.
    private boolean edgeWrapsAround = false;

    // If a snake manages to nibble on the tail
    // of another snake it will consume that tail part.
    // I.e. the nibbling snake will grow one and
    // victim will loose one.
    private boolean headToTailConsumes = true;

    // Only valid if headToTailConsumes is active.
    // When tailConsumeGrows is set to true the
    // consuming snake will grow when eating
    // another snake.
    private boolean tailConsumeGrows = false;

    // Likelihood (in percent) that a new food will be
    // added to the world
    private final int addFoodLikelihood = 15;

    // Likelihood (in percent) that a
    // food will be removed from the world
    private final int removeFoodLikelihood = 5;

    // Likelihood (in percent) that a new obstacle will be
    // added to the world
    private final int addObstacleLikelihood = 15;

    // Likelihood (in percent) that an
    // obstacle will be removed from the world
    private final int removeObstacleLikelihood = 15;

    // Points given per length unit the Snake has
    public int pointsPerLength = 1;

    // Points given per Food item consumed
    public int pointsPerFood = 1;

    // Points given per caused death (i.e. another
    // snake collides with yours)
    public int pointsPerCausedDeath = 5;

    // Points given when a snake nibbles the tail
    // of another snake
    public int pointsPerNibble = 10;

    // Points given to the last living snake (unless
    // the game has been stopped before this happens)
    public int pointsLastSnakeLiving = 5;

    public int getWidth() {
        return width;
    }

    private void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    private void setHeight(int height) {
        this.height = height;
    }

    public int getMaxNoofPlayers() {
        return maxNoofPlayers;
    }

    private void setMaxNoofPlayers(int maxNoofPlayers) {
        this.maxNoofPlayers = maxNoofPlayers;
    }

    public int getStartSnakeLength() {
        return startSnakeLength;
    }

    private void setStartSnakeLength(int startSnakeLength) {
        this.startSnakeLength = startSnakeLength;
    }

    public int getTimeInMsPerTick() {
        return timeInMsPerTick;
    }

    private void setTimeInMsPerTick(int timeInMsPerTick) {
        this.timeInMsPerTick = timeInMsPerTick;
    }

    public boolean isObstaclesEnabled() {
        return obstaclesEnabled;
    }

    private void setObstaclesEnabled(boolean obstaclesEnabled) {
        this.obstaclesEnabled = obstaclesEnabled;
    }

    public boolean isFoodEnabled() {
        return foodEnabled;
    }

    private void setFoodEnabled(boolean foodEnabled) {
        this.foodEnabled = foodEnabled;
    }

    public boolean isEdgeWrapsAround() {
        return edgeWrapsAround;
    }

    private void setEdgeWrapsAround(boolean edgeWrapsAround) {
        this.edgeWrapsAround = edgeWrapsAround;
    }

    public boolean isHeadToTailConsumes() {
        return headToTailConsumes;
    }

    private void setHeadToTailConsumes(boolean headToTailConsumes) {
        this.headToTailConsumes = headToTailConsumes;
    }

    public boolean isTailConsumeGrows() {
        return tailConsumeGrows;
    }

    private void setTailConsumeGrows(boolean tailConsumeGrows) {
        this.tailConsumeGrows = tailConsumeGrows;
    }

    public int getAddFoodLikelihood() {
        return addFoodLikelihood;
    }

    public int getRemoveFoodLikelihood() {
        return removeFoodLikelihood;
    }

    public int getAddObstacleLikelihood() {
        return addObstacleLikelihood;
    }

    public int getRemoveObstacleLikelihood() {
        return removeObstacleLikelihood;
    }

    public int getPointsPerLength() {
        return pointsPerLength;
    }

    public void setPointsPerLength(int pointsPerLength) {
        this.pointsPerLength = pointsPerLength;
    }

    public int getPointsPerFood() {
        return pointsPerFood;
    }

    public void setPointsPerFood(int pointsPerFood) {
        this.pointsPerFood = pointsPerFood;
    }

    public int getPointsPerCausedDeath() {
        return pointsPerCausedDeath;
    }

    public void setPointsPerCausedDeath(int pointsPerCausedDeath) {
        this.pointsPerCausedDeath = pointsPerCausedDeath;
    }

    public int getPointsPerNibble() {
        return pointsPerNibble;
    }

    public void setPointsPerNibble(int pointsPerNibble) {
        this.pointsPerNibble = pointsPerNibble;
    }

    public int getPointsLastSnakeLiving() {
        return pointsLastSnakeLiving;
    }

    public void setPointsLastSnakeLiving(int pointsLastSnakeLiving) {
        this.pointsLastSnakeLiving = pointsLastSnakeLiving;
    }

    @Override
    public String toString() {
        return "GameSettings{" +
                "width=" + width +
                ", height=" + height +
                ", maxNoofPlayers=" + maxNoofPlayers +
                ", startSnakeLength=" + startSnakeLength +
                ", timeInMsPerTick=" + timeInMsPerTick +
                ", obstaclesEnabled=" + obstaclesEnabled +
                ", foodEnabled=" + foodEnabled +
                ", edgeWrapsAround=" + edgeWrapsAround +
                ", headToTailConsumes=" + headToTailConsumes +
                ", tailConsumeGrows=" + tailConsumeGrows +
                ", addFoodLikelihood=" + addFoodLikelihood +
                ", removeFoodLikelihood=" + removeFoodLikelihood +
                ", addObstacleLikelihood=" + addObstacleLikelihood +
                ", removeObstacleLikelihood=" + removeObstacleLikelihood +
                '}';
    }

    public static class GameSettingsBuilder {
        // World width
        private int width = 500;

        // World height
        private int height = 500;

        // Maximum noof players in this game
        private int maxNoofPlayers = 5;

        // The starting length of a snake
        private int startSnakeLength = 1;

        // The time clients have to respond with a new move
        private int timeInMsPerTick = 250;

        // Randomly place obstacles
        private boolean obstaclesEnabled = false;

        // Randomly place food
        private boolean foodEnabled = true;

        // Traveling to the edge does not kill but moves to
        // corresponding edge on other side.
        private boolean edgeWrapsAround = false;

        // If a snake manages to nibble on the tail
        // of another snake it will consume that tail part.
        // I.e. the nibbling snake will grow one and
        // victim will loose one.
        private boolean headToTailConsumes = true;

        // Only valid if headToTailConsumes is active.
        // When tailConsumeGrows is set to true the
        // consuming snake will grow when eating
        // another snake.
        private boolean tailConsumeGrows = false;

        // Points given per length unit the Snake has
        public int pointsPerLength = 1;

        // Points given per Food item consumed
        public int pointsPerFood = 1;

        // Points given per caused death (i.e. another
        // snake collides with yours)
        public int pointsPerCausedDeath = 5;

        // Points given when a snake nibbles the tail
        // of another snake
        public int pointsPerNibble = 10;

        // Points given to the last living snake (unless
        // the game has been stopped before this happens)
        public int pointsLastSnakeLiving = 5;

        public GameSettingsBuilder() {
        }

        public GameSettingsBuilder withWidth(int width) {
            this.width = width;
            return this;
        }

        public GameSettingsBuilder withHeight(int height) {
            this.height = height;
            return this;
        }

        public GameSettingsBuilder withMaxNoofPlayers(int maxNoofPlayers) {
            this.maxNoofPlayers = maxNoofPlayers;
            return this;
        }

        public GameSettingsBuilder withStartSnakeLength(int startSnakeLength) {
            this.startSnakeLength = startSnakeLength;
            return this;
        }

        public GameSettingsBuilder withTimeInMsPerTick(int timeInMsPerTick) {
            this.timeInMsPerTick = timeInMsPerTick;
            return this;
        }

        public GameSettingsBuilder withObstaclesEnabled(boolean obstaclesEnabled) {
            this.obstaclesEnabled = obstaclesEnabled;
            return this;
        }

        public GameSettingsBuilder withFoodEnabled(boolean foodEnabled) {
            this.foodEnabled = foodEnabled;
            return this;
        }

        public GameSettingsBuilder withEdgeWrapsAround(boolean edgeWrapsAround) {
            this.edgeWrapsAround = edgeWrapsAround;
            return this;
        }

        public GameSettingsBuilder withHeadToTailConsumes(boolean headToTailConsumes) {
            this.headToTailConsumes = headToTailConsumes;
            return this;
        }

        public GameSettingsBuilder withTailConsumeGrows(boolean tailConsumeGrows) {
            this.tailConsumeGrows = tailConsumeGrows;
            return this;
        }

        public GameSettingsBuilder withPointsLastSnakeLiving(int pointsLastSnakeLiving) {
            this.pointsLastSnakeLiving = pointsLastSnakeLiving;
            return this;
        }

        public GameSettingsBuilder withPointsPerCausedDeath(int pointsPerCausedDeath) {
            this.pointsPerCausedDeath = pointsPerCausedDeath;
            return this;
        }

        public GameSettingsBuilder withPointsPerFood(int pointsPerFood) {
            this.pointsPerFood = pointsPerFood;
            return this;
        }

        public GameSettingsBuilder withPointsPerLength(int pointsPerLength) {
            this.pointsPerLength = pointsPerLength;
            return this;
        }

        public GameSettingsBuilder withPointsPerNibble(int pointsPerNibble) {
            this.pointsPerNibble = pointsPerNibble;
            return this;
        }

        public GameSettings build() {
            GameSettings gameSettings = new GameSettings();
            gameSettings.setWidth(width);
            gameSettings.setHeight(height);
            gameSettings.setMaxNoofPlayers(maxNoofPlayers);
            gameSettings.setStartSnakeLength(startSnakeLength);
            gameSettings.setTimeInMsPerTick(timeInMsPerTick);
            gameSettings.setObstaclesEnabled(obstaclesEnabled);
            gameSettings.setFoodEnabled(foodEnabled);
            gameSettings.setEdgeWrapsAround(edgeWrapsAround);
            gameSettings.setHeadToTailConsumes(headToTailConsumes);
            gameSettings.setTailConsumeGrows(tailConsumeGrows);
            gameSettings.setPointsLastSnakeLiving(pointsLastSnakeLiving);
            gameSettings.setPointsPerCausedDeath(pointsPerCausedDeath);
            gameSettings.setPointsPerFood(pointsPerFood);
            gameSettings.setPointsPerLength(pointsPerLength);
            gameSettings.setPointsPerNibble(pointsPerNibble);
            return gameSettings;
        }
    }
}
