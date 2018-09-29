import java.util.*;
import java.io.*;
//By: Yu-En(Brian) Shih
public class Scheduling{
  //Holds all the process input data
  static ArrayList <ArrayList<Integer>> data = new ArrayList <ArrayList<Integer>>();
  static int randPosition = 1;
  static boolean verbose = false;

  public static void main(String [] args){
    int shift = 0;
    //Arrays.stream(alphabet).anyMatch("A"::equals);
    if(Arrays.stream(args).anyMatch("-verbose"::equals)){
      verbose = true;
      System.out.println("You flaged the program with -verbose!");
    }
    try{
      //Opening file
      File file = new File(args[args.length-1]);
      Scanner scanner = new Scanner(file);
      //Getting number of processes
      int processCount = scanner.nextInt();

      //List for pre-hashtable setup
      ArrayList<Integer> tempList = new ArrayList <Integer>();
      int tempNum = 0;
      //Print original input
      System.out.print("The original input was:\t" + processCount + "  ");
      //Storing the next ints 4 by 4 ordered A/B/C/IO.
      for(int i = 0; i < processCount; i++){
        for(int k = 0; k < 4; k++){
          tempNum = scanner.nextInt();
          tempList.add(tempNum);
          //print original input one by one
          System.out.print( tempNum + " ");
        }
        //make the printed original input look better
        System.out.print(" ");
        data.add(new ArrayList<Integer> (tempList));
        //clearing tempList for the next 4 integers
        tempList.clear();
      }
      System.out.println();

      //Sort the processes
      sort();
      System.out.println("The (sorted) input is:\t" + processCount + "  " + data.toString().replace("[","").replace("]"," ").replace(",",""));

      System.out.println("\n\n");
      //Calling the different algorithms
      fcfs();
      randPosition = 1;
      rrq2();
      randPosition = 1;
      unip();
      randPosition = 1;
      psjf();
    }catch(FileNotFoundException e) {
      //FileNotFoundException
      System.out.println("Unable to open file '" + args[args.length-1]+ "'");
    }
  }

  //Random reads a non-negative integer x from a file  an return 1+(x mod u)
  public static int randomOS(int u){
    try{
      File file = new File("random-numbers");
      Scanner scanner = new Scanner(file);
      int num = 0;
      for(int i = 0; i < randPosition;i++){
        num = scanner.nextInt();
      }
      randPosition ++;
      return 1+(num%u);
    }catch(FileNotFoundException e){
      System.out.println("The file \"random-numbers\" not found.");
      System.exit(0);
    }
    return 0;
  }

