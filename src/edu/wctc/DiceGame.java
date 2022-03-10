package edu.wctc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiceGame {
    private	final List<Player> players;
    private	final List<Die>	dice;
    private	final int maxRolls;
    private	Player currentPlayer;
    private static int NumberOfTurnsPlayed = 0;
    private static int newRoundStartingPlayer;

    public DiceGame(int countPlayers, int countDice, int maxRolls){
        players = new ArrayList<Player>();
        dice = new ArrayList<Die>();
        this.maxRolls = maxRolls;

        while(players.size() < countPlayers){
            Player player = new Player();
            players.add(player);
        }

        while(dice.size() < countDice){
            Die die = new Die(6);
            dice.add(die);
        }

        if (countPlayers < 2){
            throw new IllegalArgumentException();
        }
    }
        //testing to get the size of both lists
//    public void getListSizes(){
//        System.out.println(players.size());
//        System.out.println(dice.size());
//    }

    private boolean allDiceHeld(){
        boolean allHeld = dice.stream()
                .allMatch(Die::isBeingHeld);

        if(allHeld){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean autoHold(int faceValue){
        //if there is a die that has given faceValue being held, return true
        //if there is a die with given faceValue that is unheld, hold it then return true(if multiple only hold one)
        //if there is no die with given face value, return false

        boolean dieWithFaceValueUnheld = dice.stream()
                .filter(Die -> !Die.isBeingHeld())
                .anyMatch(Die -> Die.getFaceValue() == faceValue);

        boolean dieWithFaceValueHeld = isHoldingDie(faceValue);

        if(dieWithFaceValueUnheld){
        dice.stream()
                .filter(Die -> !Die.isBeingHeld())
                .filter(Die -> Die.getFaceValue() == faceValue)
                .findFirst()
                .get()
                .holdDie();
        return true;
        }
        else if(dieWithFaceValueHeld){
            return true;
        }
        return false;
    }

    public boolean currentPlayerCanRoll(){
        if(!(currentPlayer.getRollsUsed() == 3) && !allDiceHeld()) {
            return true;
        }
        else {
            return false;
        }
    }

    public int getCurrentPlayerNumber(){
        return currentPlayer.getPlayerNumber();
    }

    public int getCurrentPlayerScore(){
        return currentPlayer.getScore();
    }

    public String getDiceResults(){
        // use string for the die
        // for each die run to string method
        // use collectors.joining

        String diceResults = dice.stream()
                .map(Die -> Die.toString())
                .collect(Collectors.joining());
        return diceResults;
    }

    public String getFinalWinner(){

        List<Player> listOfPlayerSortedDescending = players.stream()
                .sorted(Comparator.comparingInt(Player::getWins)
                        .reversed())
                .toList();

        Player finalWinner = listOfPlayerSortedDescending.stream()
                .findFirst()
                .get();

        return finalWinner.toString();
    }

    public String getGameResults(){

        // created list and sorted highest to lowest
        List<Player> playersOrderByScore = players.stream()
                .sorted(Comparator.comparingInt(Player::getScore)
                        .reversed())
                .toList();

        // get the person in first place
        Player firstPlacePlayer = playersOrderByScore.stream()
                .findFirst()
                .get();

        //condition if there are more than one person that has the highest score
        if((playersOrderByScore.indexOf(0)) == (playersOrderByScore.indexOf(1)))
        {
            //create new list based on previously sorted list to filter all those who share the same score
            List<Player> playersHaveHighestScore = playersOrderByScore.stream()
                    .filter(Player -> Player.getScore() == firstPlacePlayer.getScore())
                    .toList();
            //created new list base on previously sorted list to find all that does not have the same score as the highest
            List<Player> playersNotFirstPlace = playersOrderByScore.stream()
                    .filter(Player -> Player.getScore() < firstPlacePlayer.getScore())
                    .toList();

            //  add wins and losses to each player of the two lists above
            playersHaveHighestScore.forEach(Player::addWin);
            playersNotFirstPlace.forEach(Player::addLoss);
        }
        // condition if there is only one player with the highest score
        else{
            // created list to find players whose score is less than the highest
            List<Player> playersNotFirstPlace = playersOrderByScore.stream()
                            .filter(Player -> Player.getScore() < firstPlacePlayer.getScore())
                                    .toList();

            // add wins and losses to appropriate players
            firstPlacePlayer.addWin();
            playersNotFirstPlace.forEach(Player::addLoss);
        }

        String gameResults = players.stream()
                .map(Player -> Player.toString())
                .collect(Collectors.joining());

          return gameResults;
    }

    private boolean isHoldingDie(int faceValue){
        boolean dieBeingHeld = dice.stream()
                .filter(Die::isBeingHeld)
                .anyMatch(Die -> Die.getFaceValue() == faceValue);

        return dieBeingHeld;
    }

    public boolean nextPlayer(){

        // had to add variable to keep track of number of turns
        // this is to make sure we run through the list
        // probably not the best way, but couldn't find out how to run through
        // list if current player is not player 1
        NumberOfTurnsPlayed++;

        if(NumberOfTurnsPlayed == players.size())
        {
            return false;
        }

        // added new variable to keep track of new starting players number
        // added conditions to ensure all players are able to have a turn

        if(newRoundStartingPlayer != 1){
            // this condition runs when player 1 is not the starting player
            currentPlayer = players.get(NumberOfTurnsPlayed - 1);
        }
        else {
            // using this condition if player one is the first player
            // ensuring that player 1 which is in the index of 0 does not go again
            currentPlayer = players.get(NumberOfTurnsPlayed);
        }

        return true;
    }

    public void playerHold(char dieNum){
        boolean diceOptionAvailable = dice.stream()
                .anyMatch(Die -> Die.getDieNum() == dieNum);

        if(diceOptionAvailable)
        {
            dice.stream()
                    .filter(Die -> !Die.isBeingHeld())
                    .filter(Die -> Die.getDieNum() == dieNum)
                    .findFirst()
                    .get()
                    .holdDie();
        }
    }

    public void resetDice(){

        dice.forEach(Die::resetDie);

    }

    public void resetPlayers(){
        Stream<Player> allPlayersCompletedTurn = players.stream();
        allPlayersCompletedTurn.forEach(Player::resetPlayer);
    }

    public void rollDice(){
       currentPlayer.roll();

       dice.forEach(Die::rollDie);
    }

    public void scoreCurrentPlayer(){
        int score = 0;

        if(!isHoldingDie(6) && !isHoldingDie(5) && !isHoldingDie(4))
        {
            currentPlayer.setScore(0);
        }
        else if(isHoldingDie(6) && isHoldingDie(5) && isHoldingDie(4))
        {
            for (int i = 0; i < dice.size(); i++){
                score += dice.get(i).getFaceValue();
            }
            score = score - 15;
            currentPlayer.setScore(score);
        }
    }

    public void startNewGame(){
        NumberOfTurnsPlayed = 0;

        List<Player> playersOrderByScore = players.stream()
                .sorted(Comparator.comparingInt(Player::getScore)
                        .reversed())
                .toList();

        currentPlayer = playersOrderByScore.get(0);
        newRoundStartingPlayer = currentPlayer.getPlayerNumber();

        resetPlayers();
    }


}
