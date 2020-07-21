package com.hemat.rpl.activities;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hemat.rpl.R;
import com.hemat.rpl.transactionDb.AppDatabase;
import com.hemat.rpl.transactionDb.AppExecutors;
import com.hemat.rpl.transactionDb.TransactionEntry;
import com.hemat.rpl.transactionDb.TransactionViewModel;
import com.hemat.rpl.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddExpenseActivity extends AppCompatActivity {


    TextInputEditText amountTextInputEditText;
    TextInputEditText descriptionTextInputEditText;
    TextInputLayout amountTextInputLayout;
    TextInputLayout descriptionTextInputLayout;
    TextView dateTextView;
    LinearLayout dateLinearLayout;
    Spinner categorySpinner;
    ArrayList<String> categories;
    Calendar myCalendar;

    String description;
    Date dateOfExpense;

    private DatePickerDialog datePickerDialog;
    private static AppDatabase appDatabase;


    private static final String LOG_TAG = AddExpenseActivity.class.getSimpleName();



    int amount;
    String categoryOfExpense;
    String categoryOfTransaction;


    String intentFrom;

    TransactionViewModel transactionViewModel;

    int transactionid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        amountTextInputEditText = findViewById(R.id.amountTextInputEditText);
        descriptionTextInputEditText = findViewById(R.id.descriptionTextInputEditText);
        amountTextInputLayout = findViewById(R.id.amountTextInputLayout);
        descriptionTextInputLayout = findViewById(R.id.descriptionTextInputLayout);
        dateTextView = findViewById(R.id.dateTextView);
        dateLinearLayout = findViewById(R.id.dateLinerLayout);
        categorySpinner = findViewById(R.id.categorySpinner);

        appDatabase = AppDatabase.getInstance(getApplicationContext());


        transactionViewModel = ViewModelProviders.of(this)
                .get(TransactionViewModel.class);

        categories = new ArrayList<>();

        myCalendar = Calendar.getInstance();
        setDateToTextView();



        Intent intent = getIntent();

        intentFrom = intent.getStringExtra("from");

        if (intentFrom.equals(Constants.addIncomeString)) {
            categoryOfTransaction = Constants.incomeCategory;
            setTitle("Tambah Pemasukkan");
            categories.add("pemasukkan");
            categorySpinner.setClickable(false);
            categorySpinner.setEnabled(false);
            categorySpinner.setAdapter(new ArrayAdapter<>(AddExpenseActivity.this, android.R.layout.simple_list_item_1, categories));

        } else if (intentFrom.equals(Constants.addExpenseString)) {
            categoryOfTransaction = Constants.expenseCategory;
            setTitle("Tambah Pengeluaran");
            categories.add("Makanan & Minuman");
            categories.add("Liburan");
            categories.add("Pakaian");
            categories.add("Film");
            categories.add("Kesehatan");
            categories.add("Kebutuhan Pokok");
            categories.add("Lainnya");
            categorySpinner.setAdapter(new ArrayAdapter<>(AddExpenseActivity.this,
                    android.R.layout.simple_list_item_1, categories));

        } else if (intentFrom.equals(Constants.editIncomeString)) {
            setTitle("Edit Pemasukkan");

            amountTextInputEditText.setText(String.valueOf(intent.getIntExtra("jumlah", 0)));
            amountTextInputEditText.setSelection(amountTextInputEditText.getText().length());
            descriptionTextInputEditText.setText(intent.getStringExtra("deskripsi"));
            descriptionTextInputEditText.setSelection(descriptionTextInputEditText.getText().length());
            transactionid=intent.getIntExtra("id",-1);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date date = sdf.parse(intent.getStringExtra("tanggal"));
                myCalendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateTextView.setText(intent.getStringExtra("tanggal"));

            categoryOfTransaction = Constants.incomeCategory;
            categories.add("Pemasukkan");
            categorySpinner.setClickable(false);
            categorySpinner.setEnabled(false);
            categorySpinner.setAdapter(new ArrayAdapter<>(AddExpenseActivity.this, android.R.layout.simple_list_item_1, categories));

        } else if (intentFrom.equals(Constants.editExpenseString)) {
            categoryOfTransaction = Constants.expenseCategory;
            setTitle("Edit Pengeluaran");
            amountTextInputEditText.setText(String.valueOf(intent.getIntExtra("jumlah", 0)));
            amountTextInputEditText.setSelection(amountTextInputEditText.getText().length());
            descriptionTextInputEditText.setText(intent.getStringExtra("deskripsi"));
            descriptionTextInputEditText.setSelection(descriptionTextInputEditText.getText().length());
            dateTextView.setText(intent.getStringExtra("tanggal"));
            transactionid=intent.getIntExtra("id",-1);


            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date date = sdf.parse(intent.getStringExtra("tanggal"));
                myCalendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            categories.add("Makanan & Minuman");
            categories.add("Liburan");
            categories.add("Pakaian");
            categories.add("Film");
            categories.add("Kesehatan");
            categories.add("Kebutuhan Pokok");
            categories.add("Lainnya");
            categorySpinner.setAdapter(new ArrayAdapter<>(AddExpenseActivity.this, android.R.layout.simple_list_item_1, categories));
            categorySpinner.setSelection(categories.indexOf(intent.getStringExtra("kategori")));
        }

        dateLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

    }


    public void showDatePicker() {


        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setDateToTextView();
            }
        };

        DatePickerDialog datePickerDialog=new DatePickerDialog(AddExpenseActivity.this,dateSetListener,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();

    }


    public void setDateToTextView() {
        Date date = myCalendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dateToBeSet = sdf.format(date);
        dateTextView.setText(dateToBeSet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_expense_activty_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.saveButton:

                if (amountTextInputEditText.getText().toString().isEmpty()
                        || descriptionTextInputEditText.getText().toString().isEmpty()) {

                    if (amountTextInputEditText.getText().toString().isEmpty())
                        amountTextInputEditText.setError("jumlah tidak boleh kosong");
                    if (descriptionTextInputEditText.getText().toString().isEmpty())
                        descriptionTextInputEditText.setError("mohon tulis beberapa deskripsi");

                } else {
                    amount = Integer.parseInt(amountTextInputEditText.getText().toString());
                    description = descriptionTextInputEditText.getText().toString();
                    dateOfExpense = myCalendar.getTime();

                    if (intentFrom.equals(Constants.addIncomeString)
                            || intentFrom.equals(Constants.editIncomeString))
                        categoryOfExpense = "Pemasukkan";
                    else
                        categoryOfExpense = categories.get(categorySpinner.getSelectedItemPosition());

                    final TransactionEntry mTransactionEntry = new TransactionEntry(amount,
                            categoryOfExpense,
                            description,
                            dateOfExpense,
                            categoryOfTransaction
                    );

                    if(intentFrom.equals(Constants.addIncomeString)||intentFrom.equals(Constants.addExpenseString)) {


                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                appDatabase.transactionDao().insertExpense(mTransactionEntry);
                            }
                        });

                        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        Snackbar.make(getCurrentFocus(),"Transaksi Berhasil Ditambahkan",Snackbar.LENGTH_LONG).show();
                    }
                    else{
                        mTransactionEntry.setId(transactionid);
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                appDatabase.transactionDao().updateExpenseDetails(mTransactionEntry);

                            }
                        });

                        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        Snackbar.make(getCurrentFocus(),"Transaksi Berhasil Diubah",Snackbar.LENGTH_LONG).show();

                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);

                }
                break;
        }
        return true;


    }


}