  //FIX PROCESSSES ORDER.
  //First Come First Server Algorithm
  public static void fcfs(){
    int timer = 0;
    int temp;
    double cputUtil = 0;
    double ioUtil = 0;
    int processCount = 0;
    boolean process = true;
    double avgTurn = 0;
    double avgWait = 0;
    ArrayList <Integer> readyQueue = new ArrayList<Integer>();
    ArrayList <Integer> blockedQueue = new ArrayList<Integer>();
    ArrayList <Integer> remainTime = new ArrayList<Integer>();
    ArrayList <String> state = new ArrayList<String>();
    ArrayList <Integer> burst = new ArrayList<Integer>();
    ArrayList <Integer> ioTime = new ArrayList<Integer>();
    ArrayList <Integer> waitingTime = new ArrayList<Integer>();
    ArrayList <ArrayList<Integer>> printStatement = new ArrayList<ArrayList<Integer>>();
    ArrayList <Integer> tempList = new ArrayList <Integer>();

    for(int i = 0; i < 4;i ++){
      tempList.add(0);
    }
    System.out.println("The scheduling algorithm used was First Come First Served\n");
    for(int i = 0; i < data.size(); i ++) {
      state.add("Us");
      burst.add(0);
      remainTime.add(data.get(i).get(2));
      ioTime.add(0);
      waitingTime.add(0);
      printStatement.add(new ArrayList<Integer> (tempList));
    }
    while(process){
      if(verbose){
        System.out.print("\nBefore Cycle " + timer + ":\t");
        for(int i = 0; i < data.size();i++){
          if(state.get(i) == "Fn"){
            System.out.print("Terminated");
          }else if(state.get(i) == "Us"){
            System.out.print("Unstarted");
          }else if(state.get(i) == "Ru"){
            System.out.print("Running");
          }else if(state.get(i) == "Bl"){
            System.out.print("Blocked");
          }else{
            System.out.print("Ready");
          }
          System.out.print("\t" + burst.get(i) + "\t");
        }
      }
      //Ready processes
      if(state.contains("Re")){
        for(int i = 0; i < readyQueue.size();i++){
          waitingTime.set(readyQueue.get(i),waitingTime.get(readyQueue.get(i))+1);
        }
      }
      //Running processes
      if(state.contains("Ru") && burst.get(state.indexOf("Ru"))>0){
        burst.set(state.indexOf("Ru"),burst.get(state.indexOf("Ru"))-1);
        remainTime.set(state.indexOf("Ru"),remainTime.get(state.indexOf("Ru"))-1);
        cputUtil++;
      }
      //Blocked processes
      temp = 0;
      if(state.contains("Bl") ){
        for(int i = 0; i < blockedQueue.size();i++){
          if(burst.get(blockedQueue.get(i)) > 0){
            temp = 1;
            burst.set(blockedQueue.get(i),burst.get(blockedQueue.get(i))-1);
            ioTime.set(blockedQueue.get(i),ioTime.get(blockedQueue.get(i))+1);
          }
        }
        if (temp == 1){
          ioUtil++;
        }
      }
      temp = 0;
      //a repeat of a bottom method.
      if(state.contains("Bl")){

        for(int i = 0; i < data.size();i++) {
          if(state.get(i) == "Bl" && burst.get(i) == 0){
            state.set(i,"Re");
            readyQueue.add(i);
            blockedQueue.remove(blockedQueue.indexOf(i));
          }
        }

        if(!state.contains("Ru")){
          if(state.contains("Re")){
            state.set(readyQueue.get(0),"Ru");
            readyQueue.remove(0);
            temp = randomOS(data.get(state.indexOf("Ru")).get(1));
            if(temp > remainTime.get(state.indexOf("Ru"))){
              temp = remainTime.get(state.indexOf("Ru"));
            }
            burst.set(state.indexOf("Ru"),temp);
          }
        }
      }
      //checking for arrival
      for(int i = 0; i < data.size(); i++){
        if(timer == data.get(i).get(0)){
          state.set(i,"Re");
          readyQueue.add(i);
        }
      }
      //Finishing process or/and finishing everything.
      if(state.contains("Ru") && burst.get(state.indexOf("Ru")) == 0 && remainTime.get(state.indexOf("Ru")) == 0){
        // System.out.println("Process :" + processCount);
        // System.out.println("\t(A,B,C,IO) = " + data.get(state.indexOf("Ru")));
        // System.out.println("\tFinishing time :" + timer);
        printStatement.get(state.indexOf("Ru")).set(0,timer);
        temp = (timer - data.get(state.indexOf("Ru")).get(0));
        avgTurn += temp;
        // System.out.println("\tTurnaround time: " + temp);
        printStatement.get(state.indexOf("Ru")).set(1,temp);
        // System.out.println("\tI/O time: " + ioTime.get(state.indexOf("Ru")));
        printStatement.get(state.indexOf("Ru")).set(2,ioTime.get(state.indexOf("Ru")));
        temp = waitingTime.get(state.indexOf("Ru"));
        avgWait += temp;
        // System.out.println("\tWaiting time: " + temp + "\n");
        printStatement.get(state.indexOf("Ru")).set(3,temp);
        state.set(state.indexOf("Ru"),"Fn");
        processCount++;
        if(state.contains("Fn") && !state.contains("Na") && !state.contains("Us") && !state.contains("Ru") && !state.contains("Bl") && !state.contains("Re")){
          process = false;
          break;
        }
      }
      //No running process
      if(!state.contains("Ru")){
        //if there is ar ready process
        if(state.contains("Re")){
          state.set(readyQueue.get(0),"Ru");
          readyQueue.remove(0);
          temp = randomOS(data.get(state.indexOf("Ru")).get(1));
          if(temp > remainTime.get(state.indexOf("Ru"))){
            temp = remainTime.get(state.indexOf("Ru"));
          }
          burst.set(state.indexOf("Ru"),temp);
        }
        //if there is no ready, but there is a blocked process
        if(state.contains("Bl")){
          for(int i = 0; i < blockedQueue.size();i++){
            if(burst.get(blockedQueue.get(i)) == 0){
              state.set(blockedQueue.get(i),"Ru");
              temp = randomOS(data.get(state.indexOf("Ru")).get(1));
              blockedQueue.set(i,123123123);
              if(temp > remainTime.get(state.indexOf("Ru"))){
                temp = remainTime.get(state.indexOf("Ru"));
              }
              burst.set(state.indexOf("Ru"),temp);
            }
          }
          for(int i = blockedQueue.size()-1 ; i >=0;i--){
            if(blockedQueue.get(i) == 123123123){
              blockedQueue.remove(i);
            }
          }
        }
        //Ther is a running process;
      }else{
        if(burst.get(state.indexOf("Ru")) == 0){
          temp = state.indexOf("Ru");
          blockedQueue.add(temp);
          state.set(temp,"Bl");
          burst.set(temp,randomOS(data.get(temp).get(3)));
        }
      }
      if(state.contains("Bl")){
        //if blocked == 0, then add it to ready queue.
        for(int i = 0; i < blockedQueue.size();i++){
          if(burst.get(blockedQueue.get(i)) == 0){
            state.set(blockedQueue.get(i),"Re");
            readyQueue.add(blockedQueue.get(i));
            blockedQueue.set(i,123123123);
          }
        }
        for(int i = blockedQueue.size()-1 ; i >=0;i--){
          if(blockedQueue.get(i) == 123123123){
            blockedQueue.remove(i);
          }
        }

        if(!state.contains("Ru")){
          if(state.contains("Re")){
            state.set(readyQueue.get(0),"Ru");
            readyQueue.remove(0);
            temp = randomOS(data.get(state.indexOf("Ru")).get(1));
            if(temp > remainTime.get(state.indexOf("Ru"))){
              temp = remainTime.get(state.indexOf("Ru"));
            }
            burst.set(state.indexOf("Ru"),temp);
          }
        }
      }
      timer++;
    }
    System.out.println("\n");
    for(int i = 0; i < data.size();i++){
      System.out.println("Process: " + i);
      System.out.println("\t(A,B,C,I/O) = " + data.get(i));
      System.out.println("\tFinishing Time: " + printStatement.get(i).get(0));
      System.out.println("\tTurnaround time: " + printStatement.get(i).get(1));
      System.out.println("\tI/O Time: " + printStatement.get(i).get(2));
      System.out.println("\tWaiting Time: " + printStatement.get(i).get(3) + "\n");
    }
    System.out.println("Summary Data: ");
    System.out.println("\tFinishing time: " + timer);
    System.out.println("\tCPU Utilization: " + cputUtil/timer);
    System.out.println("\tI/O Utilization: " + ioUtil/timer);
    System.out.println("\tThroughput: " + 100*(data.size()/(double)timer) +" processes per hundready cycles");
    System.out.println("\tAverage turnaround time: " + avgTurn/data.size());
    System.out.println("\tAverage waiting time: " + avgWait/data.size() + "\n\n");


  }
  //Round-Robin w/ Quantum 2
  //For loop inside while loop looking at each process one by one.
  public static void rrq2(){
    int timer = 0;
    int temp;
    boolean ioBoolean = false;
    double cputUtil = 0;
    double ioUtil = 0;
    int processCount = 0;
    boolean process = true;
    double avgTurn = 0;
    double avgWait = 0;
    ArrayList <Integer> extraBurst = new ArrayList<Integer>();
    ArrayList <Integer> readyQueue = new ArrayList<Integer>();
    ArrayList <Integer> blockedQueue = new ArrayList<Integer>();
    ArrayList <Integer> remainTime = new ArrayList<Integer>();
    ArrayList <String> state = new ArrayList<String>();
    ArrayList <Integer> burst = new ArrayList<Integer>();
    ArrayList <Integer> ioTime = new ArrayList<Integer>();
    ArrayList <Integer> waitingTime = new ArrayList<Integer>();
    ArrayList <ArrayList<Integer>> printStatement = new ArrayList<ArrayList<Integer>>();
    ArrayList <Integer> tempList = new ArrayList <Integer>();

    for(int i = 0; i < 4;i ++){
      tempList.add(0);
    }
    System.out.println("The scheduling algorithm used was Round Robin Quantum 2\n");
    for(int i = 0; i < data.size(); i ++) {
      state.add("Us");
      burst.add(0);
      remainTime.add(data.get(i).get(2));
      ioTime.add(0);
      waitingTime.add(0);
      extraBurst.add(0);
      printStatement.add(new ArrayList<Integer> (tempList));
    }
    while(process){
      if(verbose){
        System.out.print("\nBefore Cycle " + timer + ":\t");
        for(int i = 0; i < data.size();i++){
          if(state.get(i) == "Fn"){
            System.out.print("Terminated");
          }else if(state.get(i) == "Us"){
            System.out.print("Unstarted");
          }else if(state.get(i) == "Ru"){
            System.out.print("Running");
          }else if(state.get(i) == "Bl"){
            System.out.print("Blocked");
          }else{
            System.out.print("Ready");
          }
          System.out.print("\t" + burst.get(i) + "\t");
        }
      }

      if(ioBoolean){
        ioUtil++;
      }
      ioBoolean = false;
      for(int i = 0; i < data.size(); i++){
        //Decreasing stuff!!!!
        if(state.get(i) == "Re"){
          waitingTime.set(i,waitingTime.get(i)+1);
        }
        else if(state.get(i) == "Ru" && burst.get(i) > 0){
          burst.set(i,burst.get(i)-1);
          remainTime.set(i,remainTime.get(i)-1);
          cputUtil++;
        }
        else if(state.get(i) == "Bl" && burst.get(i) > 0){
          burst.set(i,burst.get(i)-1);
          ioTime.set(i,ioTime.get(i)+1);
          ioBoolean = true;
        }
        else if(state.get(i) == "Us" && data.get(i).get(0) == timer){
          state.set(i,"Re");
          readyQueue.add(i);
        }
        //Converting Blocked to run or Blocked to Ready.
        if(state.get(i) == "Bl" && burst.get(i) == 0 ){
          blockedQueue.remove(0);
          temp = 0;
          if(!state.contains("Re") && !state.contains("Ru")){
            state.set(i,"Ru");
            //Checking for extra Burst.
            if(extraBurst.get(i) > 0){
              temp = extraBurst.get(i);
            }else{
              temp = randomOS(data.get(i).get(1));
              extraBurst.set(i,temp);
            }
            //Checking if extra Burst is larger than quantum time.
            if(temp >= 2){
              temp = 2;
              extraBurst.set(i,extraBurst.get(i)-2);
            }else{
              extraBurst.set(i,extraBurst.get(i)-1);
            }
            if(temp > remainTime.get(i)){
              temp = remainTime.get(i);
              extraBurst.set(i,0);
            }

            burst.set(i,temp);
          }else{
            state.set(i,"Re");
            readyQueue.add(i);
          }
        }
        //Convert Running Process to Blocking Process
        else if(state.get(i) == "Ru" && burst.get(i) == 0){
          //Convert Running to Finished
          if(remainTime.get(i) == 0){
            state.set(i,"Fn");
            // System.out.println("Process: " + processCount);
            // System.out.println("\t(A,B,C,I/O) = " + data.get(i));
            // System.out.println("\tFinishing time: " + timer);
            printStatement.get(i).set(0,timer);
            temp = (timer - data.get(i).get(0));
            avgTurn += temp;
            // System.out.println("\tTurnaround time: " + temp);
            printStatement.get(i).set(1,temp);
            // System.out.println("\tI/O time: " + ioTime.get(i));
            printStatement.get(i).set(2,ioTime.get(i));
            temp = waitingTime.get(i);
            avgWait += temp;
            // System.out.println("\tWaiting time: " + temp + "\n");
            printStatement.get(i).set(3,temp);
            processCount++;
            if(!state.contains("Us") && !state.contains("Ru") && !state.contains("Re") && !state.contains("Bl")){
              process = false;
              break;
            }
          }else if(extraBurst.get(i) > 0){
            state.set(i,"Re");
            readyQueue.add(i);
          }else{
            //Convert Running to Blocking
            state.set(i,"Bl");
            burst.set(i,randomOS(data.get(i).get(3)));
            blockedQueue.add(i);
          }
        }
      }
      //Convert Ready to Running;
      if(!state.contains("Ru")){
        if(state.contains("Re")){
          state.set(readyQueue.get(0),"Ru");
          temp = 0;
          if(extraBurst.get(readyQueue.get(0)) > 0){
            temp = extraBurst.get(readyQueue.get(0));
          }else{
            temp = randomOS(data.get(readyQueue.get(0)).get(1));
            extraBurst.set(readyQueue.get(0),temp);
          }
          //Checking if extra Burst is larger than quantum time.
          if(temp >= 2){
            temp = 2;
            extraBurst.set(readyQueue.get(0),extraBurst.get(readyQueue.get(0))-2);
          }else{
            extraBurst.set(readyQueue.get(0),extraBurst.get(readyQueue.get(0))-1);
          }
          if(temp > remainTime.get(readyQueue.get(0))){
            temp = remainTime.get(readyQueue.get(0));
            extraBurst.set(readyQueue.get(0),0);
          }
          burst.set(readyQueue.get(0),temp);
          readyQueue.remove(0);
        }
      }
      if(!process){
        break;
      }

      timer++;
    }
    System.out.println("\n");
    for(int i = 0; i < data.size();i++){
      System.out.println("Process: " + i);
      System.out.println("\t(A,B,C,I/O) = " + data.get(i));
      System.out.println("\tFinishing Time: " + printStatement.get(i).get(0));
      System.out.println("\tTurnaround time: " + printStatement.get(i).get(1));
      System.out.println("\tI/O Time: " + printStatement.get(i).get(2));
      System.out.println("\tWaiting Time: " + printStatement.get(i).get(3) + "\n");
    }
    System.out.println("Summary Data: ");
    System.out.println("\tFinishing time: " + timer);
    System.out.println("\tCPU Utilization: " + cputUtil/timer);
    System.out.println("\tI/O Utilization: " + ioUtil/timer);
    System.out.println("\tThroughput: " + 100*(data.size()/(double)timer) +" processes per hundready cycles");
    System.out.println("\tAverage turnaround time: " + avgTurn/data.size());
    System.out.println("\tAverage waiting time: " + avgWait/data.size() + "\n\n");


  }
  //Uniprogramed
  public static void unip(){
    int cpuBurst = 0;
    int ioBurst = 0;
    int cpuTotal = 0;
    int ioTotal = 0;
    int waitingTime = 0;
    Boolean extraBurstCheck = false;
    int extraBurst = 0;
    int finishingTime = 0;
    //Summary variables
    double avgTurnAroundTime = 0;
    double avgWaitingTime = 0;
    double utilCPU = 0;
    double utilIO = 0;
    System.out.println("The scheduling algorithm used was Uniprogrammed\n");
    for(int i = 0; i < data.size();i++){
      //Running the processes
      cpuTotal = data.get(i).get(2);
      utilCPU += cpuTotal;
      while(cpuTotal >0){


        cpuBurst = randomOS(data.get(i).get(1));
        //If CPU burst is larger than remaining time, then set next cpu burst to remaining time.
        if(cpuBurst > cpuTotal){
          cpuBurst = cpuTotal;
        }
        //System.out.println(cpuBurst);
        cpuTotal -= cpuBurst;
        finishingTime += cpuBurst;
        if(cpuTotal<= 0){
          break;
        }
        ioBurst = randomOS(data.get(i).get(3));
        //System.out.println(ioBurst);
        finishingTime += ioBurst;
        ioTotal += ioBurst;
      }


      //Printing out all the process info
      System.out.println("\n");
      System.out.println("Process " + i + ":");
      System.out.println("\t(A,B,C,IO) = (" + data.get(i).toString().replace(" " , "").replace("[","").replace("]","")+")");
      System.out.println("\tFinishing time: " + finishingTime);
      System.out.println("\tTurnaround time: " + (finishingTime - data.get(i).get(0)));
      System.out.println("\tI/O time: = " + ioTotal);
      System.out.println("\tWaiting time: "+ (waitingTime - data.get(i).get(0)) +"\n");
      avgTurnAroundTime += finishingTime - data.get(i).get(0);
      avgWaitingTime += waitingTime - data.get(i).get(0);
      waitingTime = finishingTime;
      utilIO += ioTotal;
      ioTotal = 0;
    }
    System.out.println("Summary Data: ");
    System.out.println("\tFinishing time: " + finishingTime);
    System.out.println("\tCPU Utilization: " + (utilCPU/(utilCPU + utilIO)));
    System.out.println("\tI/O Utilization: " + (utilIO/(utilCPU + utilIO)));
    System.out.println("\tThroughput: " + ((double)data.size()/finishingTime*100) + " processes per hundread cycles");
    System.out.println("\tAverage turnaround time: " + avgTurnAroundTime/data.size());
    System.out.println("\tAverage waiting time: " + avgWaitingTime/data.size()+ "\n\n");

    randPosition = 1;


  }
  //Preemptive Shortest Job First

