
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.util.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GameMap {
    static double XBOUND = 895;
    static double YBOUND = 700;

    static int UNIT = 10;

    private List<Route> routes = new ArrayList<>();
    private List<Wormhole> wormholes = new ArrayList<>();
    private Map<Dimension, Mappable> specifiedLocations = new HashMap<>();
    private Map<Integer, Dimension> specifiedNumbers = new HashMap<>();
     Barrack barrack;

    private Dimension flag;
    private Alien[] reachedFlag = new Alien[5];
    private ArrayList<Thread> alienLifeCycles;

    private Hero hero;
    private Weapon tesla;

     int secondsLeftToResurrectHero = 0;
    private int weatherCondition = 0;
    private double weatherConditionConstant = 1;

    private boolean canUpgradeSoldiers = true;
    private boolean SuperNaturalHelp = false;

    private boolean hasBurrowed = false;
    private Bank bank = new Bank();

    static ParallelTransition parallelTransition = new ParallelTransition();

    Text numFlag=new Text(GameMap.XBOUND+50,GameMap.YBOUND-90,"0");
    ImageView[] reached = new ImageView[5];
    Text weaponName = new Text(XBOUND + 20, YBOUND - 300, "");
    Text weaponPrice = new Text(XBOUND + 20, YBOUND - 250, "");
    Text weaponLevel = new Text(XBOUND + 20, YBOUND - 200, "");
    Text weaponKilled = new Text(XBOUND + 20, YBOUND - 150, "");
    ImageView bank_VIEW = new ImageView(new Image(getClass()
                .getResource("res/bank/bank.png").toExternalForm()));

    public void initBank(){
        //bank_VIEW =

        bank_VIEW.relocate(905, 185);
        bank_VIEW.setFitHeight(50);
        bank_VIEW.setFitWidth(50);

        bank_VIEW.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                burrowMoney(hero.getMaximumMoney() - hero.getMoney());
            }
        });

        bank_VIEW.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                burrowMoney(hero.getMaximumMoney() - hero.getMoney());
            }
        });

        bank_VIEW.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                bank_VIEW.setEffect(new Glow(10));
            }
        });

        bank_VIEW.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                bank_VIEW.setEffect(new Glow(0));
            }
        });
