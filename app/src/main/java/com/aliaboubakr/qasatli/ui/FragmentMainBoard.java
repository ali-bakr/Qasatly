package com.aliaboubakr.qasatli.ui;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.aliaboubakr.qasatli.R;
import com.aliaboubakr.qasatli.adapters.UsersAdapter;
import com.aliaboubakr.qasatli.models.UsersModel;
import com.aliaboubakr.qasatli.models.UsersViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.john.waveview.WaveView;

import java.security.Permission;
import java.util.ArrayList;

import me.itangqi.waveloadingview.WaveLoadingView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMainBoard extends Fragment {

    private static final int PERMISSION_ALL = 1;
    RecyclerView userRecyvlerView;
    UsersAdapter usersAdapter;
    UsersViewModel usersViewModel;
    FloatingActionButton fab;

    public FragmentMainBoard() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_board, container, false);
        super.onActivityCreated(savedInstanceState);


        setHasOptionsMenu(true);
        userRecyvlerView = v.findViewById(R.id.users_rv);
        userRecyvlerView.setHasFixedSize(true);
        userRecyvlerView.setLayoutManager(new LinearLayoutManager(getContext()));

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_slide_right);
        userRecyvlerView.setLayoutAnimation(controller);
        userRecyvlerView.scheduleLayoutAnimation();
        usersViewModel = new ViewModelProvider(getActivity()).get(UsersViewModel.class);
        usersViewModel.initat();
        usersViewModel.getUsersArrayListMutableLiveData().observe(this, new Observer<ArrayList<UsersModel>>() {
            @Override
            public void onChanged(ArrayList<UsersModel> usersModels) {
                usersAdapter = new UsersAdapter(getContext(), usersViewModel.getUsersArrayListMutableLiveData().getValue());
                userRecyvlerView.scheduleLayoutAnimation();
                usersAdapter.notifyDataSetChanged();

                userRecyvlerView.setAdapter(usersAdapter);

            }
        });


        fab = v.findViewById(R.id.add_customer_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddUserFragment();
            }
        });
        return v;
    }


    //open fragment add user
    private void openAddUserFragment() {
        FragmentAddUsers fragmentAddUsers = new FragmentAddUsers();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainboard_container, fragmentAddUsers);
        transaction.addToBackStack(null);
        transaction.commit();


    }

    // menue onCreateOptionsMenu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_option, menu);
      MenuItem menuItem=menu.findItem(R.id.search_itme);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

             usersAdapter.getFilter().filter(newText);

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


    // menue  onOptionsItemSelected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout_item) {
            signOut();

        }
        else if (item.getItemId() == R.id.search_itme) {


        }
        return super.onOptionsItemSelected(item);
    }


    //fore sign out firebase account
    private void signOut() {

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }




    }






