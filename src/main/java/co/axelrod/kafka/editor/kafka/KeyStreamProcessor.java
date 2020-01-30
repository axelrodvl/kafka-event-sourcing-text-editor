package co.axelrod.kafka.editor.kafka;

import co.axelrod.kafka.editor.model.Key;
import co.axelrod.kafka.editor.model.serdes.KeyKafkaSerde;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
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

    private static final String WORDS_POSTFIX = "-words";
    private static final String WORDS_COUNT_POSTFIX = "-words-count";
    private static final String LETTERS_POSTFIX = "-letters";
    private static final String LETTERS_COUNT_POSTFIX = "-letters-count";

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
        originalStream.mapValues(key -> String.valueOf(key.getKeyChar()))
                .to(fileName + LETTERS_POSTFIX, Produced.with(Serdes.String(), Serdes.String()));
        KStream<String, String> lettersStream = builder.stream(fileName + LETTERS_POSTFIX, Consumed.with(Serdes.String(), Serdes.String()));

        lettersStream
                .peek((key, value) -> log.debug("[Raw stream] Key:" + key + ", value: " + value))
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
                .peek(((key, value) -> System.out.println("[WORDS] Key: " + key + ", value: " + value)))
                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
                .peek(((key, value) -> System.out.println("[WORDS] Key: " + key + ", value: " + value)))
                .to(fileName + WORDS_POSTFIX, Produced.with(Serdes.String(), Serdes.String()));

        //KStream<String, String> wordsStream = builder.stream(fileName + WORDS_POSTFIX, Consumed.with(Serdes.String(), Serdes.String()));

//        KTable<String, Long> wordsCountTable = wordsStream
//                .groupBy((key, value) -> value)
//                .count(Materialized.with(Serdes.String(), Serdes.Long()));
//
//        wordsCountTable
//                .toStream()
//                .peek(((key, value) -> {
//                    System.out.println("[WORDS COUNT 2] Key: " + key + ", value: " + value);
//                }))
//                .peek(((key, value) -> wordsCount = value))
//                .to(fileName + WORDS_COUNT_POSTFIX, Produced.with(Serdes.String(), Serdes.Long()));

        // Serializers/deserializers (serde) for String and Long types
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

// Construct a `KStream` from the input topic "streams-plaintext-input", where message values
// represent lines of text (for the sake of this example, we ignore whatever may be stored
// in the message keys).
        KStream<String, String> textLines = builder.stream(fileName + WORDS_POSTFIX, Consumed.with(stringSerde, stringSerde));

        KTable<String, Long> wordCounts = textLines
                // Split each text line, by whitespace, into words.  The text lines are the message
                // values, i.e. we can ignore whatever data is in the message keys and thus invoke
                // `flatMapValues` instead of the more generic `flatMap`.
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                // We use `groupBy` to ensure the words are available as message keys
                .groupBy((key, value) -> value)
                // Count the occurrences of each word (message key).
                .count();

// Convert the `KTable<String, Long>` into a `KStream<String, Long>` and write to the output topic.
        wordCounts.toStream()
                .peek(((key, value) -> wordsCount = value))
                .to(fileName + WORDS_COUNT_POSTFIX, Produced.with(stringSerde, longSerde));


//        wordsStream
//                .groupBy((key, value) -> value)
//                .count(Materialized.with(Serdes.String(), Serdes.Long()))
//                .toStream()
//                .peek(((key, value) -> {
//                    System.out.println("[WORDS COUNT 2] Key: " + key + ", value: " + value);
//                }))
//                .peek(((key, value) -> wordsCount = value));
        //.to(fileName + WORDS_COUNT_POSTFIX, Produced.with(Serdes.String(), Serdes.Long()));

//                .peek(((key, value) -> {
//                    System.out.println("[WORDS COUNT] Key: " + key + ", value: " + value);
//                }))
//                .groupByKey()
//                .count(Materialized.with(Serdes.String(), Serdes.Long()))
//                .toStream()
//                .peek(((key, value) -> {
//                    System.out.println("[WORDS COUNT 2] Key: " + key + ", value: " + value);
//                }))
//                .peek(((key, value) -> wordsCount = value))
//                .to(fileName + WORDS_COUNT_POSTFIX, Produced.with(Serdes.String(), Serdes.Long()));

//
//                .flatMapValues(value -> Arrays.asList(value))
//                .groupBy((key, value) -> value)
//                .count();
//        lettersCount.toStream().to(fileName + LETTERS_COUNT_POSTFIX, Produced.with(Serdes.String(), Serdes.Long()));

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

    }

    @Override
    public void destroy() {
        if (streams != null) {
            streams.close();
        }
    }
}
