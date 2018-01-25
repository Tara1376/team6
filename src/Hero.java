/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

import java.security.interfaces.DSAPublicKey;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Tara
 */
public class Hero extends Warrior {
    private WarriorView warriorView;
    private Soldier[] soldiers = new Soldier[3];
    private Dimension soldierDims[] = new Dimension[3];
    private int resurrectionTime;
    private int powerLevel;
    private int experienceLevel;
    private int money;
    private Achievement achievement;

    private boolean shouldMove;
    private Dimension moveTo;





    Hero(Dimension dimension) {
        warriorView = new WarriorView("hero", "5/", new Dimension(400, 300));
        achievement = new Achievement();

        setDimension(dimension);
        setMoney(10000);
        setEnergy(300);
        setRadius(0.5);
        setShootingSpeed(7);
        setPowerOfBullet(20);

        soldierDims[0] = new Dimension(30, 0);
        soldierDims[1] = new Dimension(-30, 0);
        soldierDims[2] = new Dimension(0, -30);
        soldiers[0] = null;
        soldiers[1] = null;
        soldiers[2] = null;

        shouldMove = false;
        super.shouldShoot = false;
    }

   public WarriorView getWarriorView() {
        return warriorView;
    }

    void setSoldiers(Soldier[] soldiers) {
        this.soldiers = soldiers;
    }

    int getResurrectionTime() {
        this.calculateResurrectionTime();
        return resurrectionTime;
    }

    Dimension[] getSoldierDims() {
        return soldierDims;
    }

    int getMoney() {
        return money;
    }

    private void setMoney(int money) {
        this.money = money;
    }

    void addMoney(int amount) {
        this.money += amount;
    }

    Achievement getAchievement() {
        return achievement;
    }

    void reduceMoney(int amount) {
        this.money -= amount;
    }

    private boolean addPowerLevel() {
        if (powerLevel < 3) {
            powerLevel++;
            this.increaseBulletPower();
            this.increaseBulletSpeed();
            for (int i = 0; i < 3; i++) {
                if (soldiers[i] != null) {
                    soldiers[i].increaseBulletPower();
                    soldiers[i].increaseBulletSpeed();
                }
            }
            return true;
        }
        return false;
    }

    Soldier[] getSoldiers() {
        return soldiers;
    }

    boolean addExperienceLevel(int amount) {
        if (amount > 0)
            experienceLevel += amount;
        if (experienceLevel - powerLevel * 50 >= 50) {
            if (addPowerLevel()) {
                return true;
            }
        }
        return false;
    }

    private void calculateResurrectionTime() {
        this.resurrectionTime = (int) ((5.0 - (((double) this.experienceLevel / 100) + (double) this.powerLevel)) * 0.99);
    }

    Weapon buyWeapon(String nameOfWeapon, Dimension dimension, int locationNum) {
        if (this.getMoney() >= Weapon.getInitialPrice(nameOfWeapon)) {
            Weapon bought = Weapon.WeaponFactory(dimension, nameOfWeapon, locationNum);
            if (bought != null) {
                reduceMoney(bought.getPrice());
            }
            return bought;
        }
        System.out.println("Not enough money");
        return null;
    }

    boolean upgradeWeapon(Weapon toUpgrade) {
        return toUpgrade.upgrade(this);
    }

    boolean upgradeSoldiers() {
        int numAlive = 0;
        for (int i = 0; i < 3; i++) {
            if (soldiers[i] != null) {
                numAlive++;
            }
        }
        if (numAlive == 0) {
            System.out.println("There are no soldiers to upgrade.");
        } else {
            if (this.getMoney() >= numAlive * 10) {
                this.reduceMoney(numAlive * 10);
                for (int i = 0; i < 3; i++) {
                    if (soldiers[i] != null) {
                        soldiers[i].increaseRadius();
                    }
                }
                return true;
            } else {
                System.out.println("Not enough money");
            }
        }
        return false;
    }

    void showStatus() {
        System.out.println("place: " + super.getDimension() +
                "\tenergy left: " + super.getEnergy() +
                "\tnumber of aliens killed: " + achievement.getNumOfKilledByHero());
    }

