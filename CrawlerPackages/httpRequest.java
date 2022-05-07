package CrawlerPackages;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;

public class httpRequest {
    static HttpClient httpClient = HttpClient.newBuilder().build();
    public static String getCode(String domain){
        String code = new String();
        HttpRequest httpRequest = null;
        try {
            httpRequest = HttpRequest.newBuilder(new URI(domain)).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            code = httpResponse.body();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return code;
    }

    public static String getCode(String domain, String Decode){
        Charset charset = Charset.forName(Decode);
        String code = new String();
        HttpRequest httpRequest = null;
        try {
            httpRequest = HttpRequest.newBuilder(new URI(domain)).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(charset));
            code = httpResponse.body();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return code;
    }
}