/*
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                bank_VIEW);
            }
        });*/

    }

    public void initalWeaponStatus(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                        weaponName);
                AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                        weaponPrice);
                AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                        weaponLevel);
                AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                        weaponKilled);
            }
        });

    }
    
    public void initialNumFlag(){
        Font font = Font.loadFont(MyMenuItem.
                class.
                getResource("res/Font/Pieces_of_Eight.ttf").
                toExternalForm(), 25);
        numFlag.setFont(font);

        for (int i = 0; i < 5; i++){
            reached[i] = new ImageView();
        }

        reached[0].relocate(XBOUND + 70, YBOUND - 100 - 10);
        reached[1].relocate(XBOUND + 110, YBOUND - 100 - 10);
        reached[2].relocate(XBOUND + 40, YBOUND - 70 - 10);
        reached[3].relocate(XBOUND + 80, YBOUND - 70 - 10);
        reached[4].relocate(XBOUND + 120, YBOUND - 70 - 10);

        numFlag.setFill(Color.rgb(133, 171, 37));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                        numFlag);
                AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                        reached);
            }
        });
    
    }
    
    public void updateNumFlag(){
        
        numFlag.setText(String.valueOf(getNumReachedFlag()));
    
    
    }
    
    /*** STATIC FOR CUSTOM ***/
    static private int peakHourMax=16;
    static private int peakHourMin=10;        
    
    static private int peakHourMaxAlienRate=2;
    static private int peakHourMinAlienRate=3;

    
    
    public static int getPeakHourMax() {
        return peakHourMax;
    }
    
    

    public static void setPeakHourMax(int peakHourMax) {
        GameMap.peakHourMax = peakHourMax;
    }

    
    
    public static int getPeakHourMin() {
        return peakHourMin;
    }

    
    public static void setPeakHourMin(int peakHourMin) {
        GameMap.peakHourMin = peakHourMin;
    }

    
    public static int getPeakHourMaxAlienRate() {
        return peakHourMaxAlienRate;
    }

    
    public static void setPeakHourMaxAlienRate(int peakHourMaxAlienRate) {
        GameMap.peakHourMaxAlienRate = peakHourMaxAlienRate;
    }

    
    public static int getPeakHourMinAlienRate() {
        return peakHourMinAlienRate;
    }

    
    public static void setPeakHourMinAlienRate(int peakHourMinAlienRate) {
        GameMap.peakHourMinAlienRate = peakHourMinAlienRate;
    }
    
    
    
    
                /*** RADIUS RELATED CUSTOM ***/
    
    private static int whenReduceRadius = 20;
    private static int whenResetRadius = 4;
    private static double reduceRadiusRate=0.85;


    public static int getWhenReduceRadius() {
        return whenReduceRadius;
    }

    public static void setWhenReduceRadius(int whenReduceRadius) {
        GameMap.whenReduceRadius = whenReduceRadius;
    }

    public static int getWhenResetRadius() {
        return whenResetRadius;
    }
    public static void setWhenResetRadius(int whenResetRadius) {
        GameMap.whenResetRadius = whenResetRadius;
    }

    public static double getReduceRadiusRate() {
        return reduceRadiusRate;
    }

    public static void setReduceRadiusRate(double reduceRadiusRate) {

        GameMap.reduceRadiusRate = reduceRadiusRate ;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*** OLD CODES ***/
    
    
    void nextSecond() {
        oneTimeActions();

        shootAliens();
        if (moveAliens()) {
            AlienCreeps.endGame(true);
            return;
        }
    }

    /*** TEXT TIME  ***/

    GameMap(){//Hero hero) {
        flag = new Dimension(750, 300);
       // this.hero = hero;
        this.alienLifeCycles = new ArrayList<>();

        Line lines[] = new Line[5];
        ArrayList<Dimension> breakPoints = new ArrayList<>();
        ArrayList<Dimension> intersections = new ArrayList<>();
        intersections.add(new Dimension(450, 300));
        intersections.add(flag);

        breakPoints.add(new Dimension(0, 0));
        breakPoints.add(new Dimension(150, 150));
        breakPoints.add(new Dimension(300, 150));
        breakPoints.add(new Dimension(450, 300));
        breakPoints.add(new Dimension(600, 150));
        lines[0] = new Line(1.0, 0.0, breakPoints.get(0), breakPoints.get(1));
        lines[1] = new Line(0.0, 150.0, breakPoints.get(1), breakPoints.get(2));
        lines[2] = new Line(1.0, -150.0, breakPoints.get(2), breakPoints.get(3));
        lines[3] = new Line(-1.0, 750, breakPoints.get(3), breakPoints.get(4));
        lines[4] = new Line(1.0, -450, breakPoints.get(4), flag);
        routes.add(new Route(lines, intersections));


        breakPoints.set(0, new Dimension(0, 600));
        breakPoints.set(1, new Dimension(150, 450));
        breakPoints.set(2, new Dimension(300, 450));
        breakPoints.set(3, new Dimension(450, 300));
        breakPoints.set(4, new Dimension(600, 450));
        lines[0] = new Line(-1.0, 600, breakPoints.get(0), breakPoints.get(1));
        lines[1] = new Line(0, 450, breakPoints.get(1), breakPoints.get(2));
        lines[2] = new Line(-1.0, 750, breakPoints.get(2), breakPoints.get(3));
        lines[3] = new Line(1.0, -150, breakPoints.get(3), breakPoints.get(4));
        lines[4] = new Line(-1.0, 1050, breakPoints.get(4), flag);
        routes.add(new Route(lines, intersections));

        Dimension dimension;

        //dimension = new Dimension(52, 50);
        dimension = new Dimension(30.0, 195.0);
        Dimension.correctDim(dimension);
        specifiedLocations.put(dimension, null);
        specifiedNumbers.put(1, dimension);
        //dimension = new Dimension(73, 75);
        dimension = new Dimension(30.0, 515.0);
        Dimension.correctDim(dimension);
        specifiedLocations.put(dimension, null);
        specifiedNumbers.put(2, dimension);


        //dimension = new Dimension(102, 100);
        dimension = new Dimension(255.0, 130.0);
        Dimension.correctDim(dimension);
        specifiedLocations.put(dimension, null);
        specifiedNumbers.put(3, dimension);


        //dimension = new Dimension(200, 155);
        dimension = new Dimension(255.0, 575.0);
        Dimension.correctDim(dimension);
        specifiedLocations.put(dimension, null);
        specifiedNumbers.put(4, dimension);

        //dimension = new Dimension(240, 145);
        dimension = new Dimension(355.0, 355.0);
        Dimension.correctDim(dimension);
        specifiedNumbers.put(5, dimension);
        specifiedLocations.put(dimension, null);

        //dimension = new Dimension(270, 155);
        dimension = new Dimension(480.0, 225.0);
        Dimension.correctDim(dimension);
        specifiedLocations.put(dimension, null);
        specifiedNumbers.put(6, dimension);

        //dimension = new Dimension(350, 202);
        dimension = new Dimension(480.0, 480.0);
        Dimension.correctDim(dimension);
        specifiedLocations.put(dimension, null);
        specifiedNumbers.put(7, dimension);

        //dimension = new Dimension(385, 240);
        dimension = new Dimension(640.0, 130.0);
        Dimension.correctDim(dimension);
        specifiedNumbers.put(8, dimension);
        specifiedLocations.put(dimension, null);

        //dimension = new Dimension(420, 268);
        dimension = new Dimension(640.0, 575.0);
        Dimension.correctDim(dimension);
        specifiedLocations.put(dimension, null);
        specifiedNumbers.put(9, dimension);


        //dimension = new Dimension(450, 295);
        dimension = new Dimension(800.0, 225.0);
        Dimension.correctDim(dimension);
        specifiedNumbers.put(10, dimension);
        specifiedLocations.put(dimension, null);

        //dimension = new Dimension(450, 305);
        dimension = new Dimension(800.0, 480.0);
        Dimension.correctDim(dimension);
        specifiedNumbers.put(11, dimension);
        specifiedLocations.put(dimension, null);

        //dimension = new Dimension(445, 300);
        dimension = new Dimension(610.0, 350.0);
        Dimension.correctDim(dimension);
        specifiedNumbers.put(12, dimension);
        specifiedLocations.put(dimension, null);

        //dimension = new Dimension(455, 300);
        dimension = new Dimension(675.0, 350.0);
        Dimension.correctDim(dimension);
        specifiedNumbers.put(13, dimension);
        specifiedLocations.put(dimension, null);


        List<Dimension> wormholeDims = Dimension.randomDimension(6);
        wormholes.add(new Wormhole(1, wormholeDims.get(0)));
        wormholes.add(new Wormhole(0, wormholeDims.get(1)));
        wormholes.add(new Wormhole(3, wormholeDims.get(2)));
        wormholes.add(new Wormhole(2, wormholeDims.get(3)));
        wormholes.add(new Wormhole(5, wormholeDims.get(4)));
        wormholes.add(new Wormhole(4, wormholeDims.get(5)));
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    private void oneTimeActions(){
        if (AlienCreeps.getCurrentHour() == 0 && AlienCreeps.getCurrentSecond() == 1 && AlienCreeps.getCurrentDay() == 0){
            for (int i = 0; i < 6; i++){
                int finalI = i;
                Platform.runLater(() -> AlienCreeps.addElementToGameRoot(1,
                        wormholes.get(finalI).getWormholeView()));
            }
        }

        /****   CHECKING THE INTERGALACTIC BANK DEBT ****/
        if (hasBurrowed){
            if (bank.isDue(AlienCreeps.getCurrentDay() - 1)){
                if (!bank.payBack(this.hero)) {
                    for (Dimension dimension : specifiedLocations.keySet()) {
                        if (!(specifiedLocations.get(dimension) instanceof Barrack)){
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    AlienCreeps.removeElementFromGameRoot(((Weapon) specifiedLocations.get(dimension)).getWeaponView());
                                }
                            });
                            specifiedLocations.replace(dimension, null);
                        }
                    }
                    AlienCreeps.showPopupWarning("Failed to\npay bank", 40, 50);
                    System.out.println("You have failed to pay the bank back. All weapons have been destroyed.");
                }
                hasBurrowed = false;
            }else{
                if (AlienCreeps.getCurrentHour() == 0 && AlienCreeps.getCurrentSecond() == 0){
                    System.out.println((bank.getDueDate() - AlienCreeps.getCurrentDay() + 1) +
                            " day(s) left\nto pay bank.");
                    AlienCreeps.showPopupWarning((bank.getDueDate() - AlienCreeps.getCurrentDay() + 1) +
                            " day(s) left\nto pay bank.", 40, 50);
                }
            }
        }

        /**** REDUCING RADIUS BASED ON HOUR ****/

        if (AlienCreeps.getCurrentHour() == whenReduceRadius && AlienCreeps.getCurrentSecond() == 0) {
            reduceRadius();
        }
        if (AlienCreeps.getCurrentSecond() == 0 && AlienCreeps.getCurrentHour() == whenResetRadius) {
            resetRadius();
        }

        /****   CHANGING WORMHOLE DIMENSIONS RANDOMLY ****/

        randomizeWormholes();

        /***** SEEING WHETHER WE CAN USE TESLA AGAIN OR NOT ****/

        updateTeslaStatus();

        /**** SEEING IF HERO CAN COME BACK TO LIFE OR NOT ****/

        if (this.hero.isDead()) {
            this.secondsLeftToResurrectHero--;
            System.out.println("hero will be back in " + secondsLeftToResurrectHero);
            if (this.secondsLeftToResurrectHero <= 0) {
                this.secondsLeftToResurrectHero = 0;
                this.hero.setEnergy(300);
                hero.getHealthBar().initBar();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                hero.getWarriorView());
                        AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                hero.getBulletView());
                        AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                hero.getHealthBar());
                    }
                });
            }
        }

        /**** SEEING IF BARRACK HAS TRAINED NEW SOLDIERS ****/

        if (barrack != null) {
            barrack.proceed();
       //     System.out.println("proceed barrack ");
            Soldier soldier = barrack.getSoldier();
            barrack.removeSoldier();
            if (soldier != null) {
                for (int i = 0; i < 3; i++) {
                    if (hero.getSoldiers()[i] == null) {
                        Dimension soldierDimension = hero.getDimension().add(hero.getSoldierDims()[i]);
                        soldier.setDimension(soldierDimension);

                        hero.getSoldiers()[i] = soldier;

                       // new Thread(soldier).start();

                        soldier.setWarriorView(i + 1, hero.getDimension().add(hero.getSoldierDims()[i]));
                        int finalI1 = i;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                        soldier.getWarriorView());
                                AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                        soldier.getHealthBar());
                                soldier.getHealthBar().setDim(900, 240 + finalI1 * 30);
                                AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                        soldier.getBulletView());
                            }
                        });
           //             System.out.println("BARRACK MADE NEW SOLDIER");
             //           System.out.println("welcome soldier " + i);
                        break;
                    }
                }
            }
        }

        /**** GENERATING ALIENS EVERY SECONS ***/

        if (AlienCreeps.getCurrentHour() <=peakHourMax  && AlienCreeps.getCurrentHour() >= peakHourMin) {
            generateAliens(10 - peakHourMaxAlienRate);
        } else {
            generateAliens(10 - peakHourMinAlienRate);
        }
    }

    private void reduceRadius() {
        for (int i = 0; i < 3; i++) {
            Soldier s = hero.getSoldiers()[i];
            if (s != null) {
                s.reduceRadius(reduceRadiusRate);
            }
        }
        for (Dimension dimension : specifiedLocations.keySet()) {
            if (specifiedLocations.get(dimension) instanceof Weapon) {
                Weapon w = ((Weapon) specifiedLocations.get(dimension));
                w.reduceRadius(reduceRadiusRate);
            }
        }
    }

    private void resetRadius() {
        for (int i = 0; i < 3; i++) {
            Soldier s = hero.getSoldiers()[i];
            if (s != null) {
                //System.out.println("Soldier #" + (i + 1));
                s.resetRadius();
            }
        }
        for (Dimension dimension : specifiedLocations.keySet()) {
            if (specifiedLocations.get(dimension) instanceof Weapon) {
                Weapon w = ((Weapon) specifiedLocations.get(dimension));
                w.resetRadius(reduceRadiusRate);
            }
        }
    }

    void upgradeSoldier() {
        if (canUpgradeSoldiers) {
            if (this.hero.upgradeSoldiers()) {
                canUpgradeSoldiers = false;
            }
        } else {
            AlienCreeps.showPopupWarning("Try tomorrow", 85, 105);
            System.out.println("Can't upgrade soldiers twice in one day. Try again tomorrow.");
        }
    }

    private void randomizeWormholes() {
        if ((int) (Math.random() * 8) == 1) {
            List<Dimension> wormholeDims = Dimension.randomDimension(6);

            /*** check that they're not on the routes ***/
            List<Line> lines = new ArrayList<>();

            for (int i = 0; i < routes.size(); i++){
                lines.addAll(Arrays.asList(routes.get(i).getLines()));
            }

            for (int i = 0; i < 6; i++){
                for (int j = 0; j < lines.size(); j++){
                    while (lines.get(j).isOnLine(wormholeDims.get(i))){
                        System.out.println("wormhole dim is on line, must change");
                        wormholeDims.set(i, Dimension.randomDimension(1).get(0));
                    }
                }
            }
            for (int i = 0; i < 6; i++) {
                wormholes.get(i).setDimension(wormholeDims.get(i));

            }

            System.out.println("New Wormhole Dimensions are:");
            wormholeDims.forEach(System.out::println);
         //   Dimension changeDim = new Dimension(wormholeDims.get(0).getX() - this.hero.dimension.getX(),
           //         wormholeDims.get(0).getY() - this.hero.dimension.getY());
         //   System.out.println("move hero for " + changeDim);
        }

    }

    void showRemainingAliens() {
        System.out.println("*** Aliens ***");
        System.out.println("------------");
        ArrayList<Alien> remainingAliens = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            remainingAliens.addAll(routes.get(i).getAliens());
        }
        if (remainingAliens.size() > 0) {
            Collections.sort(remainingAliens);
            remainingAliens.forEach(System.out::println);
        } else {
            System.out.println("No aliens.");
        }
        System.out.print("\n\n");
    }

    void focusWeapons() {

        ArrayList<Weapon> weapons = new ArrayList<>();

        for (Integer integer : specifiedNumbers.keySet()) {
            Dimension dimension = specifiedNumbers.get(integer);
            Mappable m = specifiedLocations.get(dimension);
            if (m instanceof Weapon) {
                Weapon weapon = ((Weapon) m);
                weapons.add(weapon);
            }
        }

        for (int i = 0; i < weapons.size(); i++){
            if (weapons.get(i).getWeaponView().isFocus()){
                weapons.get(i).getWeaponView().setUnfocus();
                if (i + 1 < weapons.size()){
                    weapons.get(i + 1).getWeaponView().setFocus();

                    weaponName.setText("Name: " + weapons.get(i + 1).getName());
                    weaponPrice.setText("Price: " + weapons.get(i + 1).getPrice());
                    weaponLevel.setText("Level: " + weapons.get(i + 1).getLevel());
                    weaponKilled.setText("Num Killed: " + weapons.get(i + 1).getNumKilled());

                    Font font = Font.loadFont(MyMenuItem.
                            class.
                            getResource("res/Font/Pieces_of_Eight.ttf").
                            toExternalForm(), 25);

                    weaponName.setFont(font);
                    weaponPrice.setFont(font);
                    weaponLevel.setFont(font);
                    weaponKilled.setFont(font);
                    weaponName.setFill(Color.rgb(133, 171, 37));
                    weaponPrice.setFill(Color.rgb(133, 171, 37));
                    weaponLevel.setFill(Color.rgb(133, 171, 37));
                    weaponKilled.setFill(Color.rgb(133, 171, 37));

                }else{
                    weapons.get(0).getWeaponView().setFocus();
                }
                return;
            }
        }
        if (!weapons.isEmpty()){
            weapons.get(0).getWeaponView().setFocus();
            weaponName.setText("Name: " + weapons.get(0).getName());
            weaponPrice.setText("Price: " + weapons.get(0).getPrice());
            weaponLevel.setText("Level: " + weapons.get(0).getLevel());
            weaponKilled.setText("Num Killed: " + weapons.get(0).getNumKilled());

            Font font = Font.loadFont(MyMenuItem.
                    class.
                    getResource("res/Font/Pieces_of_Eight.ttf").
                    toExternalForm(), 30);

            weaponName.setFont(font);
            weaponPrice.setFont(font);
            weaponLevel.setFont(font);
            weaponKilled.setFont(font);
            weaponName.setFill(Color.rgb(133, 171, 37));
            weaponPrice.setFill(Color.rgb(133, 171, 37));
            weaponLevel.setFill(Color.rgb(133, 171, 37));
            weaponKilled.setFill(Color.rgb(133, 171, 37));
        }
    }

    void unFocusAll(){
        ArrayList<Weapon> weapons = new ArrayList<>();

        for (Integer integer : specifiedNumbers.keySet()) {
            Dimension dimension = specifiedNumbers.get(integer);
            Mappable m = specifiedLocations.get(dimension);
            if (m instanceof Weapon) {
                Weapon weapon = ((Weapon) m);
                if (weapon.getWeaponView().isFocus()){
                    weapon.getWeaponView().setUnfocus();
                }
            }
        }

        ArrayList<Alien> allAliens = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++){
            allAliens.addAll(routes.get(i).getAliens());
        }

        for (int i = 0; i < allAliens.size(); i++){
            if (allAliens.get(i).getAlienView().isFocus()){
                allAliens.get(i).getAlienView().setUnfocus();
                int finalI = i;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        AlienCreeps.removeElementFromGameRoot(allAliens.get(finalI).getProgressBar());
                    }
                });
            }
        }

        weaponName.setText("");
        weaponKilled.setText("");
        weaponLevel.setText("");
        weaponPrice.setText("");
    }

    void stopWalking(){
        ArrayList<Alien> allAliens = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++){
            allAliens.addAll(routes.get(i).getAliens());
        }
        for (int i = 0; i < allAliens.size(); i++){
            allAliens.get(i).stop();
        }
    }

    void upgradeWeapon(){
        for (Integer integer : specifiedNumbers.keySet()) {
            Dimension dimension = specifiedNumbers.get(integer);
            Mappable m = specifiedLocations.get(dimension);
            if (m instanceof Weapon) {
                Weapon weapon = ((Weapon) m);
                if (weapon.getWeaponView().isFocus()){
                    upgradeWeaponInPlace(weapon.getName(), integer);
                    return;
                }
            }
        }
    }

    void focusAliens(){
        ArrayList<Alien> allAliens = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++){
            allAliens.addAll(routes.get(i).getAliens());
        }

        for (int i = 0; i < allAliens.size(); i++){
            if (allAliens.get(i).getAlienView().isFocus()){
                allAliens.get(i).getAlienView().setUnfocus();
                int finalI1 = i;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        AlienCreeps.removeElementFromGameRoot(allAliens.get(finalI1).getProgressBar());
                    }
                });

                if (i + 1 < allAliens.size()){
                    allAliens.get(i + 1).getAlienView().setFocus();
                    int finalI = i;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                    allAliens.get(finalI + 1).getProgressBar());
                        }
                    });
                }else{
                    allAliens.get(0).getAlienView().setFocus();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                    allAliens.get(0).getProgressBar());
                        }
                    });
                }
                return;
            }
        }
        if (!allAliens.isEmpty()){
            allAliens.get(0).getAlienView().setFocus();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                            allAliens.get(0).getProgressBar());
                }
            });
        }
    }

    void putWeaponInPlace(String weaponName, int whichPlace) {

        if (whichPlace > specifiedLocations.keySet().size()) {
            System.out.println("There are only " + specifiedLocations.keySet().size() + " available places.");
            return;
        } else if (whichPlace <= 0) {
            System.out.println("Invalid number.");
        }

        Dimension dimension = specifiedNumbers.get(whichPlace);
        if (specifiedLocations.get(dimension) == null) {
            if (weaponName.equalsIgnoreCase("Barrack")) {
                if (this.barrack == null) {
                    if (hero.getMoney() >= 90) {
                        this.barrack = new Barrack(dimension);
                        Platform.runLater(() -> AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                barrack.getBarrackView()));

                        specifiedLocations.put(dimension, this.barrack);
                        this.hero.reduceMoney(90);
                        barrack.requestSoldier(hero.getResurrectionTime());
                        barrack.requestSoldier(hero.getResurrectionTime());
                        barrack.requestSoldier(hero.getResurrectionTime());
                    }
                } else {
                    AlienCreeps.showPopupWarning("Only one Barrack.", 39, 83);
                    System.out.println("You already have a barrack.");
                }
            } else {
                Weapon bought = this.hero.buyWeapon(weaponName, dimension, whichPlace);

                if (bought != null){
                    Thread weaponLifeCycle = new Thread(bought);
                    weaponLifeCycle.start();

                    Platform.runLater(() -> AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                            bought.getWeaponView()));

                    if (bought instanceof WeaponNearest){
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                        ((WeaponNearest) bought).getBulletView());
                            }
                        });
                    }

                    specifiedLocations.put(dimension, bought);
                }
            }
        } else {
            AlienCreeps.showPopupWarning("Location occupied.", 45, 85);
            System.out.println("There is already a weapon in this location.");
        }
        
        
        
    }

    void upgradeWeaponInPlace(String weaponName, int whichPlace) {
        if (whichPlace > specifiedLocations.keySet().size()) {
            System.out.println("There are only " + specifiedLocations.keySet().size() + " available places.");
            return;
        }
        Dimension dimension = specifiedNumbers.get(whichPlace);
        if (specifiedLocations.get(dimension) != null) {
            if (!(specifiedLocations.get(dimension) instanceof Barrack)) {
                Weapon toUpgrade = ((Weapon) specifiedLocations.get(dimension));
                if (toUpgrade.getName().equalsIgnoreCase(weaponName)) {
                    if (!hero.upgradeWeapon(toUpgrade)) {
                        AlienCreeps.showPopupWarning("Not enough money.", 35, 84);
                      //  System.out.println("Not enough money.");
                    } else {
                        AlienCreeps.showPopupWarning("Upgrade successful.", 30, 105);
                      //  System.out.println("Upgraded successfully");
                        //System.out.println(hero.payBack());
                    }
                } else {
                    System.out.println("Incorrect weaponName");
                }
            } else {
                if (weaponName.equalsIgnoreCase("Barrack")) {
                    AlienCreeps.showPopupWarning("Invalid weapon", 75, 105);
                  //  System.out.println("Can't upgrade barrack.");
                } else {
                    System.out.println("Incorrect weaponName");
                }
            }
        } else {
            System.out.println("There is no weapon in this place");
        }
    }

    private void generateAliens(int probabilityInv) {
        if (Alien.getNUM() < Alien.getMAXNUM()) {
            if ((int) (Math.random() * probabilityInv) == 0) {

                
                int size = Alien.getInitialAlienName().size();
                
                
                int whichAlien = (int) (Math.random() * size);
                String name = null;
                Alien newAlien;
                
                
                /*
                switch (whichAlien) {
                    case 0:
                        weaponName = "albertonion";
                        break;
                    case 1:
                        weaponName = "algwasonion";
                        break;
                    case 2:
                        weaponName = "activionion";
                        break;
                    case 3:
                        weaponName = "aironion";
                        break;
                }
                */
                
                
                name=Alien.getInitialAlienName().get(whichAlien).toLowerCase();
                
                
                /** changing ALIEN creation for Custom  **/
                
                if (Alien.getHashMap().containsKey(name.toLowerCase())){
                    
                    newAlien = new Alien(name);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                    newAlien.getAlienView());
                            AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                    newAlien.bulletView);
                        }
                    });
                //    System.out.println(weaponName + " entered!");
                    Thread alienLifeCycle = new Thread(newAlien);
                    alienLifeCycles.add(alienLifeCycle);
                    alienLifeCycle.start();
                    newAlien.setThreadID(alienLifeCycle.getId());

                    int routeNumber = chooseRandomRoute();
                    Route whichRoute = routes.get(routeNumber);

                    newAlien.move(whichRoute.getLines()[0].getStartPoint());
                    whichRoute.getAlienMap().get(whichRoute.getLines()[0]).add(newAlien);
                }
                
                
                
            }
        }
    }

    private int chooseRandomRoute() {
        return (int) (Math.random() * 2);
    }

    void showReachedFlag() {
        int num = 0;
        ArrayList<String> names = new ArrayList<>();
        for (Alien alien : reachedFlag) {
            if (alien != null) {
                num++;
                names.add(alien.getName());
            }
        }
        System.out.println(num + " aliens have reached flag.");
        names.forEach(System.out::println);
    }

    public int getNumReachedFlag() {
        int num = 0;
        ArrayList<String> names = new ArrayList<>();

        for (int i = 0; i < 5; i++){
            Alien alien = reachedFlag[i];
            if (alien != null){
                reached[i].setImage(alien.getAlienView().getMove_down()[1].getImage());
                reached[i].setFitWidth(25);
                reached[i].setFitHeight(25);
                //alien.getAlienView().getMove_down()[0]
                num++;
            }
        }
        return num;
    }
    
    
    private boolean reachFlag(Alien alien) {
        for (int i = 0; i < 5; i++) {
            if (reachedFlag[i] == null) {
                reachedFlag[i] = alien;
                updateNumFlag();
                System.out.println("-------------");
                System.out.println(alien.getName() + " reached flag.");
                System.out.println((i + 1) + " aliens have reached flag");
                System.out.println("------------");
                if (i == 4) {
                    System.out.println("GAME OVER");
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public boolean gameStatus() {
        int numReached = 0;
        for (int i = 0; i < 5; i++) {
            if (reachedFlag[i] != null) {
                numReached++;
            }
        }
        return numReached >= 5;
    }

    private Object lock = new Object();

    private boolean moveAliens() {

        for (int i = 0; i < routes.size(); i++) {
            List<Alien> reachBreak = routes.get(i).moveAliensOnRoute();

            for (int j = 0; j < reachBreak.size(); j++) {
                Alien alien = reachBreak.get(j);
                    if (alien.getMoveTo().equals(flag)) {
                        Alien.reduceNum(1);

                        for (int k = 0; k < alienLifeCycles.size(); k++){
                            if (alienLifeCycles.get(k).getId() == alien.getThreadID()){
                                alienLifeCycles.get(k).stop();
                                alienLifeCycles.remove(k);
                                break;
                            }
                        }

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                AlienCreeps.removeElementFromGameRoot(alien.getProgressBar());
                                AlienCreeps.removeElementFromGameRoot(alien.getAlienView());
                            }
                        });
                        return reachFlag(alien);
                    }

                int randomNumber = chooseRandomRoute();
                Route randomRoute = routes.get(randomNumber);
                randomRoute.addAlienToRoute(alien, 3);
            }
        }
        backToNormalSpeed();
        return false;
    }

    private void backToNormalSpeed() {
        List<Alien> allAliens = new ArrayList<>();
        List<Alien> reducedSpeed = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            allAliens.addAll(routes.get(i).getAliens());
            for (Dimension dimension : specifiedLocations.keySet()) {
                Mappable m = specifiedLocations.get(dimension);
                if (m instanceof Weapon) {
                    Weapon weapon = ((Weapon) m);
                    reducedSpeed.addAll(routes.get(i).aliensWithinRadius(weapon));
                }
            }
            reducedSpeed.addAll(routes.get(i).aliensWithinRadius(this.hero));
            for (int j = 0; j < 3; j++) {
                Soldier soldier = hero.getSoldiers()[j];
                if (soldier != null) {
                    reducedSpeed.addAll(routes.get(i).aliensWithinRadius(soldier));
                }
            }
        }
        allAliens.removeAll(reducedSpeed);
        for (int i = 0; i < allAliens.size(); i++) {
            allAliens.get(i).backToNormalSpeed();
        }
    }

    void moveHero(Dimension change) {
        if (this.hero.isDead()) {
            System.out.println("Hero is dead :( Can't move hero.");
        } else {
            if (this.hero.setShouldMove(change)) {
                while (this.hero.isShouldMove()){
                    //System.out.println("checking wormholes");
                    /*try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    if (checkWormhole()){
                        break;
                    }
                }
                //System.out.println("STOPPED CHECKING");
                /*while (this.hero.isShouldMove()){
                    //System.out.println("wormhole check");
                    if (checkWormhole()) {
                        break;
                    }
                }
                System.out.println("NO LONGER CHECKING WORMHOLES");
                backToNormalSpeed();*/
            }
        }
    }

    void moveHero(KeyEvent event){
        KeyCode direction = event.getCode();
        if (direction == KeyCode.UP || direction == KeyCode.DOWN || direction == KeyCode.RIGHT || direction == KeyCode.LEFT){
            if (this.hero.isDead()){
                System.out.println("Hero is dead :( can't move hero.");
            }else{
                if (this.hero.setShouldMove(event)){
                    while (this.hero.isShouldMove()){
                        if (checkWormhole()){
                            break;
                        }
                    }
                }
            }
        }

    }

    public boolean checkWormhole(){
        //System.out.println("CHECKING WORMHOLE DIMS");
        Dimension newDim = hero.getDimension();
        for (int i = 0; i < this.wormholes.size(); i++) {
            if (wormholes.get(i).isWithinRadius(newDim)) {
                hero.correctDim();

                Wormhole in = wormholes.get(i);
                Wormhole out = wormholes.get(in.getLeadsTo());
                Dimension newChange = new Dimension(out.getDimension().getX() - hero.getShootingPoint().getX() + 15,
                        out.getDimension().getY() - hero.getShootingPoint().getY() + 15);
                System.out.println("hero went into wormhole " +
                        (i + 1) +
                        " and came out from wormhole " +
                        (in.getLeadsTo() + 1));
                System.out.println("she says hi :)");
              //  hero.setShouldMove(newChange);
                this.hero.move(newChange);

                return true;
            }
        }
        System.out.print("");
        return false;
    }

    
    private Media sound=new Media(getClass().getResource("res/sound/tesla2.wav").toExternalForm());
    private MediaPlayer player=new MediaPlayer(sound);
    
    
    void useTesla(Dimension dimension) {
        if (Weapon.NUM_USED_TESLA < 2) {
            if (!Weapon.TESLA_IN_USE) {

                /*teslaView = new ImageView(new Image(getClass()
                        .getResource("res/weapons/tesla/images/tesla.png").toExternalForm()));
                teslaView.setFitHeight(64);
                teslaView.setFitWidth(64);
                teslaView.relocate(dimension.getX() - 32, dimension.getY() - 32);
                teslaView.setVisible(true);*/

                /*Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                teslaView);
                    }
                });*/

                tesla = Weapon.WeaponFactory(dimension, "Tesla", 0);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        AlienCreeps.addElementToGameRoot(AlienCreeps.gameScene.getRoot().getChildrenUnmodifiable().size(),
                                tesla.getWeaponView());
                    }
                });
                player.play();

                List<Alien> aliensToKill = new ArrayList<>();
                for (int i = 0; i < routes.size(); i++) {
                    aliensToKill.addAll(routes.get(i).aliensWithinRadius(tesla));
                }
                if (this.hero.addExperienceLevel(aliensToKill.size() * 5)) {
                    reduceAllWeaponsPrice();
                }

                for (int i = 0; i < aliensToKill.size(); i++){
                    int finalI = i;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            AlienCreeps.removeElementFromGameRoot(aliensToKill.get(finalI).getAlienView());
                        }
                    });
                }
                Alien.reduceNum(aliensToKill.size());

                this.hero.addMoney(aliensToKill.size() * 10);
                updateAchievements(aliensToKill, "weapon");
                for (int i = 0; i < routes.size(); i++) {
                    this.removeAliensFromRoute(routes.get(i), aliensToKill);
                }
                
                player=new MediaPlayer(sound);
                
            } else {
                AlienCreeps.showPopupWarning("Wait " + Weapon.SECONDS_LEFT_TO_USE_TESLA + " seconds.", 55, 110);
                System.out.println("You must wait " + Weapon.SECONDS_LEFT_TO_USE_TESLA + " more seconds.");
            }
        } else {
            AlienCreeps.showPopupWarning("Only twice.", 100, 105);
            System.out.println("Can't use tesla more than twice.");
        }

    }

    private void updateTeslaStatus() {
        if (Weapon.TESLA_IN_USE) {
            Weapon.SECONDS_LEFT_TO_USE_TESLA--;
            if (Weapon.SECONDS_LEFT_TO_USE_TESLA == 0) {
                Weapon.TESLA_IN_USE = false;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        AlienCreeps.removeElementFromGameRoot(tesla.getWeaponView());
                    }
                });
            } else{
                proceedTesla();
            }
        }
    }

    private void proceedTesla(){
        List<Alien> aliensToKill = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            aliensToKill.addAll(routes.get(i).aliensWithinRadius(tesla));
        }
        if (this.hero.addExperienceLevel(aliensToKill.size() * 5)) {
            reduceAllWeaponsPrice();
        }

        for (int i = 0; i < aliensToKill.size(); i++){
            int finalI = i;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    AlienCreeps.removeElementFromGameRoot(aliensToKill.get(finalI).getAlienView());
                }
            });
        }

        Alien.reduceNum(aliensToKill.size());

        this.hero.addMoney(aliensToKill.size() * 10);
        updateAchievements(aliensToKill, "weapon");
        for (int i = 0; i < routes.size(); i++) {
            this.removeAliensFromRoute(routes.get(i), aliensToKill);
        }
    }

    private void shootAliens() {
        heroAndSoldiersShoot();
        weaponsShoot();
    }

    private Object lock1 = new Object();

    private void weaponsShoot() {
        for (Dimension dimension : specifiedLocations.keySet()) {
            Mappable m = specifiedLocations.get(dimension);
            if (m instanceof Weapon) {
                Weapon weapon = ((Weapon) m);
                List<Alien> toShoot = new ArrayList<>();
                for (int i = 0; i < routes.size(); i++) {
                    toShoot.addAll(routes.get(i).aliensWithinRadius(weapon));
                }

                if (!toShoot.isEmpty()){
                    weapon.setShouldShoot(toShoot);
                }

                /*if (!toShoot.isEmpty()){
                    weapon.setShouldShoot(toShoot);
                    while (weapon.isShouldShoot()){
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        synchronized (lock1){
                            if (!weapon.isShouldShoot()){
                                break;
                            }
                        }
                    }
                    List<Alien> deadAliens = weapon.getKilled();
                    if (deadAliens != null && !deadAliens.isEmpty()) {
                        toShoot.removeAll(deadAliens);
                        if (this.hero.addExperienceLevel(deadAliens.size() * 5)) {
                            reduceAllWeaponsPrice();
                        }
                        this.hero.addMoney(deadAliens.size() * 10);
                        updateAchievements(deadAliens, "weapon");
                        for (int i = 0; i < routes.size(); i++)
                            this.removeAliensFromRoute(routes.get(i), deadAliens);
                        Alien.reduceNum(deadAliens.size());
                    }
                }*/
            }
        }
        //backToNormalSpeed();
    }

    public Hero getHero() {
        return hero;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    private Object lock2 = new Object();

    private void heroAndSoldiersShoot() {
    //    List<Alien> dead = new ArrayList<>();

        if (!this.hero.isDead()) {
            List<Alien> toShoot = new ArrayList<>();
            for (int i = 0; i < routes.size(); i++) {
                toShoot.addAll(routes.get(i).aliensWithinRadius(this.hero));
            }

            if (!toShoot.isEmpty()) {
                hero.setShouldShoot(toShoot);
            }

/*while (hero.isShouldShoot()){
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (lock2){
                        if (!hero.isShouldShoot()){
                            break;
                        }
                    }
                }
                List<Alien> killedByHero = this.hero.getKilled();
                if (!killedByHero.isEmpty()) {
                    if (this.hero.addExperienceLevel(killedByHero.size() * 15)) {
                        reduceAllWeaponsPrice();
                    }
                    this.hero.addMoney(killedByHero.size() * 10);
                    updateAchievements(killedByHero, "hero");
                    dead.addAll(killedByHero);
                }
                if (this.hero.isDead()) {
                    this.secondsLeftToResurrectHero = this.hero.getResurrectionTime();
                }

            } else {
              //  System.out.println("no aliens in hero radius");
            }*/
        }

        Soldier soldiers[] = this.hero.getSoldiers();
        for (int j = 0; j < 3; j++) {
            if (soldiers[j] != null) {
               // System.out.println("checking for soldier " + (j + 1));
                List<Alien> toShoot = new ArrayList<>();
                for (int i = 0; i < routes.size(); i++) {
                    toShoot.addAll(routes.get(i).aliensWithinRadius(soldiers[j]));
                }
                if (!toShoot.isEmpty()){
                    soldiers[j].setShouldShoot(toShoot);
                }

                /*if (!toShoot.isEmpty()) {
                    soldiers[j].setShouldShoot(toShoot);

                    while (soldiers[j].isShouldShoot()){
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        synchronized (lock2){
                            if (!hero.isShouldShoot()){
                                break;
                            }
                        }
                    }

                    List<Alien> killedBySoldier = soldiers[j].shoot(toShoot);
                    if (!killedBySoldier.isEmpty()) {
                        if (this.hero.addExperienceLevel(killedBySoldier.size() * 5)) {
                            reduceAllWeaponsPrice();
                        }
                        this.hero.addMoney(killedBySoldier.size() * 10);
                        dead.addAll(killedBySoldier);
                    }
                    if (soldiers[j].isDead()) {
                        soldiers[j] = null;
                        barrack.requestSoldier(this.hero.getResurrectionTime());
                    }
                } else {
                    //System.out.println("no aliens in soldier radius.");
                }*/
            }
        }


        /*for (int i = 0; i < routes.size(); i++) {
            this.removeAliensFromRoute(routes.get(i), dead);
        }

        Alien.reduceNum(dead.size());*/
    }

     void removeAliensFromRoute(Route route, List<Alien> deadAliens) {
        for (int j = 0; j < deadAliens.size(); j++) {
            Alien alienToRemove = deadAliens.get(j);
            int lineNumber = route.whichLine(alienToRemove.getCurrentDim());
            route.removeAlienFromLine(alienToRemove, lineNumber);
        }
    }

     void reduceAllWeaponsPrice() {
        for (Dimension dimension : specifiedLocations.keySet()) {
            Mappable m = specifiedLocations.get(dimension);
            if (m instanceof Weapon) {
                Weapon weapon = ((Weapon) m);
                weapon.reducePrice(0.9);
            }
        }
    }

    void updateAchievements(List<Alien> deadAliens, String killedBy) {
        Achievement achievement = hero.getAchievement();
        if (killedBy.equalsIgnoreCase("hero")) {
           // System.out.println("updating hero achi");
            for (int i = 0; i < deadAliens.size(); i++) {
                achievement.killedHero(deadAliens.get(i));
            }
        } else if (killedBy.equalsIgnoreCase("weapon")) {
            for (int i = 0; i < deadAliens.size(); i++) {
                achievement.killedWeapon(deadAliens.get(i));
            }
        }
    }


    /***** GETTERS *******/

    void setCanUpgradeSoldiers() {
        this.canUpgradeSoldiers = true;
        Soldier soldiers[] = new Soldier[3];
        for (int i = 0; i < 3; i++) {
            if (soldiers[i] != null) {
                soldiers[i].resetRadius();
            }
        }
    }

    List<Weapon> getWeapons() {
        List<Weapon> weapons = new ArrayList<>();
        for (Dimension dimension : specifiedLocations.keySet()) {
            if (specifiedLocations.get(dimension) instanceof Weapon) {
                weapons.add((Weapon) specifiedLocations.get(dimension));
            }
        }
        Collections.sort(weapons);
        return weapons;
    }

    public Map<Dimension, Mappable> getSpecifiedLocations() {
        return specifiedLocations;
    }

    List<Weapon> getWeapons(String type) {
        List<Weapon> weapons = new ArrayList<>();
        for (Dimension dimension : specifiedLocations.keySet()) {
            if (specifiedLocations.get(dimension) instanceof Weapon) {
                Weapon weapon = ((Weapon) specifiedLocations.get(dimension));
                if (weapon.getName().equalsIgnoreCase(type)) {
                    weapons.add(weapon);
                }
            }
        }
        Collections.sort(weapons);
        return weapons;
    }

    public void showAvailableLocations() {
        System.out.println("Available locations are: ");
        System.out.println("-------------------------");
        for (Integer integer : specifiedNumbers.keySet()) {
            Dimension dimension = specifiedNumbers.get(integer);
            if (specifiedLocations.get(dimension) == null){
                System.out.println(integer + " - " + dimension);
            }
        }
    }

    int randomWeather() {

        int weather = (int) (Math.random() * 6);
        System.out.println("Today's weather forcast:");
        switch (weather) {
            case 0:
                System.out.println("\t Sunny");
                updateRadiusWeather(1 / weatherConditionConstant);
                weatherConditionConstant = 1;
                break;
            case 1:
                System.out.println("\t Partly Clouldy");
                updateRadiusWeather(0.95 / weatherConditionConstant);
                weatherConditionConstant = 0.95;
                break;
            case 2:
                System.out.println("\t Rainy");
                updateRadiusWeather(0.9 / weatherConditionConstant);
                weatherConditionConstant = 0.9;
                break;
            case 3:
                System.out.println("\t Thunders expected");
                updateRadiusWeather(0.95 / weatherConditionConstant);
                weatherConditionConstant = 0.95;
                break;
            case 4:
                System.out.println("\t Hail O.o");
                updateRadiusWeather(0.8 / weatherConditionConstant);
                weatherConditionConstant = 0.8;
                break;
            case 5:
                System.out.println("\t What a Cool and pleasing day to kill Aliens");
                updateRadiusWeather(1.5 / weatherConditionConstant);
                weatherConditionConstant = 1.5;
                break;
        }

        weatherCondition = weather;
        return weather;
    }

    private void updateRadiusWeather(double a) {
        List<Weapon> weapon = getWeapons();
        double currentR;
        if (a < 0) {
            a = -a;
        }
        for (int i = 0; i < weapon.size(); i++) {
            currentR = weapon.get(i).getRadius();
            weapon.get(i).setRadius(currentR * a);
        }
        currentR = hero.getRadius();
        hero.setRadius(currentR);
        Soldier[] soldiers = hero.getSoldiers();
        for (int i = 0; i < soldiers.length; i++) {
            if (soldiers[i] != null) {
                currentR = soldiers[i].getRadius();
                soldiers[i].setRadius(a * currentR);
            }
        }
        if (a > 1) {
            System.out.println("\t Radius has increased by " + a + " :)");
        } else if (a == 1) {
            System.out.println("\t normal weather condition, no change in radius");
        } else {
            System.out.println("\t Radius has decreased by " + a + " :(");
        }
    }

    public int getWeather() {
        return weatherCondition;
    }

    void naturalDisaster() {
        int prob = (int) (Math.random() * 3);
        if (prob == 2) {
            List<Weapon> weapon = getWeapons();
            int prob2 = (int) Math.random() * weapon.size();
            if (!weapon.isEmpty())
                weapon.get(prob2).naturalDisasterWeapon();

            AlienCreeps.showPopupWarning("Natural Disaster.", 45, 105);

        }
    }

     void superNaturalHelp() {
        if (!SuperNaturalHelp) {
            int prob = (int) (Math.random() * 10);

            if (prob == 2) {
                List<Alien> aliensToKill = new ArrayList<>();
                for (int i = 0; i < routes.size(); i++) {
                    aliensToKill.addAll(routes.get(i).getAliens());
                }
                this.hero.addExperienceLevel(aliensToKill.size() * 5);
                this.hero.addMoney(aliensToKill.size() * 10);
                updateAchievements(aliensToKill, "hero");
                for (int i = 0; i < routes.size(); i++) {
                    this.removeAliensFromRoute(routes.get(i), aliensToKill);
                    
                }
                for (int i = 0; i < aliensToKill.size(); i++) {
                    
                    aliensToKill.get(i).setEnergy(-10);
                    aliensToKill.get(i).isDead();
                    
                }
                SuperNaturalHelp = true;
                AlienCreeps.showPopupWarning("Super Natural Help", 45, 105);
                System.out.println("Super  Natural  Help :))");
            }
        }
    }

    void plague() {
        int prob = (int) (Math.random() * 60);

        int numAlive = 0;
        for (int i = 0; i < 3; i++) {
            if (hero.getSoldiers()[i] != null) {
                numAlive++;
            }
        }
        
        if (prob == 1 && numAlive!=0 ) {
            Soldier[] soldiers = hero.getSoldiers();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                        for (int i = 0; i < 3; i++){
                                if (soldiers[i]!=null){
                                    AlienCreeps.removeElementFromGameRoot(soldiers[i].getWarriorView());
                                    AlienCreeps.removeElementFromGameRoot(soldiers[i].getHealthBar());
                                    AlienCreeps.removeElementFromGameRoot(soldiers[i].getBulletView());
                                    soldiers[i]=null;
                                    if (barrack != null){
                                        barrack.requestSoldier(hero.getResurrectionTime());
                                    }
                                }
                            }

                }
            });


            
            hero.setSoldiers(soldiers);

            AlienCreeps.showPopupWarning("PLAGUE", 85, 105);
            System.out.println("Unfortunately your Soldiers died as a result of a plague epidemic :(");
            
        }
    }


    void burrowMoney(int amount){
        this.hasBurrowed = true;
        bank.lendMoney(this.hero, amount);
        bank.setDueDate(AlienCreeps.getCurrentDay() + 3);
    }

    void payBack() {
        if (this.hasBurrowed) {
            if (!bank.payBack(this.hero)) {
                System.out.println("You don't have enough money to pay the bank back.");
            } else {
                System.out.println("Thank you for paying back in time :)");
                this.hasBurrowed = false;
            }
        }
    }


    @Override
    public String toString() {
        String map = "\n\n**** Game Map ****\n\n\n";
        for (int i = 0; i < routes.size(); i++) {
            map = map.concat("Route #" + (i + 1) + "\n");
            map = map.concat("----------\n");
            map = map.concat("Line Equations:\n\n");
            map = map.concat(routes.get(i).toString());
        }
        return map;
    }

}

