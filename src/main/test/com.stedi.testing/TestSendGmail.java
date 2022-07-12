package com.stedi.testing;

import com.getsimplex.steptimer.utils.SendGmail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class TestSendGmail {
 public static void main (String[] args) {
     SendGmail.send("scmurdock@gmail.com", "Hello, this is a test", "Testing");
 }
}