  public static void psjf(){
    int timer = 0;
    int temp;
    boolean ioBoolean = false;
    double cputUtil = 0;
    double ioUtil = 0;
    int processCount = 0;
    boolean process = true;
    double avgTurn = 0;
    double avgWait = 0;
    ArrayList <Integer> extraBurst = new ArrayList<Integer>();
    ArrayList <Integer> readyQueue = new ArrayList<Integer>();
    ArrayList <Integer> blockedQueue = new ArrayList<Integer>();
    ArrayList <Integer> remainTime = new ArrayList<Integer>();
    ArrayList <String> state = new ArrayList<String>();
    ArrayList <Integer> burst = new ArrayList<Integer>();
    ArrayList <Integer> ioTime = new ArrayList<Integer>();
    ArrayList <Integer> waitingTime = new ArrayList<Integer>();
    ArrayList <ArrayList<Integer>> printStatement = new ArrayList<ArrayList<Integer>>();
    ArrayList <Integer> tempList = new ArrayList <Integer>();

    for(int i = 0; i < 4;i ++){
      tempList.add(0);
    }
    for(int i = 0; i < data.size(); i ++) {
      state.add("Us");
      burst.add(0);
      remainTime.add(data.get(i).get(2));
      ioTime.add(0);
      waitingTime.add(0);
      extraBurst.add(0);
      printStatement.add(new ArrayList<Integer> (tempList));
    }
    System.out.println("The scheduling algorithm used was PSJF\n");
    while(process){
      if(verbose){
        System.out.print("\nBefore Cycle " + timer + ":\t");
        for(int i = 0; i < data.size();i++){
          if(state.get(i) == "Fn"){
            System.out.print("Terminated");
          }else if(state.get(i) == "Us"){
            System.out.print("Unstarted");
          }else if(state.get(i) == "Ru"){
            System.out.print("Running");
          }else if(state.get(i) == "Bl"){
            System.out.print("Blocked");
          }else{
            System.out.print("Ready");
          }
          System.out.print("\t" + burst.get(i) + "\t");
        }
      }
      if(ioBoolean){
        ioUtil++;
      }
      ioBoolean = false;
      for(int i = 0; i < data.size(); i++){
        //Decreasing stuff!!!!
        if(state.get(i) == "Re"){
          waitingTime.set(i,waitingTime.get(i)+1);
        }
        else if(state.get(i) == "Ru" && burst.get(i) > 0){
          burst.set(i,burst.get(i)-1);
          remainTime.set(i,remainTime.get(i)-1);
          cputUtil++;
        }
        else if(state.get(i) == "Bl" && burst.get(i) > 0){
          burst.set(i,burst.get(i)-1);
          ioTime.set(i,ioTime.get(i)+1);
          ioBoolean = true;
        }
        else if(state.get(i) == "Us" && data.get(i).get(0) == timer){
          state.set(i,"Re");
          readyQueue.add(i);
        }
        //Converting Blocked to run or Blocked to Ready.
        if(state.get(i) == "Bl" && burst.get(i) == 0 ){
          blockedQueue.remove(0);
          temp = 0;
          if(!state.contains("Re") && !state.contains("Ru")){
            state.set(i,"Ru");
            //Checking for extra Burst.
            temp = randomOS(data.get(i).get(1));
            if(temp > remainTime.get(i)){
              temp = remainTime.get(i);
            }
            burst.set(i,temp);
          }else{
            state.set(i,"Re");
            readyQueue.add(i);
          }
        }
        //Convert Running Process to Blocking Process
        else if(state.get(i) == "Ru" && burst.get(i) == 0){
          //Convert Running to Finished
          if(remainTime.get(i) == 0){
             state.set(i,"Fn");
            // System.out.println("Process: " + processCount);
            // System.out.println("\t(A,B,C,I/O) = " + data.get(i));
            // System.out.println("\tFinishing time: " + timer);
            printStatement.get(i).set(0,timer);
            temp = (timer - data.get(i).get(0));
            avgTurn += temp;
            //System.out.println("\tTurnaround time: " + temp);
            printStatement.get(i).set(1,temp);
            //System.out.println("\tI/O time: " + ioTime.get(i));
            printStatement.get(i).set(2,ioTime.get(i));
            temp = waitingTime.get(i);
            avgWait += temp;
            //System.out.println("\tWaiting time: " + temp + "\n");
            printStatement.get(i).set(3,temp);
            processCount++;
            if(!state.contains("Us") && !state.contains("Ru") && !state.contains("Re") && !state.contains("Bl")){
              process = false;
              break;
            }
          }else{
            //Convert Running to Blocking
            state.set(i,"Bl");
            burst.set(i,randomOS(data.get(i).get(3)));
            blockedQueue.add(i);
          }
        }
      }
      //Convert Ready to Running;
      if(!state.contains("Ru")){
        if(state.contains("Re")){
          //store position of the shortest remaining time.
          temp = remainTime.get(readyQueue.get(0));
          int location = readyQueue.get(0);
          int readyLocation = 0;
          for(int i = 0; i < readyQueue.size();i++){
            if(remainTime.get(readyQueue.get(i)) < temp){

              readyLocation = i;
              location = readyQueue.get(i);
              temp = remainTime.get(readyQueue.get(i));
            }
            if(remainTime.get(readyQueue.get(i)) == temp){
              if(location > readyQueue.get(i)){
                readyLocation = i;
                location = readyQueue.get(i);
                temp = remainTime.get(readyQueue.get(i));
              }
            }
          }
          state.set(location,"Ru");
          if(burst.get(location) == 0){
            temp = randomOS(data.get(location).get(1));
            if(temp > remainTime.get(location)){
              temp = remainTime.get(location);
            }
            burst.set(location,temp);
          }
          readyQueue.remove(readyLocation);
        }
      }else {
        if(state.contains("Re")){
          readyQueue.add(state.indexOf("Ru"));
          state.set(state.indexOf("Ru"),"Re");
          //store position of the shortest remaining time.
          temp = remainTime.get(readyQueue.get(0));
          int location = readyQueue.get(0);
          int readyLocation = 0;
          for(int i = 0; i < readyQueue.size();i++){
            if(remainTime.get(readyQueue.get(i)) < temp){

              readyLocation = i;
              location = readyQueue.get(i);
              temp = remainTime.get(readyQueue.get(i));
            }
            if(remainTime.get(readyQueue.get(i)) == temp){
              if(location > readyQueue.get(i)){
                readyLocation = i;
                location = readyQueue.get(i);
                temp = remainTime.get(readyQueue.get(i));
              }
            }
          }
          state.set(location,"Ru");
          if(burst.get(location) == 0){
            temp = randomOS(data.get(location).get(1));
            if(temp > remainTime.get(location)){
              temp = remainTime.get(location);
            }
            burst.set(location,temp);
          }
          readyQueue.remove(readyLocation);
        }
      }
      if(!process){
        break;
      }


      timer++;
    }
    System.out.println("\n");
    for(int i = 0; i < data.size();i++){
      System.out.println("Process: " + i);
      System.out.println("\t(A,B,C,I/O) = " + data.get(i));
      System.out.println("\tFinishing Time: " + printStatement.get(i).get(0));
      System.out.println("\tTurnaround time: " + printStatement.get(i).get(1));
      System.out.println("\tI/O Time: " + printStatement.get(i).get(2));
      System.out.println("\tWaiting Time: " + printStatement.get(i).get(3) + "\n");
    }
    System.out.println("Summary Data: ");
    System.out.println("\tFinishing time: " + timer);
    System.out.println("\tCPU Utilization: " + cputUtil/timer);
    System.out.println("\tI/O Utilization: " + ioUtil/timer);
    System.out.println("\tThroughput: " + 100*(data.size()/(double)timer) +" processes per hundready cycles");
    System.out.println("\tAverage turnaround time: " + avgTurn/data.size());
    System.out.println("\tAverage waiting time: " + avgWait/data.size() + "\n\n");


  }

  //Sorts
  public static void sort(){
    //sorts the arraylist of arraylist integers by the first item in inner list
    Collections.sort(data, new Comparator<ArrayList<Integer>>() {
      @Override
      public int compare(ArrayList<Integer> one, ArrayList<Integer> two) {
        return one.get(0).compareTo(two.get(0));
      }});
    }
}
