package org.example.services.kafka;

import org.example.avro.ClientBatch;
import org.example.avro.ClientData;
import org.example.dtos.ClientInputDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ClientBatchProducerService {
    private static final Logger log = LoggerFactory.getLogger(ClientBatchProducerService.class);

    private final KafkaTemplate<String, ClientBatch> kafkaTemplate;

    @Value("${kafka.topics.client-batch}")
    private String clientBatchTopic;

    public ClientBatchProducerService(KafkaTemplate<String, ClientBatch> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    // Genera ID único para identificar el lote utilizando UUID
    public String generateBatchId(){
        return "batch-" + UUID.randomUUID();
    }

    /* NOTA1* (Colocada en convertToAvro)
    LocalDate es un formato de java, incompatibles con otros tipos de fechas en otros lenguajes.
    Una buena es utilizar .toString para formatear la fecha a formato ISO 8601, el cual es utilizado
    de forma universal por muchos lenguajes.
    Esto logra una compatibilidad universal en la mensajería, la cual se vuelve mas escalable
     */

    // Convierte una lista de client input a client data
    private List<ClientData> convertToAvro(List<ClientInputDto> clients){
        List<ClientData> avroClients = new ArrayList<>();
        for (ClientInputDto clientDto : clients) {
            ClientData clientData = ClientData.newBuilder()
                    .setNombre(clientDto.getNombre())
                    .setApellido(clientDto.getApellido())
                    .setFechaNacimiento(clientDto.getFechaNacimiento().toString()) // NOTA 1*
                    .build();

            avroClients.add(clientData);
        }
        log.debug("Convertidos {} clientes a formato Avro", avroClients.size());
        return avroClients;
    }




    /* NOTA2* (Colocada en setupCallback

    ¿Qué es una PARTITION?
    Una partición es como un "carril" dentro de un tópico:
    Analogía del estacionamiento:
    Tópico: El estacionamiento completo (client-batch-topic)
    Particiones: Los diferentes sectores (A, B, C, D)
    Mensajes: Los autos estacionados
    Tópico: client-batch-topic
    ├── Partition 0: [msg1] [msg2] [msg3]
    ├── Partition 1: [msg4] [msg5] [msg6]
    └── Partition 2: [msg7] [msg8] [msg9]


    ¿Qué es un OFFSET?
    Un offset es la "posición exacta" del mensaje dentro de una partición:
    Partition 0: [msg1] [msg2] [msg3] [msg4]
                 ↑      ↑      ↑      ↑
    Offset:      0      1      2      3


    En nuestro código:
    result.getRecordMetadata().partition() // → Ejemplo: 1
    result.getRecordMetadata().offset()    // → Ejemplo: 247

    Significa: "Tu mensaje se guardó en la partición 1, posición 247"

    ¿Para qué sirve esta información?
    Debugging: Mensaje enviado a partition: 1, offset: 247 -> "Si hay problemas, busca en partition 1, posición 247"
    Tracking: Partition indica balanceamiento de carga y Offset orden exacto de llegada.
    Monitoreo: Ver si los mensajes se distribuyen bien entre particiones y verificar que los mensajes llegaron en orden.
     */

    // Configura los callbacks para manejar el resultado del envío a Kafka
    private void setupCallbacks(ListenableFuture<SendResult<String, ClientBatch>> future,
                                String batchId, Long ownerId, int clientCount){
        // future es la promesa de resultado de la petición asincrona, callback se ejecuta automáticamente
        // una vez la petición asincrona termina
        future.addCallback(new ListenableFutureCallback<SendResult<String, ClientBatch>>() {
            @Override
            public void onSuccess(SendResult<String, ClientBatch> result) {
                log.info("Lote enviado exitosamente a Kafka - BatchId: {}, OwnerId: {}, " +
                                "Clientes: {}, Partition: {}, Offset: {}",
                        batchId, ownerId, clientCount,
                        result.getRecordMetadata().partition(), //NOTA 2*
                        result.getRecordMetadata().offset());

            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Error al enviar lote a Kafka - BatchId: {}, OwnerId: {}, " +
                                "Clientes: {}, Error: {}",
                        batchId, ownerId, clientCount, ex.getMessage(), ex);

                /* Aca se podría implementar una lógica de reespaldo por fallos: reintentos,
                   notificaciones, email, guardar en registro el error, etc.   */
            }
        });
    }


    // Método principal de envio de clientes por kafka
    public String sendClientBatch(List<ClientInputDto> clients, Long ownerId) {
        try {
            // Genera el id
            String batchId = generateBatchId();

            // Convierte los dtos a formato Avro (los serializa)
            List<ClientData> avroClients = convertToAvro(clients);

            // Crea el obj client batch
            ClientBatch clientBatch = ClientBatch.newBuilder()
                    .setBatchId(batchId)
                    .setOwnerId(ownerId)
                    .setClients(avroClients)
                    .setTimestamp(Instant.now().toEpochMilli()) // Se utiliza para debug, audit y metrics
                    .setTotalClients(clients.size()) // Se utiliza para validación y control, verifica integridad
                    .build();
            // Notifica el envio de mensajeria (facilita debug)
            log.info("Enviando lotes de clientes a kafka - BatchId: {}, OwnerId: {}, Total{}",
                    batchId, ownerId, clients.size());

            ListenableFuture<SendResult<String, ClientBatch>> future = kafkaTemplate.send(clientBatchTopic, batchId, clientBatch);

            // Se config callbacks para manejos de exito/error
            setupCallbacks(future, batchId, ownerId, clients.size());
            return batchId;

        } catch (Exception e) {
            log.error("Error en el envio de lote de clientes a Kafka - OwnerId: {}, Error: {}",
                    ownerId, e.getMessage(), e);
            throw new RuntimeException("Error al procesar lote de clientes de forma asíncrona: " + e.getMessage());
        }
    }
}
