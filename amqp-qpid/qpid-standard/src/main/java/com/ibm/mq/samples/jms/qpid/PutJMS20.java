/*
* (c) Copyright IBM Corporation 2020
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.ibm.mq.samples.jms.qpid;

import java.util.logging.*;

import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSConsumer;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;

public class PutJMS20 extends BaseJMS20 {
  private JMSProducer destination = null;
  private Queue replyQueue = null;

  public PutJMS20 (Options o) {
    super(o);
  }

  public PutJMS20 (Options o, ConnectionFactory cf) {
    super(o, cf);
  }

  public PutJMS20 prep() {
    logger.info("Creating JMS context");
    super.prep();
    try {
      logger.info("Creating destination");
      destination =
        context.createProducer()
          .setDeliveryMode(DeliveryMode.NON_PERSISTENT)
          .setPriority(options.priority())
          .setJMSType(Constants.TYPEA)
          ;

      if (options.persist()) {
        destination.setDeliveryMode(DeliveryMode.PERSISTENT);
      }
      if (options.delay()) {
        destination.setDeliveryDelay(30 * Constants.SECOND);
      }
      if (options.expire()) {
        logger.info("Setting message expiration");
        destination.setTimeToLive(2 * Constants.MINUTE);
      }

      if (options.reply()) {
        logger.info("Setting reply to");
        if (null != rq) {
          logger.info("Setting reply to requested queue");
          destination.setJMSReplyTo(rq);
          replyQueue = rq;
        } else {
          logger.info("Setting reply to temporary queue");
          TemporaryQueue trq = context.createTemporaryQueue();
          destination.setJMSReplyTo(trq);
          replyQueue = trq;
        }
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return this;
  }


  public PutJMS20 send(String text) {
    if (! verify()) {
      logger.severe("Not able to run sample");
      return this;
    }
    int quantity = options.numberOfMessages();

    logger.info("Sending " + quantity + " message batches");

    try {
      if (null != destination && 0 < quantity) {
        for (int i = 0; i < quantity; i++) {
          int place = i + 1;
          logger.info("Sending message " + place + " of " + quantity);
          logger.info("Creating Text Message");

          //TextMessage message = context.createTextMessage(place + " : " + text);
          Message[] msgs = createMessages(place, text);

          for (Message message : msgs) {
            if (options.custom()) {
              message.setBooleanProperty(Constants.TYPEA, true);
              message.setBooleanProperty(Constants.TYPEB, false);
            }

            logger.info("Sending message via producer");

            switch(destinationType) {
              case Constants.DEST_QUEUE:
                destination.send(q, message);
                break;
              case Constants.DEST_TOPIC:
                destination.send(t, message);
                break;
            }

            new Inspector(message)
              .showMessageType()
              .showMessageHeaders()
              .showProperties()
              ;

          }
          waitForABit(Constants.SECOND);
          if (options.reply()) {
            logger.info("Expecting some replies");
            processReplies();
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return this;
  }

  public PutJMS20 close() {
    super.close();
    return this;
  }

  private Message[] createMessages(int place, String text) {
    return new Message[] {
      context.createTextMessage(place + " : " + text)
      ,createBytesMessage(place, text)
      ,createStreamMessage(place, text)
      ,createObjectMessage(place, text)
      ,createMapMessage(place, text)
    };
  }

  private BytesMessage createBytesMessage(int place, String text) {
    BytesMessage bm = context.createBytesMessage();
    try {
      bm.writeInt(place);
      bm.writeUTF("😉 BytesMessage");
      bm.writeUTF(text);
    } catch (JMSException e) {
      logger.warning("Error building BytesMessage " + e.getErrorCode());
    }
    return bm;
  }

  private StreamMessage createStreamMessage(int place, String text) {
    StreamMessage sm = context.createStreamMessage();
    try {
      sm.writeInt(place);
      sm.writeString("🤓 StreamMessage");
      sm.writeString(text);
    } catch (JMSException e) {
      logger.warning("Error building BytesMessage " + e.getErrorCode());
    }
    return sm;
  }

  private ObjectMessage createObjectMessage(int place, String text) {
    ObjectMessage om = context.createObjectMessage();
    try {
      om.setObject(new Data(place, "🦺 ObjectMessage", text));
    } catch (JMSException e) {
      logger.warning("Error building ObjectMessage " + e.getErrorCode());
    }
    return om;
  }

  private MapMessage createMapMessage(int place, String text) {
    MapMessage mm = context.createMapMessage();
    try {
      mm.setInt("place", place);
      mm.setString("utf string", "🛄 MapMessage");
      mm.setString("message text", text);
    } catch (JMSException e) {
      logger.warning("Error building MapMessage " + e.getErrorCode());
    }
    return mm;
  }

  private void processReplies() {
    JMSConsumer mc = null;
    mc = context.createConsumer(replyQueue);
    boolean incoming = true;
    while (incoming) {
      logger.info("Reading reply message");
      incoming = processMessage((Message) mc.receive(10 * Constants.SECOND)).haveMessage();
    }
  }

  private Inspector processMessage(Message msg) {
    Inspector i = new Inspector(msg);
    try {
      i.showMessageType()
        .showMessageBody()
        ;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return i;
  }

}