    void showKnightStatus() {
        for (int i = 0; i < 3; i++) {
            if (soldiers[i] != null) {
                System.out.print("Soldier #" + (i + 1));
                soldiers[i].showStatus();
            } else {
                System.out.println("Soldier #" + (i + 1) + " not found.");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("*** Hero ***\n----------\n");
        string.append("place: ").append(super.getDimension()).append("\tenergy left: ").append(super.getEnergy()).append("\n\n");

        string.append("*** Soldiers ***\n----------\n");
        boolean hasSoldiers = false;
        for (int i = 0; i < 3; i++) {
            if (soldiers[i] != null) {
                string.append("Soldier #" + (i + 1) + soldiers[i].toString());
                hasSoldiers = true;
            }
        }
        if (!hasSoldiers) {
            string.append("No soldiers found");
        }

        string.append("\n\n");
        return string.toString();
    }

    /**** Hero changes its dimension and his alive soldiers also change dimensions. ****/

    @Override
    public void move(Dimension changeDimension){

        double deltaX = changeDimension.getX();
        double deltaY = changeDimension.getY();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (deltaY > 0) {
                    warriorView.moveDown(deltaY);
                }else if (deltaY < 0){
                    warriorView.moveUp(-1 * deltaY);
                }
                if (deltaX > 0){
                    warriorView.moveRight(deltaX);
                }else if (deltaX < 0){
                    warriorView.moveLeft(-1 * deltaX);
                }
            }
        });

        Dimension newDim = new Dimension(getShootingPoint().getX() + changeDimension.getX(),
                getShootingPoint().getY() + changeDimension.getY());

        Dimension.correctDim(newDim);

        setDimension(newDim);

        correctDim();
        // System.out.println("hero moved to " + newDim);
        for (int i = 0; i < 3; i++) {
            if (soldiers[i] != null) {
                soldiers[i].move(changeDimension);
                soldiers[i].correctDim();
            }
        }


    }

    private Object lock = new Object();

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (shouldMove){

                //TODO improve calculations
                Dimension moveFrom = super.dimension;
                double deltaX = (moveTo.getX() - moveFrom.getX());
                double deltaY = (moveTo.getY() - moveFrom.getY());
                double slope = deltaY / deltaX;

                int signX = Double.compare(deltaX, 0.0);//0;
                if (signX != 0)
                    signX = signX / Math.abs(signX);
                /*if (deltaX > 0){
                    signX = 1;
                }else if (deltaX < 0){
                    signX = -1;
                }*/

                int signY = Double.compare(deltaY, 0.0);
                if (signY != 0)
                    signY = signY / Math.abs(signY);
                /*if (deltaY > 0){

                    signY = 1;
                }else if(deltaY < 0){
                    signY = -1;
                }*/

                if (signX == 0 && signY == 0){
                    deltaX = 0;
                    deltaY = 0;
                }else if (signX == 0){
                    deltaX = 0;
                    deltaY = 10.0 * signY;
                }else if (signY == 0){
                    deltaY = 0;
                    deltaX = 10.0 * signX;
                }else{
                    double dummy = 1.0 + slope * slope;
                    dummy = 100.0 / dummy;
                    dummy = Math.sqrt(dummy);
                    dummy = Math.round(dummy * 10);
                    deltaX = (dummy / 10.0) * signX;
                    deltaY = deltaX * slope;
                }

                Dimension changeDim = new Dimension(deltaX, deltaY);
                while (!super.dimension.equals(moveTo)){
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (lock){
                        double xRemain = (moveTo.getX() - super.dimension.getX());

                        if (Double.compare(Math.abs(xRemain), Math.abs(deltaX)) < 0){
                            changeDim.setX(xRemain);
                        }
                        double yRemain = (moveTo.getY() - super.dimension.getY());
                        if (Double.compare(Math.abs(yRemain), Math.abs(deltaY)) < 0){
                            changeDim.setY(yRemain);
                        }
                        Dimension.correctDim(changeDim);

                        move(changeDim);
                    }
                }
                //System.out.println("hero reached destination");
                setShouldMove(false);
            }

            if (shouldShoot){
                killed.addAll(shoot(toShoot));
            }
        }
    }

    public boolean setShouldMove(Dimension changeDim){
        Dimension newDim = new Dimension(getShootingPoint().getX() + changeDim.getX(),
                getShootingPoint().getY() + changeDim.getY());

        if (newDim.isWithinBounds(GameMap.XBOUND - 50,
                20,
                GameMap.YBOUND - 75,
                20)
                ) {
            this.shouldMove = true;
            Dimension.correctDim(newDim);
            this.moveTo = newDim;
        } else {
            System.out.println("out of range.");
            this.shouldMove = false;
        }
        return this.shouldMove;
    }

    public boolean setShouldMove(KeyEvent event){
        KeyCode direction = event.getCode();
        Dimension changeDim = null;
        switch (direction){
            case DOWN:
                changeDim = new Dimension(0, 10);
                break;
            case UP:
                changeDim = new Dimension(0, -10);
                break;
            case LEFT:
                changeDim = new Dimension(-10, 0);
                break;
            case RIGHT:
                changeDim = new Dimension(10, 0);
                break;
        }
        return setShouldMove(changeDim);
    }

    public void setShouldMove(boolean shouldMove){
        this.shouldMove = shouldMove;
    }

    public boolean isShouldMove() {
        return shouldMove;
    }
}

