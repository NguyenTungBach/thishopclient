package com.example.thishopclient.controller;

import com.example.thishopclient.entity.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class ProductController {

    private final String REST_API_LIST = "http://localhost:8088/api/v1/admin/product";
    private final String REST_API_CREATE = "http://localhost:8088/api/v1/admin/product";
    private final String REST_API_PRODUCT_BUY_BY_ID = "http://localhost:8088/api/v1/admin/product/";

    private static Client createJerseyRestClient() {
        ClientConfig clientConfig = new ClientConfig();
        // Config logging for client side
        clientConfig.register( //
                new LoggingFeature( //
                        Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME), //
                        Level.INFO, //
                        LoggingFeature.Verbosity.PAYLOAD_ANY, //
                        10000));
        return ClientBuilder.newClient(clientConfig);
    }

    @GetMapping(value = "/getProducts")
    public String index(Model model) {
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_LIST);
        List<Product> ls = target.request(MediaType.APPLICATION_JSON_TYPE).get(List.class);
        model.addAttribute("lsProduct", ls);
        return "index";
    };

    //Create
    @GetMapping(value = "/createProduct")
    public String createProduct() {
        return "create";
    }

    @PostMapping("/createProduct")
    public String createUser(@RequestParam String name,
                             @RequestParam int price,
                             @RequestParam int quantity) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);

        String jsonUser = convertToJson(product);

        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_CREATE);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(jsonUser, MediaType.APPLICATION_JSON));
        return "redirect:/getProducts";
    }

    //Update
    @RequestMapping(value = "/updateProduct/{id}",method = RequestMethod.POST)
    public String updateUser(Model model, @PathVariable int id,@RequestParam int quantity) {
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_PRODUCT_BUY_BY_ID + id +"?quantity=" + quantity);
        target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(null, MediaType.APPLICATION_JSON));
        return "redirect:/getProducts";
    }

    private static String convertToJson(Product product) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(product);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
