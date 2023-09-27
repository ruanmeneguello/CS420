package com.stedi.testing;

import com.getsimplex.steptimer.datarepository.CustomerRepository;
import com.getsimplex.steptimer.datarepository.EmailMessageRepository;
import com.getsimplex.steptimer.datarepository.GenericRepository;
import com.getsimplex.steptimer.model.Customer;
import com.getsimplex.steptimer.model.EmailMessage;
import com.getsimplex.steptimer.utils.JedisClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TestJsonJedis {

    public static void main (String[] args) throws Exception{

        CustomerRepository customerRepository = new CustomerRepository();
        //JedisClient jedisClient = new JedisClient();

        Customer customer = new Customer();
        customer.setCustomerName("John Smith");
        customer.setPhone("8015551212");
        customer.setEmail("johnsmith@gmail.com");
        customer.setGender("male");
        customer.setBirthDay("01/01/2001");

//        EmailMessage emailMessage = new EmailMessage();
//
//        emailMessage.setMessageText("Hi");
//        emailMessage.setName("John Doe");
//        emailMessage.setSubject("Test");
//        emailMessage.setToAddress("johndoe@gmail.com");

        //jedisClient.jsonArrayAdd("TestCustomersArray","$" ,customer);

        customerRepository.addToArrayAtKey("$",customer);//$ means we are adding to the root of a JSON object rather than a nested array

        ArrayList<Customer> customers = customerRepository.getArrayAtKey("$");

//        for(Object object:jsonArray){
//            JSONObject jsonObject = (JSONObject) object;
//            System.out.println(jsonObject.toString());
//        }

        customers.forEach(eachCustomer->System.out.println(eachCustomer.getCustomerName()));
    }
}