class WarriorView extends StackPane{

    private ImageView[] move_down;
    private ImageView[] move_up;
    private ImageView[] move_left;
    private ImageView[] move_right;
    private int move_down_index;
    private int move_up_index;
    private int move_left_index;
    private int move_right_index;

    public WarriorView(String name, String number, Dimension dim) {
        System.out.println("setting view for " + name);
        this.move_down = new ImageView[3];
        this.move_up = new ImageView[3];
        this.move_right = new ImageView[3];
        this.move_left = new ImageView[3];

        move_down_index = 0;
        move_up_index = 0;
        move_right_index = 0;
        move_left_index = 0;

        String address = "res/" + name + "/movement/" + number;
        move_down[0] = new ImageView(new Image(getClass()
                .getResource(address + "down1.png").toExternalForm()));
        move_down[0].setFitWidth(30);
        move_down[0].setFitHeight(35);
        move_down[0].setVisible(true );

        move_down[1] = new ImageView(new Image(getClass()
                .getResource(address + "down2.png").toExternalForm()));
        move_down[1].setFitWidth(30);
        move_down[1].setFitHeight(35);
        move_down[1].setVisible(false);

        move_down[2] = new ImageView(new Image(getClass()
                .getResource(address + "down3.png").toExternalForm()));
        move_down[2].setFitWidth(30);
        move_down[2].setFitHeight(35);
        move_down[2].setVisible(false);

        move_up[0] = new ImageView(new Image(getClass()
                .getResource(address + "up1.png").toExternalForm()));
        move_up[0].setFitWidth(30);
        move_up[0].setFitHeight(35);
        move_up[0].setVisible(false);

        move_up[1] = new ImageView(new Image(getClass()
                .getResource(address + "up2.png").toExternalForm()));
        move_up[1].setFitWidth(30);
        move_up[1].setFitHeight(35);
        move_up[1].setVisible(false);

        move_up[2] = new ImageView(new Image(getClass()
                .getResource(address + "up3.png").toExternalForm()));
        move_up[2].setFitWidth(30);
        move_up[2].setFitHeight(35);
        move_up[2].setVisible(false);

        move_left[0] = new ImageView(new Image(getClass()
                .getResource(address + "left1.png").toExternalForm()));
        move_left[0].setFitWidth(30);
        move_left[0].setFitHeight(35);
        move_left[0].setVisible(false);

        move_left[1] = new ImageView(new Image(getClass()
                .getResource(address + "left2.png").toExternalForm()));
        move_left[1].setFitWidth(30);
        move_left[1].setFitHeight(35);
        move_left[1].setVisible(false);

        move_left[2] = new ImageView(new Image(getClass()
                .getResource(address + "left3.png").toExternalForm()));
        move_left[2].setFitWidth(30);
        move_left[2].setFitHeight(35);
        move_left[2].setVisible(false);

        move_right[0] = new ImageView(new Image(getClass()
                .getResource(address + "right1.png").toExternalForm()));
        move_right[0].setFitWidth(30);
        move_right[0].setFitHeight(35);
        move_right[0].setVisible(false);

        move_right[1] = new ImageView(new Image(getClass()
                .getResource(address + "right2.png").toExternalForm()));
        move_right[1].setFitWidth(30);
        move_right[1].setFitHeight(35);
        move_right[1].setVisible(false);

        move_right[2] = new ImageView(new Image(getClass()
                .getResource(address + "right3.png").toExternalForm()));
        move_right[2].setFitWidth(30);
        move_right[2].setFitHeight(35);
        move_right[2].setVisible(false);

        getChildren().addAll(move_down[0],
                move_down[1],
                move_down[2],
                move_up[0],
                move_up[1],
                move_up[2],
                move_left[0],
                move_left[1],
                move_left[2],
                move_right[0],
                move_right[1],
                move_right[2]);
        setTranslateX(dim.getX());
        setTranslateY(dim.getY());
    }

    public void move(KeyEvent event, double delta){
        KeyCode direction = event.getCode();
        switch (direction){
            case LEFT:
                moveLeft(delta);
                break;
            case RIGHT:
                moveRight(delta);
                break;
            case UP:
                moveUp(delta);
                break;
            case DOWN:
                moveDown(delta);
                break;
        }
    }

    private void clear(){
        move_right[0].setVisible(false);
        move_right[1].setVisible(false);
        move_right[2].setVisible(false);
        move_left[0].setVisible(false);
        move_left[1].setVisible(false);
        move_left[2].setVisible(false);
        move_up[0].setVisible(false);
        move_up[1].setVisible(false);
        move_up[2].setVisible(false);
        move_down[0].setVisible(false);
        move_down[1].setVisible(false);
        move_down[2].setVisible(false);
    }

//    private Object lock1 = new Object();

