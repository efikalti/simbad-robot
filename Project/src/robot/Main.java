/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import simbad.gui.Simbad;
import simbad.sim.EnvironmentDescription;
import subsumption.BehaviorBasedAgent;

import javax.vecmath.Vector3d;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EnvironmentDescription environment = new Env();

        BehaviorBasedAgent robot = new MyRobot(new Vector3d(-9,0,-6), "Robot", 12, true);

        environment.add(robot);
        Simbad frame = new Simbad(environment, false);

    }

}
