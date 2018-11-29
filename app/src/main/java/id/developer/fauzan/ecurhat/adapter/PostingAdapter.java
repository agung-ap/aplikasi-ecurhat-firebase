package id.developer.fauzan.ecurhat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.developer.fauzan.ecurhat.R;
import id.developer.fauzan.ecurhat.model.Posting;

public class PostingAdapter extends RecyclerView.Adapter<PostingAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Posting> postingList;
    private DataListener listener;

    public PostingAdapter(Context context, DataListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.posting, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.dari.setText("Dari : " + postingList.get(i).getDari());
        viewHolder.untuk.setText("Untuk : " + postingList.get(i).getUntuk());
        viewHolder.pesan.setText("Pesan : " + postingList.get(i).getPesan());
    }

    @Override
    public int getItemCount() {
        if (null == postingList) return 0;
        return postingList.size();
    }

    public void setPostingData(ArrayList<Posting> data) {
        postingList = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.dari_list)
        TextView dari;
        @BindView(R.id.untuk_list)
        TextView untuk;
        @BindView(R.id.pesan_list)
        TextView pesan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //set item to click
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            listener.onClick(postingList.get(position));
        }
    }

    //Membuat Interface
    public interface DataListener {
        void onClick(Posting dataPosition);
    }
}
