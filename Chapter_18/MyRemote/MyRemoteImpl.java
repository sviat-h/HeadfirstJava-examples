package MyRemote;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MyRemoteImpl extends UnicastRemoteObject implements MyRemote {
    public String sayHello(){
        return "Server say: 'Hello!'";
    }
    public MyRemoteImpl() throws RemoteException{}

    public static void main(String[] args) {
        try{
            MyRemote service = new MyRemoteImpl();
            Naming.rebind("Remote hello", service);
        } catch (Exception ex){ex.printStackTrace();}
    }

}

