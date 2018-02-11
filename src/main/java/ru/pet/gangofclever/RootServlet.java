package ru.pet.gangofclever;

import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.pet.gangofclever.ThymeleafListener.engine;

@WebServlet(urlPatterns = "/", loadOnStartup = 1)
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10) //10 MB in memory limit
public class RootServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       // out(req, resp, "");
        resp.setCharacterEncoding("utf-8"); // <--- very important thing: UTF-8 without BOM

        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        engine.process("index", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8"); // <--- very important thing: UTF-8 without BOM

        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());

        try {
//            http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
            Part filePart = req.getPart("fileToUpload");
            if (filePart.getSize() == 0) {
                throw new IllegalStateException("Upload file have not been selected");
            }

            StringBuilder template = new StringBuilder("^");
            for (Map.Entry<String,Integer> e: LetterSets.orangeSet.entrySet()) {
                template.append(e.getKey())
                        .append("{0,")
                        .append(e.getValue())
                        .append("}");
            }
            template.append("$");
            Pattern p = Pattern.compile(template.toString());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(filePart.getInputStream(), "UTF-8"))) {
                List<String> chosenString = new ArrayList<String>();
                String line = "";
                String sortedword = "";
                while ((line = reader.readLine()) != null)
                {
                    for (String word :  line.split(" ")) {
                        sortedword = Stream.of(word.split(""))
                                .sorted()
                                .collect(Collectors.joining());
                        if (p.matcher(sortedword).matches())
                            chosenString.add(word);
                    }
                }

                webContext.setVariable("words", chosenString);
                webContext.setVariable("set", LetterSets.orangeSet);
                engine.process("result", webContext, resp.getWriter());
            }
        } catch (Exception e) {
            webContext.setVariable("exception", e);
            engine.process("exception", webContext, resp.getWriter());
        }
    }

//    private List<String> choseWords(List<String> words, Map<String, Integer> set) {
//        List<String> chosenString = new ArrayList<>();
//
//        StringBuilder template = new StringBuilder("^[");
//        for (Map.Entry<String,Integer> e: set.entrySet()) {
//            template.append(e.getKey());
//        }
//        template.append("]+?$");
//
//        Pattern p = Pattern.compile(template.toString());
//        for (String s: words) {
//            if(p.matcher(s).matches())
//                chosenString.add(s);
//        }
//
//        return chosenString;
//    }
}
