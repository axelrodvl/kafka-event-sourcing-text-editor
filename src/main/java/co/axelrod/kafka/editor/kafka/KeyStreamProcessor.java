package co.axelrod.kafka.editor.kafka;

import lombok.Getter;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KeyStreamProcessor {
    private final Properties properties = new Properties();

    private static final String LETTERS_POSTFIX = "-letters";

    @Getter
    private int keyCount = 0;

    @Autowired
    private FileManager fileManager;

    private KafkaStreams streams;

    public KeyStreamProcessor(KafkaProperties kafkaProperties) {
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaProperties.getApplicationId());
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
    }

    public void start(String fileName) {
//        StreamsBuilder builder = new StreamsBuilder();
//        KStream<String, Key> originalStream = builder.stream(fileName, Consumed.with(Serdes.String(), new KeyKafkaSerde()));
//
//        //keyCount = originalStream.groupByKey().count().toStream();
//
//        KStream<String, String> letterStream = originalStream.mapValues((key) -> String.valueOf(key.getKeyChar()));
//
//        fileManager.createFile(fileName + LETTERS_POSTFIX);
//        letterStream.to(fileName + LETTERS_POSTFIX, Produced.with(Serdes.String(), Serdes.String()));
//
////        KStream<Long, String> wordStream = builder.stream("bad-git-letters", Consumed.with(Serdes.Long(), Serdes.String())).groupByKey().reduce(((value1, value2) -> {
////            System.out.println(value1);
////            System.out.println(value2);
////            return value1 + value2 + "WOW";
////        })).toStream();
////        wordStream.to("bad-git-letters3", Produced.with(Serdes.Long(), Serdes.String()));
//
//
////        KStream<String, String> wordStream = builder.stream("bad-git-letters", Consumed.with(Serdes.String(), Serdes.String()))
////                .groupByKey()
////                .reduce(new Reducer<String>() {
////                    @Override
////                    public String apply(String value1, String value2) {
////                        return value1 + value2;
////                    }
////                }).toStream();
////        wordStream.to("bad-git-letters3", Produced.with(Serdes.String(), Serdes.String()));
//
////        KStream<String, String> wordStream = builder.stream("bad-git-letters", Consumed.with(Serdes.String(), Serdes.String()))
////                .groupByKey()
////                .aggregate("", (key, value, aggregate) -> aggregate + value, Materialized.as("store").withValueSerde(Serdes.String())).toStream();
////        wordStream.to("bad-git-letters3", Produced.with(Serdes.String(), Serdes.String()));
//
////                .reduce((key, value) -> {
////            return "";
////        }).toStream("fds", Produced.with(Serdes.Long(), Serdes.String()));
//
////
////
////        KTable<String, Key> wordCounts = textLines
////                .flatMapValues(textLine -> Arrays.asList(textLine.getKeyChar()))
////                .groupBy((key, word) -> word)
////                .count(Materialized.<String, Key, KeyValueStore<Bytes, byte[]>>as("counts-store"));
////        wordCounts.toStream().to("WordsWithCountsTopic", Produced.with(Serdes.String(), Serdes.Long()));
//
//        streams = new KafkaStreams(builder.build(), properties);
//        streams.cleanUp();
//        streams.start();
//
//        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    public void destroy() {
//        if (streams != null) {
//            streams.close();
//        }
    }
}
