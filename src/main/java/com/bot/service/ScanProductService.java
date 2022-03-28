package com.bot.service;

import com.bot.eshop.amazon.model.AmazonProduct;
import com.bot.eshop.amazon.model.AmazonSaleProduct;
import com.bot.eshop.pccomponentes.model.PcComponentesProduct;
import com.bot.eshop.pccomponentes.model.PcComponentesSaleProduct;
import com.bot.event.EmailEvent;
import com.bot.model.Product;
import com.bot.model.SaleProduct;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Transactional
public class ScanProductService {

    private final String findByName = "findByName";
    private final String save = "save";
    private final String removeById = "removeById";
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

        Optional<Product> productOptional = (Optional<Product>) findByNameProduct.invoke(productRepository, product.getName());

        if(productOptional.isPresent()) {
            Product productSaved = productOptional.get();
            log.info(productSaved.toString());
            double difference = productSaved.getPrice() - product.getPrice();
            Optional<SaleProduct> saleProductOptional = (Optional<SaleProduct>) findByNameProductSale.invoke(saleProductRepository, product.getName());
            manageSaleProduct(difference, saleProductOptional, product, saleProductRepository);
        } else {
            log.info(saveProduct.invoke(productRepository, product).toString());
        }
    }

    protected void manageSaleProduct(double difference, Optional<SaleProduct> saleProductOptional, Product product, Object saleProductRepository) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method saveSaleProduct = JpaRepository.class.getMethod(save, Object.class);
        Method removeByIdProductSale = saleProductRepository.getClass().getMethod(removeById, long.class);

        if(difference > minDifference) {
            if(saleProductOptional.isPresent()) {
                SaleProduct saleProduct = saleProductOptional.get();
                saleProduct.setCurrentPrice(product.getPrice());
                if(difference > saleProduct.getDiscount()) {
                    EmailEvent.areNewSales = true;
                }
                saleProduct.setDiscount(difference);
                saleProduct.setCreated(Instant.now());
                saveSaleProduct.invoke(saleProductRepository, saleProduct);
            } else {
                SaleProduct saleProduct;
                if(product instanceof PcComponentesProduct) {
                    saleProduct = new PcComponentesSaleProduct((PcComponentesProduct) product,
                            product.getPrice(),
                            product.getHref(),
                            difference);
                } else {
                    saleProduct = new AmazonSaleProduct((AmazonProduct) product,
                            product.getPrice(),
                            product.getHref(),
                            difference);
                }
                saveSaleProduct.invoke(saleProductRepository, saleProduct);
                EmailEvent.areNewSales = true;
            }
        } else {
            if(saleProductOptional.isPresent()) {
                removeByIdProductSale.invoke(saleProductRepository, saleProductOptional.get().getId());
            }
        }
    }
}
