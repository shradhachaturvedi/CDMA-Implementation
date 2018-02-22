
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class demo {
   
     public static void main(String args[]) throws InterruptedException, IOException{
         
        PipedOutputStream pout1 = new PipedOutputStream();
        PipedInputStream pin1 = new PipedInputStream(pout1);
        
        PipedOutputStream pout2 = new PipedOutputStream();
        PipedInputStream pin2 = new PipedInputStream(pout2);
        
        PipedOutputStream pout3 = new PipedOutputStream();
        PipedInputStream pin3 = new PipedInputStream(pout3);
        
        PipedOutputStream pout4 = new PipedOutputStream();
        PipedInputStream pin4 = new PipedInputStream(pout4);
        
        Scanner sc = new Scanner(System.in);
        ArrayList <Integer> message1 = new ArrayList<>();
        ArrayList <Integer> message2 = new ArrayList<>();
        ArrayList <Integer> message3 = new ArrayList<>();
        ArrayList <Integer> message4 = new ArrayList<>();
        ArrayList <Integer> addition = new ArrayList<>();
        ArrayList <Integer> t1 = new ArrayList<>();
        ArrayList <Integer> t2 = new ArrayList<>();
        ArrayList <Integer> t3 = new ArrayList<>();
        ArrayList <Integer> t4 = new ArrayList<>();
        ArrayList <Integer> seq = new ArrayList<>();
        
        System.out.println("Enter chip sequence for transmitter 1 :");
        for(int i=0;i<8;i++)
            t1.add(sc.nextInt());
        System.out.println("Enter chip sequence for transmitter 2 :");
        for(int i=0;i<8;i++)
            t2.add(sc.nextInt());
        System.out.println("Enter chip sequence for transmitter 3 :");
        for(int i=0;i<8;i++)
            t3.add(sc.nextInt());
        System.out.println("Enter chip sequence for transmitter 4 :");
        for(int i=0;i<8;i++)
            t4.add(sc.nextInt());
        System.out.println("Enter bit sequence");
        for(int i=0;i<4;i++)
          seq.add(sc.nextInt());  
        
        ForkJoinPool pool = new ForkJoinPool(2);
        System.out.println("");
        message1 = pool.invoke(new Transmitter1(t1,seq.get(0),pout1));
        System.out.println("");
        System.out.println("Transmitter 1 chip sequence at joiner : ");
        Iterator itr = message1.iterator();
        while(itr.hasNext()){
              System.out.print(" ");
              int a1 = (int)itr.next();
              System.out.print(a1);
        }
        
        System.out.println("");
        message2=pool.invoke(new Transmitter1(t2,seq.get(1),pout2));
        System.out.println("");
        System.out.println("Transmitter 2 chip sequence at joiner : ");
        Iterator itr1 = message2.iterator();
        while(itr1.hasNext()){
              System.out.print(" ");
              int a1 = (int)itr1.next();
              System.out.print(a1);
        }
        
        System.out.println("");
        message3 = pool.invoke(new Transmitter1(t3,seq.get(2),pout3));
        System.out.println("");
        System.out.println("Transmitter 3 chip sequence at joiner : ");
        Iterator itr2 = message3.iterator();
        while(itr2.hasNext()){
              System.out.print(" ");
              int a1 = (int)itr2.next();
              System.out.print(a1);
        }
        
        System.out.println("");
        message4 = pool.invoke(new Transmitter1(t4,seq.get(3),pout4));
        System.out.println("");
        System.out.println("Transmitter 4 chip sequence at joiner : ");
        Iterator itr3 = message4.iterator();
        while(itr3.hasNext()){
              System.out.print(" ");
              int a1 = (int)itr3.next();
              System.out.print(a1);
        }
           
        int sum;
        System.out.println("");
        System.out.println("");
        System.out.println("Addition of sequences at received at joiner : ");
        for(int i=0;i<8;i++){
              System.out.print(" ");
              sum = message1.get(i)+message2.get(i)+message3.get(i)+message4.get(i);
              addition.add(sum);
              System.out.print(sum);
        }
        
        
        pool.invoke(new Receiver1(addition,pin1));
        pool.invoke(new Receiver1(addition,pin2));
        pool.invoke(new Receiver1(addition,pin3));
        pool.invoke(new Receiver1(addition,pin4));
     }
}

class Transmitter1 extends RecursiveTask<ArrayList<Integer>>{
    private DataOutputStream out;
    ArrayList a;
    int seq; 
    static int c =0;
    
    Transmitter1(ArrayList a,int seq, OutputStream os){
        this.a = a;
        this.seq = seq;
        out = new DataOutputStream(os);
    }

    @Override
    protected ArrayList<Integer> compute() {
        c++;
        Iterator itr = a.iterator();
        System.out.println("");
        System.out.println("At transmitter" + c +" sending to receiver :");
        while(itr.hasNext()){
            try {
               System.out.print(" ");
               int c = (int)itr.next();
               System.out.print(c);
               out.writeInt(c);
            } catch (IOException ex) {
                System.out.println("IO Exception at Transmitter ");
            }
        }
        try {
            out.flush(); 
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Transmitter1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(seq == 0){
            for(int i=0;i<a.size();i++){
                if( ((int)a.get(i)) == 1)
                    a.set(i,-1);
                else
                    a.set(i,1);
            }
        }
        return a;  
   }
}

class Receiver1 extends RecursiveTask<ArrayList<Integer>>{
    static int counter =0;
    ArrayList<Integer> addition = new ArrayList<>();
    ArrayList<Integer> t = new ArrayList<>();
    private DataInputStream in;
    Receiver1(ArrayList a,InputStream is){
        this.addition=a;
        in = new DataInputStream(is);
    }
 
    @Override
    synchronized protected ArrayList<Integer> compute() {
        counter++;
        System.out.println("");
        System.out.println("Original sequence at receiver "+counter+":");
        for(int i=0;i<8;i++){
            try {
                System.out.print(" ");
                int p = in.readInt();
                System.out.print(p);
                t.add(p);
            } catch (IOException ex) {
                System.out.println("IO Exception at Receiver");
            }
        }
        int mult,sum=0;
        System.out.println("");
        System.out.println("Dot Product of original sequence and sum of sequences :");
        for (int i=0;i<8;i++) {
            System.out.print(" ");
            mult = t.get(i) * addition.get(i);
            System.out.print(mult);
            sum+=mult;
        }
        System.out.println("");
        System.out.println("Answer : " + sum/8);
        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(Receiver1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return t;
    }
}
