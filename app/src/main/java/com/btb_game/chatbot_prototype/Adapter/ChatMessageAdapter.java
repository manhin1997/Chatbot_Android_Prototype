package com.btb_game.chatbot_prototype.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.btb_game.chatbot_prototype.Model.ChatMessage;
import com.btb_game.chatbot_prototype.R;

import java.util.List;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage>{

    private static final int MY_MESSAGE = 0;
    private static final int BOT_MESSAGE = 1;

    public ChatMessageAdapter(Context context, List<ChatMessage> data) {
        super(context, R.layout.user_query_layout);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);

        if (item.isMine() && !item.isImage()){
            return MY_MESSAGE;
        }else{
            return BOT_MESSAGE;
        }
    }
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        int viewType = getItemViewType(position);

        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.user_query_layout, parent, false);
            TextView textView = convertView.findViewById(R.id.text);
            textView.setText(getItem(position).getContent());
        }
        else if (viewType == BOT_MESSAGE) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.bots_reply_layout, parent, false);
            TextView textView = convertView.findViewById(R.id.text);
            textView.setText(getItem(position).getContent());
        }
        convertView.findViewById(R.id.chatMessageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Clicked..." ,Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
