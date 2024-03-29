package com.btb_game.chatbot_prototype;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.btb_game.chatbot_prototype.Adapter.ChatMessageAdapter;
import com.btb_game.chatbot_prototype.Model.ChatMessage;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton btnSend;
    EditText editTextMsg;
    ImageButton btnImage;

    private Bot bot;
    public static Chat chat;
    private ChatMessageAdapter adapter;
    private int imageid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = findViewById(R.id.listView);
        btnSend = findViewById(R.id.btnSend);
        editTextMsg = findViewById(R.id.edtTextMsg);
        btnImage = findViewById(R.id.btnImage);

        adapter = new ChatMessageAdapter(this,new ArrayList<ChatMessage>());
        listView.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMsg.getText().toString();

                String response = chat.multisentenceRespond(editTextMsg.getText().toString());
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(MainActivity.this,"Please enter a query",Toast.LENGTH_SHORT).show();
                    return;
                }

                sendMessage(message);
                botsReply(response);

                editTextMsg.setText("");
                listView.setSelection(adapter.getCount() - 1);
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });


        boolean avaliable = isSDCartAvaliable();

        AssetManager assets = getResources().getAssets();
        File fileName = new File(Environment.getExternalStorageDirectory().toString() + "/henrybot/bots/henrybot");
        Log.d("File location",fileName.toString());
        Log.d("Storage available",avaliable? "True": "False");
        boolean makeFile = fileName.mkdirs();

        if(fileName.exists()){
            try {
                for (String dir: assets.list("henrybot")){
                    File subDir = new File(fileName.getPath() + "/" + dir);
                    boolean subDir_Check = subDir.mkdirs();

                    for(String file : assets.list("henrybot/" + dir)){
                        File newFile = new File(fileName.getPath() + "/" + dir + "/" + file);

                        if(newFile.exists()){
                            continue;
                        }

                        InputStream in;
                        OutputStream out;
                        in = assets.open("henrybot/" + dir + "/" + file);
                        out = new FileOutputStream(fileName.getPath() + "/" + dir + "/" + file);

                        //copy files from assets to the mobile's sd card or any secondary memory
                        copyFile(in,out);
                        in.close();
                        out.flush();
                        out.close();
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }

        }
        //get working directory

        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/henrybot";
        AIMLProcessor.extension = new PCAIMLProcessorExtension();

        bot = new Bot("henrybot", MagicStrings.root_path,"chat");
        chat = new Chat(bot);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;

        while((read = in.read(buffer)) != -1){
            out.write(buffer,0,read);
        }

    }


    private boolean isSDCartAvaliable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)? true : false;
    }

    private void botsReply(String response){
        ChatMessage chatMessage = new ChatMessage(false,false,response);
        adapter.add(chatMessage);
    }
    private void sendMessage(String message){
        ChatMessage chatMessage = new ChatMessage(false,true,message);
        adapter.add(chatMessage);
    }

    private void sendImage(Bitmap image){
        String message = bitmaptostring.convertIconToString(image);
        ChatMessage chatMessage = new ChatMessage(true,true, message);
        adapter.add(chatMessage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        sendImage(bitmap);

        String response = chat.multisentenceRespond("%image" + String.valueOf(imageid));
        imageid = imageid + 1;
        botsReply(response);
        listView.setSelection(adapter.getCount() - 1);
    }
}
