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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.UUID;

@Component
@Slf4j
public class KeyStreamProcessor {
    private final Properties properties = new Properties();

    private static final String WORDS_POSTFIX = "-words";
    private static final String LETTERS_POSTFIX = "-letters";
    private static final String LETTERS_COUNT_POSTFIX = "-letters-count";

    @Getter
    private long keyCount;

    @Autowired
    private FileManager fileManager;

    private KafkaStreams streams;

    public KeyStreamProcessor(KafkaProperties kafkaProperties) {
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10);
    }

    public void start(String fileName) {
        keyCount = 0;
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, UUID.randomUUID().toString());

        fileManager.createFile(fileName + LETTERS_POSTFIX);
        fileManager.createFile(fileName + WORDS_POSTFIX);
        fileManager.createFile(fileName + LETTERS_COUNT_POSTFIX);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Key> originalStream = builder.stream(fileName, Consumed.with(Serdes.String(), new KeyKafkaSerde()));
        KStream<String, String> letterStream = originalStream.mapValues((key) -> String.valueOf(key.getKeyChar()));
        letterStream.to(fileName + LETTERS_POSTFIX, Produced.with(Serdes.String(), Serdes.String()));

        // Counting letters
        countLetters(fileName, builder);

//        StreamsBuilder builder = new StreamsBuilder();
//        /*        KStream<String, Key> originalStream = */builder.stream(fileName, Consumed.with(Serdes.String(), new KeyKafkaSerde()))
//                .peek((key, value) -> log.info("[Raw stream] Key:" + key + ", value: " + value.getKeyChar()))
//                .mapValues((key) -> String.valueOf(key.getKeyChar()))
//                .peek((key, value) -> log.info("[After mapping to chars] Key:" + key + ", value: " + value.toString()))
//                .groupByKey()
//                .count()
//                .toStream()
//                .peek((key, value) -> log.info("[Counting] Key:" + key + ", value: " + value.toString()))
//                .to(fileName + LETTERS_COUNT_POSTFIX, Produced.with(Serdes.String(), Serdes.Long()));

        // Streaming words
//        builder.stream(fileName + LETTERS_POSTFIX, Consumed.with(Serdes.String(), Serdes.String()))
//                .groupByKey()
//                .reduce((oldValue, newValue) -> oldValue + newValue)
//                .toStream()
//                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
//                .to(fileName + WORDS_POSTFIX, Produced.with(Serdes.String(), Serdes.String()));

//                .flatMapValues(value -> Arrays.asList(value))
//                .groupBy((key, value) -> value)
//                .count();
//        lettersCount.toStream().to(fileName + LETTERS_COUNT_POSTFIX, Produced.with(Serdes.String(), Serdes.Long()));

//        KStream<Long, String> wordStream = builder.stream("bad-git-letters", Consumed.with(Serdes.Long(), Serdes.String())).groupByKey().reduce(((value1, value2) -> {
//            System.out.println(value1);
//            System.out.println(value2);
//            return value1 + value2 + "WOW";
//        })).toStream();
//        wordStream.to("bad-git-letters3", Produced.with(Serdes.Long(), Serdes.String()));


//        KStream<String, String> wordStream = builder.stream("bad-git-letters", Consumed.with(Serdes.String(), Serdes.String()))
//                .groupByKey()
//                .reduce(new Reducer<String>() {
//                    @Override
//                    public String apply(String value1, String value2) {
//                        return value1 + value2;
//                    }
//                }).toStream();
//        wordStream.to("bad-git-letters3", Produced.with(Serdes.String(), Serdes.String()));

//        KStream<String, String> wordStream = builder.stream("bad-git-letters", Consumed.with(Serdes.String(), Serdes.String()))
//                .groupByKey()
//                .aggregate("", (key, value, aggregate) -> aggregate + value, Materialized.as("store").withValueSerde(Serdes.String())).toStream();
//        wordStream.to("bad-git-letters3", Produced.with(Serdes.String(), Serdes.String()));

//                .reduce((key, value) -> {
//            return "";
//        }).toStream("fds", Produced.with(Serdes.Long(), Serdes.String()));

//
//
//        KTable<String, Key> wordCounts = textLines
//                .flatMapValues(textLine -> Arrays.asList(textLine.getKeyChar()))
//                .groupBy((key, word) -> word)
//                .count(Materialized.<String, Key, KeyValueStore<Bytes, byte[]>>as("counts-store"));
//        wordCounts.toStream().to("WordsWithCountsTopic", Produced.with(Serdes.String(), Serdes.Long()));

        streams = new KafkaStreams(builder.build(), properties);
        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private void countLetters(String fileName, StreamsBuilder builder) {
        builder.stream(fileName + LETTERS_POSTFIX, Consumed.with(Serdes.String(), Serdes.String()))
                .peek((key, value) -> log.debug("[Raw stream] Key:" + key + ", value: " + value))
                .groupByKey()
                .count(Materialized.with(Serdes.String(), Serdes.Long()))
                .toStream()
                .peek((key, value) -> keyCount = value)
                .to(fileName + LETTERS_COUNT_POSTFIX, Produced.with(Serdes.String(), Serdes.Long()));

//        builder.stream(fileName + LETTERS_COUNT_POSTFIX, Consumed.with(Serdes.String(), Serdes.Long()))
//                .peek((key, value) -> {
//                    keyCount = value;
//                });
    }

    public void destroy() {
        if (streams != null) {
            streams.close();
        }
    }
}
