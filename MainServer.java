/**
 * Created by sam on 11/22/16.
 */

//Buahin Samuel
//ITCS 3166 Final Project

import java.io.*;
import java.net.*;
import java.util.*;

public class MainServer {

    private static String[][] board = new String[6][7];
    private static int []hghtCk= {6,6,6,6,6,6,6};
    private static int checkExit =0;
    private static String disc;
   // private static boolean boo=false;

    static ServerSocket server;//server socket

    static Socket client1; //socket for first client
    static Socket client2; // socket for second client

    //handles data input and output streams
    static DataOutputStream client1writer;
    static DataOutputStream client2writer;

    //input
    static DataInputStream reader1;
    static DataInputStream reader2;

    static final int PORT = 9999;//port

    //Main Method
    public static void main(String[]args)
    {
        //Try-Catch Block for Socket Errors.
        try
        {
            server = new ServerSocket(PORT);// server wth port

            System.out.println("Server Started");//Diplas server started to the console

            client1 = server.accept();//accepts connections from client

            //this is reader and writer for player 1
            client1writer = new DataOutputStream(client1.getOutputStream());
            reader1 = new DataInputStream(client1.getInputStream());

            client2 = server.accept();//accepts connections from second client

            //this is reader and writer for player 2
            client2writer = new DataOutputStream(client2.getOutputStream());
            reader2 = new DataInputStream(client2.getInputStream());

            //lets you know the connections went through
            System.out.println("Player 1 connected with disc X.");
            System.out.println("Player 2 connected with disc O.");

            //IDs for player 1 & 2
            String ID1 = "X";//this is for player 1
            String ID2 = "O";//this is for player 2

            //sends the clients(player) IDs which is the discs used
            client1writer.writeUTF(ID1);
            client2writer.writeUTF(ID2);

            setArray();//set the board array up equating al characters to '-'

           int col;//will be used to hold the columns

            while(checkExit!=8)//this is an abitrary number which will be used to check and exit the program if achieved
            {
/////////////////////PLAYER 1/////////////////////////

                disc="X";
                System.out.println("\nWaiting on Player 1");

                sendMessage(client1, getHgt());//calls the method to send the height array to the player

                //converts the 2D board array into a string and sends it to player 1 to
                //to know the current state of the board game
                StringJoiner sj = new StringJoiner(System.lineSeparator());
                for (String[] row : board) {
                    sj.add(Arrays.toString(row));
                }

                String result = sj.toString();

                client1writer.writeUTF(result);//sends to player 1
                client1writer.flush();//flushes the writer1

                //reads input frm player 1
                col=reader1.readInt();
                System.out.println("\n\nPlayer 1 entered " + col);

                EnterPlay(col); //enters the value entered by user into the board
                displayBoard();//displays the gameboard

                //checks for winner
                checkExit=CheckFlat(); //checks flat
                checkExit=CheckDiagUp(); //checks diagonal up
                checkExit=CheckDiagDown(); //checks diagonal down
                checkExit=CheckStraight(); //checks straight up which isi the same down

                //checks if there is a winner.
                //if there is, program exits
                if (checkExit==8)
                {
                    client1writer.writeInt(0);
                    break;
                }
                else
                ///this is sent to player 1 as a check to continue and not stop
                {
                    client1writer.writeInt(1);
                }
                client1writer.flush();

/////////////////////PLAYER 2/////////////////////////
                ///player 2 - everything is similar to player 1's

                disc="O";
                System.out.println("\nWaiting on Player 2");

                sendMessage(client2, hghtCk);

                //converts the array into a string and sends it to player 2
                StringJoiner sj1 = new StringJoiner(System.lineSeparator());
                for (String[] row : board) {
                    sj1.add(Arrays.toString(row));
                }
                String result1 = sj1.toString();

                //sends to player 2
                client2writer.writeUTF(result1);
                client2writer.flush();//flushes the writer2

                //reads column input from player 2
                col=reader2.readInt();
                System.out.println("\n\nPlayer 2 entered " + col);

                EnterPlay(col); //enters the value entered by user into the board
                displayBoard();//displays the gameboard

                //checks for winner
                checkExit=CheckFlat(); //checks flat
                checkExit=CheckDiagUp(); //checks diagonal up
                checkExit=CheckDiagDown(); //checks diagonal down
                checkExit=CheckStraight(); //checks straight up which isi the same down

                //checks if there is a winner.
                //if there is, program exits
                if (checkExit==8)
                {
                    client2writer.writeInt(0);
                    break;
                }
                else
                ///this is sent to player 2 as a check to continue and not stop
                {
                    client2writer.writeInt(1);
                }

                client2writer.flush();

            }//end of while loop

            System.out.println("\nServer closed. Thank You.");

            //closes the reader and writers for both clients
            client1writer.close();
            reader1.close();
            client1.close();

            client2writer.close();
            reader2.close();
            client2.close();

            //closes the server
            server.close();
        }
        catch (IOException IOex)
        {
            System.out.println("Server Error.");
        }
    }

