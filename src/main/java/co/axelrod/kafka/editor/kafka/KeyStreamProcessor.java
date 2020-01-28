package co.axelrod.kafka.editor.kafka;

import co.axelrod.kafka.editor.model.Key;
import co.axelrod.kafka.editor.model.serdes.KeyKafkaSerde;
import lombok.Getter;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KeyStreamProcessor {
    @Getter
    private int keyCount = 0;

    @Autowired
    private KafkaProperties kafkaProperties;

    private KafkaStreams streams;

    public void start(String fileName) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaProperties.getApplicationId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Key> originalStream = builder.stream("bad-git", Consumed.with(Serdes.String(), new KeyKafkaSerde()));
        KStream<String, String> letterStream = originalStream.mapValues((key) -> String.valueOf(key.getKeyChar()));
        letterStream.to("bad-git-letters", Produced.with(Serdes.String(), Serdes.String()));

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

        streams = new KafkaStreams(builder.build(), props);
        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    public void destroy() {
        streams.close();
    }
}