class Route {
    private Line[] lines = new Line[5];
    private List<Dimension> intersections = new ArrayList<>();
    private Map<Line, ArrayList<Alien>> alienMap = new HashMap<>();

    Route(Line[] lines, List<Dimension> intersections) {
        for (int i = 0; i < 5; i++) {
            this.lines[i] = lines[i];
        }
        for (int i = 0; i < lines.length; i++) {
            alienMap.put(lines[i], new ArrayList<>());
        }
        this.intersections.addAll(intersections);
    }

    private final Object lock = new Object();
    private final Object lock1 = new Object();

    List<Alien> moveAliensOnRoute() {
        List<Alien> reachedIntersection = new ArrayList<>();

        for (int i = 4; i >= 0; i--) {
            List<Alien> toMove = alienMap.get(lines[i]);

            for (int j = 0; j < toMove.size(); j++) {

                Alien current = toMove.get(j);

                Dimension destination = lines[i].moveAlienOnLine(current);

                if (destination == null) {
                    destination = lines[i].getEndPoint();
                    current.setMoveTo(destination);
                    alienMap.get(lines[i]).remove(current);

                    if (intersections.contains(destination)) {
                        reachedIntersection.add(current);
                    } else {
                        alienMap.get(lines[i + 1]).add(current);
                    }
                } else {
                    current.setMoveTo(destination);
                    if (intersections.contains(destination)) {
                        alienMap.get(lines[i]).remove(current);
                        reachedIntersection.add(current);
                    }
                }
            }
        }
        return reachedIntersection;
    }