    //This method displays the game
    public static void displayBoard()
    {
        for(int i=0;i<6;i++)
        {
            System.out.print("\n");
            for(int j=0;j<7;j++)
            {
                System.out.print(board[i][j]);
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    //this will initialize the board game to all '-'
    public static void setArray()
    {
        for(int i=0;i<6;i++)
        {
            for(int j=0;j<7;j++)
            {board[i][j] = "-";}
        }

    }

    //enters the user play into the game board
    public static void EnterPlay(int col)
    {
            for (int n = 5; n >= 0; n--)
            {
                if (board[n][col - 1] == "-")
                {
                    board[n][col - 1] = disc;//the space on the game board to the disc string/char

                    //reduce the column height by one.
                    redCOl(col);
                    break;
                }
            }
    }

    //checks if there is a winner on the horizontal
    public static int CheckFlat()
    { //will need to change any 'X' to the variable that holds the character
        for (int i=0;i<6;i++)
        {
            //this side checks from 0-3
            if((board[i][0]==disc)&&(board[i][1]==disc)&&(board[i][2]==disc)&&(board[i][3]==disc))
            {
                whoWon(disc);
                checkExit=8;
                break;
            }
            if ((board[i][1]==disc)&&(board[i][2]==disc)&&(board[i][3]==disc)&&(board[i][4]==disc))
            {
                whoWon(disc);
                checkExit=8;
                break;
            }
            if ((board[i][2]==disc)&&(board[i][3]==disc)&&(board[i][4]==disc)&&(board[i][5]==disc))
            {
                whoWon(disc);
                checkExit=8;
                break;
            }

            if ((board[i][3]==disc)&&(board[i][4]==disc)&&(board[i][5]==disc)&&(board[i][6]==disc))
            {
                whoWon(disc);
                checkExit=8;
                break;
            }
        }
        return checkExit;
    }

    //checks if there is a winner on the diagonal up from left
    public static int CheckDiagUp()
    {
        for (int i=3;i<6;i++)
        {
            if((board[i][0]==disc)&&(board[i-1][1]==disc)&&(board[i-2][2]==disc)&&(board[i-3][3]==disc))
            {
                whoWon(disc);
                checkExit=8;
                break;
            }
            if((board[i][1]==disc)&&(board[i-1][2]==disc)&&(board[i-2][3]==disc)&&(board[i-3][4]==disc))
            {
                whoWon(disc);
                checkExit=8;
                break;
            }
            if((board[i][2]==disc)&&(board[i-1][3]==disc)&&(board[i-2][4]==disc)&&(board[i-3][5]==disc))
            {
                whoWon(disc);
                checkExit=8;
                break;
            }
            if((board[i][3]==disc)&&(board[i-1][4]==disc)&&(board[i-2][5]==disc)&&(board[i-3][6]==disc))
            {
                whoWon(disc);
                checkExit=8;
                break;
            }
        }
        return checkExit;
    }


    //checks if there is a winner on diagonal down from left
    public static int CheckDiagDown()
    {
        for (int i=0;i<3;i++)
        {
            if((board[i][0]==disc)&&(board[i+1][1]==disc)&&(board[i+2][2]==disc)&&(board[i+3][3]==disc))
            {
                whoWon(disc);
                checkExit=8;
                break;
            }
            if((board[i][1]==disc)&&(board[i+1][2]==disc)&&(board[i+2][3]==disc)&&(board[i+3][4]==disc))
            {
                whoWon(disc);
                checkExit=8;
                break;
            }
            if((board[i][2]==disc)&&(board[i+1][3]==disc)&&(board[i+2][4]==disc)&&(board[i+3][5]==disc))
            {
                whoWon(disc);
                checkExit=8;
                break;
            }
            if((board[i][3]==disc)&&(board[i+1][4]==disc)&&(board[i+2][5]==disc)&&(board[i+3][6]==disc))
            {
                whoWon(disc);
                checkExit=8;
                break;
            }
        }
        return checkExit;
    }

    //checks if there a winner staight up or down
    public static int CheckStraight()
    {
        for (int i=0;i<3;i++)
        {
            for(int j=0;j<7;j++)
            {
                if ((board[i][j] == disc) && (board[i + 1][j] == disc) && (board[i + 2][j] == disc) && (board[i + 3][j] == disc))
                {
                    whoWon(disc);
                    checkExit = 8;
                    break;
                }
            }
        }
        return checkExit;
    }

    //checks which player has and displays the winner to the console
    public static void whoWon(String disc)
    {
        //for player 1
        if (disc.equals("X"))
        {
            System.out.println("\n\n****Player 1 Won!!!****\n\t with disc X");
        }
        //for player 2
        if (disc.equals("O"))
        {
            System.out.println("\n\n****Player 2 Won!!!****\n\t with disc O");
        }
    }

    //sends an array of the columns to the player
    public static void sendMessage(Socket s, int[] array) throws IOException
    {
        OutputStream os = s.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);

        oos.writeObject(array);//writes

        oos.flush();//flushes the writer
    }

    //this reduces the column number by 1
    public static void redCOl(int location)
    {
        int d=location-1;
        hghtCk[d] = hghtCk[d]-1;
    }

    //this gets the hght[] array when called
    public static int[] getHgt()
    {
        return hghtCk;
    }
    //////////end of methods////////////
}
