package com.arquillian.jms.e2e;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class MessageSenderImpl implements MessageSender {

	@Autowired
	private JmsTemplate consumerJmsTemplate;

    public void sendMessage(final String message) {

    	consumerJmsTemplate.send(new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {

                return session.createTextMessage(message);
            }
        });
    }
}