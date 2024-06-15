package com.azbackend.azbackend;

import com.azure.identity.ClientSecretCredential;
import com.azure.messaging.servicebus.*;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/service-bus-topic")
@RequiredArgsConstructor
public class ServiceBusTopicController {


    @Value("${service_bus.topic.namespace}")
    private String serviceBusTopicNamespace;
    @Value("${service_bus.topic.name}")
    private String serviceBusTopicName;
    @Value("${service_bus.topic.subscription}")
    private String serviceBusTopicSubscription;
    private final ClientSecretCredential clientSecretCredential;

    @PostMapping(value = "/enqueue")
    public ResponseEntity enqueue() {
        ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                .fullyQualifiedNamespace(serviceBusTopicNamespace+".servicebus.windows.net")
                .credential(clientSecretCredential)
                .sender()
                .topicName(serviceBusTopicName)
                .buildClient();

        String mins = (new SimpleDateFormat("mm")).format(new Date());
        String hrs = (new SimpleDateFormat("HH")).format(new Date());

        String serviceBusMessageClientMesage = "TEST_MESSAGE_" + hrs + ":" + mins + UUID.randomUUID().toString();
        senderClient.sendMessage(new ServiceBusMessage(serviceBusMessageClientMesage));
        senderClient.close();
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/enqueue-multiple")
    public ResponseEntity enqueueMultiple() {
        ServiceBusSenderClient sender = new ServiceBusClientBuilder()
                .credential(serviceBusTopicNamespace + ".servicebus.windows.net", clientSecretCredential)
                .sender()
                .queueName(serviceBusTopicName)
                .buildClient();
        String mins = (new SimpleDateFormat("mm")).format(new Date());
        String hrs = (new SimpleDateFormat("HH")).format(new Date());
        String time = hrs + ":" + mins;
        ServiceBusMessageBatch batch = sender.createMessageBatch();
        Integer messageCount = 10;
        for (int i = 0; i < messageCount; i++) {
            String serviceBusMessageClientMesage = i + "-->" + "TEST_MESSAGE_" + time + "_" + UUID.randomUUID().toString();
            batch.tryAddMessage(new ServiceBusMessage(serviceBusMessageClientMesage));
        }
        sender.sendMessages(batch);
        sender.close();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/peek")
    public ResponseEntity peek() {
        ServiceBusReceiverClient receiver = new ServiceBusClientBuilder()
                .fullyQualifiedNamespace(serviceBusTopicNamespace + ".servicebus.windows.net")
                .credential(clientSecretCredential)
                .receiver()
                .topicName(serviceBusTopicName)
                .subscriptionName(serviceBusTopicSubscription)
                .buildClient();

        ServiceBusReceivedMessage serviceBusReceivedMessage = receiver.peekMessage();
        receiver.close();
        return ResponseEntity.ok(serviceBusReceivedMessage.getBody().toString());
    }

    @PostMapping("/dequeue")
    public ResponseEntity dequeue() {
        ServiceBusReceiverClient receiver = new ServiceBusClientBuilder()
                .fullyQualifiedNamespace(serviceBusTopicNamespace + ".servicebus.windows.net")
                .credential(clientSecretCredential)
                .receiver()
                .topicName(serviceBusTopicName)
                .subscriptionName(serviceBusTopicSubscription)
                .receiveMode(ServiceBusReceiveMode.RECEIVE_AND_DELETE)
                .buildClient();

        List<ServiceBusReceivedMessage> serviceBusReceivedMessage = receiver.receiveMessages(1).stream().toList();
        List<String> resBody = serviceBusReceivedMessage.stream().map(e -> e.getBody().toString()).toList();
        receiver.close();
        return ResponseEntity.ok(resBody);
    }

}
