package robot;

import simbad.sim.*;
import subsumption.Behavior;
import subsumption.BehaviorBasedAgent;
import subsumption.Sensors;
import subsumption.Velocities;

import javax.vecmath.Vector3d;

public class MyRobot extends BehaviorBasedAgent {

    private LightSensor lightL;
    private LightSensor lightR;

    public MyRobot (Vector3d position, String name, int sensorCount, boolean addLineSensors) {
        super(position,name, sensorCount, addLineSensors);

        lightL = RobotFactory.addLightSensorLeft(this);
        lightR = RobotFactory.addLightSensorRight(this);

        Sensors sensors = this.getSensors();
        Behavior[] behaviors = { new ReachGoal(sensors, this), new FollowLine(sensors, this),
                                 new CircumNavigate(sensors, this, true),
                                 new FollowLight(sensors, this)};
        boolean subsumes[][] = { { false, true, true, true },
                                 { false, false, true, true },
                                 { false, false, false, true },
                                 { false, false, false, false }};
        this.initBehaviors(behaviors, subsumes);
    }

    public LightSensor getLightLeftSensor() {
        return lightL;
    }

    public LightSensor getLightRightSensor() {
        return lightR;
    }
}

class FollowLight extends Behavior {

    float offset = 0.001f;

    LightSensor lightR;
    LightSensor lightL;

    public FollowLight(Sensors sensors, MyRobot robot) {
        super(sensors);
        lightL = robot.getLightLeftSensor();
        lightR = robot.getLightRightSensor();
    }

    public Velocities act() {
        double luxL = lightL.getLux();
        double luxR = lightR.getLux();
        if (Math.abs(luxL - luxR) > offset) return new Velocities(0.5, 0.0);
        return new Velocities(0.5, Math.PI/2 * (0.5 * Math.signum(luxL - luxR)));
    }

    public boolean isActive() {
        return true;
    }

}

class ReachGoal extends Behavior {

    LightSensor lightR;
    LightSensor lightL;

    public ReachGoal(Sensors sensors, MyRobot robot) {
        super(sensors);
        lightL = robot.getLightLeftSensor();
        lightR = robot.getLightRightSensor();
    }

    public Velocities act() {
        return new Velocities(0.0, 0.0);
    }

    public boolean isActive() {
        double luxL = lightL.getLux();
        double luxR = lightR.getLux();

        if (luxL >= 0.5136 || luxR >= 0.5136 ) {
            return true;
        }
        return false;
    }

}

class FollowLine extends Behavior {
    private int left;
    private int right;
    private int hitCount;

    private float limit = 0.8f;

    LightSensor lightR;
    LightSensor lightL;

    public FollowLine(Sensors sensors, MyRobot robot) {
        super(sensors);
        lightL = robot.getLightLeftSensor();
        lightR = robot.getLightRightSensor();
    }

    public Velocities act() {

        float translationalV = 0.2f;
        if (left == right) {
            if (left > 4) {
                double luxR = lightR.getLux();
                double luxL = lightL.getLux();
                return new Velocities(0,  1 * Math.signum(luxL - luxR));
            }
            translationalV = 1;
        }
        else if (right != left && Math.abs(left - right) > 4) {
            translationalV = 0;
        }
        return new Velocities(translationalV,  2 * (left-right)/hitCount);

    }

    public boolean isActive() {
        left = 0;
        right = 0;
        hitCount = 0;

        if (getSensors().getSonars().getFrontQuadrantMeasurement() < limit) {
            return false;
        }

        LineSensor lineSensor = getSensors().getLineSensors();

        for (int i=0; i<lineSensor.getNumSensors()/2; i++)
        {
            left += lineSensor.hasHit(i) ? 1 : 0;
            right += lineSensor.hasHit(lineSensor.getNumSensors()-i-1)? 1 : 0;
            hitCount++;
        }

        if (left==0 && right==0)
        {
            return false;
        }
        return true;
    }

}

class CircumNavigate extends Behavior{
    boolean CLOCKWISE;
    static double SAFETY = 0.6f;
    boolean completed;

    double luxL = 0;
    double luxR = 0;

    LightSensor lightR;
    LightSensor lightL;


    public CircumNavigate(Sensors sensors, MyRobot robot, boolean CLOCKWISE) {
        super(sensors);
        completed=true;
        this.CLOCKWISE = CLOCKWISE;
        lightL = robot.getLightLeftSensor();
        lightR = robot.getLightRightSensor();
    }

