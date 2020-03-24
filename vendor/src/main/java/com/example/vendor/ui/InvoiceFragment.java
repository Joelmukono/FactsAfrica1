package com.example.vendor.ui;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendor.R;
import com.example.vendor.db.InvoiceContract;
import com.example.vendor.db.InvoiceDbHelper;
import com.example.vendor.models.Invoice;
import com.example.vendor.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvoiceFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "InvoiceFragment";
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private String token;
    private SharedPreferences mPreference;

    //BindViews
    @BindView(R.id.date_today)
    TextView mPickDate;
    @BindView(R.id.business_name_details)
    TextView mBusinessName;
    @BindView(R.id.add_client)
    TextView mAddClient;
    @BindView(R.id.item_one)
    TextView mItemOne;
    @BindView(R.id.add_photo_to_invoice)
    TextView mAddPhotoToInvoice;
    @BindView(R.id.invoice_photo)
    ImageView mInvoicePhoto;
    @BindView(R.id.wrap)
    FloatingActionButton mSubmitInvoice;
    @BindView(R.id.subtotal_amount)
            TextView mSubtotalAmount;
    @BindView(R.id.total_amount_to_be_paid)
            TextView mTotalAmountToBePaid;
    @BindView(R.id.invoice_number)
            TextView mInvoiceNumber;
    @BindView(R.id.item_price)
            TextView mItemPrice;
    @BindView(R.id.item_quantity)
            TextView mQuantity;
    @BindView(R.id.item_amount)
            TextView mAmount;
    ConstraintLayout constraintLayout;



    ScrollView scrollView;
    private Invoice mInvoice;
    private String title;
    private int page;
    private View rootView;
    private String dirpath;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    InvoiceDbHelper dbHelper;

    //Calender
    private Calendar myCal = Calendar.getInstance();

    private int year = myCal.get(Calendar.YEAR);
    private int month = myCal.get(Calendar.MONTH);
    private int day = myCal.get(Calendar.DAY_OF_WEEK);


    public InvoiceFragment() {
        // Required empty public constructor
    }


    public static InvoiceFragment newInstance(int page, String title) {
        InvoiceFragment invoiceFragment = new InvoiceFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        invoiceFragment.setArguments(args);
        return invoiceFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

        mDateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;

            String date = month + "/" + day + "/" + year;


            mPickDate.setText(date);


        };





    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_invoice, container, false);
        dbHelper = new InvoiceDbHelper(rootView.getContext());
//        token = mPreference.getString("token", "");
        scrollView = rootView.findViewById(R.id.scroll_view);
        constraintLayout = rootView.findViewById(R.id.invoice_constraint_layout);
        ButterKnife.bind(this, rootView);

        //Log.d(TAG, "onCreateView: " + mBusinessName.getText().toString().trim());

        mPickDate.setOnClickListener(this);
        mAddClient.setOnClickListener(this);
        mAddPhotoToInvoice.setOnClickListener(this);
        mSubmitInvoice.setOnClickListener(this);
        mBusinessName.setOnClickListener(this);
