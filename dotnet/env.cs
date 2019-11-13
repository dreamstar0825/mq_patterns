﻿using System;
using System.IO;
using System.Collections.Generic;

using Newtonsoft.Json;


namespace ibmmq_samples
{
    class Env
    {
        public class MQEndPoints
        {
            public List<ConnVariables> mq_endpoints;
        }

        public class ConnVariables
        {
            public string host = null;
            public string qmgr = null;
            public int port = 0;
            public string channel = null;
            public string queue_name = null;
            public string model_queue_name = null;
            public string topic_name = null;
            public string app_user = null;
            public string app_password = null;
            public string cipher_suite = null;
            public string key_repository = null;
            public void dump()
            {
                Console.WriteLine("hostname {0} ", host);
                Console.WriteLine("port {0} ", port);
                Console.WriteLine("qmgr {0} ", qmgr);
                Console.WriteLine("channel {0} ", channel);
                Console.WriteLine("queue {0} ", queue_name);
                Console.WriteLine("topic {0} ", topic_name);
                Console.WriteLine("app_user {0} ", app_user);
                //Console.WriteLine("app_password {0} ", app_password);
                Console.WriteLine("cipherSpec {0} ", cipher_suite);
                Console.WriteLine("sslKeyRepository{0} ", key_repository);
            }
        }

        private MQEndPoints points = null;
        private Env.ConnVariables conn = null;

        internal ConnVariables Conn { get => conn; set => conn = value; }

        public bool EnvironmentIsSet()
        {
            bool isSet = false;

            try
            {
                Console.WriteLine("Looking for file");
                using (StreamReader r = new StreamReader("env.json"))
                {
                    Console.WriteLine("File found");
                    string json = r.ReadToEnd();

                    points = JsonConvert.DeserializeObject<Env.MQEndPoints>(json);

                    if (points != null && points.mq_endpoints != null && points.mq_endpoints.Count > 0)
                    {
                        Conn = points.mq_endpoints[0];
                        Conn.dump();
                        isSet = true;
                    }
                    else
                    {
                        Console.WriteLine("MQ Settings not found, unable to determine connection variables");
                    }
                    Console.WriteLine("");
                }
                return isSet;
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception caught: {0}", e);
                Console.WriteLine(e.GetBaseException());
                return isSet;
            }

        }

    }
}
