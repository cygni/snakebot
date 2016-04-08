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
    private boolean obstaclesEnabled = true;

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
    private int addFoodLikelihood = 15;

    // Likelihood (in percent) that a
    // food will be removed from the world
    private int removeFoodLikelihood = 5;

    // Likelihood (in percent) that a new obstacle will be
    // added to the world
    private int addObstacleLikelihood = 15;

    // Likelihood (in percent) that an
    // obstacle will be removed from the world
    private int removeObstacleLikelihood = 15;

    // Points given per length unit the Snake has
    private int pointsPerLength = 1;

    // Points given per Food item consumed
    private int pointsPerFood = 1;

    // Points given per caused death (i.e. another
    // snake collides with yours)
    private int pointsPerCausedDeath = 5;

    // Points given when a snake nibbles the tail
    // of another snake
    private int pointsPerNibble = 10;

    // Points given to the last living snake (unless
    // the game has been stopped before this happens)
    private int pointsLastSnakeLiving = 10;

    // Number of rounds a tail is protected after nibble
    private int noofRoundsTailProtectedAfterNibble = 3;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxNoofPlayers() {
        return maxNoofPlayers;
    }

    public int getStartSnakeLength() {
        return startSnakeLength;
    }

    public int getTimeInMsPerTick() {
        return timeInMsPerTick;
    }

    public boolean isObstaclesEnabled() {
        return obstaclesEnabled;
    }

    public boolean isFoodEnabled() {
        return foodEnabled;
    }

    public boolean isEdgeWrapsAround() {
        return edgeWrapsAround;
    }

    public boolean isHeadToTailConsumes() {
        return headToTailConsumes;
    }

    public boolean isTailConsumeGrows() {
        return tailConsumeGrows;
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

    public int getPointsPerFood() {
        return pointsPerFood;
    }

    public int getPointsPerCausedDeath() {
        return pointsPerCausedDeath;
    }

    public int getPointsPerNibble() {
        return pointsPerNibble;
    }

    public int getPointsLastSnakeLiving() {
        return pointsLastSnakeLiving;
    }

    public int getNoofRoundsTailProtectedAfterNibble() {
        return noofRoundsTailProtectedAfterNibble;
    }

    private void setWidth(int width) {
        this.width = width;
    }

    private void setHeight(int height) {
        this.height = height;
    }

    private void setMaxNoofPlayers(int maxNoofPlayers) {
        this.maxNoofPlayers = maxNoofPlayers;
    }

    private void setStartSnakeLength(int startSnakeLength) {
        this.startSnakeLength = startSnakeLength;
    }

    private void setTimeInMsPerTick(int timeInMsPerTick) {
        this.timeInMsPerTick = timeInMsPerTick;
    }

    private void setObstaclesEnabled(boolean obstaclesEnabled) {
        this.obstaclesEnabled = obstaclesEnabled;
    }

    private void setFoodEnabled(boolean foodEnabled) {
        this.foodEnabled = foodEnabled;
    }

    private void setEdgeWrapsAround(boolean edgeWrapsAround) {
        this.edgeWrapsAround = edgeWrapsAround;
    }

    private void setHeadToTailConsumes(boolean headToTailConsumes) {
        this.headToTailConsumes = headToTailConsumes;
    }

    private void setTailConsumeGrows(boolean tailConsumeGrows) {
        this.tailConsumeGrows = tailConsumeGrows;
    }

    private void setAddFoodLikelihood(int addFoodLikelihood) {
        this.addFoodLikelihood = addFoodLikelihood;
    }

    private void setRemoveFoodLikelihood(int removeFoodLikelihood) {
        this.removeFoodLikelihood = removeFoodLikelihood;
    }

    private void setAddObstacleLikelihood(int addObstacleLikelihood) {
        this.addObstacleLikelihood = addObstacleLikelihood;
    }

    private void setRemoveObstacleLikelihood(int removeObstacleLikelihood) {
        this.removeObstacleLikelihood = removeObstacleLikelihood;
    }

    private void setPointsPerLength(int pointsPerLength) {
        this.pointsPerLength = pointsPerLength;
    }

    private void setPointsPerFood(int pointsPerFood) {
        this.pointsPerFood = pointsPerFood;
    }

    private void setPointsPerCausedDeath(int pointsPerCausedDeath) {
        this.pointsPerCausedDeath = pointsPerCausedDeath;
    }

    private void setPointsPerNibble(int pointsPerNibble) {
        this.pointsPerNibble = pointsPerNibble;
    }

    private void setPointsLastSnakeLiving(int pointsLastSnakeLiving) {
        this.pointsLastSnakeLiving = pointsLastSnakeLiving;
    }

    private void setNoofRoundsTailProtectedAfterNibble(int noofRoundsTailProtectedAfterNibble) {
        this.noofRoundsTailProtectedAfterNibble = noofRoundsTailProtectedAfterNibble;
    }

    @Override
    public String toString() {
        return "GameSettings{" +
                "width=" + width +
                ",\nheight=" + height +
                ",\nmaxNoofPlayers=" + maxNoofPlayers +
                ",\nstartSnakeLength=" + startSnakeLength +
                ",\ntimeInMsPerTick=" + timeInMsPerTick +
                ",\nobstaclesEnabled=" + obstaclesEnabled +
                ",\nfoodEnabled=" + foodEnabled +
                ",\nedgeWrapsAround=" + edgeWrapsAround +
                ",\nheadToTailConsumes=" + headToTailConsumes +
                ",\ntailConsumeGrows=" + tailConsumeGrows +
                ",\naddFoodLikelihood=" + addFoodLikelihood +
                ",\nremoveFoodLikelihood=" + removeFoodLikelihood +
                ",\naddObstacleLikelihood=" + addObstacleLikelihood +
                ",\nremoveObstacleLikelihood=" + removeObstacleLikelihood +
                ",\npointsPerLength=" + pointsPerLength +
                ",\npointsPerFood=" + pointsPerFood +
                ",\npointsPerCausedDeath=" + pointsPerCausedDeath +
                ",\npointsPerNibble=" + pointsPerNibble +
                ",\npointsLastSnakeLiving=" + pointsLastSnakeLiving +
                ",\nnoofRoundsTailProtectedAfterNibble=" + noofRoundsTailProtectedAfterNibble +
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
        private boolean obstaclesEnabled = true;

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
        private int pointsPerLength = 1;

        // Points given per Food item consumed
        private int pointsPerFood = 1;

        // Points given per caused death (i.e. another
        // snake collides with yours)
        private int pointsPerCausedDeath = 5;

        // Points given when a snake nibbles the tail
        // of another snake
        private int pointsPerNibble = 10;

        // Points given to the last living snake (unless
        // the game has been stopped before this happens)
        private int pointsLastSnakeLiving = 5;

        // Number of rounds a tail is protected after nibble
        private int noofRoundsTailProtectedAfterNibble = 3;

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

        public GameSettingsBuilder withNoofRoundsTailProtectedAfterNibble(int noofRoundsTailProtectedAfterNibble) {
            this.noofRoundsTailProtectedAfterNibble = noofRoundsTailProtectedAfterNibble;
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
            gameSettings.setNoofRoundsTailProtectedAfterNibble(noofRoundsTailProtectedAfterNibble);
            return gameSettings;
        }
    }
}
