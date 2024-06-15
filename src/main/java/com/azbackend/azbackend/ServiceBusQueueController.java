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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/service-bus-queue")
@RequiredArgsConstructor
public class ServiceBusQueueController {


    @Value("${service_bus.queue.namespace}")
    private String serviceBusQueueNamespace;
    @Value("${service_bus.queue.name}")
    private String serviceBusQueueName;

    private final ClientSecretCredential clientSecretCredential;

    @PostMapping(value = "/enqueue")
    public ResponseEntity enqueue() {
        ServiceBusSenderClient sender = new ServiceBusClientBuilder()
                .credential(serviceBusQueueNamespace + ".servicebus.windows.net", clientSecretCredential)
                .sender()
                .queueName(serviceBusQueueName)
                .buildClient();
        String mins = (new SimpleDateFormat("mm")).format(new Date());
        String hrs = (new SimpleDateFormat("HH")).format(new Date());

        String serviceBusMessageClientMesage = "TEST_MESSAGE_" + hrs + ":" + mins + UUID.randomUUID().toString();
        sender.sendMessage(new ServiceBusMessage(serviceBusMessageClientMesage));
        sender.close();
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/enqueue-multiple")
    public ResponseEntity enqueueMultiple() {
        ServiceBusSenderClient sender = new ServiceBusClientBuilder()
                .credential(serviceBusQueueNamespace + ".servicebus.windows.net", clientSecretCredential)
                .sender()
                .queueName(serviceBusQueueName)
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

    @PostMapping(value = "/dequeue-all")
    public ResponseEntity dequeueAll() throws InterruptedException {
        List<String> a = new ArrayList<>();
        ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
                .fullyQualifiedNamespace(serviceBusQueueNamespace + ".servicebus.windows.net")
                .credential(clientSecretCredential)
                .processor()
                .queueName(serviceBusQueueName)
                .processMessage(context -> processMessage(context, a))
                .processError(context -> processError(context))
                .buildProcessorClient();

        System.out.println("Starting the processor");
        processorClient.start();
        TimeUnit.SECONDS.sleep(10);
        System.out.println("Stopping and closing the processor");
        processorClient.close();
        return ResponseEntity.ok().body(a);

    }

    @PostMapping("/peek")
    public ResponseEntity peek() {
        ServiceBusReceiverClient receiver = new ServiceBusClientBuilder()
                .fullyQualifiedNamespace(serviceBusQueueNamespace + ".servicebus.windows.net")
                .credential(clientSecretCredential)
                .receiver()
                .queueName(serviceBusQueueName)
                .buildClient();

        ServiceBusReceivedMessage serviceBusReceivedMessage = receiver.peekMessage();
        System.out.println(serviceBusReceivedMessage.getBody());
        receiver.close();
        return ResponseEntity.ok(serviceBusReceivedMessage.getBody().toString());
    }

    @PostMapping("/dequeue")
    public ResponseEntity dequeue() {
        ServiceBusReceiverClient receiver = new ServiceBusClientBuilder()
                .fullyQualifiedNamespace(serviceBusQueueNamespace + ".servicebus.windows.net")
                .credential(clientSecretCredential)
                .receiver()
                .queueName(serviceBusQueueName)
                .receiveMode(ServiceBusReceiveMode.RECEIVE_AND_DELETE)
                .buildClient();

        List<ServiceBusReceivedMessage> serviceBusReceivedMessage = receiver.receiveMessages(1).stream().toList();
        List<String> resBody = serviceBusReceivedMessage.stream().map(e -> e.getBody().toString()).toList();
        receiver.close();
        return ResponseEntity.ok(resBody);
    }

    private static void processMessage(ServiceBusReceivedMessageContext context, List<String> a) {
        ServiceBusReceivedMessage message = context.getMessage();
        System.out.printf("Processing message. Session: %s, Sequence #: %s. Contents: %s%n", message.getMessageId(),
                message.getSequenceNumber(), message.getBody());
        a.add(message.getBody().toString());
    }

    private static void processError(ServiceBusErrorContext context) {
        System.out.printf("Error when receiving messages from namespace: '%s'. Entity: '%s'%n",
                context.getFullyQualifiedNamespace(), context.getEntityPath());

        if (!(context.getException() instanceof ServiceBusException)) {
            System.out.printf("Non-ServiceBusException occurred: %s%n", context.getException());
            return;
        }

        ServiceBusException exception = (ServiceBusException) context.getException();
        ServiceBusFailureReason reason = exception.getReason();

        if (reason == ServiceBusFailureReason.MESSAGING_ENTITY_DISABLED
                || reason == ServiceBusFailureReason.MESSAGING_ENTITY_NOT_FOUND
                || reason == ServiceBusFailureReason.UNAUTHORIZED) {
            System.out.printf("An unrecoverable error occurred. Stopping processing with reason %s: %s%n",
                    reason, exception.getMessage());
        } else if (reason == ServiceBusFailureReason.MESSAGE_LOCK_LOST) {
            System.out.printf("Message lock lost for message: %s%n", context.getException());
        } else if (reason == ServiceBusFailureReason.SERVICE_BUSY) {
            try {
                // Choosing an arbitrary amount of time to wait until trying again.
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.err.println("Unable to sleep for period of time");
            }
        } else {
            System.out.printf("Error source %s, reason %s, message: %s%n", context.getErrorSource(),
                    reason, context.getException());
        }
    }
}
