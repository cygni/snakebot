package se.cygni.snake.api.model;

public class GameSettings {

    // Maximum noof players in this game
    private int maxNoofPlayers = 10;

    // The starting length of a snake
    private int startSnakeLength = 1;

    // The time clients have to respond with a new move
    private int timeInMsPerTick = 250;

    // Randomly place obstacles
    private boolean obstaclesEnabled = true;

    // Randomly place food
    private boolean foodEnabled = true;

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

    // Snake grow every N world ticks.
    // 0 for disabled
    private int spontaneousGrowthEveryNWorldTick = 3;

    // Indicates that this is a training game,
    // Bots will be added to fill up remaining players.
    private boolean trainingGame = false;
    
    // Points given per length unit the Snake has
    private int pointsPerLength = 1;

    // Points given per Food item consumed
    private int pointsPerFood = 2;

    // Points given per caused death (i.e. another
    // snake collides with yours)
    private int pointsPerCausedDeath = 5;

    // Points given when a snake nibbles the tail
    // of another snake
    private int pointsPerNibble = 10;

    // Number of rounds a tail is protected after nibble
    private int noofRoundsTailProtectedAfterNibble = 3;

    // The starting count for food
    private int startFood = 0;

    // The starting count for obstacles
    private int startObstacles = 5;

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

    public int getNoofRoundsTailProtectedAfterNibble() {
        return noofRoundsTailProtectedAfterNibble;
    }

    public int getSpontaneousGrowthEveryNWorldTick() {
        return spontaneousGrowthEveryNWorldTick;
    }

    public boolean isTrainingGame() {
        return trainingGame;
    }

    public void setMaxNoofPlayers(int maxNoofPlayers) {
        this.maxNoofPlayers = maxNoofPlayers;
    }

    public void setStartSnakeLength(int startSnakeLength) {
        this.startSnakeLength = startSnakeLength;
    }

    public void setTimeInMsPerTick(int timeInMsPerTick) {
        this.timeInMsPerTick = timeInMsPerTick;
    }

    public void setObstaclesEnabled(boolean obstaclesEnabled) {
        this.obstaclesEnabled = obstaclesEnabled;
    }

    public void setFoodEnabled(boolean foodEnabled) {
        this.foodEnabled = foodEnabled;
    }

    public void setHeadToTailConsumes(boolean headToTailConsumes) {
        this.headToTailConsumes = headToTailConsumes;
    }

    public void setTailConsumeGrows(boolean tailConsumeGrows) {
        this.tailConsumeGrows = tailConsumeGrows;
    }

    public void setAddFoodLikelihood(int addFoodLikelihood) {
        this.addFoodLikelihood = addFoodLikelihood;
    }

    public void setRemoveFoodLikelihood(int removeFoodLikelihood) {
        this.removeFoodLikelihood = removeFoodLikelihood;
    }

    public void setPointsPerLength(int pointsPerLength) {
        this.pointsPerLength = pointsPerLength;
    }

    public void setPointsPerFood(int pointsPerFood) {
        this.pointsPerFood = pointsPerFood;
    }

    public void setPointsPerCausedDeath(int pointsPerCausedDeath) {
        this.pointsPerCausedDeath = pointsPerCausedDeath;
    }

    public void setPointsPerNibble(int pointsPerNibble) {
        this.pointsPerNibble = pointsPerNibble;
    }

    public void setNoofRoundsTailProtectedAfterNibble(int noofRoundsTailProtectedAfterNibble) {
        this.noofRoundsTailProtectedAfterNibble = noofRoundsTailProtectedAfterNibble;
    }

    public void setSpontaneousGrowthEveryNWorldTick(int spontaneousGrowthEveryNWorldTick) {
        this.spontaneousGrowthEveryNWorldTick = spontaneousGrowthEveryNWorldTick;
    }

    public void setTrainingGame(boolean trainingGame) {
        this.trainingGame = trainingGame;
    }

    @Override
    public String toString() {
        return "GameSettings{" +
                "\n, maxNoofPlayers=" + maxNoofPlayers +
                "\n, startSnakeLength=" + startSnakeLength +
                "\n, timeInMsPerTick=" + timeInMsPerTick +
                "\n, obstaclesEnabled=" + obstaclesEnabled +
                "\n, foodEnabled=" + foodEnabled +
                "\n, headToTailConsumes=" + headToTailConsumes +
                "\n, tailConsumeGrows=" + tailConsumeGrows +
                "\n, addFoodLikelihood=" + addFoodLikelihood +
                "\n, startObstacles=" + startObstacles +
                "\n, startFood=" + startFood +
                "\n, removeFoodLikelihood=" + removeFoodLikelihood +
                "\n, spontaneousGrowthEveryNWorldTick=" + spontaneousGrowthEveryNWorldTick +
                "\n, trainingGame=" + trainingGame +
                "\n, pointsPerLength=" + pointsPerLength +
                "\n, pointsPerFood=" + pointsPerFood +
                "\n, pointsPerCausedDeath=" + pointsPerCausedDeath +
                "\n, pointsPerNibble=" + pointsPerNibble +
                "\n, noofRoundsTailProtectedAfterNibble=" + noofRoundsTailProtectedAfterNibble +
                '}';
    }

