package com.bot.service;

import com.bot.model.Product;
import com.bot.model.SaleProduct;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Optional;

@Slf4j
public class ScanProductService {

    private final String findByName = "findByName";
    private final String save = "save";
    private final double minDifference = 10;

    protected ChromeOptions loadChromeConfig() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/static/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        return options;
    }

    protected void waitingForOrderingProducts(WebDriver driver, boolean waitForJQuery) {
        WebDriverWait wait = new WebDriverWait(driver,10);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // wait for Jquery
        if(waitForJQuery) {
            ExpectedCondition<Boolean> jQueryLoad = condition -> {
                try {
                    return ((Long) js.executeScript("return jQuery.active") == 0);
                } catch (Exception e) {
                    return true;
                }
            };

            wait.until(jQueryLoad);
        }

        // wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = condition -> js.executeScript("return document.readyState")
                .toString().equals("complete");

        wait.until(jsLoad);
    }

    protected void manageProduct(Product product, Object productRepository, Object saleProductRepository) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method findByNameProduct = productRepository.getClass().getMethod(findByName, String.class);
        Method saveProduct = JpaRepository.class.getMethod(save, Object.class);
        Method findByNameProductSale = saleProductRepository.getClass().getMethod(findByName, String.class);
        Method saveSaleProduct = JpaRepository.class.getMethod(save, Object.class);

        Object productOptional = findByNameProduct.invoke(productRepository, product.getName());

        if(((Optional<Product>) productOptional).isPresent()) {
            Product productSaved = ((Optional<Product>) productOptional).get();
            log.info(productSaved.toString());
            double difference = productSaved.getPrice() - product.getPrice();
            if(difference > minDifference) {
                Object saleProductOptional = findByNameProductSale.invoke(saleProductRepository, product.getName());
                if(((Optional<SaleProduct>) saleProductOptional).isPresent()) {
                    SaleProduct saleProduct = ((Optional<SaleProduct>) saleProductOptional).get();
                    saleProduct.setCurrentPrice(product.getPrice());
                    saleProduct.setDiscount(difference);
                    saleProduct.setCreated(Instant.now());
                    saveSaleProduct.invoke(saleProductRepository, saleProduct);
                } else {
                    saveSaleProduct.invoke(saleProductRepository, new SaleProduct(
                            product,
                            product.getPrice(),
                            product.getHref(),
                            difference));
                }
            }
        } else {
            log.info(saveProduct.invoke(productRepository, product).toString());
        }
    }
}
