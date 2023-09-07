package com.stedi.testing;

import com.getsimplex.steptimer.model.Customer;
import com.getsimplex.steptimer.utils.JedisClient;

public class TestJsonJedis {

    public static void main (String[] args) throws Exception{
        JedisClient jedisClient = new JedisClient();
        Object object = jedisClient.jsonGet("arr");
        System.out.println("Object "+object);
        Customer customer = new Customer();
        customer.setCustomerName("Sean Murdock");
        customer.setPhone("8017190908");
        customer.setEmail("scmurdock@gmail.com");
        customer.setGender("male");
        customer.setBirthDay("05/03/1979");

        jedisClient.jsonArrayAdd("TestCustomersArray","$" ,customer);

        object = jedisClient.jsonGet("TestCustomersArray");

        System.out.println("Updated customer array:"+object);


    }
}
