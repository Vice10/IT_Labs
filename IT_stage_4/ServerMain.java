import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;

public class ServerMain {
    public static final String UNIQUE_BINDING_NAME = "server.base";

    public static void main(String[] args) throws IOException, AlreadyBoundException, InterruptedException, ParseException, org.json.simple.parser.ParseException {

        //String baseName = "myDB";
        final Base base = new Base("");

        final Registry registry = LocateRegistry.createRegistry(2732);

        Remote stub = UnicastRemoteObject.exportObject(base, 0);
        registry.bind(UNIQUE_BINDING_NAME, stub);

        Thread.sleep(Integer.MAX_VALUE);
    }
}
