/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import javax.vecmath.*;
import simbad.sim.*;

public class Env extends EnvironmentDescription {
    public Env(){
        setupEnvironment();
    }

    public void setupEnvironment() {
        addLines();

        addObstacles();

        addLights();
    }


    private void addLines() {
        add(new Line(new Vector3d(-0,0,-6),4, this));
        add(new Line(new Vector3d(6,0,2),4,this));
        Line l0 = new Line(new Vector3d(-9,0,-6),7f,this);
        l0.rotate90(1);
        add(l0);
        Line l1 = new Line(new Vector3d(-6,0,-6),2,this);
        l1.rotate90(1);
        add(l1);
        Line l2 = new Line(new Vector3d(-3,0,-2),5,this);
        l2.rotate90(1);
        add(l2);
        Line l3 = new Line(new Vector3d(2,0,2),4,this);
        l3.rotate90(1);
        add(l3);
    }

    private void addObstacles() {

        add(new Arch(new Vector3d(6,0,4),this));
        add(new Wall(new Vector3d(-0.5,0,4),10,1.5f,this));
        add(new Wall(new Vector3d(12.5,0,4),10,1.5f,this));
        add(new Wall(new Vector3d(6,0,8),6,1.5f,this));

        Wall w1 = new Wall(new Vector3d(3,0,6),4,1.5f,this);
        w1.rotate90(1);
        add(w1);

        Wall w2 = new Wall(new Vector3d(9,0,6),4,1.5f,this);
        w2.rotate90(1);
        add(w2);

        add(new Box(new Vector3d(2,0,2),new Vector3f(3f,1.5f,4f),this));
        add(new Box(new Vector3d(-6,0,-6),new Vector3f(2f,1.5f,2f),this));
        add(new Box(new Vector3d(-4,0,0.5),new Vector3f(2f,1.5f,7f),this));


    }

    private void addLights() {

        light1IsOn=true;
        light2IsOn=true;
        light1Color= new Color3f(1,0,0);
        light1Position = new Vector3d(6,0,6);
        light2Position = new Vector3d(6,3,6);
    }
}