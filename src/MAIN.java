import GUI.MainFrame;

import Classes.*;
import Database.DBConnection;
import Database.Load_Everything;
import Database.DAO.*;

public class MAIN 
{
    public static void main(String[] args) 
    {
        DBConnection.connectToDatabase();

        Load_Everything.load();

        new MainFrame();
    }
}