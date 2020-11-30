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

package com.ibm.mq.samples.jms;

import java.util.logging.*;

import com.ibm.mq.samples.jms.BasicProducer;
import com.ibm.mq.samples.jms.BasicConsumerWrapper;


public class BasicSampleDriver {
  private static final String MODE_PUT = "put";
  private static final String MODE_GET = "get";
  private static final String MODE_DEFAULT = MODE_PUT;

  private static final int DEFAULT_PUT_COUNT = 10;

  private static final Logger logger = Logger.getLogger("com.ibm.mq.samples.jms");

  private String mode = MODE_DEFAULT;
  private int numberOfMessages = DEFAULT_PUT_COUNT;

  public static void main(String[] args) {
    new BasicSampleDriver()
      .determineMode(args)
      .parseArguments(args)
      .runSample();
  }

  private BasicSampleDriver determineMode(String[] args) {
    if (args.length > 0) {
      String requestedMode = args[0].toLowerCase();
      logger.info("Requested mode is " + requestedMode);

      switch(requestedMode) {
        case MODE_PUT:
        case MODE_GET:
          mode = requestedMode;
          break;
        }
      }

      logger.info("Will be running " + mode + " sample");
      return this;
  }

  private BasicSampleDriver parseArguments(String[] args) {
    switch (mode) {
      case MODE_PUT:
        break;
      case MODE_GET:
        logger.info("processing put options");
        if (args.length > 1) {
            try {
                numberOfMessages = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                logger.info("Defaulting number of puts");
            }
        }
    }
    return this;
  }

  public BasicSampleDriver runSample() {
    switch(mode) {
      case MODE_PUT:
        doPut();
        break;
      case MODE_GET:
        doGet();
        break;
    }
    return this;
  }

  public void doPut() {
    logger.info("Will be putting " + numberOfMessages + " messages");
    BasicProducer bp = new BasicProducer(BasicProducer.PRODUCER_PUT);
    bp.send("This is a Put message from the sample driver", numberOfMessages);
    bp.close();
  }

  public void doGet() {
    logger.info("Will be getting messages");
    BasicConsumerWrapper.performGet();
  }

}
