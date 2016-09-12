package se.cygni.snake.game;

public class GameFeatures {

    private int width = 46;

    private int height = 34;

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

    // The amount of obstacles to populate the
    // board with.
    private double obstacleIntensity = 1;

    // Snake grow every N world ticks.
    // 0 for disabled
    private int spontaneousGrowthEveryNWorldTick = 3;

    // Indicates that this is a training game,
    // Bots will be added to fill up remaining players.
    private boolean trainingGame = true;

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

    // The starting count for obstacles
    private int startObstacles = 0;

    // The starting count for food
    private int startFood = 0;


    /**
     * Enforces limits on some values
     */
    public void applyValidation() {
        startSnakeLength = Math.min(10, startSnakeLength);
        startSnakeLength = Math.max(1, startSnakeLength);

        maxNoofPlayers = Math.min(20, maxNoofPlayers);
        maxNoofPlayers = Math.max(2, maxNoofPlayers);

        spontaneousGrowthEveryNWorldTick = spontaneousGrowthEveryNWorldTick < 2 ? 2 : spontaneousGrowthEveryNWorldTick;
        startObstacles = Math.max(0, startObstacles);
        startFood = Math.max(0, startFood);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    public double getObstacleIntensity() {
        return obstacleIntensity;
    }

    public void setObstacleIntensity(double obstacleIntensity) {
        this.obstacleIntensity = obstacleIntensity;
    }

    public int getRemoveFoodLikelihood() {
        return removeFoodLikelihood;
    }

    public void setRemoveFoodLikelihood(int removeFoodLikelihood) {
        this.removeFoodLikelihood = removeFoodLikelihood;
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

    public int getNoofRoundsTailProtectedAfterNibble() {
        return noofRoundsTailProtectedAfterNibble;
    }

    public void setNoofRoundsTailProtectedAfterNibble(int noofRoundsTailProtectedAfterNibble) {
        this.noofRoundsTailProtectedAfterNibble = noofRoundsTailProtectedAfterNibble;
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
}