    public void moveRight(double delta){
        clear();
        move_right_index++;
        move_right_index %= 3;
        /*if (move_right_index == 0){
            move_right_index = 1;
        }else {
            move_right_index = 0;
        }*/
        setTranslateX(getTranslateX() + delta);
        move_right[move_right_index].setVisible(true);
        move_right[move_right_index].setVisible(true);
        /*try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (lock1){
        }*/
    }

 //   private Object lock2 = new Object();

    public void moveLeft(double delta){
        clear();
        move_left_index++;
        move_left_index %= 3;
        /*if (move_left_index == 0){
            move_left_index = 1;
        }else{
            move_left_index = 0;
        }*/

        setTranslateX(getTranslateX() - delta);
        move_left[move_left_index].setVisible(true);
        /*try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (lock2){
        }*/
    }

  //  private Object lock3 = new Object();

    public void moveUp(double delta){
        clear();
        move_up_index++;
        move_up_index %= 3;
        /*if (move_up_index == 0){
            move_up_index = 1;
        }else {
            move_up_index = 0;
        }*/
        setTranslateY(getTranslateY() - delta);
        move_up[move_up_index].setVisible(true);
       /* try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (lock3){
        }*/

    }

   // private Object lock4 = new Object();

    public void moveDown(double delta){
        clear();
        move_down_index++;
        move_down_index %= 3;

        /*if (move_down_index == 0){
            move_down_index = 1;
        }else{
            move_down_index = 0;
        }*/
        setTranslateY(getTranslateY() + delta);
        move_down[move_down_index].setVisible(true);
       /* try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (lock4){
        }*/

    }
}


class Achievement {
    private int numTypeAlien = 4;
    private int[] numOfKilledByWeapon = new int[numTypeAlien];
    private int[] numOfKilledBySoldier = new int[numTypeAlien];
    private int[] numOfKilledByHero = new int[numTypeAlien];
    private Map<String, Boolean> achieved = new HashMap<>();

    Achievement() {
        achieved.put("Great Hunter", false);
        achieved.put("Good Gene", false);
        achieved.put("Greek Goddess", false);
        achieved.put("Eagle Eye", false);
        achieved.put("Restless Shooter", false);
        achieved.put("Brave Warrior", false);
        achieved.put("Butcher", false);
        achieved.put("Blood Sucker", false);
    }

    void killedWeapon(Alien alien) {
        
        alien.getAlienView().dead();
        
        switch (alien.getName()) {
            
            case "Albertonion":
                numOfKilledByWeapon[0]++;
                if (numOfKilledByWeapon[0] > 9)
                    achieved.replace("Restless Shooter", true);
                break;
            case "Algwasonion":
                numOfKilledByWeapon[1]++;
                if (numOfKilledByWeapon[1] > 9)
                    achieved.replace("Brave Warrior", true);
                break;
            case "Activionion":
                numOfKilledByWeapon[2]++;
                if (numOfKilledByHero[2] > 9)
                    achieved.replace("Butcher", true);
                break;
            case "Aironion":
                numOfKilledByWeapon[3]++;
                if (numOfKilledByHero[3] > 9)
                    achieved.replace("Blood Sucker", true);
                break;
        }
    }

    /*public void killedSoldier(Alien alien){
        switch (alien.getName()){
            case "Albertonion":
                numOfKilledBySoldier[0]++;
                break;
            case "Algwasonion":
                numOfKilledBySoldier[1]++;
                break;
            case "Activionion":
                numOfKilledBySoldier[2]++;
                break;
            case "Aironion":
                numOfKilledBySoldier[3]++;
                break;
        }
    }  */
    
    void killedHero(Alien alien) {
        
        alien.getAlienView().dead();
        switch (alien.getName()) {
            case "Albertonion":
                numOfKilledByHero[0]++;
                if (numOfKilledByHero[0] > 4)
                    achieved.replace("Great Hunter", true);
                break;
            case "Algwasonion":
                numOfKilledByHero[1]++;
                if (numOfKilledByHero[1] > 4)
                    achieved.replace("Good Gene", true);
                break;
            case "Activionion":
                numOfKilledByHero[2]++;
                if (numOfKilledByHero[2] > 4)
                    achieved.replace("Greek Goddess", true);
                if (numOfKilledByHero[2] > 9)
                    achieved.replace("Eagle Eye", true);
                break;
            case "Aironion":
                numOfKilledByHero[3]++;
                break;
        }
    }

    int getNumOfKilledByHero() {
        
        
        int num = 0;
        for (int i = 0; i < numOfKilledByHero.length; i++) {
            num += numOfKilledByHero[i];
        }
        return num;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String key : achieved.keySet()) {
            if (achieved.get(key)) {
                str.append(key).append("\n");
            }
        }
        return str.toString();
    }
}