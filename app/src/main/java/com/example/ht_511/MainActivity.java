package com.example.ht_511;

import android.Manifest;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static final String TITLE = "TITLE";
    private static final String SUBTITLE = "SUBTITLE";
    private static final int REQUEST_PERMISSION = 8;
    List<Map<String, String>> lines;
    BaseAdapter adapter;
    List<String> stringList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lines.remove(i);
                adapter.notifyDataSetChanged();
            }
        });

        final SwipeRefreshLayout refreshLayout = findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lines.clear();
                init();
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void init() {
        stringList = new ArrayList<>();

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Разрешение есть", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }

        try {
            stringList = getListFromFile();
        } catch (IOException e) {
            Toast.makeText(this, "Нет файла", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        lines = prepareContent();
        adapter = createAdapter();

        listView = findViewById(R.id.list);

        listView.setAdapter(adapter);

        try {
            sendListToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<String> getListFromFile() throws IOException {
        List<String> list = new ArrayList<>();
        if (externalStorageIsReadable()) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "HT_5.1.1.txt");
            Scanner scanner = new Scanner(file);
            list = Arrays.asList(scanner.nextLine().split(";"));
        }
        return list;
    }

    public boolean externalStorageIsReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public BaseAdapter createAdapter() {
        String[] from = {TITLE, SUBTITLE};
        int[] to = {R.id.text1, R.id.text2};
        return new SimpleAdapter(this, lines, R.layout.textviews, from, to);
    }

    public List<Map<String, String>> prepareContent() {
        List<Map<String, String>> newList = new ArrayList<>();
        for (String line :
                stringList) {
            Map<String, String> map = new HashMap<>();
            map.put(TITLE, line);
            map.put(SUBTITLE, line.length() + "");
            newList.add(map);
        }
        return newList;
    }

    public void sendListToFile() throws IOException {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "HT_5.1.1.txt");
        if (!file.exists())
            Toast.makeText(this, "Создайте файл HT_5.1.1.txt", Toast.LENGTH_SHORT).show();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        for (String line :
                stringList) {
            bufferedWriter.append(line).append(";");
        }
        bufferedWriter.close();
    }
}
