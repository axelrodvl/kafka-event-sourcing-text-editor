package co.axelrod.kafka.editor.kafka;

import co.axelrod.kafka.editor.model.Key;
import co.axelrod.kafka.editor.model.serdes.KeyKafkaSerde;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

@Component
@Slf4j
public class KeyStreamProcessor implements DisposableBean {
    private final Properties properties = new Properties();

    private static final String LETTERS_POSTFIX = "-letters";
    private static final String LETTERS_COUNT_POSTFIX = "-letters-count";
    private static final String WORDS_POSTFIX = "-words";

    @Getter
    private long keyCount;

    @Getter
    private long wordsCount;

    private final FileManager fileManager;

    private KafkaStreams streams;

    public KeyStreamProcessor(KafkaProperties kafkaProperties, FileManager fileManager) {
        this.fileManager = fileManager;

        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10);

        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    }

    public void start(String fileName) {
        keyCount = 0;
        wordsCount = 0;

        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, UUID.randomUUID().toString());

        fileManager.createFile(fileName + LETTERS_POSTFIX);
        fileManager.createFile(fileName + WORDS_POSTFIX);
        fileManager.createFile(fileName + LETTERS_COUNT_POSTFIX);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Key> originalStream = builder.stream(fileName, Consumed.with(Serdes.String(), new KeyKafkaSerde()));

        // Streaming letters from POJO
        originalStream.mapValues(key -> String.valueOf(key.getKeyChar()))
                .to(fileName + LETTERS_POSTFIX, Produced.with(Serdes.String(), Serdes.String()));

        KStream<String, String> lettersStream = builder
                .stream(fileName + LETTERS_POSTFIX, Consumed.with(Serdes.String(), Serdes.String()));

        // Counting letters
        lettersStream
                .peek((key, value) -> log.debug("[RAW STREAM] Key:" + key + ", value: " + value))
                .groupByKey()
                .count(Materialized.with(Serdes.String(), Serdes.Long()))
                .toStream()
                .peek((key, value) -> keyCount = value)
                .to(fileName + LETTERS_COUNT_POSTFIX, Produced.with(Serdes.String(), Serdes.Long()));

        // Streaming words
        lettersStream
                .groupByKey()
                .reduce((oldValue, newValue) -> oldValue + newValue)
                .toStream()
                .peek(((key, value) -> log.info("[WORDS] Key: " + key + ", value: " + value)))
                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
                .peek(((key, value) -> log.info("[WORDS] Key: " + key + ", value: " + value)))
                .to(fileName + WORDS_POSTFIX, Produced.with(Serdes.String(), Serdes.String()));

        streams = new KafkaStreams(builder.build(), properties);
        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    @Override
    public void destroy() {
        if (streams != null) {
            streams.close();
        }
    }
}
