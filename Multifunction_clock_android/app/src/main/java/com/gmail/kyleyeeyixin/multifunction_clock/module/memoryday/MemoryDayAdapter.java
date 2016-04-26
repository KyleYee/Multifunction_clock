package com.gmail.kyleyeeyixin.multifunction_clock.module.memoryday;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.gmail.kyleyeeyixin.multifunction_clock.R;
import com.gmail.kyleyeeyixin.multifunction_clock.model.chime.Chime;
import com.gmail.kyleyeeyixin.multifunction_clock.model.memory_day.MemoryDay;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 纪念日
 * Created by yunnnn on 2016/4/13.
 */
public class MemoryDayAdapter extends RecyclerView.Adapter<MemoryDayAdapter.ViewHolder> {

    private Context mContext;
    private List<MemoryDay> mList;

    //Item点击回调
    public interface OnItemClickListener {
        public void onItemClick(View v, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //Send点击回调
    public interface OnSendListener {
        public void onSendClick(int position, boolean isOpen, Switch v);

    }

    private OnSendListener onSendListener;

    public void setOnSendListener(OnSendListener onSendListener) {
        this.onSendListener = onSendListener;
    }

    public MemoryDayAdapter(Context context, List<MemoryDay> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.memory_day_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            if (holder == null) {
                return;
            }
            holder.content.setText(mList.get(position).getContent().toString());
            holder.date.setText(mList.get(position).getYear() + "年" +
                    mList.get(position).getMouth() + "月" +
                    mList.get(position).getDay() + "日");

            //点击发送事件
            holder.send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSendListener.onSendClick(position, holder.send.isChecked(), holder.send);
                    setIsOpen(holder, position);
                }
            });
            //改变状态
            holder.send.setChecked(mList.get(position).isType());
            setIsOpen(holder, position);
        }
    }

    private void setIsOpen(ViewHolder holder, int position) {
        if (holder.send.isChecked()) {
            mList.get(position).setType(true);
        } else {
            mList.get(position).setType(false);
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.content)
        TextView content;
        @Bind(R.id.date)
        TextView date;
        @Bind(R.id.send)
        Switch send;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }
}
