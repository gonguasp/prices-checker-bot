package com.bot.eshop.amazon.service;

import com.bot.config.Config;
import com.bot.eshop.amazon.model.AmazonProduct;
import com.bot.eshop.amazon.model.AmazonSaleProduct;
import com.bot.eshop.amazon.repository.AmazonProductRepository;
import com.bot.eshop.amazon.repository.AmazonSaleProductRepository;
import com.bot.service.ScanProductService;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Service
@Slf4j
@Data
@Transactional
public class AmazonService extends ScanProductService {

    @NonNull
    private final Config config;

    @Autowired
    private AmazonProductRepository amazonProductRepository;

    @Autowired
    private AmazonSaleProductRepository amazonSaleProductRepository;

    public List<AmazonSaleProduct> getSales() {
        List<AmazonSaleProduct> amazonSaleProductList = amazonSaleProductRepository.findAll();
        for (AmazonSaleProduct amazonSaleProduct : amazonSaleProductList) {
            amazonSaleProduct.add(Link.of(amazonSaleProduct.getHref(), "BUY"));
        }
        return amazonSaleProductList;
    }

    public void scanProducts() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        WebDriver driver = new ChromeDriver(loadChromeConfig());

        for (String url : config.getAmazon()) {
            log.info("Loading web page!");
            driver.get(url);
            log.info("Web page loaded!");
            log.info("Waiting for Javascript to finish");
            waitingForOrderingProducts(driver, true);
            log.info("Javascript finished");

            try {
                manageProduct(new AmazonProduct(
                                driver.findElement(By.id("productTitle")).getText(),
                                Double.parseDouble(
                                        driver.findElement(By.cssSelector("[class$='riceToPay']")).getText()
                                                .replace("€", "")
                                                .replace(",", ".")
                                                .replace("\n", ".")),
                                url),
                        amazonProductRepository, amazonSaleProductRepository);
            } catch (NoSuchElementException e) {
                log.error("Component " + url + " is not anymore available");
            }
        }

        driver.quit();
    }

    public boolean reset() {
        amazonProductRepository.deleteAll();
        amazonSaleProductRepository.deleteAll();
        return true;
    }
}
