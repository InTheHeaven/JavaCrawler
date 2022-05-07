package CrawlerPackages;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class textMach{
    public static String[] match(String regex, String staff){
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(staff);
        while(matcher.find()){
            list.add(matcher.group(1));
        }
        return list.toArray(new String[0]);
    }
}
