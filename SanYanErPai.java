import CrawlerPackages.httpRequest;
import CrawlerPackages.textMach;
import CrawlerPackages.textWrite;
import java.io.File;


// thread to download a book
class SanYanErPai_book_thread implements Runnable{

    // properties
    SanYanErPai_book book;
    Thread thread;

    // construction functions
    SanYanErPai_book_thread(){}
    SanYanErPai_book_thread(SanYanErPai_book book){
        this.book = book;
    }
    public void run(){
        try {
        this.book.download();
        Thread.sleep(5000);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }
    void start(){
        if (this.thread == null){
            Thread thread = new Thread(this, book.name);
            thread.start();
        }
    }
}

// thread to download a chapter
class SanYanErPai_chapter_thread implements Runnable{

    // properties
    SanYanErPai_chapter chapter;
    Thread thread;

    // construction functions
    SanYanErPai_chapter_thread(){}
    SanYanErPai_chapter_thread(SanYanErPai_chapter chapter){
        this.chapter = chapter;
    }

    public void run(){
        try {
            this.chapter.download();
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    void start(){
        if (this.thread == null){
            Thread thread = new Thread(this, chapter.name);
            thread.start();
        }
    }
}

class SanYanErPai_chapter{

    // four properties of the chapter
    int num;
    String name;
    String href;
    String path;

    // two construct functions of class chapter
    SanYanErPai_chapter(){}
    SanYanErPai_chapter(int num, String name, String href, String path){
        this.num = num; this.name = name; this.href = href; this.path = path;
    }

    void download() throws InterruptedException {

        // get the same aspect of the href
        String[] hrefShells = this.href.split("/");
        String halfHref = new String();
        for (int i = 0; i < hrefShells.length - 1; ++i){
            halfHref += hrefShells[i] + "/";
        }
//        System.out.println(this.href);

        // get the first page's source code of this chapter
        String sourceCode = httpRequest.getCode(this.href,"gb18030");

        // cut the first page's source code short
        String shrink = textMach.match(
                "<div id=\"arcxsbd\">(.*?)<!--arc ad开始-->"
                , sourceCode
        )[0];
//        System.out.println(shrink);

        // get the content of first page
        String content = this.name + "\n" +
                textMach.match(
                        "<div id=\"onearcxsbd\" class=\"onearcxsbd\">(.*?)<div class=\"list-pages page-center \">"
                , shrink)[0];
//        System.out.println(content);
//        System.out.println("\n\n\n");

        // make a list to storage the difference part of each page
        String[] href = textMach.match("href='(.*?)'", shrink);

        // identify the chapter weather have more than one page
        if (href.length > 0){
            String PageSourceCode = new String();
            for (int i = 0; i < href.length - 1; ++i){

                // eliminate the invalid href
                if (href[i].equals("#")){
                    continue;
                }

                PageSourceCode = httpRequest.getCode(halfHref + href[i]
                        , "gb18030");
//                System.out.println(halfHref + href[i]);
//                System.out.println(PageSourceCode);

                // get this page's content
                PageSourceCode = textMach.match(
                        "<div id=\"onearcxsbd\" class=\"onearcxsbd\">(.*?)<div class=\"list-pages page-center \">"
                        , PageSourceCode)[0];

                // attach the page behind the content
                content += PageSourceCode + "\n";
            }
        }

        // replace escape character with the character it corresponded
        content = content.replace("&ldquo;", "“");
        content = content.replace("&rdquo;", "”");
        content = content.replace("&mdash;", "—");
        content = content.replace("&nbsp;", " ");
        content = content.replace("&lsquo;", "‘");
        content = content.replace("&rsquo;", "’");
        content = content.replace("</p>", "\n");
        content = content.replace("<br />", "\n");
        content = content.replace("<br>", "\n");
        content = content.replaceAll("<.*?>", "");
//        System.out.println(content);
//        System.out.println("---------------");

        // write the content into its file
        textWrite.write(this.path, num + "、" + this.name + ".txt", content);
    }
}

class SanYanErPai_book{

    //two property
    String name;
    String href;

    // two construct functions
    SanYanErPai_book(){}
    SanYanErPai_book(String name, String href){
        this.name = name; this.href = href;
    }

    // method to download the book
    void download(){
//            System.out.println(this.name);
//            System.out.println(this.href);

        // the path to storage the book
        // if it does not exist, creat it
        File dir = new File("三言二拍\\" + this.name);
        if (!dir.exists()){
            dir.mkdir();
        }
        String sourceCode_book = httpRequest.getCode(this.href, "gb18030");
//        System.out.println(sourceCode_book);

        // cut the book's source code short
        String shrink = textMach.match("<ul class=\"zhangjie2\">(.*?)</ul>"
                , sourceCode_book)[0];
//        System.out.println(shrink);

        // to get the string that contain the books' name and a part of domain
        String[] chapterList = textMach.match("<li>(.*?)</li>", shrink);

        // the number of the chapter
        int num = 1;

        // go over the chapter list
        for (String theChapter:chapterList){
            String name = textMach.match("title=\"(.*?)\"", theChapter)[0];

            // attach the difference part with the same aspect to get the complete domain
            String href = "https://www.xyyuedu.com" +
                    textMach.match("href=\"(.*?)\"", theChapter)[0];
//            System.out.println(name);
//            System.out.println(href);

            // make the object of this chapter
            SanYanErPai_chapter thisChapter = new SanYanErPai_chapter(
                    num, name, href, "三言二拍\\" + this.name
            );

            // make a thread for this chapter and start it
            SanYanErPai_chapter_thread thisThread = new SanYanErPai_chapter_thread(thisChapter);
            thisThread.start();

            // update the number
            ++num;
        }
    }
}

public class SanYanErPai {

    // domain of the five books
    static String url_o = "https://www.xyyuedu.com/gdmz/sanyanerpai/index.html";

    public static void main(String[] args) {

        // check the directory for the book weather exist
        // if the directory is not exist creat it
        File dir = new File("三言二拍");
        if (!dir.exists()){
            dir.mkdir();
        }

        String sourceCode_o = httpRequest.getCode(url_o, "gb18030");
//        System.out.println(sourceCode_o);

        // cut source code short
        String polish = textMach.match("<div class=\"mingzhu-main\">(.*?)</div>"
        , sourceCode_o)[0];

        // each string element of this array contain one book's name and domain
        String[] bookList = textMach.match("<li>(.*?)</li>", polish);

        // go over the book list
        for (String book : bookList){
//        for (int i = 0; i < bookList.length; ++i){
//            String book = bookList[1];

            // get the property of each book
            String href = "https://www.xyyuedu.com"
                    + textMach.match("<a href=\"(.*?)\" title=\"", book)[0];
            String name = textMach.match("title=\"(.*?)\"", book)[0];

            // make an object for the book
            SanYanErPai_book thisBook = new SanYanErPai_book(name, href);

            // make a thread for the book and start it
            SanYanErPai_book_thread thisThread = new SanYanErPai_book_thread(thisBook);
            thisThread.start();
        }
    }
}
