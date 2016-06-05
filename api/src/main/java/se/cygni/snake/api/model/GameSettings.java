package se.cygni.snake.api.model;

public class GameSettings {
    // World width
    private WorldSize width = WorldSize.MEDIUM;

    // World height
    private WorldSize height = WorldSize.MEDIUM;

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

    // Points given to the last living snake (unless
    // the game has been stopped before this happens)
    private int pointsLastSnakeLiving = 10;

    // Number of rounds a tail is protected after nibble
    private int noofRoundsTailProtectedAfterNibble = 3;

    // If a snake kills itself by hitting a wall or another
    // snake.
    private int pointsSuicide = -10;

    // The starting count for obstacles
    private int startObstacles = 0;

    // The starting count for food
    private int startFood = 0;

    public WorldSize getWidth() {
        return width;
    }

    public WorldSize getHeight() {
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

    public int getSpontaneousGrowthEveryNWorldTick() {
        return spontaneousGrowthEveryNWorldTick;
    }

    public boolean isTrainingGame() {
        return trainingGame;
    }

    public int getPointsSuicide() {
        return pointsSuicide;
    }

    public void setWidth(WorldSize width) {
        this.width = width;
    }

    public void setHeight(WorldSize height) {
        this.height = height;
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

    public void setEdgeWrapsAround(boolean edgeWrapsAround) {
        this.edgeWrapsAround = edgeWrapsAround;
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

    public void setAddObstacleLikelihood(int addObstacleLikelihood) {
        this.addObstacleLikelihood = addObstacleLikelihood;
    }

    public void setRemoveObstacleLikelihood(int removeObstacleLikelihood) {
        this.removeObstacleLikelihood = removeObstacleLikelihood;
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

    public void setPointsLastSnakeLiving(int pointsLastSnakeLiving) {
        this.pointsLastSnakeLiving = pointsLastSnakeLiving;
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

    public void setPointsSuicide(int pointsSuicide) {
        this.pointsSuicide = pointsSuicide;
    }

    @Override
    public String toString() {
        return "GameSettings{" +
                "width=" + width +
                "\n, height=" + height +
                "\n, maxNoofPlayers=" + maxNoofPlayers +
                "\n, startSnakeLength=" + startSnakeLength +
                "\n, timeInMsPerTick=" + timeInMsPerTick +
                "\n, obstaclesEnabled=" + obstaclesEnabled +
                "\n, foodEnabled=" + foodEnabled +
                "\n, edgeWrapsAround=" + edgeWrapsAround +
                "\n, headToTailConsumes=" + headToTailConsumes +
                "\n, tailConsumeGrows=" + tailConsumeGrows +
                "\n, addFoodLikelihood=" + addFoodLikelihood +
                "\n, removeFoodLikelihood=" + removeFoodLikelihood +
                "\n, addObstacleLikelihood=" + addObstacleLikelihood +
                "\n, removeObstacleLikelihood=" + removeObstacleLikelihood +
                "\n, spontaneousGrowthEveryNWorldTick=" + spontaneousGrowthEveryNWorldTick +
                "\n, trainingGame=" + trainingGame +
                "\n, pointsPerLength=" + pointsPerLength +
                "\n, pointsPerFood=" + pointsPerFood +
                "\n, pointsPerCausedDeath=" + pointsPerCausedDeath +
                "\n, pointsPerNibble=" + pointsPerNibble +
                "\n, pointsLastSnakeLiving=" + pointsLastSnakeLiving +
                "\n, noofRoundsTailProtectedAfterNibble=" + noofRoundsTailProtectedAfterNibble +
                "\n, pointsSuicide=" + pointsSuicide +
                '}';
    }

    public int getStartObstacles() {
        return startObstacles;
    }

    public void setStartObstacles(int startObstacles) {
        this.startObstacles = startObstacles;
    }

    public int getStartFood() {
        return startFood;
    }

    public void setStartFood(int startFood) {
        this.startFood = startFood;
    }

    public static class GameSettingsBuilder {
        private WorldSize width = WorldSize.MEDIUM;
        private WorldSize height = WorldSize.MEDIUM;
        private int maxNoofPlayers = 5;
        private int startSnakeLength = 1;
        private int timeInMsPerTick = 250;
        private boolean obstaclesEnabled = true;
        private boolean foodEnabled = true;
        private boolean edgeWrapsAround = false;
        private boolean headToTailConsumes = true;
        private boolean tailConsumeGrows = false;
        private int addFoodLikelihood = 15;
        private int removeFoodLikelihood = 5;
        private int addObstacleLikelihood = 15;
        private int removeObstacleLikelihood = 15;
        private int spontaneousGrowthEveryNWorldTick = 3;
        private boolean trainingGame = false;
        private int pointsPerLength = 1;
        private int pointsPerFood = 2;
        private int pointsPerCausedDeath = 5;
        private int pointsPerNibble = 10;
        private int pointsLastSnakeLiving = 10;
        private int noofRoundsTailProtectedAfterNibble = 3;
        private int pointsSuicide = -10;
        private int startObstacles = 0;
        private int startFood = 0;

        public GameSettingsBuilder() {
        }

        public GameSettingsBuilder withWidth(WorldSize width) {
            this.width = width;
            return this;
        }

        public GameSettingsBuilder withHeight(WorldSize height) {
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

        public GameSettingsBuilder withAddFoodLikelihood(int addFoodLikelihood) {
            this.addFoodLikelihood = addFoodLikelihood;
            return this;
        }

        public GameSettingsBuilder withRemoveFoodLikelihood(int removeFoodLikelihood) {
            this.removeFoodLikelihood = removeFoodLikelihood;
            return this;
        }

        public GameSettingsBuilder withAddObstacleLikelihood(int addObstacleLikelihood) {
            this.addObstacleLikelihood = addObstacleLikelihood;
            return this;
        }

        public GameSettingsBuilder withRemoveObstacleLikelihood(int removeObstacleLikelihood) {
            this.removeObstacleLikelihood = removeObstacleLikelihood;
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

        public GameSettingsBuilder withPointsLastSnakeLiving(int pointsLastSnakeLiving) {
            this.pointsLastSnakeLiving = pointsLastSnakeLiving;
            return this;
        }

        public GameSettingsBuilder withNoofRoundsTailProtectedAfterNibble(int noofRoundsTailProtectedAfterNibble) {
            this.noofRoundsTailProtectedAfterNibble = noofRoundsTailProtectedAfterNibble;
            return this;
        }

        public GameSettingsBuilder withPointsSuicide(int pointsSuicide) {
            this.pointsSuicide = pointsSuicide;
            return this;
        }

        public GameSettingsBuilder withStartObstacles(int startObstacles) {
            this.startObstacles = startObstacles;
            return this;
        }

        public GameSettingsBuilder withStartFood(int startFood) {
            this.startFood = startFood;
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
            gameSettings.setAddFoodLikelihood(addFoodLikelihood);
            gameSettings.setRemoveFoodLikelihood(removeFoodLikelihood);
            gameSettings.setAddObstacleLikelihood(addObstacleLikelihood);
            gameSettings.setRemoveObstacleLikelihood(removeObstacleLikelihood);
            gameSettings.setSpontaneousGrowthEveryNWorldTick(spontaneousGrowthEveryNWorldTick);
            gameSettings.setTrainingGame(trainingGame);
            gameSettings.setPointsPerLength(pointsPerLength);
            gameSettings.setPointsPerFood(pointsPerFood);
            gameSettings.setPointsPerCausedDeath(pointsPerCausedDeath);
            gameSettings.setPointsPerNibble(pointsPerNibble);
            gameSettings.setPointsLastSnakeLiving(pointsLastSnakeLiving);
            gameSettings.setNoofRoundsTailProtectedAfterNibble(noofRoundsTailProtectedAfterNibble);
            gameSettings.setPointsSuicide(pointsSuicide);
            gameSettings.setStartObstacles(startObstacles);
            gameSettings.setStartFood(startFood);
            return gameSettings;
        }
    }
}
