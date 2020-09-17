package com.aliaboubakr.qasatli.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.media.MediaDrm;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

public class FragmentGetUserInfo extends Fragment {
private TextView mNnameTV, mPhoneTV, mAdressTV, mWholeCashTV, mCashPaidTV, mCashRemainigTV, mMonthlyCashTV, mItemTV,mLastPasiDateTV;
     EditText mCashAddET;
    private     DatabaseReference databaseReference;
    private Executor executor;
    private androidx.biometric.BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;
    Bundle bundle;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    String userNumber=user.getPhoneNumber();
    UsersModel users = new UsersModel();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         super.onCreateView(inflater, container, savedInstanceState);
   View v=inflater.inflate(R.layout.fragment_get_user_info,container,false);


        mNnameTV =v.findViewById(R.id.get_user_info_name_tv);
        mPhoneTV =v.findViewById(R.id.get_user_info_phone_tv);
        mAdressTV=v.findViewById(R.id.get_user_info_address_tv);
        mWholeCashTV=v.findViewById(R.id.get_user_info_whole_cash_tv);
        mCashPaidTV=v.findViewById(R.id.get_user_info_cashـpaid_tv);
        mCashRemainigTV=v.findViewById(R.id.get_user_info_cash_remaining_tv);
        mMonthlyCashTV=v.findViewById(R.id.get_user_info_monthly_cash_tv);
        mItemTV=v.findViewById(R.id.get_user_info_item_tv);
        mLastPasiDateTV=v.findViewById(R.id.get_user_info_lastpaid_date_tv);



        getData();
        v.findViewById(R.id.get_user_info_go_to_add_cash_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
                 mydialog.setTitle(" تسديد مبلغ ");
               mCashAddET=new EditText(getActivity());
               mCashAddET.setInputType(InputType.TYPE_CLASS_NUMBER);
                mydialog.setView(mCashAddET);

                mydialog.setPositiveButton("تسديد ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     //   authunticatUserFingerPrint();
                   //     Toast.makeText(getActivity(), "tasded", Toast.LENGTH_SHORT).show();

                        setBiometricPrompt();
                    }
                });
                AlertDialog alertDialog = mydialog.create();
              alertDialog.show();

            }
        });



   return v;
    }

    private void getData() {

        bundle = this.getArguments();
        if (bundle != null) {
            mNnameTV .setText( "الاسم : "+bundle.getString("name"));
            mPhoneTV.setText("الهاتف : "+bundle.getString("phone"));
            mAdressTV .setText( "العنوان : "+bundle.getString("address"));
            mWholeCashTV.setText("المبلغ الكلي : "+bundle.getString("totalcash"));
            mCashPaidTV .setText( "المبلغ المدفوع : "+bundle.getString("paiedcash"));
            mCashRemainigTV.setText("المبلغ المتبقي : "+bundle.getString("remainingcash"));
            mMonthlyCashTV .setText( "القسط الشهري : "+bundle.getString("monthelycash"));
            mItemTV.setText("المشتريات : "+bundle.getString("item"));
            mLastPasiDateTV.setText(    "تاريخ اخر سداد :  "+bundle.getString("lastdate")+"\n"+" اخر مبلغ  "+bundle.getString("lastcash"));

        }
    }


    void  updateData(){
//values before update
        String paidNow,remainig;
        paidNow=mCashAddET.getText().toString();
        remainig=bundle.getString("remainingcash");

        int paidUpdated,ramainingUpdated ;

        //paidcash+paidnow to get paid number updated
        paidUpdated=Integer.parseInt(bundle.getString("paiedcash"))+Integer.parseInt(mCashAddET.getText().toString());
       // remainig- paid now to get updated number to use it
          ramainingUpdated=(Integer.parseInt(remainig))-(Integer.parseInt(paidNow));

        databaseReference= FirebaseDatabase.getInstance().getReference().child("clients");
        bundle = this.getArguments();
        if (bundle != null) {
        if (mCashAddET.getText().toString().equals(""))
            mCashAddET.setError("نص فارغ ");
        else {


            users.setName(bundle.getString("name"));
            users.setPhone(bundle.getString("phone"));
            users.setAdress(bundle.getString("address"));
            users.setItem(bundle.getString("item"));
            users.setWholeCash(bundle.getString("totalcash"));
            users.setCashPaid(String.valueOf(paidUpdated));
            users.setLastPaidCash(paidNow);
            users.setCashRemainig(String.valueOf(ramainingUpdated));
            users.setMonthlyCash(bundle.getString("monthelycash"));
            Calendar calendar = Calendar.getInstance();
            users.setLastPaidData(DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime()));
           // databaseReference.child(users.getName()).setValue(users);
            databaseReference.child(userNumber).child(users.getName()).setValue(users);

            eventListener();



            try {

               String number = users.getPhone();  // The number on which you want to send SMS
            String message="عزيزي العميل تم سداد مبلغ "+paidNow+" الان المبلغ الكلي المدفوع هو "+paidUpdated+" اما المبلغ المتبقي هو "+ramainingUpdated+"من اصل اصل المبلغ الكلي "+users.getWholeCash()+"وذلك في تاريخ "+users.getLastPaidData();

                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address",number);
                smsIntent.putExtra("sms_body",message);
                smsIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(smsIntent);
/*

*/

            } catch (Exception e) {
                Toast.makeText(getActivity(),
                        "SMS faild, please try again later!",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }
        }
    }



    void eventListener(){

        databaseReference.addValueEventListener(new ValueEventListener() { //attach listener

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //something changed!

               // getData();
              //  mCashAddET.setText("");
                Toast.makeText(getActivity(), "تم تحديث البيانات ", Toast.LENGTH_LONG).show();


                goToMain();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { //update UI here if error occurred.
                Toast.makeText(getActivity(), "لم يتم الاضافة ", Toast.LENGTH_LONG).show();

            }
        });
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

                users.setLastPaidCash(mCashPaidTV.getText().toString());
                updateData();
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
    void goToMain(){


        FragmentMainBoard fragmentMainBoard=new FragmentMainBoard();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainboard_container,fragmentMainBoard);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}
