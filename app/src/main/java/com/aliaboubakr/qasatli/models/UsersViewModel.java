package com.aliaboubakr.qasatli.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class UsersViewModel extends ViewModel {

    MutableLiveData<ArrayList<UsersModel>> usersArrayListMutableLiveData;

public void initat(){

if (usersArrayListMutableLiveData!=null){
    return; }


usersArrayListMutableLiveData= UsersRepo.getInstance().getUsers();
}

public LiveData<ArrayList<UsersModel>>getUsersArrayListMutableLiveData(){

    return usersArrayListMutableLiveData;
}

}
