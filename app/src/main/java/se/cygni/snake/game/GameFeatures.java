package se.cygni.snake.game;

public class GameFeatures {

    // World width
    public int width = 25;

    // World height
    public int height = 25;

    // Maximum noof players in this game
    public int maxNoofPlayers = 5;

    // The starting length of a snake
    public int startSnakeLength = 1;

    // The time clients have to respond with a new move
    public int timeInMsPerTick = 250;

    // Randomly place obstacles
    public boolean obstaclesEnabled = false;

    // Randomly place food
    public boolean foodEnabled = true;

    // Traveling to the edge does not kill but moves to
    // corresponding edge on other side.
    public boolean edgeWrapsAround = false;

    // If a snake manages to nibble on the tail
    // of another snake it will consume that tail part.
    // I.e. the nibbling snake will grow one and
    // victim will loose one.
    public boolean headToTailConsumes = false;

    // Only valid if headToTailConsumes is active.
    // When tailConsumeGrows is set to true the
    // consuming snake will grow when eating
    // another snake.
    public boolean tailConsumeGrows = false;

    // Likelihood (in percent) that a new food will be
    // added to the world
    public int addFoodLikelihood = 15;

    // Likelihood (in percent) that a
    // food will be removed from the world
    public int removeFoodLikelihood = 5;

    // Likelihood (in percent) that a new obstacle will be
    // added to the world
    public int addObstacleLikelihood = 15;

    // Likelihood (in percent) that an
    // obstacle will be removed from the world
    public int removeObstacleLikelihood = 15;

    // Snake grow every N world ticks.
    // 0 for disabled
    public int spontaneousGrowthEveryNWorldTick = 2;

    // Indicates that this is a training game,
    // Bots will be added to fill up remaining players.
    public boolean trainingGame = false;

    /**
     * Enforces limits on some values
     */
    public void applyValidation() {
        width = width > 150 ? 150 : width;
        height = height > 150 ? 150 : height;
        startSnakeLength = startSnakeLength > 10 ? 10 : startSnakeLength;
        maxNoofPlayers = maxNoofPlayers > 20 ? 20 : maxNoofPlayers;
        spontaneousGrowthEveryNWorldTick = spontaneousGrowthEveryNWorldTick < 2 ? 2 : spontaneousGrowthEveryNWorldTick;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMaxNoofPlayers() {
        return maxNoofPlayers;
    }

    public void setMaxNoofPlayers(int maxNoofPlayers) {
        this.maxNoofPlayers = maxNoofPlayers;
    }

    public int getStartSnakeLength() {
        return startSnakeLength;
    }

    public void setStartSnakeLength(int startSnakeLength) {
        this.startSnakeLength = startSnakeLength;
    }

    public int getTimeInMsPerTick() {
        return timeInMsPerTick;
    }

    public void setTimeInMsPerTick(int timeInMsPerTick) {
        this.timeInMsPerTick = timeInMsPerTick;
    }

    public boolean isObstaclesEnabled() {
        return obstaclesEnabled;
    }

    public void setObstaclesEnabled(boolean obstaclesEnabled) {
        this.obstaclesEnabled = obstaclesEnabled;
    }

    public boolean isFoodEnabled() {
        return foodEnabled;
    }

    public void setFoodEnabled(boolean foodEnabled) {
        this.foodEnabled = foodEnabled;
    }

    public boolean isEdgeWrapsAround() {
        return edgeWrapsAround;
    }

    public void setEdgeWrapsAround(boolean edgeWrapsAround) {
        this.edgeWrapsAround = edgeWrapsAround;
    }

    public boolean isHeadToTailConsumes() {
        return headToTailConsumes;
    }

    public void setHeadToTailConsumes(boolean headToTailConsumes) {
        this.headToTailConsumes = headToTailConsumes;
    }

    public boolean isTailConsumeGrows() {
        return tailConsumeGrows;
    }

    public void setTailConsumeGrows(boolean tailConsumeGrows) {
        this.tailConsumeGrows = tailConsumeGrows;
    }

    public int getAddFoodLikelihood() {
        return addFoodLikelihood;
    }

    public void setAddFoodLikelihood(int addFoodLikelihood) {
        this.addFoodLikelihood = addFoodLikelihood;
    }

    public int getRemoveFoodLikelihood() {
        return removeFoodLikelihood;
    }

    public void setRemoveFoodLikelihood(int removeFoodLikelihood) {
        this.removeFoodLikelihood = removeFoodLikelihood;
    }

    public int getAddObstacleLikelihood() {
        return addObstacleLikelihood;
    }

    public void setAddObstacleLikelihood(int addObstacleLikelihood) {
        this.addObstacleLikelihood = addObstacleLikelihood;
    }

    public int getRemoveObstacleLikelihood() {
        return removeObstacleLikelihood;
    }

    public void setRemoveObstacleLikelihood(int removeObstacleLikelihood) {
        this.removeObstacleLikelihood = removeObstacleLikelihood;
    }

    public int getSpontaneousGrowthEveryNWorldTick() {
        return spontaneousGrowthEveryNWorldTick;
    }

    public void setSpontaneousGrowthEveryNWorldTick(int spontaneousGrowthEveryNWorldTick) {
        this.spontaneousGrowthEveryNWorldTick = spontaneousGrowthEveryNWorldTick;
    }

    public boolean isTrainingGame() {
        return trainingGame;
    }

    public void setTrainingGame(boolean trainingGame) {
        this.trainingGame = trainingGame;
    }
}