    public Velocities act() {
        RangeSensorBelt sonars = getSensors().getSonars();
        float translationalV = 0.2f;
        float rotationalV = 0.2f;


        if (sonars.getFrontQuadrantHits() > 0 && sonars.getMeasurement(minFrontMeasurement(sonars)) < SAFETY) {
            // Turn until you no longer see the obstacle
            translationalV = 0;
            if (CLOCKWISE) {
                rotationalV = rotationalV;
            }
            return new Velocities(translationalV,rotationalV);
        }

        if (CLOCKWISE) {
            if (sonars.getRightQuadrantHits() > 0) {
                if (sonars.getRightQuadrantHits() == 3) {
                    return new Velocities(translationalV,0);
                }
                if (sonars.getMeasurement(minRightMeasurement(sonars)) < SAFETY) {
                    translationalV = 0;
                    return new Velocities(translationalV, rotationalV);
                }
            }
        }
        else {
            if (sonars.getLeftQuadrantHits() > 0) {
                if (sonars.getLeftQuadrantHits() == 3) {
                    return new Velocities(translationalV,0);
                }
                if (sonars.getMeasurement(minLeftMeasurement(sonars)) < SAFETY) {
                    translationalV = 0;
                    return new Velocities(translationalV,-rotationalV);
                }
            }
        }


        int min = minMeasurement(sonars);

        if (sonars.getFrontQuadrantHits() > 0) {
            rotationalV = -Math.min(1, (float)Math.abs(2f - sonars.getMeasurement(min)));
            if (CLOCKWISE) {
                rotationalV = -rotationalV;
            }
        }

        return new Velocities(translationalV,rotationalV);
    }


    public boolean isActive() {

        RangeSensorBelt sonars = getSensors().getSonars();

        if (!completed){
            if (sonars.getFrontQuadrantHits() == 0) {
                if (CLOCKWISE) {
                    if (!rightHasHit()) {
                        completed = true;
                        System.out.println("Right sensors detect nothing");
                        return false;
                    }
                }
                else if (!leftHasHit()) {
                    completed = true;
                    System.out.println("Left sensors detect nothing");
                    return false;
                }


                if (lightR.getLux() > luxR && lightL.getLux() > luxL) {
                    completed = true;
                    System.out.println("Greater lum in both light sensors");
                    return false;
                }
            }
            return true;
        }

        if (sonars.getFrontQuadrantHits() > 0) {
            completed=false;
            luxL = lightL.getLux();
            luxR = lightR.getLux();
            this.CLOCKWISE = luxL < luxR;
            return true;
        }
        return false;

    }


    private int minFrontMeasurement(RangeSensorBelt sonars) {
        int min = 0;
        for (int i=0; i < 2; i++) {
            if (sonars.getMeasurement(i)<sonars.getMeasurement(min)) min = i;
        }
        if (sonars.getMeasurement(11)<sonars.getMeasurement(min)) min = 11;

        return min;
    }

    private int minLeftMeasurement(RangeSensorBelt sonars) {
        int min = 2;
        for (int i=3; i < 5; i++) {
            if (sonars.getMeasurement(i)<sonars.getMeasurement(min)) min = i;
        }
        return min;
    }

    private int minRightMeasurement(RangeSensorBelt sonars) {
        int min = 8;
        for (int i=8; i < 11; i++) {
            if (sonars.getMeasurement(i)<sonars.getMeasurement(min)) min = i;
        }
        return min;
    }

    private int minMeasurement(RangeSensorBelt sonars) {
        int min = minFrontMeasurement(sonars);

        int offset = 8;
        if (CLOCKWISE) {
            offset = 2;
        }
        for (int i = offset; i<offset+3; i++) {
            if (sonars.getMeasurement(i)<sonars.getMeasurement(min)) min = i;
        }

        return min;
    }

    private boolean leftHasHit() {
        if (getSensors().getSonars().hasHit(2)) {
            return true;
        }
        if (getSensors().getSonars().hasHit(3)) {
            return true;
        }
        return false;
    }

    private boolean rightHasHit() {
        if (getSensors().getSonars().hasHit(9)) {
            return true;
        }
        if (getSensors().getSonars().hasHit(10)) {
            return true;
        }
        return false;
    }
}
