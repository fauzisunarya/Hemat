package com.hemat.rpl.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hemat.rpl.R;
import com.hemat.rpl.activities.AddExpenseActivity;
import com.hemat.rpl.transactionDb.AppDatabase;
import com.hemat.rpl.transactionDb.TransactionEntry;
import com.hemat.rpl.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {


    Context context;
    private List<TransactionEntry> transactionEntries;
    private AppDatabase appDatabase;

    public CustomAdapter(Context context, List<TransactionEntry> transactionEntries){
        this.context=context;
        this.transactionEntries=transactionEntries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String amount;
        holder.categoryTextViewrv.setText(transactionEntries.get(position).getCategory());
        if(transactionEntries.get(position).getTransactionType().equals(Constants.incomeCategory)) {
            amount="+"+transactionEntries.get(position).getAmount();
            holder.amountTextViewrv.setText(amount);
            holder.amountTextViewrv.setTextColor(Color.parseColor("#aeea00"));
        }
        else {
            amount="-"+transactionEntries.get(position).getAmount();
            holder.amountTextViewrv.setText(amount);
            holder.amountTextViewrv.setTextColor(Color.parseColor("#ff5722"));
        }

        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        String dateToBeSet=sdf.format(transactionEntries.get(position).getDate());
        holder.dateTextViewrv.setText(dateToBeSet);
        holder.descriptionTextViewrv.setText(transactionEntries.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        if (transactionEntries == null || transactionEntries.size() == 0){
            return 0;
        } else {
            return transactionEntries.size();
        }
    }

    public List<TransactionEntry> getTransactionEntries() {
        return transactionEntries;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView categoryTextViewrv;
        TextView amountTextViewrv;
        TextView descriptionTextViewrv;
        TextView dateTextViewrv;

        public ViewHolder(View itemView) {
            super(itemView);

            categoryTextViewrv=itemView.findViewById(R.id.categoryTextViewrv);
            amountTextViewrv=itemView.findViewById(R.id.amountTextViewrv);
            descriptionTextViewrv=itemView.findViewById(R.id.descriptionTextViewrv);
            dateTextViewrv=itemView.findViewById(R.id.dateTextViewrv);

            appDatabase = AppDatabase.getInstance(context);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,AddExpenseActivity.class);

                    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
                    String date=sdf.format(transactionEntries.get(getAdapterPosition()).getDate());

                    if (transactionEntries.get(getAdapterPosition()).getTransactionType().equals(Constants.incomeCategory)) {
                        intent.putExtra("from", Constants.editIncomeString);
                        intent.putExtra("jumlah",transactionEntries.get(getAdapterPosition()).getAmount());
                        intent.putExtra("deskripsi",transactionEntries.get(getAdapterPosition()).getDescription());
                        intent.putExtra("tanggal",date);
                        intent.putExtra("id",transactionEntries.get(getAdapterPosition()).getId());
                    }
                    else {
                        intent.putExtra("from", Constants.editExpenseString);
                        intent.putExtra("jumlah",transactionEntries.get(getAdapterPosition()).getAmount());
                        intent.putExtra("deskripsi",transactionEntries.get(getAdapterPosition()).getDescription());
                        intent.putExtra("tanggal",date);
                        intent.putExtra("kategori",transactionEntries.get(getAdapterPosition()).getCategory());
                        intent.putExtra("id",transactionEntries.get(getAdapterPosition()).getId());
                    }


                    context.startActivity(intent);
                }
            });
        }
    }
}