//        displayDatabaseInfo();

        return rootView;
    }




    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View v) {
        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        if (v == mPickDate) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
        if (v == mAddPhotoToInvoice) {
            Log.d(TAG, "onClick: Add Photo");
            startGallery();
        } if(v==mSubmitInvoice){
            takeScreenShot();
        } if(v==mBusinessName){
            openBuyerFragment();
            addInvoiceToApi();
        }

    }

    private void addInvoiceToApi() {
        //User user = new User(1,2,"","",null, token,"","");
        //Invoice invoice = new Invoice(1, user.getId(),)
    }

    private void openBuyerFragment() {

    }

    private void takeScreenShot() {
        File myFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "invoice.pdf");
        try {
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(constraintLayout.getWidth(),constraintLayout.getHeight(), 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            canvas.drawPaint(paint);
            scrollView.draw(canvas);
            pdfDocument.finishPage(page);


            FileOutputStream fileOutputStream = new FileOutputStream(myFile);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            pdfDocument.writeTo(fileOutputStream);
            pdfDocument.close();
            writer.close();
            fileOutputStream.close();
            Toast.makeText(getContext(), "File Saved", Toast.LENGTH_SHORT).show();

        } catch (Exception ex){
            Toast.makeText(getContext(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }



        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("application/pdf");
        email.putExtra(Intent.EXTRA_EMAIL, "receiver_email_address");
        email.putExtra(Intent.EXTRA_SUBJECT, "subject");
        email.putExtra(Intent.EXTRA_TEXT, "email body");
        Uri uri1 = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "invoice.pdf"));
        email.putExtra(Intent.EXTRA_STREAM, uri1);
        email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(email);

    }


    private void startGallery() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mInvoicePhoto.setImageBitmap(imageBitmap);
        }


    }

    private void displayDatabaseInfo(){

        SQLiteDatabase db = dbHelper.getReadableDatabase();


//        String [] projection = {InvoiceContract.AddressEntry.COLUMN_BUSINESS_NAME,
//                InvoiceContract.AddressEntry._ID,
//                InvoiceContract.AddressEntry.COLUMN_PHONE_NUMBER,
//                InvoiceContract.AddressEntry.COLUMN_EMAIL,
//                InvoiceContract.AddressEntry.COLUMN_ADDRESS
//
//        };
        String [] projection1 = {InvoiceContract.ItemsEntry._ID,
                InvoiceContract.ItemsEntry.COLUMN_ITEM_NAME,
                InvoiceContract.ItemsEntry.COLUMN_QUANTITY,
                InvoiceContract.ItemsEntry.COLUMN_AMOUNT,
                InvoiceContract.ItemsEntry.COLUMN_MULTIPLIED_TOTAL,
                InvoiceContract.ItemsEntry.COLUMN_SUB_TOTAL,
                InvoiceContract.ItemsEntry.COLUMN_NET_TOTAL,
                InvoiceContract.ItemsEntry.COLUMN_STATUS
        };
        //Cursor cursor = db.query(InvoiceContract.AddressEntry.TABLE_NAME, projection, null, null, null, null, null );
        Cursor cursor1 = db.query(InvoiceContract.ItemsEntry.TABLE_NAME, projection1, null, null, null, null, null );

//        int idColumnIndex = cursor.getColumnIndex(InvoiceContract.AddressEntry._ID);
//        int nameColumnIndex = cursor.getColumnIndex(InvoiceContract.AddressEntry.COLUMN_BUSINESS_NAME);
//        int phoneColumnIndex = cursor.getColumnIndex(InvoiceContract.AddressEntry.COLUMN_PHONE_NUMBER);
//        int emailColumnIndex = cursor.getColumnIndex(InvoiceContract.AddressEntry.COLUMN_EMAIL);
//        int addressColumnIndex = cursor.getColumnIndex(InvoiceContract.AddressEntry.COLUMN_ADDRESS);
        int idColumnIndex = cursor1.getColumnIndex(InvoiceContract.ItemsEntry._ID);
        int itemNameColumnIndex = cursor1.getColumnIndex(InvoiceContract.ItemsEntry.COLUMN_ITEM_NAME);
        int quantityColumnIndex = cursor1.getColumnIndex(InvoiceContract.ItemsEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor1.getColumnIndex(InvoiceContract.ItemsEntry.COLUMN_AMOUNT);
        int multipliedPriceColumnIndex = cursor1.getColumnIndex(InvoiceContract.ItemsEntry.COLUMN_MULTIPLIED_TOTAL);
        int subTotalPriceColumnIndex = cursor1.getColumnIndex(InvoiceContract.ItemsEntry.COLUMN_SUB_TOTAL);
        int netColumnIndex = cursor1.getColumnIndex(InvoiceContract.ItemsEntry.COLUMN_NET_TOTAL);
        int statusColumnIndex = cursor1.getColumnIndex(InvoiceContract.ItemsEntry.COLUMN_STATUS);
            //cursor.moveToLast();
            cursor1.moveToLast();

//            String currentId = cursor.getString(idColumnIndex);
//            String currentName = cursor.getString(nameColumnIndex);
//            String currentPhone = cursor.getString(phoneColumnIndex);
//            String currentEmail = cursor.getString(emailColumnIndex);
//            String currentAddress = cursor.getString(addressColumnIndex);
            String currentItemName = cursor1.getString(itemNameColumnIndex);
            String currentPrice = cursor1.getString(quantityColumnIndex);
            String currentQuantity = cursor1.getString(priceColumnIndex);
            String currentMultipliedPrice = cursor1.getString(multipliedPriceColumnIndex);
            String currentSubTotal = cursor1.getString(subTotalPriceColumnIndex);
            String currentNet = cursor1.getString(netColumnIndex);
            String currentId = cursor1.getString(idColumnIndex);
            String currentStatus = cursor1.getString(statusColumnIndex);

            try {
               // mBusinessName.append("\n"+ currentName + "\n" + currentPhone + "\n" + currentEmail + "\n" + currentAddress);
                mItemOne.setText(currentItemName);
                mItemPrice.setText(currentPrice);
                mQuantity.setText(currentQuantity);
                mAmount.setText(currentMultipliedPrice);
                //mItemOne.append("\n"+ currentItemName + "\t\t\t\t\t\t" + currentPrice + "\t\t\t\t\t\t\t\t\t" + currentQuantity + "\t\t\t\t\t\t\t\t\t\t"+currentMultipliedPrice);
                mSubtotalAmount.setText(currentSubTotal);
                mTotalAmountToBePaid.setText(currentNet);
                mInvoiceNumber.setText(currentId);

            } finally {
                //cursor.close();
                cursor1.close();
            }

    }



}
