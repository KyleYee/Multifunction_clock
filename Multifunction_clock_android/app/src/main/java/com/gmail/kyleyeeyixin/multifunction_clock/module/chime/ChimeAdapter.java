package com.gmail.kyleyeeyixin.multifunction_clock.module.chime;

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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 整点报时
 * Created by yunnnn on 2016/4/13.
 */
public class ChimeAdapter extends RecyclerView.Adapter<ChimeAdapter.ViewHolder> {

    private Context mContext;
    private List<Chime> mList;

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
        public void onSendClick(int position,boolean isOpen);
    }

    private OnSendListener onSendListener;

    public void setOnSendListener(OnSendListener onSendListener) {
        this.onSendListener = onSendListener;
    }

    public ChimeAdapter(Context context, List<Chime> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.alarm_clock_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            if (mList != null && mList.size() != 0) {
                //设置小时
                final int hour = mList.get(position).getHour();
                if (hour < 10) {
                    holder.hour.setText("0" + hour);
                } else {
                    holder.hour.setText(hour + "");
                }
                //设置分钟
                holder.minute.setText("00");
                //点击发送事件
                holder.send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSendListener.onSendClick(position, holder.send.isChecked());
                    }
                });
                //设置打开或者关闭
                setIsOpen(holder,position);
            }
        }
    }

    private void setIsOpen(ViewHolder holder, int position) {
        if (holder.send.isChecked()) {
            mList.get(position).setType(true);
            holder.isOpen.setText("打开");
        } else {
            mList.get(position).setType(false);
            holder.isOpen.setText("关闭");
        }
    }


    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.hour)
        TextView hour;
        @Bind(R.id.minute)
        TextView minute;
        @Bind(R.id.send)
        Switch send;
        @Bind(R.id.content)
        TextView content;
        @Bind(R.id.isOpen)
        TextView isOpen;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            content.setText("整点报时");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }
}
