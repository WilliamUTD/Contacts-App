package com.wjh160030.contactsapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//*****************************************************************************************
//The recycler view class which will take a list and inflate a view with the contents of the list
//Written by William Hood and Albin Mathew
// wjh160030, ajm161130
//**********************************************************************************
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.myViewHolder> {

    ArrayList<ContactClass> contacts;
    Context context;

    //**************************************************
    //default constructor. setting up context and list
    //Written by WIlliam Hood
    //**************************************************
    ContactAdapter(Context c, ArrayList<ContactClass> al){
        context = c;
        contacts = al;

    }

    //*******************************************************
    //The view holder class is needed to link the xml layout
    //and the needed fields conected here
    //Written by WIlliam Hood
    //*******************************************************
    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView first_name;
        TextView last_name;
        LinearLayout parentview;

        myViewHolder(View view){
            super(view);
            first_name = view.findViewById(R.id.first_name_view);
            last_name = view.findViewById(R.id.last_name_view);
            parentview = view.findViewById(R.id.parentview);
        }

    }

    //*************************************************
    //This method cretes the view holder for each row of the data
    //Written by WIlliam Hood
    //*************************************************
    public ContactAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //getInflater layout
        Context c = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(c);

        //inflate the custom layout
        View contactView = inflater.inflate(R.layout.contactsrecycler, parent,false);

        //return holder instance
        myViewHolder vh = new myViewHolder(contactView);
        return vh;

    }

    //*************************************************
    //binds values to the text view in list
    //Written by WIlliam Hood
    //*************************************************
    public void onBindViewHolder(@NonNull myViewHolder holder, final int position){
        final ContactClass contact = contacts.get(position);
        String lastname = contact.getLastName().trim()+",";
        holder.first_name.setText(contact.getFirst_name().trim());
        holder.last_name.setText(lastname.trim());
        //onclick listener
        final int pos = position;
        holder.parentview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ((MainActivity)context).launchSecondActivity(contact, position);

            }

        });


    }

    //**********************************************************
    //This is a necessary method when implementing recyclerview
    //Written by WIlliam Hood
    //***********************************************************
    public int getItemCount() {
        return contacts.size();
    }


}

