package com.aliaboubakr.qasatli.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aliaboubakr.qasatli.R;
import com.aliaboubakr.qasatli.models.UsersModel;
import com.aliaboubakr.qasatli.ui.FragmentGetUserInfo;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collection;

import me.itangqi.waveloadingview.WaveLoadingView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> implements Filterable {

    ArrayList<UsersModel>usersModels;
    ArrayList<UsersModel>usersModelsAll;
    Context mContext;
    String num;
    Intent my_callIntent;
    Bundle bundle1 = new Bundle();

    public UsersAdapter(Context context,ArrayList<UsersModel> usersModels) {
        this.usersModels = usersModels;
        this.usersModelsAll=new ArrayList<>(usersModels);
      this.mContext=context;
    }


    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);

        return new UsersViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final UsersViewHolder holder, final int position) {

   holder.mIndex.setText(String.valueOf(position));
        holder.mName.setText("الاسم : "+usersModels.get(position).getName());
        holder.mMonthlyCash.setText("القسط الشهري : "+usersModels.get(position).getMonthlyCash());
        holder.mTotalCash.setText("المبلغ الكلي: "+usersModels.get(position).getWholeCash());
       holder.mCashRemaining.setText("المبلغ المتبقي: "+usersModels.get(position).getCashRemainig());
     holder.mLastPaidDate.setText("اخر سداد : "+usersModels.get(position).getLastPaidData()+"\n"+" اخر مبلغ  "+usersModels.get(position).getLastPaidCash());
         int percentage=((Integer.parseInt(usersModels.get(position).getCashPaid())*100)/(Integer.parseInt(usersModels.get(position).getWholeCash())));

     holder.waveLoadingView.setProgressValue(percentage);
        holder.waveLoadingView.setBottomTitle("");
        holder.waveLoadingView.setCenterTitle((String.valueOf(percentage))+"%");
        holder.waveLoadingView.setTopTitle("");

     

        holder.mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num=usersModels.get(position).getPhone();
                //     call the get method that has been handeld in OrdersRecyclerView class ;
                try {
                    my_callIntent = new Intent(Intent.ACTION_CALL);
                    my_callIntent.setData(Uri.parse("tel:"+num));
                    //here the word 'tel' is important for making a call...
                    mContext.startActivity(my_callIntent);
                } catch (ActivityNotFoundException e) {
                    Log.e("-----------------------",e.getMessage());
                }

            }
        });

        holder.mSmsMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = usersModels.get(position).getPhone();  // The number on which you want to send SMS
                String message="عزيزي العميل نود تذكيرك بموعد السداد الشهري حيث كان اخر موعد سداد  "+usersModels.get(position).getLastPaidData();

                try {

                   // The number on which you want to send SMS
                  //  mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts(message, number, null)));


                    Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address",number);
                    smsIntent.putExtra("sms_body",message);
                    smsIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(smsIntent);

                    //Uri uri = Uri.parse("tel:"+number);
                    //Intent sendIntent = new Intent(Intent.ACTION_VIEW,uri);
                    //sendIntent.putExtra("sms_body", message);
                    //sendIntent.setType("vnd.android-dir/mms-sms");
                    //mContext.startActivity(sendIntent);

                } catch (Exception e) {
                    Toast.makeText(mContext,
                            "SMS faild, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });
       // holder.mName.setText(usersModels.get(position).getName());

//


        holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        putBundel(position);
        AppCompatActivity activity = (AppCompatActivity) v.getContext();
        FragmentGetUserInfo getUserInfo = new FragmentGetUserInfo();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.mainboard_container, getUserInfo).addToBackStack(null).commit();
       getUserInfo.setArguments(bundle1);

    }
});




    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return usersModels.size();
    }

    @Override
    public Filter getFilter() {

        return filter;
    }

    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<UsersModel> filteredList=new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(usersModelsAll);

            }else {for (UsersModel nameModelSearch:usersModelsAll){
                if (nameModelSearch.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                    filteredList.add(nameModelSearch);}
            }

            }

            FilterResults filterResults=new FilterResults();
            filterResults.values=filteredList;
            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
usersModels.clear();
usersModels.addAll((Collection<? extends UsersModel>) results.values);
notifyDataSetChanged();
        }
    };

    class UsersViewHolder extends RecyclerView.ViewHolder{
        TextView mName,mMonthlyCash,mTotalCash,mCashRemaining,mLastPaidDate;
       Button mCall,mSmsMessage,mIndex;
        WaveLoadingView waveLoadingView;



        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            waveLoadingView=itemView.findViewById(R.id.item_wave_view);
            mName=itemView.findViewById(R.id.item_name_tv);
            mMonthlyCash=itemView.findViewById(R.id.item_monthly_cash_tv);
            mTotalCash=itemView.findViewById(R.id.item_total_cash_tv);
            mCashRemaining=itemView.findViewById(R.id.item_cash_remainig_tv);
            mLastPaidDate=itemView.findViewById(R.id.item_last_paid_date_tv);
            mCall=itemView.findViewById(R.id.item_call_btn);
            mSmsMessage=itemView.findViewById(R.id.item_sms_btn);
            mIndex=itemView.findViewById(R.id.index_btn);




        }
    }


    void putBundel(int position){

        bundle1.putString("name",usersModels.get(position).getName());
        bundle1.putString("phone",usersModels.get(position).getPhone());
        bundle1.putString("address",usersModels.get(position).getAdress());
        bundle1.putString("item",usersModels.get(position).getItem());
        bundle1.putString("totalcash",usersModels.get(position).getWholeCash());
        bundle1.putString("paiedcash",usersModels.get(position).getCashPaid());
        bundle1.putString("remainingcash",usersModels.get(position).getCashRemainig());
        bundle1.putString("monthelycash",usersModels.get(position).getMonthlyCash());
        bundle1.putString("lastdate",usersModels.get(position).getLastPaidData());
        bundle1.putString("lastcash",usersModels.get(position).getLastPaidCash());
    }
}
