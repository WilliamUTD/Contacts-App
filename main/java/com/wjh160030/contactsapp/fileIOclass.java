package com.wjh160030.contactsapp;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

//This is wher the file IO is handled. TThe file is created and written to here.
////Written by William Hood and Albin Mathew
//// wjh160030, ajm161130

public class fileIOclass {


    RandomAccessFile fileIO;
    final int length = 82;
    ArrayList<ContactClass> contactList = new ArrayList<>();

    //create the subdirectory and file if neither exists.
    //WRITTEN BY WILLIAM HOOD.
    fileIOclass(File f) {
        try {
            File file = new File("ContactsList");
            if(!file.exists())
                file.createNewFile();
            fileIO = new RandomAccessFile("ContactsList", "rw");
        }catch (Exception e){}

    }

    //Reads the file for and creates a list of contact objects
    //WRITTEN BY WILLIAM HOOD
    public ArrayList<ContactClass> getContactsList(){
        //if file legnth is greatert than one then try to read form it
        try {
            fileIO.seek(0);
           if(fileIO.length()<1){
               return null;
           }
        }catch(Exception e) {        }
        //reading from the file if file has information
        while(true){
            try{
                byte[] info = new byte[80];
                fileIO.read(info);
                contactList.add(new ContactClass(new String(info)));
            } //break if end of file
            catch(Exception e){ break;}
        }
        return contactList;
    }

    //**************************************
    //Written by WILLIAM HOOD
    //writes contact to file
    //**************************************
    public void saveContact(ContactClass contact) {

        try {
            fileIO.writeBytes(contact.toString());
        }
        catch(Exception e){

        }
    }

    public void deleteContact(ArrayList<ContactClass> arraylist){
        try {
            fileIO.setLength((long) 0);
            fileIO.seek(0);
            for(int i =0; i<arraylist.size(); i++){
                fileIO.write(arraylist.get(i).toString().getBytes());
            }
        }catch (Exception e){   }
    }


}
