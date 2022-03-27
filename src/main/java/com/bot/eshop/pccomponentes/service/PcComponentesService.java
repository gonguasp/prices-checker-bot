package com.bot.eshop.pccomponentes.service;

import com.bot.config.Urls;
import com.bot.eshop.pccomponentes.model.PcComponentesProduct;
import com.bot.eshop.pccomponentes.model.PcComponentesSaleProduct;
import com.bot.eshop.pccomponentes.repository.PcComponentesProductRepository;
import com.bot.eshop.pccomponentes.repository.PcComponentesSaleProductRepository;
import com.bot.service.ScanProductService;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@Slf4j
@Data
public class PcComponentesService extends ScanProductService {

    @Autowired
    private PcComponentesProductRepository pcComponentesProductRepository;

    @Autowired
    private PcComponentesSaleProductRepository pcComponentesSaleProductRepository;

    @NonNull
    private final Urls urls;

    @Transactional
    public void cleanPcComponentesSaleProductsDB() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        pcComponentesSaleProductRepository.removeAllByCreatedBefore(cal.toInstant());
    }

    public void scanProducts(String urlPart) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String url = urls.getPcComponentes().get(urlPart).toString();
        WebDriver driver = getDriver(url, false, true);
        manageProducts(driver.findElements(By.className("col-xs-6")));
        driver.quit();
    }

    public List<PcComponentesSaleProduct> getSales() {
        List<PcComponentesSaleProduct> pcComponentesSaleProductList = pcComponentesSaleProductRepository.findAll();
        for (PcComponentesSaleProduct pcComponentesSaleProduct : pcComponentesSaleProductList) {
            pcComponentesSaleProduct.add(Link.of(pcComponentesSaleProduct.getHref(), "BUY"));
        }
        return pcComponentesSaleProductList;
    }

    public void scanFocusedProducts() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        WebDriver driver = null;

        for (String url : ((LinkedHashMap<String, String>) urls.getPcComponentes().get("focus-on-components")).values()) {
            driver = getDriver(url, true, false);
            WebElement productInfo = driver.findElement(By.id("add-cart"));

            manageProduct(new PcComponentesProduct(
                    productInfo.getAttribute("data-name"),
                    Double.parseDouble(productInfo.getAttribute("data-price")),
                    productInfo.getAttribute("data-brand"),
                    productInfo.getAttribute("data-category"),
                    url,
                    true), pcComponentesProductRepository, pcComponentesSaleProductRepository);

            driver.close();
        }

        if(driver != null) {
            driver.quit();
        }
    }

    private WebDriver getDriver(String url, boolean focused, boolean waitForJQuery) {
        WebDriver driver = new ChromeDriver(loadChromeConfig());

        log.info("Loading web page!");
        driver.get(url);
        log.info("Web page loaded!");

        if(!focused) {
            Select selectElement = new Select(driver.findElement(By.id("listOrder")));
            selectElement.selectByValue("price-asc");
        }

        log.info("Waiting for Javascript to finish");
        waitingForOrderingProducts(driver, waitForJQuery);
        log.info("Javascript finished");

        return driver;
    }

    private void manageProducts(List<WebElement> webElementList) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        for (WebElement divElement : webElementList) {
            WebElement articleElement = divElement.findElement(By.tagName("article"));
            String productName = articleElement.getAttribute("data-name");

            manageProduct(new PcComponentesProduct(
                    productName,
                    Double.parseDouble(articleElement.getAttribute("data-price")),
                    articleElement.getAttribute("data-brand"),
                    articleElement.getAttribute("data-category"),
                    articleElement.findElement(By.tagName("a")).getAttribute("href"),
                    false
            ), pcComponentesProductRepository, pcComponentesSaleProductRepository);
        }
    }
}
