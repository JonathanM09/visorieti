package com.example.visorieti;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.visorieti.Interfaz.InterfazAPI;
import com.example.visorieti.questionario.Questionario;
import com.example.visorieti.questionario.SharedPreferencesManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import gov.census.cspro.csentry.fileaccess.FileAccessHelper;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static FileAccessHelper fileAccessHelper;
    private String var = "";
    private File directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        requestPermission();
        requestPermission2();
        fileAccessHelper = new FileAccessHelper(this);

//        fileAccessHelper = new FileAccessHelper(this);


//        try {
//            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//            String version = pInfo.versionName;
//            tvVersion.setText(version);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        //Intent intent2 = getIntent();
        Uri uri = getIntent().getData();

        if (uri != null) {
            List<String> params = uri.getPathSegments();
            //var = params.toString();
            // if (params.toString() == ""){
            //var = params.toString();
            var = params.get(params.size() - 1);
            //}
//            Toast.makeText(this, "No se encuentra el cuestionario", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se encuentra el cuestionario", Toast.LENGTH_SHORT).show();
        }
//        tv2 = findViewById(R.id.textView2);
        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        try {
            if (!SharedPreferencesManager.getSomeBooleanValue("PERMISOS")) {
                Intent intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                    SharedPreferencesManager.setSomeBooleanValue("PERMISOS", true);
                }
            }
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }
        if (!var.equals(""))
            createUtilsAssets(var);
    }

    private void createUtilsAssets(String llave) {
//        InputStream in = getResources().openRawResource(R.raw.sup_hogares);
        InputStream in = getResources().openRawResource(R.raw.supeti);
        FileOutputStream out;
        try {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String directory3 = directory + "/Visor";
            if (!(new File(directory3).exists())) {
                if (new File(directory3.toString()).mkdirs()) {
                    Log.i(TAG, "abrirCsPro: se ha creado la carpeta csdbPath");
                } else Log.e(TAG, "abrirCsPro: no se ha creado la carpeta");
            }


//            if ((new File(directory3 + getResources().getResourceName(R.raw.sup_hogares)).exists())) {
//                if (new File(directory3 + getResources().getResourceName(R.raw.sup_hogares)).delete()) {

            if ((new File(directory3 + getResources().getResourceName(R.raw.supeti)).exists())) {
                if (new File(directory3 + getResources().getResourceName(R.raw.supeti)).delete()) {
                    Log.i("Existe", "abrirCsPro: se ha eliminado el archivo .pff exitosamente");
                } else
                    Log.e("TAG", "createUtilsAssets: Error al elimianr");
            }
//            out = new FileOutputStream(directory3 + "/" + "sup_hogares.pen");
            out = new FileOutputStream(directory3 + "/" + "supeti.pen");
//            out = new FileOutputStream("/storage/emulated/0/Android/data/gov.census.cspro.csentry/files/csentry/" +"sup_cen2020.pen");
            String pen = out.toString();


            byte[] buff = new byte[1024];
            int read;
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
            in.close();
            out.close();

            fileAccessHelper.pushFiles(
                    directory3.toString(),
//                    "sup_hogares.pen",
                    "supeti.pen",
                    "/.",
                    false,
                    true,
                    strings -> {
                        find(llave);
                        return null;

                    }
                    , s -> null);

//            fileAccessHelper.pushFiles(
//                    directory3.toString(),
//                    "sup_cen2020.pen",
//                    "/.",
//                    false,
//                    true,
//                    strings -> null
//                    , s -> null);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPermission2() {
        if (this.checkSelfPermission(
                WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                this.checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.i("TAG", "requestPermission2: ok");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestPermissions(new String[]{
                        WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
            }
        }
    }

//    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                intent.addCategory("android.intent.category.DEFAULT");
//                intent.setData(Uri.parse(String.format("package:%s", this.getPackageName())));
//                startActivityForResult(intent, 2296);
//            } catch (Exception e) {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivityForResult(intent, 2296);
//            }
//        } else {
//            //below android 11
//            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE},
//                    2);
//        }
//    }


    private void find(String codigo) {



            SSLContext sslContext = null;
            TrustManager[] trustAllCerts = null;
            try {
                trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            if (sslContext != null) {
                httpClient.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                        .hostnameVerifier((hostname, session) -> true)
                        .build();
            }


        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://www.censospanama.pa/")
//                .baseUrl("https://www.censospanama.pa/epm-api/")       //EML
//                .baseUrl("https://www.inec.gob.pa/epm-api/")             //EML
                .baseUrl("https://www.inec.gob.pa/eti-api/")             //CEA
                .addConverterFactory(GsonConverterFactory.create())
                .client(Objects.requireNonNull(httpClient).build())
            .build();

        InterfazAPI interfazAPI = retrofit.create(InterfazAPI.class);
        Call<Questionario> call = interfazAPI.find(codigo);
        if (!codigo.equals("")) {
            call.enqueue(new Callback<Questionario>() {
                             @SuppressLint("SdCardPath")
                             @Override
                             public void onResponse(Call<Questionario> call, Response<Questionario> response) {
                                 try {

                                     if (response.isSuccessful()) {
                                         Questionario p = response.body();

//                      tv1.setText(p.getLlave());
//                        tv2.setText(p.getDatos());
                                         //directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                         File directory2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Visor");
                                         BufferedWriter bw;
                                         BufferedWriter bw2;
                                         BufferedWriter bw3;

//                                         FileWriter fw = null;
                                         File file = new File(directory2, "data.pff");
                                         String filePff = file + "";
                                         File file2 = new File(directory2, Objects.requireNonNull(p).getEntrevistaId() + ".dat");
                                         String fileDAT = file2 + "";
                                         File file3 = new File(directory2, Objects.requireNonNull(p).getEntrevistaId() + ".dat.csnot");
                                         String fileNote = file3.toString();
                                         String penPath = directory2.toString();

                                         if (!(directory2.exists())) {
                                             if (new File(directory2.toString()).mkdirs()) {
                                                 Log.i(TAG, "abrirCsPro: se ha creado la carpeta csdbPath");
                                             } else Log.e(TAG, "abrirCsPro: no se ha creado la carpeta");
                                         }


                                         if (!(new File(penPath).exists())) {
                                             Log.i("Existe", "abrirCsPro: se ha eliminado el archivo .pff exitosamente");
                                         }

                                         if ((new File(filePff).exists())) {
                                             if (new File(filePff).delete()) {
                                                 Log.i("Existe", "abrirCsPro: se ha eliminado el archivo .pff exitosamente");
                                             } else
                                                 Log.e("Crear crearPFFH", "No se ha eliminado el archivo .pff ");
                                         }

                                         if ((new File(fileNote).exists())) {
                                             if (new File(fileNote).delete()) {
                                                 Log.i("Existe", "Se ha eliminado el archivo .Note exitosamente");
                                             } else
                                                 Log.e("Crear crearPFFH", "No se ha eliminado el archivo .Note ");
                                         }


                                         File pffPathFile = new File(filePff);
                                         if (pffPathFile.createNewFile()) {
                                             Log.i("CaPro", "abrirCsPro: archivo creado");
                                         }

                                         File[] files = file2.getParentFile().listFiles((dir, name)
                                                 -> name.startsWith(file2.getName()));

                                         if (files != null && files.length > 0) {
                                             for (File f : files) {
                                                 f.delete();
                                             }
                                         }


                                         File pffPathFile2 = new File(fileDAT);
                                         if (pffPathFile2.createNewFile()) {
                                             Log.i("CsPro", "abrirCsPro: archivo creado");
                                         }


                                         if (!("".equals(p.getNotas()))) {
                                             if (file3.createNewFile()) {
                                                 Log.i("Notas", "archivo creado");
                                             }


                                             try {
                                                 bw3 = new BufferedWriter(new FileWriter(file3));
                                                 bw3.write(p.getNotas());
                                                 bw3.close();
                                             } catch (Exception e) {
                                                 Log.e("Error", "error al escribir en notas" + e.getMessage());
                                             }
                                         }


                                         try {

                                             bw2 = new BufferedWriter(new FileWriter(file2));


                                             String raw_data = p.getDatos();
//                                             byte[] bytes = raw_data.getBytes(StandardCharsets.US_ASCII);
//                                             raw_data = new String(bytes, StandardCharsets.US_ASCII);
//
//                                             raw_data = "\uFEFF" + raw_data;
                                             if(Character.isDigit(raw_data.charAt(0))){
                                                  raw_data = "\uFEFF" + raw_data;
                                             }
                                             bw2.write(raw_data);

                                             bw2.close();


                                         } catch (Exception e) {
                                             Log.e("Error pff", "abrirCsPro: error en crear el pff  //  " + e.getMessage());

                                         }
                                         try {

                                             String inputDataPath = new File(fileDAT).getName();
                                             bw = new BufferedWriter(new FileWriter(filePff));

                                             bw.write("[Run Information]\n");
                                             bw.write("Version=CSPro 7.7\n");
                                             bw.write("AppType=Entry\n");
                                             bw.write("\n");
                                             bw.write("[DataEntryInit]\n");
                                             bw.write("Interactive=Ask\n");
                                             bw.write("\n");
//                                             bw.write("StartMode=ADD\n\n");
                                             // bw.write("Key=" + inputDataPath.replace(".dat", "") + "\n\n");
                                             bw.write("StartMode=ADD;" + inputDataPath.replace(".dat", "") + "\n\n");
                                             bw.write("[Files]\n");
//                                             bw.write("Application=" + "/storage/emulated/0/Android/data/gov.census.cspro.csentry/files/csentry/sup_hogares.pen" + "\n");
                                             bw.write("Application=" + "/storage/emulated/0/Android/data/gov.census.cspro.csentry/files/csentry/supeti.pen" + "\n");
                                             bw.write("InputData=/storage/emulated/0/Android/data/gov.census.cspro.csentry/files/csentry/" + inputDataPath + "\n");
                                             bw.write("\n");
                                             bw.close();
//                                             fileAccessHelper.pushFiles(
//                                                     directory2.toString(),
//                                                     "data.pff",
//                                                     "/.",
//                                                     false,
//                                                     true,
//                                                     strings ->{
//                                                         fileAccessHelper.pushFiles(
//                                                                 directory2.toString(),
//                                                                 inputDataPath,
//                                                                 "/.",
//                                                                 false,
//                                                                 true,
//                                                                 s ->{
//                                                                     Intent intent = new Intent();
//                                                                     intent.setComponent(new ComponentName("gov.census.cspro.csentry",
//                                                                             "gov.census.cspro.csentry.ui.EntryActivity"));
//                                                                     intent.putExtra("PffFilename", "/storage/emulated/0/Android/data/gov.census.cspro.csentry/files/csentry/data.pff");
//                                                                     intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                                                     startActivityForResult(intent, 1);
//                                                                 return null;}
//                                                                 , s -> null);
//
//                                                         fileAccessHelper.pushFiles(
//                                                                 directory2.toString(),
//                                                                 inputDataPath + ".csnot",
//                                                                 "/.",
//                                                                 false,
//                                                                 true,
//                                                                 s -> null
//                                                                 , s -> null);
//                                                     return null;}
//                                                     , s -> null);

                                             fileAccessHelper.pushFiles(
                                                     directory2.toString(),
                                                     inputDataPath,
                                                     "/.",
                                                     false,
                                                     true,
                                                     strings -> {
                                                         fileAccessHelper.pushFiles(
                                                                 directory2.toString(),
                                                                 inputDataPath+".csnot",
                                                                 "/.",
                                                                 false,
                                                                 true,
                                                                 sss -> null,
                                                                 sss -> null);

                                                         fileAccessHelper.pushFiles(
                                                                 directory2.toString(),
                                                                 inputDataPath+".dat",
                                                                 "/.",
                                                                 false,
                                                                 true,
                                                                 sss -> null,
                                                                 sss -> null);

                                                         fileAccessHelper.pushFiles(
                                                             directory2.toString(),
                                                             "data.pff",
                                                             "/.",
                                                             false,
                                                             true,
                                                             sstrings -> {
//
                                                                 Intent intent = new Intent();
                                                                 intent.setComponent(new ComponentName("gov.census.cspro.csentry",
                                                                         "gov.census.cspro.csentry.ui.EntryActivity"));
                                                                 intent.putExtra("PffFilename", "/storage/emulated/0/Android/data/gov.census.cspro.csentry/files/csentry/data.pff");
                                                                 intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                 startActivityForResult(intent, 105);
                                                                    return null;

                                                             },
                                                             s -> null);
                                                        return null;
                                                     }
                                                     , s -> null);








                                         } catch (Exception e) {
                                             Log.e("Error pff", "abrirCsPro: error en crear " +
                                                     "el pff  //  " + e.getMessage());
                                         }

//                                         fileAccessHelper.pushFiles(
//                                                 directory2.toString(),
//                                                 "data.pff",
//                                                 "/.",
//                                                 false,
//                                                 true,
//                                                 strings ->{
//                                                     fileAccessHelper.pushFiles(
//                                                             directory2.toString(),
//                                                             fileDAT.toString(),
//                                                             "/.",
//                                                             false,
//                                                             true,
//                                                             s ->{
//                                                                 Intent intent = new Intent();
//                                                                 intent.setComponent(new ComponentName("gov.census.cspro.csentry",
//                                                                         "gov.census.cspro.csentry.ui.EntryActivity"));
//                                                                 intent.putExtra("PffFilename", "/storage/emulated/0/Android/data/gov.census.cspro.csentry/files/csentry/data.pff");
//                                                                 intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                                                 startActivityForResult(intent, 1);
//                                                                 return null;}
//                                                             , s -> null);
//
//                                                     fileAccessHelper.pushFiles(
//                                                             directory2.toString(),
//                                                             fileDAT + ".csnot",
//                                                             "/.",
//                                                             false,
//                                                             true,
//                                                             s -> null
//                                                             , s -> null);
//                                                     return null;}
//                                                 , s -> null);


//

//                                         File filepff = filePff + directory2;

//                                         fileAccessHelper.pushFiles(directory2.toString(),"data.pff","/.",false,true,
//                                                 strings -> {
//                                         Intent intent = new Intent();
//                                         intent.setComponent(new ComponentName("gov.census.cspro.csentry",
//                                                 "gov.census.cspro.csentry.ui.EntryActivity"));
//                                         intent.putExtra("PffFilename", "/storage/emulated/0/Android/data/gov.census.cspro.csentry/files/csentry/data.pff");
//                                         intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                         startActivityForResult(intent, 1);
//                                                     return  null;
//                                                 },s -> null);
                                     }
                                 } catch (Exception ex) {
                                     Toast.makeText(MainActivity.this, ex.getMessage(),
                                             Toast.LENGTH_SHORT).show();
                                 }
                             }

                             @SuppressLint("SdCardPath")
                             @Override
                             public void onFailure(Call<Questionario> call, Throwable t) {
                                 Toast.makeText(MainActivity.this, "Error de conexion",
                                         Toast.LENGTH_SHORT).show();
                                 try {
                                     BufferedWriter bw;
                                     File file3 = new File(directory, var + ".dat");
                                     File file = new File(directory, "data.pff");
                                     String inputDataPath = new File(String.valueOf(file3)).getName();
                                     bw = new BufferedWriter(new FileWriter(file));

                                     bw.write("[Run Information]\n");
                                     bw.write("Version=CSPro 7.5\n");
                                     bw.write("AppType=Entry\n");
                                     bw.write("\n");
                                     bw.write("[DataEntryInit]\n");
                                     bw.write("Interactive=Ask\n");
                                     bw.write("\n");
//                                             bw.write("StartMode=ADD\n\n");
                                     bw.write("StartMode=MODIFY;" + inputDataPath.replace(".dat", "") + "\n\n");
                                     bw.write("[Files]\n");
//                                     bw.write("Application=" + "/storage/emulated/0/Android/data/gov.census.cspro.csentry/files/csentry/sup_hogares.pen" + "\n");
                                     bw.write("Application=" + "/storage/emulated/0/Android/data/gov.census.cspro.csentry/files/csentry/supeti.pen" + "\n");
                                     bw.write("InputData=/storage/emulated/0/Android/data/gov.census.cspro.csentry/files/csentry/" + inputDataPath + "\n");
                                     bw.write("\n");

                                     bw.close();
                                 } catch (Exception e) {
                                     Log.e("Error pff", "abrirCsPro: error en crear el pff  //  " + e.getMessage());
                                 }

                                 Intent intent = new Intent();
                                 intent.setComponent(new ComponentName("gov.census.cspro.csentry",
                                         "gov.census.cspro.csentry.ui.EntryActivity"));
                                 intent.putExtra("PffFilename", "/storage/emulated/0/Android/data/gov.census.cspro.csentry/files/csentry/data.pff");
                                 intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                 startActivityForResult(intent, 1);
                             }
                         }
            );
        } else {
            Toast.makeText(MainActivity.this, "Error", +Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 105) {
            Log.d("TAG", "onActivityResult: ");
            finish();
        }
    }

}
