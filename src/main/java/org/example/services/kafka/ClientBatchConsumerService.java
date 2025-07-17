package org.example.services.kafka;


import org.example.avro.ClientBatch;
import org.example.avro.ClientData;
import org.example.dtos.ClientInputDto;
import org.example.models.Client;
import org.example.services.interfaces.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientBatchConsumerService {
    private final static Logger log = LoggerFactory.getLogger(ClientBatchConsumerService.class);
    private final ClientService clientService;

    public ClientBatchConsumerService(ClientService clientService) {
        this.clientService = clientService;
    }


    private List<ClientInputDto> convertAvroToClientDtos(List<ClientData> avroClients) {
        List<ClientInputDto> clientDtos = new ArrayList<>();
        for (ClientData avroClient: avroClients) {
            try {
                ClientInputDto clientDto = new ClientInputDto();
                clientDto.setNombre(avroClient.getNombre().toString());
                clientDto.setApellido(avroClient.getApellido().toString());

                clientDto.setFechaNacimiento(
                        LocalDate.parse(avroClient.getFechaNacimiento().toString())
                );

                clientDtos.add(clientDto);

            } catch (Exception e) {
                log.error("Error procesando cliente - Nombre: {}, Apellido: {}, Error: {}",
                        avroClient.getNombre(), avroClient.getApellido(), e.getMessage(), e);
                throw new IllegalArgumentException(
                        "Error procesando cliente " + avroClient.getNombre() + ": " + e.getMessage());
            }
        }
        log.debug("Convertidos {} clientes de Avro a DTOs", clientDtos.size());
        return clientDtos;
    }


    //NOTA 1*
    private void validateClientBatch(ClientBatch clientBatch) {
        if (clientBatch == null) {
            throw new IllegalArgumentException("ClientBatch no puede ser null");
        }

        if (clientBatch.getBatchId() == null || clientBatch.getBatchId().toString().trim().isEmpty()) {
            throw new IllegalArgumentException("BatchId es requerido");
        }

        if (clientBatch.getOwnerId() <= 0) {
            throw new IllegalArgumentException("OwnerId debe ser positivo");
        }

        if (clientBatch.getClients() == null || clientBatch.getClients().isEmpty()) {
            throw new IllegalArgumentException("La lista de clientes no puede estar vacía");
        }

        if (clientBatch.getTotalClients() != clientBatch.getClients().size()) {
            throw new IllegalArgumentException(
                    String.format("Inconsistencia en cantidad: esperados %d, recibidos %d",
                            clientBatch.getTotalClients(), clientBatch.getClients().size()));
        }
    }



    @Transactional(rollbackFor = Exception.class)
    public void processClientBatch(ClientBatch clientBatch) {
        try {
            validateClientBatch(clientBatch);
            List<ClientInputDto> clientDtos = convertAvroToClientDtos(clientBatch.getClients());

            log.debug("Iniciando procesamiento de {} clientes para owner {}",
                    clientDtos.size(), clientBatch.getOwnerId());

            List<Client> savedClients = clientService.createClientBatch(clientDtos, clientBatch.getOwnerId());

            log.info("Proceso de guardado finalizado - BatchId: {}, Clientes guardados: {}/{}",
                    clientBatch.getBatchId(), savedClients.size(), clientBatch.getTotalClients());
        } catch (Exception e){
            log.error("Error en processClientBatch - BatchId: {}, Error: {}",
                    clientBatch.getBatchId(), e.getMessage(), e);
            throw e;
        }
    }



    @KafkaListener(//NOTA 2*
            topics = "${kafka.topics.client-batch}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeClientBatch(//NOTA 3*
            @Payload ClientBatch clientBatch,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        try{
            log.info("Recibido lote de clientes - BatchId: {}, OwnerId: {}, Total: {}, " +
                            "Topic: {}, Partition: {}, Offset: {}",
                    clientBatch.getBatchId(), clientBatch.getOwnerId(),
                    clientBatch.getTotalClients(), topic, partition, offset);

            processClientBatch(clientBatch);
            // IMPORTANTE: Solo se confrima después del procesamiento exitoso
            acknowledgment.acknowledge(); //NOTA 4*

            log.info("Lote procesado exitosamente - BatchId: {}, OwnerId: {}, " +
                            "Clientes procesados: {}",
                    clientBatch.getBatchId(), clientBatch.getOwnerId(),
                    clientBatch.getTotalClients());
        } catch (Exception e){
            log.error("Error procesando lote de clientes - BatchId: {}, OwnerId: {}, " +
                            "Topic: {}, Partition: {}, Offset: {}, Error: {}",
                    clientBatch.getBatchId(), clientBatch.getOwnerId(),
                    topic, partition, offset, e.getMessage(), e);

            handleProcessingError(clientBatch, e, topic, partition, offset);
        }
    }



                // NOTA 5*
    private void handleProcessingError(ClientBatch clientBatch, Exception error,
                                       String topic, int partition, long offset) {

        log.error("=== ERROR CRÍTICO EN PROCESAMIENTO DE LOTE ===");
        log.error("BatchId: {}", clientBatch.getBatchId());
        log.error("OwnerId: {}", clientBatch.getOwnerId());
        log.error("Total Clientes: {}", clientBatch.getTotalClients());
        log.error("Topic: {}, Partition: {}, Offset: {}", topic, partition, offset);
        log.error("Error: {}", error.getMessage(), error);
        log.error("===============================================");
        /*
         * IMPORTANTE: NO SE LLAMA A acknowledgment.acknowledge() aca
         * Esto significa que Kafka reintentara este mensaje automáticamente
         * cuando el consumer se reinicie o reconecte
         */
    }


    // ====================================== NOTAS PA APRENDER =========================================
    /*

    NOTA 1* - Validaciones en Consumer:

    ¿Por qué validar si ya validamos en el Producer?

    Principio de "Defense in Depth" (Defensa en Profundidad):
    - El mensaje pudo haberse corrompido en tránsito
    - Otra aplicación pudo haber enviado mensajes malformados al mismo tópico
    - Los schemas de Avro pueden evolucionar y crear incompatibilidades
    - Es mejor prevenir que el procesamiento falle a mitad de camino

    Validaciones en Consumer vs Producer:
    Producer: Valida antes de enviar (evita enviar basura)
    Consumer: Valida antes de procesar (evita procesar basura)

    Esto garantiza robustez end-to-end

    --------------------------------------------------------------------

    NOTA 2* (Colocada en @KafkaListener)

    @KafkaListener: Es la anotación que convierte este método en un "consumidor" de Kafka

    topics = "${kafka.topics.client-batch}":
    - Especifica de qué tópico escuchar mensajes
    - Usa configuración desde application.yml: "client-batch-topic"

    groupId = "${kafka.consumer.group-id}":
    - Define el "grupo de consumidores" al que pertenece esta instancia
    - Kafka garantiza que cada mensaje se procese por UNA SOLA instancia del grupo
    - Si tienes 3 instancias del mismo grupo, Kafka distribuye mensajes entre las 3
    - Configurado en application.yml: "client-batch-group"

    containerFactory = "kafkaListenerContainerFactory":
    - Especifica qué configuración usar para este listener
    - Definida en KafkaConfig.java con configuraciones como:
      * Deserializadores (cómo convertir bytes a objetos)
      * Acknowledgment manual
      * Concurrencia (cuántos threads paralelos)


    NOTA 3* - Parámetros del método consumeClientBatch:
    @Payload ClientBatch clientBatch:
    - El mensaje principal deserializado automáticamente de Avro a objeto Java
    - Kafka + Schema Registry convierten los bytes del mensaje a ClientBatch

    @Header(KafkaHeaders.RECEIVED_TOPIC) String topic:
    - Metadato: de qué tópico vino el mensaje
    - Útil para logging y debugging

    @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition:
    - Metadato: de qué partición vino el mensaje
    - Ayuda a entender la distribución de carga

    @Header(KafkaHeaders.OFFSET) long offset:
    - Metadato: posición exacta del mensaje en la partición
    - Crucial para tracking y recuperación ante errores

    Acknowledgment acknowledgment:
    - Control MANUAL para confirmar que el mensaje fue procesado exitosamente
    - Si no llamamos acknowledgment.acknowledge(), Kafka reintentará el mensaje
    - Esto nos da control total sobre cuándo considerar un mensaje "completado"

    --------------------------------------------------------------------

     NOTA 4* - Acknowledgment (acknowledgment.acknowledge()):

    ¿Qué pasa cuando llamamos acknowledgment.acknowledge()?
    - Le decimos a Kafka: "Este mensaje fue procesado correctamente"
    - Kafka actualiza el "committed offset" para este grupo de consumidores
    - El mensaje NO se volverá a procesar

    ¿Qué pasa si NO llamamos acknowledge()?
    - Kafka considera que el mensaje falló
    - En el próximo reinicio del consumidor, el mensaje se reprocesará
    - Esto es perfecto para garantizar que no perdamos datos ante errores

    Flujo de ejemplo:
    1. Mensaje llega → offset 247
    2. Procesamos exitosamente → acknowledge()
    3. Kafka marca offset 247 como "committed"
    4. Si la app se reinicia, Kafka comienza desde offset 248

    Si falla:
    1. Mensaje llega → offset 247
    2. Falla el procesamiento → NO acknowledge()
    3. Si la app se reinicia, Kafka comienza desde offset 247 nuevamente

    --------------------------------------------------------------------

    NOTA 5* - Manejo de errores:

    ¿Qué estrategias de error handling podemos implementar?

    1. **Retry Automático**:
       - Kafka puede reintentar automáticamente si no hacemos acknowledge()
       - Útil para errores transitorios (BD temporalmente no disponible)

    2. **Dead Letter Queue (DLQ)**:
       - Enviar mensajes problemáticos a un tópico especial para revisión manual
       - Evita que mensajes "tóxicos" bloqueen el procesamiento

    3. **Circuit Breaker**:
       - Parar el consumo temporalmente si hay muchos errores consecutivos
       - Previene cascada de fallos

    4. **Alerting/Monitoring**:
       - Notificar a operaciones sobre errores críticos
       - Métricas para monitoreo de salud del sistema

    En nuestro caso, implementamos logging detallado y NO hacemos acknowledge(),
    lo que permite que Kafka reintente el mensaje automáticamente.

    IMPORTANTE: NO llamamos acknowledgment.acknowledge() en handleProcessingError()
    Esto significa que Kafka reintentará este mensaje automáticamente
    cuando el consumer se reinicie o reconecte

    ==================================================================
     */

}
