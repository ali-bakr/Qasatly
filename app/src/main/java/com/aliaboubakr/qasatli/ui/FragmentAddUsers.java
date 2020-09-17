package com.aliaboubakr.qasatli.ui;


import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CancellationSignal;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.aliaboubakr.qasatli.R;
import com.aliaboubakr.qasatli.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAddUsers extends Fragment {
  private   EditText nameET, phoneET, adressET, wholeCashET, cashPaidET, monthlyCashET, itemET;
private     DatabaseReference databaseReference;
FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
String userNumber=user.getPhoneNumber();
    private Executor executor;
    private androidx.biometric.BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;
    public FragmentAddUsers() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_users, container, false);

        nameET = v.findViewById(R.id.add_user_name_et);
        phoneET = v.findViewById(R.id.add_user_phone_et);
        adressET = v.findViewById(R.id.add_user_address_et);
        wholeCashET = v.findViewById(R.id.add_user_whole_cash_et);
        cashPaidET = v.findViewById(R.id.add_user_cashـpaid_et);
       // cashRemainigET = v.findViewById(R.id.add_user_cash_remaining_et);
        monthlyCashET = v.findViewById(R.id.add_user_monthly_cash_et);
        itemET = v.findViewById(R.id.add_user_item_et);




        //refrence
        databaseReference = FirebaseDatabase.getInstance().getReference().child("clients");

        v.findViewById(R.id.add_user_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               setBiometricPrompt();
               // authunticatUserFingerPrint();
            }
        });

        return v;

    }

    private void addUser() {
        if (nameET.getText().toString().equals(""))
            nameET.setError("نص فارغ ");
        else if (phoneET.getText().toString().equals(""))
            phoneET.setError("نص فارغ ");
        else if (adressET.getText().toString().equals(""))
            adressET.setError("نص فارغ ");
        else if (itemET.getText().toString().equals(""))
            itemET.setError("نص فارغ ");
        else if (wholeCashET.getText().toString().equals(""))
            wholeCashET.setError("نص فارغ ");
        //else if (cashRemainigET.getText().toString().equals(""))
         //   cashRemainigET.setError("نص فارغ ");
        else if (cashPaidET.getText().toString().equals(""))
            cashPaidET.setError("نص فارغ ");
        else if (monthlyCashET.getText().toString().equals(""))
            monthlyCashET.setError("نص فارغ ");
        else {

            int cashremain=Integer.parseInt(wholeCashET.getText().toString())-Integer.parseInt(cashPaidET.getText().toString());

            UsersModel users = new UsersModel();
            users.setName(nameET.getText().toString().trim());
            users.setPhone(phoneET.getText().toString().trim());
            users.setAdress(adressET.getText().toString().trim());
            users.setItem(itemET.getText().toString().trim());
            users.setWholeCash(wholeCashET.getText().toString().trim());
            users.setCashPaid(cashPaidET.getText().toString().trim());
            users.setCashRemainig(String.valueOf(cashremain));
            users.setMonthlyCash(monthlyCashET.getText().toString().trim());
            users.setLastPaidCash(cashPaidET.getText().toString().trim());
            Calendar calendar=Calendar.getInstance();
           // calendar.
            users.setLastPaidData(DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime()));
            databaseReference.child(userNumber).child(users.getName()).setValue(users);
            //databaseReference.child(users.getName()).setValue(users);

           eventListener();

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //databaseReference = FirebaseDatabase.getInstance().getReference().child("clients");
          //  databaseReference.child(userNumber).child(users.getName()).setValue(users);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////



        }

    }




    void  setBiometricPrompt(){
        executor = ContextCompat.getMainExecutor(getActivity());
        biometricPrompt = new androidx.biometric.BiometricPrompt(getActivity(),
                executor, new androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getActivity(),
                        "التحقق مرفوض لن يتم السماح لك بتعديل البيانات   " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull androidx.biometric.BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getActivity(),
                        "تم التعرف علي البصمة", Toast.LENGTH_SHORT).show();
                addUser();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getActivity(), "حاول مرا اخري  ",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                .setTitle("تاكيد الهوية ")
                .setSubtitle("تاكيد الهوية باستخدام بصمة الاصبع ")
                .setNegativeButtonText("Use account password")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        biometricPrompt.authenticate(promptInfo);

    }
    void trashFeileds(){

        nameET.setText("");
        phoneET.setText("");
        adressET.setText("");
        itemET.setText("");
        wholeCashET.setText("");
        cashPaidET.setText("");
        monthlyCashET.setText("");








    }

    void eventListener(){

        databaseReference.addValueEventListener(new ValueEventListener() { //attach listener

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //something changed!
               Toast.makeText(getActivity(), "تم الاضافة يرجي التحقق من العميل ف الصفحة الرئيسية ", Toast.LENGTH_LONG).show();
                trashFeileds();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) { //update UI here if error occurred.
                Toast.makeText(getActivity(), "لم يتم الاضافة ", Toast.LENGTH_LONG).show();

            }
        });
    }

}
