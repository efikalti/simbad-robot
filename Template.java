import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.System.exit;
import java.util.Vector;
import javax.vecmath.*;
import simbad.gui.*;
import simbad.sim.*;

/**
 *
 * @author Dimitrs
 */
public class Main {
    
    private static State initial;

    public Main() {
    }
public static void main(String[] args) throws IOException {
    
    
        EnvironmentDescription environment = new EnvironmentDescription();    

        environment.add(new Line(new Vector3d(-0,0,-6),4,environment));
        environment.add(new Line(new Vector3d(6,0,2),4,environment));
        Line l0 = new Line(new Vector3d(-9,0,-6),7f,environment);
        l0.rotate90(1);
        environment.add(l0);
        Line l1 = new Line(new Vector3d(-6,0,-6),2,environment);
        l1.rotate90(1);
        environment.add(l1);
        Line l2 = new Line(new Vector3d(-3,0,-2),5,environment);
        l2.rotate90(1);
        environment.add(l2);
        Line l3 = new Line(new Vector3d(2,0,2),4,environment);
        l3.rotate90(1);
        environment.add(l3);
        environment.add(new Arch(new Vector3d(6,0,4),environment));
        environment.add(new Wall(new Vector3d(-0.5,0,4),10,1.5f,environment));
        environment.add(new Wall(new Vector3d(12.5,0,4),10,1.5f,environment));        
        environment.add(new Wall(new Vector3d(6,0,8),6,1.5f,environment));
        
        Wall w1 = new Wall(new Vector3d(3,0,6),4,1.5f,environment);
        w1.rotate90(1);
        environment.add(w1);

        Wall w2 = new Wall(new Vector3d(9,0,6),4,1.5f,environment);
        w2.rotate90(1);
        environment.add(w2);
        
        environment.add(new Box(new Vector3d(2,0,2),new Vector3f(3f,1.5f,4f),environment));
        environment.add(new Box(new Vector3d(-6,0,-6),new Vector3f(2f,1.5f,2f),environment));
        environment.add(new Box(new Vector3d(-4,0,0.5),new Vector3f(2f,1.5f,7f),environment));
        
        
        environment.light1IsOn=true;
        environment.light2IsOn=true;
        environment.light1Color= new Color3f(1,0,0);
        environment.light1Position = new Vector3d(6,0,6);
        environment.light2Position = new Vector3d(6,3,6);    

/* Add your robot */
    
        Simbad frame = new Simbad(environment, false);
    }

}
