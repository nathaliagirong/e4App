package com.example.e4app;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;

public class MainActivity extends AppCompatActivity {

    private int x = 0;
    private int y = 0;
    private int[] arrayTest = new int[10000];
    ArrayList<Integer> iconList = new ArrayList<Integer>();
    StringWriter stringWriter = new StringWriter();

    String filename = "csvfile.csv";
    FileOutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createArray();
    }

    void createArray() {

        File file = new File("foo.csv");
        CsvWriter csvWriter = new CsvWriter();

        try (CsvAppender csvAppender = csvWriter.append(file, StandardCharsets.UTF_8)) {
            // header
            csvAppender.appendLine("header1", "header2");

            // 1st line in one operation
            csvAppender.appendLine("value1", "value2");

            // 2nd line in split operations
            csvAppender.appendField("value3");
            csvAppender.appendField("value4");
            csvAppender.endLine();
            Log.i("fin2", "LLEGAAA");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
