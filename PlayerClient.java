/**
 * Created by sam on 11/22/16.
 */

//Buahin Samuel
//ITCS 3166 Final Project

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PlayerClient {

    static Socket client;
    static int []hghtCk;

    //Main Method Begins
    public static void main(String[]args) throws ClassNotFoundException {
        try
        {
            client = new Socket("localhost",9999);//
            System.out.println("Connecting to Server...");

            //this will handle the transmission of data to and from the server and client
            DataInputStream reader = new DataInputStream(client.getInputStream());
            DataOutputStream writer = new DataOutputStream(client.getOutputStream());

            //accepts the Player ID from server and displays it
            //the ID is also used as the disc in the game

            String ID = reader.readUTF();

            ///if the ID is 'X' it should know it is player 1
            ///if ID is 'O'it should know it is player 2

            if (ID.equals("X"))
            {
                System.out.println("PLAYER 1");
            }

            if (ID.equals("O"))
            {
                System.out.println("PLAYER 2");
            }
            System.out.println("Your disc is: " + ID);

            //this wil be used to hold the col entered by player
            int col;

            int check=1;//this will be used as a check to cause the program to exit after a winner has been found
            //server sends check to client everytime it sends a request and is check is 0 then a winner has been found
            //the the game exit te loop

            while(check!=0)
            {
                //this accepts the array from the server which lets you know how deep the column is.
                hghtCk = getMessage(client);

                // /prints the array to the player
                int n = hghtCk.length;
                System.out.println();
                for (int i=0;i<n;i++)
                {
                    System.out.print(hghtCk[i] + " ");
                }
                System.out.println();

                //this will read and print the board to player
                String board=reader.readUTF();
                System.out.println("\n" + board.replace("[", "").replace("]", "").replace(",",""));//removes any other character form the string

                /////////////
                col = GetCol();//gets the column

                //this checks to make sure the column is not full
                //if it is it askes the player for another entry
                while(hghtCk[col-1]==0)
                {
                    System.out.println("The Column you entered is full\nPlease enter new column either the column " +col);
                    col = GetCol();//gets the column
                }

                /////////////
                writer.writeInt(col);//sends column to server

                //flushes the writer
                writer.flush();
                check= reader.readInt();//reads check from server one more time
            }

            //whoever first wins gets this displayed on it console

            if (check==0)
            {
                System.out.println("*****You have won!!!******");
            }

            //closes the client socket  reader and writer
            client.close();
            reader.close();
            writer.close();
        }

        //exception for try
        catch (IOException IOex)
        {
            System.out.println("\nSorry things didn't go so well. Better Luck next time.");
        }
    }

///////////METHODS/////////

    //checks and validates user input to make the right numbers are entered
    public static int GetCol()
    {
        Scanner s = new Scanner(System.in);
        int num = 0; //this will hold the number
        boolean isN; //Boolean that wil be used to control the do-while
        do {
            System.out.println("\nPlease Enter a choice between 1-7");

            if (s.hasNextInt()) //this will check if what is entered is a number or not
            {
                num = s.nextInt();//it will pt the number into the variable

                if ((num>=1)&&(num<=7)) //checks is number is betweem 1-7
                {isN = true;}//sets the Boolean to true to exit the loop
                else
                {   num=0; //if its not in the range it will set the number back to 0
                    System.out.println("\nInvalid Entry\n");
                    isN = false;  //sets the boolean to false to go through the loop again.
                }
            }
            else
                {
                System.out.println("\nInvalid Entry\n");
                isN = false;
                s.next();
                }
        }
        while (!(isN));
        /////action here
        return num;
    }

    //recieves the array from the server
    public static int[] getMessage(Socket s)
            throws IOException, ClassNotFoundException {
        InputStream is = s.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        int[] array = (int[])ois.readObject();
        return array;
    }
////////////////END OF METHODS///////////////

}
