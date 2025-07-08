from confluent_kafka import Consumer, KafkaError
import requests
import json

bootstrap_servers = "localhost:9092"
topic = "currentGame"
group_id = "groupCurrent"

# Création d'une instance de consommateur Kafka
consumer = Consumer({
"bootstrap.servers": bootstrap_servers,
"group.id": group_id,
"auto.offset.reset": "earliest"
})

# S'abonner au topic Kafka
consumer.subscribe([topic])

# Lire les messages du topic Kafka
while True:
    msg = consumer.poll(10.0) # timeout de 1 seconde

    if msg is None:
        continue
    if msg.error():
        if msg.error().code() == KafkaError._PARTITION_EOF:
            print("Reached end of partition")
        else:
            print("Error: {}".format(msg.error()))
    else:
        print(format(msg.value().decode("utf-8")))