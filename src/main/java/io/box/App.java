package io.box;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLDivElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class App
{
    private static WebClient webClient;
    private List<HomeModel> homeList;

    private App() {
        webClient = new WebClient();
        homeList = new ArrayList<>();
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setJavaScriptEnabled(false);
    }

    public static void main( String[] args )
    {
        App app = new App();

        try {
            String URL = app.getURL(Constants.MAINLINK).get(0);
            System.out.println("sdsdasdasda"+URL);
            app.setDetails(app.getPage(URL));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getURL(String URI) throws IOException {
        return getAnchors(URI).stream().map(htmlAnchor -> htmlAnchor.getHrefAttribute()).collect(Collectors.toList());
    }

    private List<HtmlAnchor> getAnchors(String URI) throws IOException {
        HtmlPage currentPage = getPage(URI);
        return (List<HtmlAnchor>)currentPage.getByXPath("//a[contains(@class, 'list-card-link list-card-img')][contains(@tabindex, '-1')]");
    }

    private HtmlPage getPage(String URI) throws IOException {
        return webClient.getPage(URI);
    }

    private void setDetails(HtmlPage page){
        HomeModel model = new HomeModel();
        model.setPrice(getSpanElementText(page, "ds-value"));
        model.setAddress(getAddress(page));
        model.setOwner(getSpanElementText(page, "ds-listing-agent-display-name"));
        model.setDescription(getDescription(page));

        getImages(page);// not added to model
        homeList.add(model);
    }

    private String getSpanElementText(HtmlPage page, String classValue){
        String xpath = String.format("//span[contains(@class, '%s')]", classValue);
        List<HtmlSpan> span = (List<HtmlSpan>)page.getByXPath(xpath);
        return span.isEmpty() ? "Empty" : span.get(0).getTextContent();
    }

    private String getDescription(HtmlPage page){
        List<HtmlDivision> description = (List<HtmlDivision>)page.getByXPath("//div[contains(@class, 'Text-aiai24-0')]");

        String text = "";

        if(!description.isEmpty()){
            for (HtmlDivision div: description) {
                text += div.getTextContent();
            }
        }

        return text;
    }

    private String getAddress(HtmlPage page){
        List<HtmlHeading1> heading = (List<HtmlHeading1>)page.getByXPath("//h1[contains(@class, 'ds-address-container')]");
        return heading.isEmpty() ? "Empty" : heading.get(0).getTextContent();
    }

    private List<String> getImages(HtmlPage page){
        List<HtmlUnknownElement> images = (List<HtmlUnknownElement>)page.getByXPath("//source");
        images.forEach(System.out::println);
        return null;
    }
}
