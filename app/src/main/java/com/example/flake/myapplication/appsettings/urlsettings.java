package com.example.flake.myapplication.appsettings;

/**
 * Created by Дмитрий on 21.04.2015.
 */
public class urlsettings {
    private static String adress_server="dfdfdfdfkjnmb.esy.es";
    private static String adress_load="/backend/get_all_products.php";
    private static String adress_add="/backend/create_product.php";
    public static final String FILE_UPLOAD_URL = "http://ikbsp.ru/backend/AndroidFileUpload/fileUpload.php";
    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    //Следующие строки не трогать. нужны для получения адресов
    public static String url_create_secrets="http://"+adress_server+adress_add;
    public static String url_all_secrets="http://"+adress_server+adress_load;
    public static String url_get_comments="http://"+adress_server+"/backend/get_comments.php";
    public static String set_like="http://"+adress_server+"/backend/set_like.php";
    public static String send_comment="http://"+adress_server+"/backend/create_comment.php";
}