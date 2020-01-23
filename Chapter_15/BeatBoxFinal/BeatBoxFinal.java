package BeatBoxFinal;
import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class BeatBoxFinal {

    private static final long serialVersionUID = 1;

    JFrame theFrame;
    JPanel mainPanel;
    JList incomingList;
    JTextField userMessage;
    //We store the checkboxes in an ArrayList
    ArrayList<JCheckBox> checkboxList;
    int nextNum;
    Vector<String> listVector = new Vector<String>();
    String userName;
    ObjectOutputStream out;
    ObjectInputStream in;
    HashMap<String, boolean[]> otherSeqsMap = new HashMap<String, boolean[]>();

    Sequencer sequencer;
    Sequence sequence;
    Sequence mySequence = null;
    Track track;


    //These are the names of the instruments, as a String array, for building the GUI labels (on each row)
    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal",
            "Hand Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap",
            "Low-mid Tom", "High Agogo", "Open Hi Conga"};

    //These represent the actual drum "keys". The drum channel is like a piano, except each 'key' on the
    //piano is a different drum. So the number 35 is the key for the Bass drum, 42 is Closed Hi-Hat, etc...
    int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};

    public static void main(String[] args){
        //args[0] is your user ID/screen name
        String nameUser = JOptionPane.showInputDialog(null, "What is your username?", "BeatBoxFinal by Icezman", JOptionPane.QUESTION_MESSAGE);
        new BeatBoxFinal().startUp(nameUser);
        System.out.println("Your username is: " + nameUser);
        //this adds a command-line argument for your screen name
        //example: %java BeatBoxFinal theFlash
    }//end main method

    public void startUp(String name){
        userName = name;
        //open connection to the server
        //nothing new... Set up the networking, I/O and make (and start) the reader thread
        try{
            Socket sock = new Socket("127.0.0.1", 5000);
            out = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());
            Thread remote = new Thread(new RemoteReader());
            remote.start();
        }catch(Exception ex){
            System.out.println("COuldn't connect - You'll have to play alone.");
        }//end catch
        setUpMidi();
        buildGUI();
    }//end startUp method

    public void buildGUI(){
        System.out.println("The current version is: " + serialVersionUID);

        theFrame = new JFrame("Icezman's BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        //An 'empty border' gives us a margin between the edges of the panel and
        //where the components are placed. Purely aesthetic.
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        checkboxList = new ArrayList<JCheckBox>();

        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTemp = new JButton("Tempo down");
        downTemp.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTemp);

        //JButton save = new JButton("Save");
        //save.addActionListener(new MySendListener());
        //buttonBox.add(save);

        //JButton restore = new JButton("Restore");
        //restore.addActionListener(new MyReadInListener());
        //buttonBox.add(restore);

        JButton send = new JButton("Send");
        send.addActionListener(new MySendListener());
        buttonBox.add(send);

        userMessage = new JTextField();
        buttonBox.add(userMessage);

        //JList is a component we haven't used before. This is where the incoming messages are displayed.
        //only instead of a normal chat where you just LOOK at the messages, in this app you can SELECT a message
        //from the list to load and play the attached beat pattern.
        incomingList = new JList();
        incomingList.addListSelectionListener(new MyListSelectionListener());
        incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane theList = new JScrollPane(incomingList);
        buttonBox.add(theList);
        //No data to start with
        incomingList.setListData(listVector);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for(int i = 0; i < 16; i++){
            nameBox.add(new Label(instrumentNames[i]));
        }//end for loop
        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16,16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        //Here we make the checkboxes, set them to 'false' (so they aren't checked) and add them
        //to the ArrayList AND to the GUI panel.
        for (int i = 0; i < 256; i++){
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        }//end for loop

        //setUpMidi(); //Is this still relevant???

        theFrame.setBounds(50,50,300,300);
        theFrame.pack();
        theFrame.setVisible(true);
    }//end method buildGUI

    //The usual MIDI set-up stuff for getting the Sequencer, the Sequence, and the Track
    public void setUpMidi(){

        try{
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e){ e.printStackTrace(); }
    }//close SetUpMidi method

    //THIS IS WHERE IT ALL HAPPENS! Where we turn checkbox state into MIDI events, and add them to the track.

    public void buildTrackAndStart(){
        //we'll make a 16-element array to hold the values for one instrument, across all 16 beats.
        //if the instrument is supposed to play on that beat, the value at that element will be the key.
        //if that instrument is NOT supposed to play on that beat, put in a zero.
        //build a track by walking through the checkboxes to get their state and mapping that to an instrument (and
        //making the MidiEvent for it). This is pretty complex but it is EXACTLY as it was in the previous chapters.

        ArrayList<Integer> trackList = null;//this will hold the instruments for each

        //Get rid of the old track and make a fresh one
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        //Do this for each of the 16 ROWS (i.e. Bass, Congo, etc...)
        for (int i = 0; i < 16; i++){
            trackList = new ArrayList<Integer>();

            //Do this for each of the BEATS for this row
            for (int j = 0; j < 16; j++){
                JCheckBox jc = (JCheckBox) checkboxList.get(j + (16*i));

                //is the checkbox at this beat selected? If yes, put the key value in this slot in the array
                //(the slot that represents this beat). Otherwise, the instrument is NOT supposed to play at this beat
                //so set it to zero.
                if( jc.isSelected()){
                    //set the 'key'. That represents which instrument this is (Bass, Hi-Hat, etc...
                    //The instruments Array holds the actual MIDI numbers for each instrument.
                    int key = instruments[i];
                    trackList.add(key);
                } else {
                    //because this slot should be empty in the track
                    trackList.add(null);
                }
            }//close inner if loop
            //for this instrument, and for all 16 beats, make events and add them to the track
            makeTracks(trackList);
        }//close for loop
        track.add(makeEvent(192,9,1,0,15));
        try{
            sequencer.setSequence(sequence);
            //let's you specify the number of loop iterations, or in this case, continuous looping.
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);

            //NOW PLAY THE THING!
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) { e.printStackTrace(); }
    }//close buildTrackAndStart method


    //=================================== Start inner classes ===================

    //this is the first of the inner classes, listeners for the buttons.
    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a){
            buildTrackAndStart();
        }//close actionPerformed method
    }//close inner class

    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent a){
            sequencer.stop();
        }
    }//close inner class

    //the next 2 change the tempo, it scales the sequencers tempo by the factor provided.
    //the default is 1.0, so we're adjusting +/- 3% per click.
    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a){
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }//close inner class

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a){
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * .97));
        }
    }//close inner class

    //This is to save a pattern
    public class MySendListener implements ActionListener{
        //It all happens when a user clicks the button and the ActionEvent fires
        public void actionPerformed(ActionEvent a){
            //make a boolean Array to hold the state of each checkbox
            boolean[] checkboxState = new boolean[256];

            for(int i = 0; i < 256; i++){

                //Walk through the checkboxList (ArrayList of checkboxes) and get
                //the state of each one and add it to the boolean array.
                JCheckBox check = (JCheckBox) checkboxList.get(i);
                if(check.isSelected()){
                    checkboxState[i] = true;
                }
            }
            String messageToSend = null;
            try{
                out.writeObject(userName + nextNum++ + ": " + userMessage.getText());
                out.writeObject(checkboxState);
            }catch(Exception ex){
                System.out.println("Sorry dude, Couldn't send it to the server.");
            }
            userMessage.setText("");
        }//close actionPerformed method
    }//close inner class


    //this is new -- A ListSelectionListener that tells us when the user made a selection on the list of messages.
    //When the user selects a message we IMMEDIATLY load the associated beat pattern (it's in the HashMap called otherSeqsMap)
    //and start playing it. There's some if tests because of little quirky things about getting ListSelectionEvents.
    public class MyListSelectionListener implements ListSelectionListener{
        public void valueChanged(ListSelectionEvent le){
            if(!le.getValueIsAdjusting()){
                String selected = (String) incomingList.getSelectedValue();
                if(selected != null){
                    //now go to the map and change the sequence
                    boolean[] selectedState = (boolean[]) otherSeqsMap.get(selected);
                    changeSequence(selectedState);
                    sequencer.stop();
                    buildTrackAndStart();
                }
            }
        }//close valueChanged
    }//closer inner class

    //this is the thread job -- read in data from the server. In this code 'data' will always be two serialized
    //objects: the String message and the beat pattern(an ArrayList of checkbox state values)
    public class RemoteReader implements Runnable{
        boolean[] checkboxState = null;
        String nameToShow = null;
        Object obj = null;
        public void run(){
            try{
                while((obj = in.readObject()) != null){
                    System.out.println("Got an object from server");
                    System.out.println(obj.getClass());
                    String nameToShow = (String) obj;
                    checkboxState = (boolean[]) in.readObject();
                    otherSeqsMap.put(nameToShow, checkboxState);
                    listVector.addElement(nameToShow);
                    incomingList.setListData(listVector);
                    //when a message comes in, we read (deserialize) the two objects(the message and the ArrayList of Boolean
                    //checkbox state values) and add it to the JList component. adding to a JList is a two-step thing:
                    //You keep a Vector of the lists data (Vector is an old-fashioned ArrayList) and then tell the JList to use
                    //that Vector as its source for what to display in the list.
                }//close while
            } catch(Exception ex){
                ex.printStackTrace();
            }//close catch
        }//close run method
    }//close inner class

    public class MyPlayMineListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            if(mySequence != null){
                //restore to my original
                sequence = mySequence;
            }
        }//close actionPerformed
    }//close inner class

    //this method is called when the user selects something form the list. We IMMEDIATLY change the pattern to the one they selected.
    public void changeSequence(boolean[] checkboxState){
        for (int i = 0; i < 256; i++){
            JCheckBox check = (JCheckBox) checkboxList.get(i);
            if(checkboxState[i]){
                check.setSelected(true);
            }else {
                check.setSelected(false);
            }
        }//close loop
    }//close changeSequence method

    //public class MyReadInListener implements ActionListener{
    //	public void actionPerformed(ActionEvent a){
    //		boolean[] checkboxState = null;
    //		try{
    //			FileInputStream fileIn = new FileInputStream(new File("Checkbox.ser"));
    //			ObjectInputStream is = new ObjectInputStream(fileIn);
    //			//read the single object in the file (the boolean array) and cast it back to a boolean array
    //			//(remember readObject() returns a reference of type Object!
    //			checkboxState = (boolean[]) is.readObject();
    //		}catch (Exception ex) {
    //			ex.printStackTrace();
    //		}

    //		//now restore the state of each of the checkboxes in the ArrayList of actual JCheckBox objects (checkboxList)
    //		for (int i = 0; i < 256; i++){
    //			JCheckBox check = (JCheckBox) checkboxList.get(i);
    //			if(checkboxState[i]){
    //				check.setSelected(true);
    //			}else {
    //				check.setSelected(false);
    //			}
    //		}
    //		//Now stop whatever is currently playing and rebuild the sequence using the new state of
    //		//the checkboxes in the ArrayList
    //		sequencer.stop();
    //		buildTrackAndStart();
    //	}
    //}

    //This makes events for one instrument at a time, for all 16 beats. So it might get an int[] for the Bass drum,
    //and each index in the array will hold either the key of that instrument, or a zero. If it's a zero,
    //the instrument isn't supposed to play at that beat. Otherwise, make an event and add it to the track.
    public void makeTracks(ArrayList list){
        Iterator it = list.iterator();
        for (int i = 0; i < 16; i++){

            Integer num = (Integer) it.next();
            if(num != null){
                int numKey = num.intValue();
                //make the NOTE ON and NOTE OFF events and add them to the track.
                track.add(makeEvent(144,9,numKey, 100, i));
                track.add(makeEvent(128,9,numKey, 100, i + 1));
            }//end inner if loop
        }//end inner for loop
    }//end makeTracks method

    //this is the utility method to make events.
    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick){
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch(Exception e) { e.printStackTrace(); }
        return event;
    }//close makeEvent method
}//end class
