package com.example.vendor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendor.R;
import com.example.vendor.models.Invoice;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VendorInvoiceAdapter extends RecyclerView.Adapter<VendorInvoiceAdapter.VendorInvoiceViewHolder> implements Filterable {
   private List<Invoice> mInvoices;
   private List<Invoice>mInvoiceList;
   private Context mContext;
   int row_index = -1;

   ItemClickListener itemClickListener;

   public VendorInvoiceAdapter(){}

   public VendorInvoiceAdapter(List<Invoice> mInvoices,Context mContext){
       this.mInvoices=mInvoices;
       this.mContext=mContext;
       mInvoiceList= new ArrayList<>(mInvoices);
   }


    @Override
    public Filter getFilter() {
        return testInvoice;
    }


    // Ft Filter
    private Filter testInvoice = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Invoice> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length()==0){
                filteredList.addAll(mInvoiceList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Invoice invoice: mInvoiceList){
                    if(invoice.getInvoiceStatus().toString().toLowerCase().contains(filterPattern)){
                        filteredList.add(invoice);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mInvoices.clear();
            mInvoiceList.addAll((List)results.values);
            notifyDataSetChanged();

        }
    };


    @NonNull
    @Override
    public VendorInvoiceAdapter.VendorInvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoices_list, parent, false);
        VendorInvoiceViewHolder viewHolder = new VendorInvoiceViewHolder(view);
        return viewHolder;


    }









    @Override
    public void onBindViewHolder(@NonNull VendorInvoiceAdapter.VendorInvoiceViewHolder holder, int position) {
       holder.bindInvoice(mInvoices.get(position));
    }

    @Override
    public int getItemCount() {
        return mInvoices.size();
    }

  public class VendorInvoiceViewHolder extends RecyclerView.ViewHolder {
      @BindView(R.id.invoiceNo)
      TextView mInvoiceId;
      @BindView(R.id.payAmount) TextView mPayAmount;
      @BindView(R.id.invoice_date) TextView mInvoiceDate;
      @BindView(R.id.invoice_status) TextView mInvoiceStatus;
      Context mContext;
      String currency = " Amount Ksh: ";
      String dueBy = "Due by: ";
      String invoiceNumber = " Invoice Number: ";
      public VendorInvoiceViewHolder(@NonNull View itemView) {
          super(itemView);
          this.mContext = mContext;
          ButterKnife.bind(this, itemView);
      }
      public void bindInvoice(Invoice invoice) {

          if(invoice.getInvoiceStatus().toString().equals("1")){
              mInvoiceStatus.setTextColor(Color.parseColor("#ECB753"));
              mInvoiceStatus.setText(invoice.getInvoiceStatus().toString());
              mInvoiceStatus.setText("pending");
          } else if(invoice.getInvoiceStatus().toString().equals("2")){
              mInvoiceStatus.setTextColor(Color.parseColor("#0B6623"));
              mInvoiceStatus.setText(invoice.getInvoiceStatus().toString());
              mInvoiceStatus.setText("approved");
          } else if(invoice.getInvoiceStatus().toString().equals("3")){
              mInvoiceStatus.setTextColor(Color.parseColor("#FF0000"));
              mInvoiceStatus.setText(invoice.getInvoiceStatus().toString());
              mInvoiceStatus.setText("declined");
          }

          mInvoiceId.setText(invoiceNumber + invoice.getBuyerId().toString());
          mPayAmount.setText(currency + invoice.getInvoiceAmount());

          mInvoiceDate.setText(dueBy + invoice.getDueDate());
//            mInvoiceStatus.setText(invoice.getStatus());
      }

  }

  public interface ItemClickListener{
        void onClick(View view, int position);
    }
    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

}