    void addAlienToRoute(Alien alien, int lineNumber) {
        alien.setMoveTo(lines[lineNumber].getStartPoint());

        alienMap.get(lines[lineNumber]).add(alien);
    }

    public Map<Line, ArrayList<Alien>> getAlienMap() {
        return alienMap;
    }

    void removeAlienFromLine(Alien alien, int lineNumber) {
        if (lineNumber >= 0) {
            alienMap.get(lines[lineNumber]).remove(alien);
        }
    }

    int whichLine(Dimension dimension) {
  //      System.out.println("checking dimension " + dimension);
        for (int i = 0; i < 5; i++) {
            if (lines[i].isOnLine(dimension)) {
                return i;
            }
        }
        return -1;
    }

    List<Alien> aliensWithinRadius(Shooter shooter) {
        List<Alien> toShoot = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            List<Alien> checking = alienMap.get(lines[i]); //get the aliens of each line
            for (int j = 0; j < checking.size(); j++) {
                Alien a = checking.get(j);
//                    System.out.println(shooter.getClass().getName() + ": " + shooter.getShootingPoint());
                    /*System.out.println(a.getName() + ": " + a.getCurrentDim());
                    System.out.println(shooter.getShootingPoint().distanceFrom(a.getCurrentDim()));
                    System.out.println("•••••••••");*/
                    //a.getCurrentDim();
                if (shooter.isWithinRadius(a.getCurrentDim())) {
                 //   System.out.println(a.getName() + " is within radius of " + shooter.getClass().getName());
                   // System.out.println("*********");
                    toShoot.add(a);
                }
            }
        }
        return toShoot;
    }

    List<Alien> getAliens() {
        List<Alien> aliens = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            aliens.addAll(alienMap.get(lines[i]));
        }
        return aliens;
    }

    public Line[] getLines() {
        return lines;
    }

    @Override
    public String toString() {
        String description = "";
        for (int i = 0; i < 5; i++) {
            description = description.concat("Line #" + (i + 1) + "\n");
            description = description.concat("*********\n");
            description = description.concat(lines[i].toString());
        }
        return description;
    }
}

