package com.example.visorieti.Interfaz;

import com.example.visorieti.questionario.Questionario;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface InterfazAPI {

    @Headers("x-api-key: " + "8affd3c7-ec93-4b62-8de4-983008ead13f") //Mercado Laboral
//    @Headers("8affd3c7-ec93-4b62-8de4-983008ead13f")

//    @GET("/dev-api/api/cuestionarios/single/{Llave}")             //Censo
//    public Call<Questionario> find(@Path("Llave") String id);

//    @GET("api/entrevistas/single/{Llave}")                          //Mercado Laboral
//    public Call<Questionario> find(@Path("Llave") String id);

    @GET("api/entrevista/single/{Llave}")                          //ETI
    public Call<Questionario> find(@Path("Llave") String id);

}


