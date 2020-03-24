package com.example.vendor.ui.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendor.R;
import com.example.vendor.adapters.VendorInvoiceAdapter;
import com.example.vendor.models.Invoice;
import com.example.vendor.network.FactsAfricaApi;
import com.example.vendor.network.FactsAfricaClient;
import com.example.vendor.ui.MakeInvoiceActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private String token;
    private SharedPreferences mPreference;
    @BindView(R.id.homeVendor) RecyclerView mInvoicesRecycler; //recycler view
    @BindView(R.id.count_rows)
    TextView mCountRows;
    private List<Invoice> invoices;  //invoice list
    VendorInvoiceAdapter adapter;
    private List<Invoice> addedInvoice = new ArrayList<>();
    private Invoice invoice;

    private HomeViewModel homeViewModel;
    private View rootView;
    FloatingActionButton floatingActionButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        //View root = inflater.inflate(R.layout.fragment_home, container, false);
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        floatingActionButton = rootView.findViewById(R.id.fab_invoice);
        mPreference = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        token = mPreference.getString("token", "");


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MakeInvoiceActivity.class);
                startActivity(intent);
            }
        });

//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        ButterKnife.bind(this, rootView);
        getAllInvoices();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mInvoicesRecycler);

        return rootView;
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    invoice = invoices.get(position);
                    addedInvoice.add(invoice);
                    invoices.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(),"Item added to batch",Toast.LENGTH_LONG).show();

                    break;

            }

        }
    };



    //Get Invoices
    public void getAllInvoices() {

        FactsAfricaApi service = FactsAfricaClient.getClient().create(FactsAfricaApi.class);
        Call<List<Invoice>> call = service.getAllInvoices(token);
        Log.v("URL", call.request().url().toString());
        call.enqueue(new Callback<List<Invoice>>() {
            @Override
            public void onResponse(Call<List<Invoice>> call, Response<List<Invoice>> response) {
                invoices = response.body();
//                Log.d(TAG, "onResponse: "+invoices.get(0).getInvoiceAmount());
                adapter = new VendorInvoiceAdapter(invoices, rootView.getContext());
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
                mInvoicesRecycler.setLayoutManager(layoutManager);
                mInvoicesRecycler.setHasFixedSize(true);
                mInvoicesRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Invoice>> call, Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}