class Line {
    private double slope;
    private double intercept;
    private Dimension startPoint;
    private Dimension endPoint;

    Line(double slope, double intercept, Dimension startPoint, Dimension endPoint) {
        this.slope = slope;
        this.intercept = intercept;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    synchronized Dimension moveAlienOnLine(Alien alien) {
        double currentX = alien.getCurrentDim().getX();

        double newX = currentX + GameMap.UNIT * alien.getSpeed();
        double newY = slope * newX + intercept;

  //      System.out.println("new dim" + newX + " " + newY);
    //    System.out.println("endoflinedim = " + endPoint);
      //  System.out.println(Double.compare(newX, endPoint.getX()));

        if (Double.compare(newX, endPoint.getX()) >= 0){
   //         System.out.println("END OF LINE.");
            return null;
        }

        //  if (newX < endPoint.getX()) {
        return new Dimension(newX, newY);
        //    }
        //return null;
    }

    boolean isOnLine(Dimension dimension) {
        double xToCheck = dimension.getX();
        double yToCheck = dimension.getY();
        if (Double.compare(xToCheck, startPoint.getX()) > 0 && Double.compare(xToCheck, endPoint.getX()) < 0) {
            if (xToCheck * slope + intercept == yToCheck) {
                return true;
            }
        }
        return dimension.equals(startPoint);
    }

    Dimension getStartPoint() {
        return startPoint;
    }

    Dimension getEndPoint() {
        return endPoint;
    }

    @Override
    public String toString() {
        String equation = "";

        if (slope == 0) {
            equation = "y = " + intercept;
        } else {
            if (slope == 1) {
                equation = equation.concat("y = x");
            } else if (slope == -1) {
                equation = equation.concat("y = -x");
            } else {
                equation = equation.concat("y = " + slope + "x");
            }

            if (intercept > 0) {
                equation = equation.concat(" + " + intercept);
            } else if (intercept < 0) {
                equation = equation.concat(" - " + (-1 * intercept));
            }
        }
        return "Start Point: " + startPoint + "\n" +
                "End Point: " + endPoint + "\n" +
                "Equation: " + equation + "\n\n";
    }
}

class Wormhole {
    private int leadsTo;
    private Dimension dimension;
    private double radius;
    private WormholeView wormholeView;