    public int getStartFood() {
        return startFood;
    }

    public void setStartFood(int startFood) {
        this.startFood = startFood;
    }

    public int getStartObstacles() {
        return startObstacles;
    }

    public void setStartObstacles(int startObstacles) {
        this.startObstacles = startObstacles;
    }

    public static class GameSettingsBuilder {
        private int maxNoofPlayers = 10;
        private int startSnakeLength = 1;
        private int timeInMsPerTick = 250;
        private boolean obstaclesEnabled = true;
        private boolean foodEnabled = true;
        private boolean headToTailConsumes = true;
        private boolean tailConsumeGrows = false;
        private int addFoodLikelihood = 15;
        private int removeFoodLikelihood = 5;
        private int spontaneousGrowthEveryNWorldTick = 3;
        private boolean trainingGame = false;
        private int pointsPerLength = 1;
        private int pointsPerFood = 2;
        private int pointsPerCausedDeath = 5;
        private int pointsPerNibble = 10;
        private int noofRoundsTailProtectedAfterNibble = 3;
        private int startFood = 0;
        private int startObstacles = 5;

        public GameSettingsBuilder() {
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


        public GameSettingsBuilder withHeadToTailConsumes(boolean headToTailConsumes) {
            this.headToTailConsumes = headToTailConsumes;
            return this;
        }

        public GameSettingsBuilder withTailConsumeGrows(boolean tailConsumeGrows) {
            this.tailConsumeGrows = tailConsumeGrows;
            return this;
        }

        public GameSettingsBuilder withAddFoodLikelihood(int addFoodLikelihood) {
            this.addFoodLikelihood = addFoodLikelihood;
            return this;
        }

        public GameSettingsBuilder withRemoveFoodLikelihood(int removeFoodLikelihood) {
            this.removeFoodLikelihood = removeFoodLikelihood;
            return this;
        }

        public GameSettingsBuilder withSpontaneousGrowthEveryNWorldTick(int spontaneousGrowthEveryNWorldTick) {
            this.spontaneousGrowthEveryNWorldTick = spontaneousGrowthEveryNWorldTick;
            return this;
        }

        public GameSettingsBuilder withTrainingGame(boolean trainingGame) {
            this.trainingGame = trainingGame;
            return this;
        }

        public GameSettingsBuilder withPointsPerLength(int pointsPerLength) {
            this.pointsPerLength = pointsPerLength;
            return this;
        }

        public GameSettingsBuilder withPointsPerFood(int pointsPerFood) {
            this.pointsPerFood = pointsPerFood;
            return this;
        }

        public GameSettingsBuilder withPointsPerCausedDeath(int pointsPerCausedDeath) {
            this.pointsPerCausedDeath = pointsPerCausedDeath;
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

        public GameSettingsBuilder withStartFood(int startFood) {
            this.startFood = startFood;
            return this;
        }

        public GameSettingsBuilder withStartObstacles(int startObstacles) {
            this.startObstacles = startObstacles;
            return this;
        }

        public GameSettings build() {
            GameSettings gameSettings = new GameSettings();
            gameSettings.setMaxNoofPlayers(maxNoofPlayers);
            gameSettings.setStartSnakeLength(startSnakeLength);
            gameSettings.setTimeInMsPerTick(timeInMsPerTick);
            gameSettings.setObstaclesEnabled(obstaclesEnabled);
            gameSettings.setFoodEnabled(foodEnabled);
            gameSettings.setHeadToTailConsumes(headToTailConsumes);
            gameSettings.setTailConsumeGrows(tailConsumeGrows);
            gameSettings.setAddFoodLikelihood(addFoodLikelihood);
            gameSettings.setStartObstacles(startObstacles);
            gameSettings.setRemoveFoodLikelihood(removeFoodLikelihood);
            gameSettings.setSpontaneousGrowthEveryNWorldTick(spontaneousGrowthEveryNWorldTick);
            gameSettings.setTrainingGame(trainingGame);
            gameSettings.setPointsPerLength(pointsPerLength);
            gameSettings.setPointsPerFood(pointsPerFood);
            gameSettings.setPointsPerCausedDeath(pointsPerCausedDeath);
            gameSettings.setPointsPerNibble(pointsPerNibble);
            gameSettings.setNoofRoundsTailProtectedAfterNibble(noofRoundsTailProtectedAfterNibble);
            gameSettings.setStartFood(startFood);
            return gameSettings;
        }
    }
}