    Wormhole(int leadsTo, Dimension dimension) {
        wormholeView = new WormholeView(dimension);

        this.leadsTo = leadsTo;
        this.dimension = dimension;
        radius = 1;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                wormholeView.changeDim(dimension);
            }
        });
    }

    int getLeadsTo() {
        return leadsTo;
    }

    boolean isWithinRadius(Dimension otherDim) {
        //return otherDim.equals(dimension);
        return otherDim.distanceFrom(dimension) < 15;//radius * GameMap.UNIT;// 5;// radius * GameMap.UNIT;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public WormholeView getWormholeView() {
        return wormholeView;
    }
}

class WormholeView extends StackPane{
    private ImageView view;

    public WormholeView(Dimension dim){
        view = new ImageView(new Image(getClass()
                .getResource("res/wormhole/images/wormhole_1.png").toExternalForm()));
        view.setFitWidth(50);
        view.setFitHeight(50);
        view.setVisible(true);

        getChildren().add(view);

        relocate(dim.getX(), dim.getY());

        //setTranslateX(dim.getX());
        //setTranslateY(dim.getY());
    }

    public void changeDim(Dimension newDim){

        relocate(newDim.getX(), newDim.getY());
    //    setTranslateX(newDim.getX());
      //  setTranslateY(newDim.getY());
    }

}
